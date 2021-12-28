package dev.itsmeow.imdlib.util.config;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.itsmeow.imdlib.util.ClassLoadHacks;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.LazyLoadedValue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class CommonFabricConfigContainer extends FabricConfigContainer {

    public static final Set<FabricConfigContainer> INSTANCES = new HashSet<>();

    private LazyLoadedValue<ConfigBuilderFabric> builder;

    public CommonFabricConfigContainer(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        super(type, init);
        this.builder = new LazyLoadedValue<>(() -> new ConfigBuilderFabric(this.getConfigName(), type, init, onLoad));
        ClassLoadHacks.runIf(Platform.getEnvironment() == Env.CLIENT, () -> () -> ClientPlayConnectionEvents.INIT.register((phase, handler) -> {
            this.createOrLoad(null);
            builder.get().onLoad(null);
        }));
        LifecycleEvent.SERVER_BEFORE_START.register(state -> {
            this.createOrLoad(null);
            builder.get().onLoad(state);
        });
        INSTANCES.add(this);
    }

    @Override
    protected LazyLoadedValue<ConfigBuilderFabric> getBuilder() {
        return builder;
    }
}
