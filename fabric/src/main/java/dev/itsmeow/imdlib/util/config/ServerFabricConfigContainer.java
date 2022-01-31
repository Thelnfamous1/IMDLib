package dev.itsmeow.imdlib.util.config;

import dev.architectury.event.events.common.LifecycleEvent;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class ServerFabricConfigContainer extends FabricConfigContainer {

    public static ServerFabricConfigContainer INSTANCE = null;

    private LazyLoadedValue<ConfigBuilderFabric> builder;

    public ServerFabricConfigContainer(Consumer<ConfigBuilder> init, Consumer<MinecraftServer> onLoad) {
        super(CommonConfigAPI.ConfigType.SERVER, init);
        this.builder = new LazyLoadedValue<>(() -> new ConfigBuilderFabric(this.getConfigName(), init, onLoad));
        LifecycleEvent.SERVER_BEFORE_START.register(state -> {
            this.invalidate();
            this.builder = new LazyLoadedValue<>(() -> new ConfigBuilderFabric(this.getConfigName(), init, onLoad));
            this.createOrLoad(state.getWorldPath(LevelResource.ROOT).resolve("serverconfig"));
            builder.get().onLoad(state);
        });
        INSTANCE = this;
    }

    public ServerFabricConfigContainer(String levelId) {
        super(CommonConfigAPI.ConfigType.SERVER, INSTANCE.init);
        this.name = levelId + "-" + this.name;
        this.builder = new LazyLoadedValue<>(() -> new ConfigBuilderFabric(this.getConfigName(), init, server -> {}));
    }

    public ServerFabricConfigContainer() {
        super(CommonConfigAPI.ConfigType.SERVER, INSTANCE.init);
        this.name = this.name + "-default";
        this.builder = new LazyLoadedValue<>(() -> new ConfigBuilderFabric(this.getConfigName(), init, server -> {}));
    }

    public void loadFromFile(File file) throws ValueDeserializationException, IOException {
        FiberSerialization.deserialize(this.init(), Files.newInputStream(file.toPath()), janksonSerializer);
    }

    @Override
    protected LazyLoadedValue<ConfigBuilderFabric> getBuilder() {
        return builder;
    }
}
