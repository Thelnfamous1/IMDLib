package dev.itsmeow.imdlib.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.blockentity.HeadBlockEntity;
import dev.itsmeow.imdlib.config.EntityRegistrarConfigHandler;
import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.item.IContainerItem;
import dev.itsmeow.imdlib.item.ItemModFishBucket;
import dev.itsmeow.imdlib.mixin.EntityTypeAccessor;
import dev.itsmeow.imdlib.util.HeadType;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.LinkedHashMap;
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

    @ExpectPlatform
    public static void platformInit(boolean useAttributeEvents, EntityRegistrarHandler handler) {
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static EntityRegistrarConfigHandler getConfigHandlerFor(EntityRegistrarHandler handler) {
        throw new RuntimeException();
    }

    public void init() {

        //items
        Registry<Item> items = IMDLib.REGISTRIES.get().get(net.minecraft.core.Registry.ITEM_REGISTRY);

        for (HeadType type : HeadType.values()) {
            items.register(new ResourceLocation(type.getMod(), type.getName()), type::getItem);
        }

        // Containers & eggs
        for (EntityTypeContainer<?> container : ENTITIES.values()) {
            if (container instanceof EntityTypeContainerContainable<?, ?>) {
                EntityTypeContainerContainable<?, ?> c = (EntityTypeContainerContainable<?, ?>) container;
                if (!items.containsValue(c.getContainerItem())) {
                    items.register(new ResourceLocation(modid, c.getContainerItemName()), c::getContainerItem);
                }
                if (!items.containsValue(c.getEmptyContainerItem())) {
                    items.register(new ResourceLocation(modid, c.getEmptyContainerItemName()), c::getEmptyContainerItem);
                }
            }
            if (container.hasEgg()) {
                items.register(new ResourceLocation(modid, container.egg.name), () -> container.egg);
            }
        }

        //blocks
        Registry<Block> blocks = IMDLib.REGISTRIES.get().get(net.minecraft.core.Registry.BLOCK_REGISTRY);
        for (HeadType type : HeadType.values()) {
            blocks.register(new ResourceLocation(type.getMod(), type.getName()), type::getBlock);
        }
        Registry<BlockEntityType<?>> blockEntities = IMDLib.REGISTRIES.get().get(net.minecraft.core.Registry.BLOCK_ENTITY_TYPE_REGISTRY);
        blockEntities.register(new ResourceLocation(modid, "head"), () -> HeadBlockEntity.HEAD_TYPE);
        Registry<EntityType<?>> entityTypes = IMDLib.REGISTRIES.get().get(net.minecraft.core.Registry.ENTITY_TYPE_REGISTRY);
        //entity types
        for (EntityTypeContainer<?> container : ENTITIES.values()) {
            entityTypes.register(new ResourceLocation(modid, container.getEntityName()), container::getEntityType);
            if (!useAttributeEvents) {
                //container.registerAttributes();
            }
        }
        platformInit(useAttributeEvents, this);
    }

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

    public <T extends Mob & IContainable, I extends Item & IContainerItem<T>> EntityTypeContainerContainable<T, I> addContainable(Class<T> entityClass, EntityType.EntityFactory<T> factory, String name, String itemName, String emptyItemName, Supplier<AttributeSupplier.Builder> attributeMap, Function<EntityTypeContainerContainable.Builder<T, I>, EntityTypeContainerContainable.Builder<T, I>> transformer) {
        return add(transformer.apply(EntityTypeContainerContainable.Builder.create(entityClass, factory, name, itemName, emptyItemName, attributeMap, modid)));
    }

    public <T extends Mob & IContainable> EntityTypeContainerContainable<T, ItemModFishBucket<T>> addContainableB(Class<T> entityClass, EntityType.EntityFactory<T> factory, String name, String itemName, String emptyItemName, Supplier<AttributeSupplier.Builder> attributeMap, Function<EntityTypeContainerContainable.Builder<T, ItemModFishBucket<T>>, EntityTypeContainerContainable.Builder<T, ItemModFishBucket<T>>> transformer) {
        return add(transformer.apply(EntityTypeContainerContainable.Builder.create(entityClass, factory, name, itemName, emptyItemName, attributeMap, modid)));
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


}
