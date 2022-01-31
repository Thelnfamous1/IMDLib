package dev.itsmeow.imdlib.util.config;

import blue.endless.jankson.JsonObject;
import dev.itsmeow.imdlib.IMDLib;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.LazyLoadedValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public abstract class FabricConfigContainer {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected final JanksonValueSerializer janksonSerializer = new JanksonValueSerializer(false) {
        @Override
        public void writeTarget(JsonObject target, OutputStream out) throws IOException {
            out.write(("// " + FabricConfigContainer.this.getConfigComment() + "\n" + target.toJson(true, true)).getBytes(StandardCharsets.UTF_8));
        }
    };

    private final CommonConfigAPI.ConfigType type;
    protected String name;
    protected final Consumer<ConfigBuilder> init;
    private ConfigBranch builtConfig;
    private boolean initialized = false;

    public FabricConfigContainer(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init) {
        this.type = type;
        this.name = IMDLib.getRegistries().get().getModId() + "-" + type.name().toLowerCase();
        this.init = init;
    }

    public String getConfigComment() {
        return "";
    }

    public void invalidate() {
        this.initialized = false;
    }

    public CommonConfigAPI.ConfigType getType() {
        return type;
    }

    public ConfigBranch getBranch() {
        if(builtConfig == null) {
            return this.init();
        }
        return builtConfig;
    }

    public String getConfigName() {
        return name;
    }

    protected abstract LazyLoadedValue<ConfigBuilderFabric> getBuilder();

    protected ConfigBranch init() {
        if(!initialized) {
            this.initialized = true;
            init.accept(getBuilder().get());
            this.builtConfig = getBuilder().get().getBuilder().build();
        }
        return this.builtConfig;
    }

    public File getConfigFile(Path serverConfigPath) {
        Path configFolder = serverConfigPath != null ? serverConfigPath : FabricLoader.getInstance().getConfigDir();
        if(serverConfigPath != null && !serverConfigPath.toFile().exists()) {
            serverConfigPath.toFile().mkdirs();
        }
        return new File(configFolder.toFile(), getConfigName() + ".json5");
    }

    public void createOrLoad(Path serverConfigPath) {
        setupConfigFile(this.getConfigFile(serverConfigPath), this.init(), janksonSerializer);
    }

    public void saveBranch(File configFile, ConfigBranch branch) {
        try {
            FiberSerialization.serialize(branch, Files.newOutputStream(configFile.toPath()), janksonSerializer);
            LOGGER.info("Successfully wrote menu edits to config file '{}'", configFile.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
