package dev.itsmeow.imdlib.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityRegistrarHandler {

    public final String modid;
    public final LinkedHashMap<String, EntityTypeContainer<? extends MobEntity>> ENTITIES = new LinkedHashMap<>();
    private static final Method ADDSPAWN = ObfuscationReflectionHelper.findMethod(Biome.class, "func_201866_a", EntityClassification.class, SpawnListEntry.class);
    private static final Field SERIALIZABLE = ObfuscationReflectionHelper.findField(EntityType.class, "field_200733_aL");


    public EntityRegistrarHandler(String modid) {
        this.modid = modid;
    }

    @SuppressWarnings("unchecked")
    public <T extends MobEntity> EntityTypeContainer<T> getEntityTypeContainer(String name) {
        return (EntityTypeContainer<T>) ENTITIES.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends MobEntity> EntityType<T> getEntityType(String name) {
        return (EntityType<T>) ENTITIES.get(name).entityType;
    }

    public <T extends MobEntity> EntityTypeContainer<T> add(EntityTypeContainer.Builder<T> builder) {
        EntityTypeContainer<T> c = builder.build();
        c.entityType = this.createEntityType(c);
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
            ENTITIES.values().forEach(c -> c.initConfiguration(builder));
        }

        public void onLoad() {
            // Replace entity data
            for(EntityTypeContainer<?> container : ENTITIES.values()) {
                EntityTypeContainer<?>.EntityConfiguration section = container.getConfiguration();
                container.configurationLoad();

                // Parse biomes
                List<Biome> biomesList = new ArrayList<Biome>();
                for(String biomeID : section.biomesList.get()) {
                    Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeID));
                    if(biome == null) { // Could not get biome with ID
                        LogManager.getLogger().error("[" + modid + "] Invalid biome configuration entered for entity \"" + container.entityName + "\" (biome was mistyped or a biome mod was removed?): " + biomeID);
                    } else { // Valid biome
                        biomesList.add(biome);
                    }
                }

                container.setBiomes(biomesList.toArray(new Biome[0]));
            }
        }

        @SuppressWarnings("unchecked")
        public void onWorldLoad() {
            // Fill containers with proper values from their config sections
            this.onLoad();

            // Add spawns based on new container data
            if(!ENTITIES.values().isEmpty()) {
                for(EntityTypeContainer<?> entry : ENTITIES.values()) {
                    EntityType<?> type = entry.entityType;
                    if(entry.doSpawning) {
                        entry.registerPlacement();
                        for(Biome biome : entry.getBiomes()) {
                            try {
                                biome.addSpawn(entry.spawnType, new SpawnListEntry((EntityType<? extends MobEntity>) type, entry.spawnWeight, entry.spawnMinGroup, entry.spawnMaxGroup));
                            } catch(RuntimeException e) {
                                try {
                                    ADDSPAWN.invoke(biome, entry.spawnType, new SpawnListEntry((EntityType<? extends MobEntity>) type, entry.spawnWeight, entry.spawnMinGroup, entry.spawnMaxGroup));
                                } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
                                    e2.printStackTrace();
                                    e.printStackTrace();
                                }
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
            for(EntityTypeContainer<?> cont : ENTITIES.values()) {
                cont.clientCustomConfigurationInit(builder);
            }
            ENTITIES.values().forEach(c -> c.clientCustomConfigurationInit(builder));
        }

        public void onLoad() {
            ENTITIES.values().forEach(EntityTypeContainer::clientConfigurationLoad);
        }

    }

}
