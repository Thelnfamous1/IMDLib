package dev.itsmeow.imdlib;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.utils.GameInstance;
import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public class IMDLib {

    private static Optional<Registries> REGISTRIES = Optional.empty();
    private static MinecraftServer SERVER;

    public static Optional<Registries> getRegistries() {
        return REGISTRIES;
    }

    public static <T> Registrar<T> getRegistry(ResourceKey<net.minecraft.core.Registry<T>> key) {
        if(!getRegistries().isPresent()) {
            throw new RuntimeException("Registries have not been initialized yet! Call IMDLib.setRegistry(modid) or use IMDLib.entityHandler(modid)");
        }
        return getRegistries().get().get(key);
    }

    public static void setRegistry(String modid) {
        REGISTRIES = Optional.of(Registries.get(modid));
    }

    public static void setStaticServerInstance(MinecraftServer server) {
        SERVER = server;
    }

    public static MinecraftServer getStaticServerInstance() {
        if(SERVER == null) {
            MinecraftServer s = GameInstance.getServer();
            if(s != null) {
                SERVER = s;
                return SERVER;
            }
            throw new RuntimeException("Server not initialized yet! Call IMDLib.setStaticServerInstance(server) or use IMDLib.entityHandler(modid)");
        }
        return SERVER;
    }

    public static EntityRegistrarHandler entityHandler(String modid) {
        setRegistry(modid);
        return new EntityRegistrarHandler(modid);
    }

}
