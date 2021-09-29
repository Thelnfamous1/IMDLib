package dev.itsmeow.imdlib.util.config;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public class CommonConfigAPI {

    @ExpectPlatform
    public static ConfigBuilder createConfig(ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        throw new RuntimeException("ExpectPlatform CommonConfigAPI.createConfig() failed");
    }

    @ExpectPlatform
    public static ConfigBuilder createServerConfig(Consumer<ConfigBuilder> init, Consumer<MinecraftServer> onLoad) {
        throw new RuntimeException("ExpectPlatform CommonConfigAPI.createServerConfig() failed");
    }

    public enum ConfigType {
        SERVER,
        CLIENT,
        COMMON;
    }
}
