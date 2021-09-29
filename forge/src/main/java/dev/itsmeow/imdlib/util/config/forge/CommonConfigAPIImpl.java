package dev.itsmeow.imdlib.util.config.forge;

import dev.itsmeow.imdlib.util.config.CommonConfigAPI;
import dev.itsmeow.imdlib.util.config.ConfigBuilder;
import dev.itsmeow.imdlib.util.config.ConfigBuilderForge;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public class CommonConfigAPIImpl {

    public static ConfigBuilder createConfig(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        return new ConfigBuilderForge(type, init, onLoad);
    }

    public static ConfigBuilder createServerConfig(Consumer<ConfigBuilder> init, Consumer<MinecraftServer> onLoad) {
        return new ConfigBuilderForge(init, onLoad);
    }
}
