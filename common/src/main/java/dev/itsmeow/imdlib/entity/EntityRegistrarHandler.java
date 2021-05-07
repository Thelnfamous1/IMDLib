package dev.itsmeow.imdlib.entity;

import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.blockentity.HeadBlockEntity;
import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.item.IContainerItem;
import dev.itsmeow.imdlib.item.ItemModFishBucket;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import dev.itsmeow.imdlib.mixin.EntityTypeAccessor;
import dev.itsmeow.imdlib.mixin.SpawnSettingsAccessor;
import dev.itsmeow.imdlib.util.ClassLoadHacks;
import dev.itsmeow.imdlib.util.HeadType;
import me.shedaniel.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityRegistrarHandler {
    public static boolean useAttributeEvents;

    static {
        try {
            Class.forName("net.minecraftforge.event.entity.EntityAttributeCreationEvent");
            useAttributeEvents = true;
        } catch (ClassNotFoundException | LinkageError e) {
            useAttributeEvents = false;
        }
    }

    public final String modid;
    public final LinkedHashMap<String, EntityTypeContainer<? extends Mob>> ENTITIES = new LinkedHashMap<>();


    public EntityRegistrarHandler(String modid) {
        this.modid = modid;
    }

    private static void setFinalField(Field field, Object object, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(object, newValue);
    }

    //TODO
    /*
    public void subscribe(IEventBus modBus) {
        modBus.register(new EventHandler(this));
        ClassLoadHacks.runIf(useAttributeEvents, () -> () -> modBus.register(new EntityAttributeRegistrar(this)));
    }

     */

    @SuppressWarnings("unchecked")
    public <T extends Mob> EntityTypeContainer<T> getEntityTypeContainer(String name) {
        return (EntityTypeContainer<T>) ENTITIES.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Mob> EntityType<T> getEntityType(String name) {
        return (EntityType<T>) ENTITIES.get(name).getEntityType();
    }

    public <T extends Mob> EntityTypeContainer<T> add(Class<T> entityClass, EntityType.EntityFactory<T> factory, String name, Supplier<AttributeSupplier.Builder> attributeMap, Function<EntityTypeContainer.Builder<T>, EntityTypeContainer.Builder<T>> transformer) {
        return add(transformer.apply(EntityTypeContainer.Builder.create(entityClass, factory, name, attributeMap, modid)));
    }

    public <T extends Mob & IContainable, I extends Item & IContainerItem<T>> EntityTypeContainerContainable<T, I> addContainable(Class<T> entityClass, EntityType.EntityFactory<T> factory, String name, Supplier<AttributeSupplier.Builder> attributeMap, Function<EntityTypeContainerContainable.Builder<T, I>, EntityTypeContainerContainable.Builder<T, I>> transformer) {
        return add(transformer.apply(EntityTypeContainerContainable.Builder.create(entityClass, factory, name, attributeMap, modid)));
    }

    public <T extends Mob & IContainable> EntityTypeContainerContainable<T, ItemModFishBucket<T>> addContainableB(Class<T> entityClass, EntityType.EntityFactory<T> factory, String name, Supplier<AttributeSupplier.Builder> attributeMap, Function<EntityTypeContainerContainable.Builder<T, ItemModFishBucket<T>>, EntityTypeContainerContainable.Builder<T, ItemModFishBucket<T>>> transformer) {
        return add(transformer.apply(EntityTypeContainerContainable.Builder.create(entityClass, factory, name, attributeMap, modid)));
    }

    public <T extends Mob, C extends EntityTypeContainer<T>> C add(IEntityBuilder<T, C, ?> builder) {
        C c = builder.build();
        c.entityType = this.createEntityType(c);
        c.onCreateEntityType();
        ENTITIES.put(c.getEntityName(), c);
        return c;
    }

    public <T extends Mob> EntityType<T> createEntityType(EntityTypeContainer<T> container) {
        return createEntityType(container.getDefinition().getEntityFactory(), container.getEntityName(), container.getDefinition().getSpawnClassification(), 64, 1, true, container.getWidth(), container.getHeight());
    }

    public <T extends Entity> EntityType<T> createEntityType(EntityType.EntityFactory<T> factory, String entityNameIn, MobCategory classification, int trackingRange, int updateInterval, boolean velUpdates, float width, float height) {
        EntityType<T> type = EntityType.Builder.of(factory, classification).clientTrackingRange(trackingRange).updateInterval(updateInterval).sized(width, height).noSave().build(modid + ":" + entityNameIn.toLowerCase());
        //type.setRegistryName(modid + ":" + entityNameIn.toLowerCase());
        ((EntityTypeAccessor) type).setSerialize(true);

        return type;
    }

    public ServerEntityConfiguration serverConfig(ForgeConfigSpec.Builder builder) {
        return new ServerEntityConfiguration(builder);
    }

    public ClientEntityConfiguration clientConfig(ForgeConfigSpec.Builder builder) {
        return new ClientEntityConfiguration(builder);
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
            for (EntityTypeContainer<?> container : handler.ENTITIES.values()) {
                event.getRegistry().register(container.entityType);
                if (!useAttributeEvents) {
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
            for (EntityTypeContainer<?> container : handler.ENTITIES.values()) {
                if (container instanceof EntityTypeContainerContainable<?, ?>) {
                    EntityTypeContainerContainable<?, ?> c = (EntityTypeContainerContainable<?, ?>) container;
                    if (!ForgeRegistries.ITEMS.containsValue(c.getContainerItem()) && handler.modid.equals(c.getContainerItem().getRegistryName().getNamespace())) {
                        event.getRegistry().register(c.getContainerItem());
                    }
                    if (!ForgeRegistries.ITEMS.containsValue(c.getEmptyContainerItem()) && handler.modid.equals(c.getEmptyContainerItem().getRegistryName().getNamespace())) {
                        event.getRegistry().register(c.getEmptyContainerItem());
                    }
                }
                if (container.hasEgg()) {
                    event.getRegistry().register(container.egg);
                }
            }
        }

        @SubscribeEvent
        public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
            HeadBlockEntity.registerType(event, handler.modid);
        }
    }

    public static class EntityAttributeRegistrar {
        private final EntityRegistrarHandler handler;

        public EntityAttributeRegistrar(EntityRegistrarHandler handler) {
            this.handler = handler;
        }

        @SubscribeEvent
        public void attributeCreate(EntityAttributeCreationEvent event) {
            for (EntityTypeContainer<?> container : handler.ENTITIES.values()) {
                event.put(container.entityType, container.getAttributeBuilder().get().create());
            }
        }
    }



}
