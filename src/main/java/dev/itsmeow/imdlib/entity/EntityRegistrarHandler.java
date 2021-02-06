package dev.itsmeow.imdlib.entity;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public class EntityRegistrarHandler {

    public final String modid;
    public final LinkedHashMap<String, EntityTypeContainer<? extends MobEntity>> ENTITIES = new LinkedHashMap<>();
    private static final Field SERIALIZABLE = ObfuscationReflectionHelper.findField(EntityType.class, "field_200733_aL");
    private static Map<RegistryKey<Biome>, WeakReference<MobSpawnInfo>> spawnInfo = new HashMap<>();


    public EntityRegistrarHandler(String modid) {
        this.modid = modid;
    }

    public void gatherData(DataGenerator gen, ExistingFileHelper helper) {
        gen.addProvider(new ModSpawnEggItem.DataProvider(this, gen, helper));
    }

    @SuppressWarnings("unchecked")
    public <T extends MobEntity> EntityTypeContainer<T> getEntityTypeContainer(String name) {
        return (EntityTypeContainer<T>) ENTITIES.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends MobEntity> EntityType<T> getEntityType(String name) {
        return (EntityType<T>) ENTITIES.get(name).entityType;
    }

    public <T extends MobEntity, C extends EntityTypeContainer<T>> C add(IEntityBuilder<T, C, ?> builder) {
        C c = builder.build();
        c.entityType = this.createEntityType(c);
        c.onCreateEntityType();
        ENTITIES.put(c.entityName, c);
        return c;
    }

    public <T extends MobEntity> EntityType<T> createEntityType(EntityTypeContainer<T> container) {
        return createEntityType(container.entityClass, container.factory, container.entityName, container.spawnType, 64, 1, true, container.width, container.height);
    }

    public <T extends Entity> EntityType<T> createEntityType(Class<T> EntityClass, Function<World, T> func, String entityNameIn, EntityClassification classification, int trackingRange, int updateInterval, boolean velUpdates, float width, float height) {
        EntityType<T> type = EntityType.Builder.<T>create((etype, world) -> func.apply(world), classification).setTrackingRange(trackingRange).setUpdateInterval(updateInterval).setShouldReceiveVelocityUpdates(velUpdates).size(width, height).setCustomClientFactory((e, world) -> func.apply(world)).disableSerialization().build(modid + ":" + entityNameIn.toLowerCase());
        type.setRegistryName(modid + ":" + entityNameIn.toLowerCase());

        // It's okay, I hate it too
        try {
            setFinalField(SERIALIZABLE, type, true);
        } catch(Exception e) {
            LogManager.getLogger().warn("Unable to set serializable for {}. This could result in possible saving issues with entities!", entityNameIn);
        }
        return type;
    }

    private static void setFinalField(Field field, Object object, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(object, newValue);
    }

    public ServerEntityConfiguration serverConfig(ForgeConfigSpec.Builder builder) {
        return new ServerEntityConfiguration(builder);
    }

    public ClientEntityConfiguration clientConfig(ForgeConfigSpec.Builder builder) {
        return new ClientEntityConfiguration(builder);
    }

    public class ServerEntityConfiguration {

        ServerEntityConfiguration(ForgeConfigSpec.Builder builder) {
            builder.push("entities");
            {
                ENTITIES.values().forEach(c -> c.initConfiguration(builder));
            }
            builder.pop();
        }

        public void onLoad() {
            // Replace entity data
            ENTITIES.values().forEach(EntityTypeContainer::configurationLoad);

            if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
                MutableRegistry<Biome> biomeRegistry = ServerLifecycleHooks.getCurrentServer().func_244267_aX().getRegistry(Registry.BIOME_KEY);
                for(ResourceLocation key : biomeRegistry.keySet()) {
                    Biome biome = biomeRegistry.getOptional(key).get();
                    MobSpawnInfo spawnInfo = biome.getMobSpawnInfo();
                    // make spawns mutable
                    spawnInfo.spawners = new HashMap<>(spawnInfo.spawners);
                    // make spawner lists mutable
                    for(EntityClassification classification : EntityClassification.values()) {
                        ArrayList<MobSpawnInfo.Spawners> newList = new ArrayList<>();
                        List<MobSpawnInfo.Spawners> oldList = spawnInfo.spawners.get(classification);
                        if(oldList != null) {
                            for (MobSpawnInfo.Spawners spawner : oldList) {
                                newList.add(spawner);
                            }
                        }
                        spawnInfo.spawners.put(classification, newList);
                    }
                    // make costs mutable
                    spawnInfo.spawnCosts = new HashMap<>(spawnInfo.spawnCosts);
                    for(EntityTypeContainer<?> entry : ENTITIES.values()) {
                        if(entry.doSpawning && entry.spawnWeight > 0 && entry.getBiomeIDs().contains(key.toString())) {
                            entry.registerPlacement();
                            List<MobSpawnInfo.Spawners> list = spawnInfo.spawners.get(entry.spawnType);
                            if (list != null) {
                                list.add(entry.getSpawnEntry());
                            }
                            if (entry.spawnCostPer != 0 && entry.spawnMaxCost != 0) {
                                // stupid private constructors
                                MobSpawnInfo.SpawnCosts costs = new MobSpawnInfo.Builder().withSpawnCost(entry.entityType, entry.spawnCostPer, entry.spawnMaxCost).copy().spawnCosts.get(entry.entityType);
                                spawnInfo.spawnCosts.put(entry.entityType, costs);
                            }
                        }
                    }
                }
            }
        }

    }

    public class ClientEntityConfiguration {

        ClientEntityConfiguration(ForgeConfigSpec.Builder builder) {
            builder.comment("This is the CLIENTSIDE configuration for " + modid + ".",
            "To configure SERVER values (spawning, behavior, etc), go to:",
            "saves/(world)/serverconfig/" + modid + "-server.toml",
            "or, on a dedicated server:",
            "(world)/serverconfig/" + modid + "-server.toml");
            builder.push("entities");
            {
                ENTITIES.values().forEach(c -> c.clientCustomConfigurationInit(builder));
            }
            builder.pop();
        }

        public void onLoad() {
            ENTITIES.values().forEach(EntityTypeContainer::clientConfigurationLoad);
        }

    }

}
