package dev.itsmeow.imdlib.entity;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import dev.itsmeow.imdlib.tileentity.TileEntityHead;
import dev.itsmeow.imdlib.util.ClassLoadHacks;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
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
    public static boolean useAttributeEvents;
    static {
        try {
            Class.forName("net.minecraftforge.event.entity.EntityAttributeCreationEvent");
            useAttributeEvents = true;
        } catch (ClassNotFoundException | LinkageError e) {
            useAttributeEvents = false;
        }
    }


    public EntityRegistrarHandler(String modid) {
        this.modid = modid;
    }

    @SuppressWarnings("deprecation")
    public void subscribe(IEventBus modBus) {
        modBus.addListener((GatherDataEvent event) -> {
            event.getGenerator().addProvider(new ModSpawnEggItem.DataProvider(this, event.getGenerator(), event.getExistingFileHelper()));
        });
        modBus.addListener((RegistryEvent.Register<EntityType<?>> event) -> {
            for(EntityTypeContainer<?> container : ENTITIES.values()) {
                event.getRegistry().register(container.entityType);
                if(!useAttributeEvents) {
                    container.registerAttributes();
                }
            }
        });
        modBus.addListener((RegistryEvent.Register<Block> event) -> {
            for (HeadType type : HeadType.values()) {
                event.getRegistry().registerAll(type.getBlockSet().toArray(new Block[0]));
            }
        });
        modBus.addListener((RegistryEvent.Register<Item> event) -> {
            // Heads
            for (HeadType type : HeadType.values()) {
                event.getRegistry().registerAll(type.getItemSet().toArray(new Item[0]));
            }

            // Containers & eggs
            for(EntityTypeContainer<?> container : ENTITIES.values()) {
                if (container instanceof EntityTypeContainerContainable<?, ?>) {
                    EntityTypeContainerContainable<?, ?> c = (EntityTypeContainerContainable<?, ?>) container;
                    if (!ForgeRegistries.ITEMS.containsValue(c.getContainerItem()) && c.getContainerItem().getRegistryName().getNamespace().equals(modid)) {
                        event.getRegistry().register(c.getContainerItem());
                    }
                    if (!ForgeRegistries.ITEMS.containsValue(c.getEmptyContainerItem()) && c.getEmptyContainerItem().getRegistryName().getNamespace().equals(modid)) {
                        event.getRegistry().register(c.getEmptyContainerItem());
                    }
                }
                if(container.hasEgg) {
                    event.getRegistry().register(container.egg);
                }
            }
        });
        modBus.addListener((RegistryEvent.Register<TileEntityType<?>> event) -> {
            TileEntityHead.registerType(event, modid);
        });
        ClassLoadHacks.runIf(useAttributeEvents, () -> () -> {
            modBus.register(new EntityAttributeRegistrar(this));
        });
    }

    public static class EntityAttributeRegistrar {
        private final EntityRegistrarHandler handler;

        public EntityAttributeRegistrar(EntityRegistrarHandler handler) {
            this.handler = handler;
        }

        @SubscribeEvent
        public void attributeCreate(EntityAttributeCreationEvent event) {
            for(EntityTypeContainer<?> container : handler.ENTITIES.values()) {
                event.put(container.entityType, container.getAttributeBuilder().get().create());
            }
        }
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
        try {
            // attempt using AT, which might not present in implementer
            // to explain, I do this to avoid the "no data fixer registered" log spam - it's not really an issue but why not avoid it
            type.serializable = true;
        } catch(Exception e) {
            // attempt using reflection, which doesn't work on newer JDK. maybe implement some logic that checks the security manager?
            try {
                setFinalField(SERIALIZABLE, type, true);
            } catch(Exception e2) {
                LogManager.getLogger().error("Unable to set serializable for {}. This could result in possible saving issues with entities!", entityNameIn);
                e2.printStackTrace();
            }
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
                MutableRegistry<Biome> biomeRegistry = ServerLifecycleHooks.getCurrentServer().getDynamicRegistries().getRegistry(Registry.BIOME_KEY);
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
                                MobSpawnInfo.SpawnCosts costs = new MobSpawnInfo.Builder().withSpawnCost(entry.entityType, entry.spawnCostPer, entry.spawnMaxCost).build().spawnCosts.get(entry.entityType);
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
