package dev.itsmeow.imdlib.util.config.forge;

import dev.itsmeow.imdlib.util.config.CommonConfigAPI;
import dev.itsmeow.imdlib.util.config.ConfigBuilder;
import dev.itsmeow.imdlib.util.config.ConfigBuilderForge;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public class CommonConfigAPIImpl {

    public static void createConfig(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        new ConfigBuilderForge(type, init, onLoad);
    }

    public static void createClientReplaceConfig(Consumer<ConfigBuilder> init, Runnable onLoad) {
        new ConfigBuilderForge(CommonConfigAPI.ConfigType.CLIENT, init, onLoad, false);
    }

    public static void createServerConfig(Consumer<ConfigBuilder> init, Consumer<MinecraftServer> onLoad) {
        new ConfigBuilderForge(init, onLoad);
    }

    public static void loadClientReplace() {
    }
}
