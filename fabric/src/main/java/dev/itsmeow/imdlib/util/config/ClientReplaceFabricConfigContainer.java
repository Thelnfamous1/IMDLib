package dev.itsmeow.imdlib.util.config;

import net.minecraft.util.LazyLoadedValue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ClientReplaceFabricConfigContainer extends FabricConfigContainer {

    public static final Set<ClientReplaceFabricConfigContainer> INSTANCES = new HashSet<>();

    private LazyLoadedValue<ConfigBuilderFabric> builder;

    public ClientReplaceFabricConfigContainer(Consumer<ConfigBuilder> init, Runnable onLoad) {
        super(CommonConfigAPI.ConfigType.CLIENT, init);
        this.builder = new LazyLoadedValue<>(() -> new ConfigBuilderFabric(this.getConfigName(), CommonConfigAPI.ConfigType.CLIENT, init, onLoad));
        INSTANCES.add(this);
    }

    public void load() {
        this.createOrLoad(null);
        builder.get().onLoad(null);
    }

    @Override
    protected LazyLoadedValue<ConfigBuilderFabric> getBuilder() {
        return builder;
    }

}
