package dev.itsmeow.imdlib.config;

import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;
import net.minecraftforge.common.ForgeConfigSpec;
/*import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;*/

public class ForgeEntityRegistrarConfigHandler implements EntityRegistrarConfigHandler {
    private final EntityRegistrarHandler handler;

    public ForgeEntityRegistrarConfigHandler(EntityRegistrarHandler handler) {
        this.handler = handler;
    }


    public ServerEntityConfiguration serverConfig(ForgeConfigSpec.Builder builder) {
        return new ServerEntityConfiguration(builder);
    }

    public ClientEntityConfiguration clientConfig(ForgeConfigSpec.Builder builder) {
        return new ClientEntityConfiguration(builder);
    }

    public class ServerEntityConfiguration {

        ServerEntityConfiguration(ForgeConfigSpec.Builder builder) {
            /*builder.push("entities");
            {
                handler.ENTITIES.values().forEach(c -> c.createConfiguration(builder));
            }
            builder.pop();*/
        }

        public void onLoad() {
            // Update entity data
            /*handler.ENTITIES.values().forEach(e -> e.getConfiguration().load());

            if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
                Optional<WritableRegistry<Biome>> biomeRegistry = ServerLifecycleHooks.getCurrentServer().registryAccess().registry(Registry.BIOME_REGISTRY);
                if (biomeRegistry.isPresent()) {
                    for (ResourceLocation key : biomeRegistry.get().keySet()) {
                        Biome biome = biomeRegistry.get().getOptional(key).get();
                        MobSpawnSettings spawnInfo = biome.getMobSettings();
                        // make spawns mutable
                        spawnInfo.spawners = new HashMap<>(spawnInfo.spawners);
                        // make spawner lists mutable
                        for (MobCategory classification : MobCategory.values()) {
                            ArrayList<MobSpawnSettings.SpawnerData> newList = new ArrayList<>();
                            List<MobSpawnSettings.SpawnerData> oldList = spawnInfo.spawners.get(classification);
                            if (oldList != null) {
                                newList.addAll(oldList);
                            }
                            spawnInfo.spawners.put(classification, newList);
                        }
                        // make costs mutable
                        spawnInfo.spawnCosts = new HashMap<>(spawnInfo.spawnCosts);
                        for (EntityTypeContainer<?> entry : ENTITIES.values()) {
                            EntityTypeContainer<?>.EntityConfiguration config = entry.getConfiguration();
                            if (config.doSpawning.get() && config.spawnWeight.get() > 0 && entry.getBiomeIDs().contains(key.toString())) {
                                entry.registerPlacement();
                                List<MobSpawnInfo.Spawners> list = spawnInfo.spawners.get(entry.getDefinition().getSpawnClassification());
                                if (list != null) {
                                    list.add(entry.getSpawnEntry());
                                }
                                if (config.spawnCostPer.get() != 0 && config.spawnMaxCost.get() != 0 && entry.getSpawnCostBiomeIDs().contains(key.toString())) {
                                    // stupid private constructors
                                    MobSpawnInfo.SpawnCosts costs = new MobSpawnInfo.Builder().withSpawnCost(entry.getEntityType(), config.spawnCostPer.get(), config.spawnMaxCost.get()).build().spawnCosts.get(entry.getEntityType());
                                    spawnInfo.spawnCosts.put(entry.getEntityType(), costs);
                                }
                            }
                        }
                    }
                }


            }*/
        }

    }

    public class ClientEntityConfiguration {

        ClientEntityConfiguration(ForgeConfigSpec.Builder builder) {
            /*builder.comment("This is the CLIENT SIDE configuration for " + modid + ".",
                    "To configure SERVER values (spawning, behavior, etc), go to:",
                    "saves/(world)/serverconfig/" + modid + "-server.toml",
                    "or, on a dedicated server:",
                    "(world)/serverconfig/" + modid + "-server.toml");
            builder.push("entities");
            {
                handler.ENTITIES.values().forEach(c -> c.clientCustomConfigurationInit(builder));
            }
            builder.pop();*/
        }

        public void onLoad() {
            //handler.ENTITIES.values().forEach(EntityTypeContainer::clientCustomConfigurationLoad);
        }

    }

}