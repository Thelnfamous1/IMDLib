package dev.itsmeow.imdlib.util.config.fabric;

import dev.itsmeow.imdlib.util.config.CommonConfigAPI;
import dev.itsmeow.imdlib.util.config.ConfigBuilder;
import dev.itsmeow.imdlib.util.config.ConfigBuilderFabric;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public class CommonConfigAPIImpl {

    public static ConfigBuilder createConfig(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        return new ConfigBuilderFabric(type, init, onLoad);
    }

    public static ConfigBuilder createServerConfig(Consumer<ConfigBuilder> init, Consumer<MinecraftServer> onLoad) {
        return new ConfigBuilderFabric(init, onLoad);
    }
}
