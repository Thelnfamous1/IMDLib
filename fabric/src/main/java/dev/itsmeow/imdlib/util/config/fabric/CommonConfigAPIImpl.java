package dev.itsmeow.imdlib.util.config.fabric;

import dev.itsmeow.imdlib.util.config.CommonConfigAPI;
import dev.itsmeow.imdlib.util.config.ConfigBuilder;
import dev.itsmeow.imdlib.util.config.FabricConfigContainer;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public class CommonConfigAPIImpl {

    public static void createConfig(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        new FabricConfigContainer(type, init, onLoad);
    }

    public static void createServerConfig(Consumer<ConfigBuilder> init, Consumer<MinecraftServer> onLoad) {
        new FabricConfigContainer(init, onLoad);
    }
}
