package dev.itsmeow.imdlib.entity;

import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.item.IContainerItem;
import dev.itsmeow.imdlib.item.ItemModFishBucket;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import dev.itsmeow.imdlib.tileentity.TileEntityHead;
import dev.itsmeow.imdlib.util.ClassLoadHacks;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityRegistrarHandler {

    public final String modid;
    public final LinkedHashMap<String, EntityTypeContainer<? extends MobEntity>> ENTITIES = new LinkedHashMap<>();
    private static final Field SERIALIZABLE = ObfuscationReflectionHelper.findField(EntityType.class, "field_200733_aL");
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

    public void subscribe(IEventBus modBus) {
        modBus.register(new EventHandler(this));
        ClassLoadHacks.runIf(useAttributeEvents, () -> () -> modBus.register(new EntityAttributeRegistrar(this)));
    }

    public static class EventHandler {
        private final EntityRegistrarHandler handler;

        public EventHandler(EntityRegistrarHandler handler) {
            this.handler = handler;
        }

        @SubscribeEvent
        public void gatherData(GatherDataEvent event) {
            event.getGenerator().addProvider(new ModSpawnEggItem.DataProvider(handler, event.getGenerator(), event.getExistingFileHelper()));
        }

        @SubscribeEvent
        public void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
            for(EntityTypeContainer<?> container : handler.ENTITIES.values()) {
                event.getRegistry().register(container.entityType);
                if(!useAttributeEvents) {
                    container.registerAttributes();
                }
            }
        }

        @SubscribeEvent
        public void registerBlocks(RegistryEvent.Register<Block> event) {
            for (HeadType type : HeadType.values()) {
                event.getRegistry().registerAll(type.getBlockSet().toArray(new Block[0]));
            }
        }

        @SubscribeEvent
        public void registerItems(RegistryEvent.Register<Item> event) {
            // Heads
            for (HeadType type : HeadType.values()) {
                event.getRegistry().registerAll(type.getItemSet().toArray(new Item[0]));
            }

            // Containers & eggs
            for(EntityTypeContainer<?> container : handler.ENTITIES.values()) {
                if (container instanceof EntityTypeContainerContainable<?, ?>) {
                    EntityTypeContainerContainable<?, ?> c = (EntityTypeContainerContainable<?, ?>) container;
                    if (!ForgeRegistries.ITEMS.containsValue(c.getContainerItem()) && handler.modid.equals(c.getContainerItem().getRegistryName().getNamespace())) {
                        event.getRegistry().register(c.getContainerItem());
                    }
                    if (!ForgeRegistries.ITEMS.containsValue(c.getEmptyContainerItem()) && handler.modid.equals(c.getEmptyContainerItem().getRegistryName().getNamespace())) {
                        event.getRegistry().register(c.getEmptyContainerItem());
                    }
                }
                if(container.hasEgg()) {
                    event.getRegistry().register(container.egg);
                }
            }
        }

        @SubscribeEvent
        public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
            TileEntityHead.registerType(event, handler.modid);
        }
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
        return (EntityType<T>) ENTITIES.get(name).getEntityType();
    }

    public <T extends MobEntity> EntityTypeContainer<T> add(Class<T> entityClass, EntityType.IFactory<T> factory, String name, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, Function<EntityTypeContainer.Builder<T>, EntityTypeContainer.Builder<T>> transformer) {
        return add(transformer.apply(EntityTypeContainer.Builder.create(entityClass, factory, name, attributeMap, modid)));
    }

    public <T extends MobEntity & IContainable, I extends Item & IContainerItem<T>> EntityTypeContainerContainable<T, I> addContainable(Class<T> entityClass, EntityType.IFactory<T> factory, String name, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, Function<EntityTypeContainerContainable.Builder<T, I>, EntityTypeContainerContainable.Builder<T, I>> transformer) {
        return add(transformer.apply(EntityTypeContainerContainable.Builder.create(entityClass, factory, name, attributeMap, modid)));
    }

    public <T extends MobEntity & IContainable> EntityTypeContainerContainable<T, ItemModFishBucket<T>> addContainableB(Class<T> entityClass, EntityType.IFactory<T> factory, String name, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, Function<EntityTypeContainerContainable.Builder<T, ItemModFishBucket<T>>, EntityTypeContainerContainable.Builder<T, ItemModFishBucket<T>>> transformer) {
        return add(transformer.apply(EntityTypeContainerContainable.Builder.create(entityClass, factory, name, attributeMap, modid)));
    }

    public <T extends MobEntity, C extends EntityTypeContainer<T>> C add(IEntityBuilder<T, C, ?> builder) {
        C c = builder.build();
        c.entityType = this.createEntityType(c);
        c.onCreateEntityType();
        ENTITIES.put(c.getEntityName(), c);
        return c;
    }

    public <T extends MobEntity> EntityType<T> createEntityType(EntityTypeContainer<T> container) {
        return createEntityType(container.getDefinition().getEntityFactory(), container.getEntityName(), container.getDefinition().getSpawnClassification(), 64, 1, true, container.getWidth(), container.getHeight());
    }

    public <T extends Entity> EntityType<T> createEntityType(EntityType.IFactory<T> factory, String entityNameIn, EntityClassification classification, int trackingRange, int updateInterval, boolean velUpdates, float width, float height) {
        EntityType<T> type = EntityType.Builder.create(factory, classification).setTrackingRange(trackingRange).setUpdateInterval(updateInterval).setShouldReceiveVelocityUpdates(velUpdates).size(width, height).disableSerialization().build(modid + ":" + entityNameIn.toLowerCase());
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
                ENTITIES.values().forEach(c -> c.createConfiguration(builder));
            }
            builder.pop();
        }

        public void onLoad() {
            // Update entity data
            ENTITIES.values().forEach(e -> e.getConfiguration().load());

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
                            newList.addAll(oldList);
                        }
                        spawnInfo.spawners.put(classification, newList);
                    }
                    // make costs mutable
                    spawnInfo.spawnCosts = new HashMap<>(spawnInfo.spawnCosts);
                    for(EntityTypeContainer<?> entry : ENTITIES.values()) {
                        EntityTypeContainer<?>.EntityConfiguration config = entry.getConfiguration();
                        if(config.doSpawning.get() && config.spawnWeight.get() > 0 && entry.getBiomeIDs().contains(key.toString())) {
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
        }

    }

    public class ClientEntityConfiguration {

        ClientEntityConfiguration(ForgeConfigSpec.Builder builder) {
            builder.comment("This is the CLIENT SIDE configuration for " + modid + ".",
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
            ENTITIES.values().forEach(EntityTypeContainer::clientCustomConfigurationLoad);
        }

    }

}
