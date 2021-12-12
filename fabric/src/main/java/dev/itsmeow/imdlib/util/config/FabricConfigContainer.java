package dev.itsmeow.imdlib.util.config;

import dev.itsmeow.imdlib.IMDLib;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import me.shedaniel.architectury.event.events.LifecycleEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class FabricConfigContainer {

    private static final Logger LOGGER = LogManager.getLogger();

    private final CommonConfigAPI.ConfigType type;
    private ConfigBuilderFabric builder;
    private final String name;
    private final Consumer<ConfigBuilder> init;
    private ConfigBranch builtConfig;
    private boolean initialized = false;

    public FabricConfigContainer(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        this.type = type;
        this.builder = new ConfigBuilderFabric(type, init, onLoad);
        this.name = IMDLib.getRegistries().getModId() + "-" + type.name().toLowerCase();
        this.init = init;
        LifecycleEvent.SERVER_BEFORE_START.register(state -> {
            this.createOrLoad(null);
            builder.onLoad(state);
        });
    }

    public FabricConfigContainer(Consumer<ConfigBuilder> init, Consumer<MinecraftServer> onLoad) {
        this.type = CommonConfigAPI.ConfigType.SERVER;
        this.name = IMDLib.getRegistries().getModId() + "-" + type.name().toLowerCase();
        this.init = init;
        LifecycleEvent.SERVER_BEFORE_START.register(state -> {
            this.initialized = false;
            this.builder = new ConfigBuilderFabric(init, onLoad);
            this.createOrLoad(state.getWorldPath(LevelResource.ROOT).resolve("serverconfig"));
            builder.onLoad(state);
        });
    }

    public String getConfigName() {
        return name;
    }

    private ConfigBranch init() {
        if(!initialized) {
            this.initialized = true;
            init.accept(builder);
            this.builtConfig = builder.getBuilder().build();
        }
        return this.builtConfig;
    }

    public void createOrLoad(Path serverConfigPath) {
        Path configFolder = serverConfigPath != null ? serverConfigPath : FabricLoader.getInstance().getConfigDir();
        if(serverConfigPath != null && !serverConfigPath.toFile().exists()) {
            serverConfigPath.toFile().mkdirs();
        }
        File file = new File(configFolder.toFile(), getConfigName() + ".json5");
        JanksonValueSerializer configSerializer = new JanksonValueSerializer(false);
        setupConfigFile(file, this.init(), configSerializer);
    }

    private void deserializeFromDefault(ConfigBranch configNode, JanksonValueSerializer serializer) throws IOException {
        File defaultFolder = FabricLoader.getInstance().getGameDir().resolve("defaultconfigs").toFile();
        if(!defaultFolder.exists()) {
            defaultFolder.mkdirs();
        }
        File defaultConfig = new File(defaultFolder, getConfigName() + ".json5");
        if(defaultConfig.exists()) {
            try {
                FiberSerialization.deserialize(configNode, Files.newInputStream(defaultConfig.toPath()), serializer);
            } catch (ValueDeserializationException e) {
                String fileName = (getConfigName() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".json5");
                LOGGER.error("Found a syntax error in the *default* config!");
                if (defaultConfig.renameTo(new File(defaultConfig.getParent(), fileName))) {
                    LOGGER.info("*Default* config file successfully renamed to '{}'.", fileName);
                }
                e.printStackTrace();
            }
        }
    }

    private void setupConfigFile(File configFile, ConfigBranch configNode, JanksonValueSerializer serializer) {
        boolean recreate = false;
        while (true) {
            try {
                if (!configFile.exists() || recreate) {
                    this.deserializeFromDefault(configNode, serializer);
                    FiberSerialization.serialize(configNode, Files.newOutputStream(configFile.toPath()), serializer);
                    LOGGER.info("Successfully created the config file in '{}'", configFile.toString());
                    break;
                } else {
                    try {
                        FiberSerialization.deserialize(configNode, Files.newInputStream(configFile.toPath()), serializer);
                        FiberSerialization.serialize(configNode, Files.newOutputStream(configFile.toPath()), serializer);
                        LOGGER.info("Successfully loaded '{}'", configFile.toString());
                        break;
                    } catch (ValueDeserializationException e) {
                        String fileName = (getConfigName() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".json5");
                        LOGGER.error("Found a syntax error in the config!");
                        if (configFile.renameTo(new File(configFile.getParent(), fileName))) {
                            LOGGER.info("Config file successfully renamed to '{}'.", fileName);
                        }
                        recreate = true;
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
