package dev.itsmeow.imdlib.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.itsmeow.imdlib.blockentity.HeadBlockEntity;
import dev.itsmeow.imdlib.config.EntityRegistrarConfigHandler;
import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.item.IContainerItem;
import dev.itsmeow.imdlib.item.ItemModFishBucket;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import dev.itsmeow.imdlib.mixin.EntityTypeAccessor;
import dev.itsmeow.imdlib.util.HeadType;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import me.shedaniel.architectury.registry.entity.EntityAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityRegistrarHandler {

    public final String modid;
    public final LinkedHashMap<String, EntityTypeContainer<? extends Mob>> ENTITIES = new LinkedHashMap<>();
    protected final LazyLoadedValue<Registries> registry;

    public EntityRegistrarHandler(String modid) {
        this.modid = modid;
        this.registry = new LazyLoadedValue<>(() -> Registries.get(modid));
    }

    public Registries getRegistries() {
        return registry.get();
    }

    @ExpectPlatform
    public static EntityRegistrarConfigHandler getConfigHandlerFor(EntityRegistrarHandler handler) {
        throw new RuntimeException();
    }

    public void init() {
        BiomeTypes.init(this.getRegistries());
        for (HeadType type : HeadType.values()) {
            type.register(this.getRegistries());
        }

        // Containers & eggs
        Registry<Item> items = this.getRegistries().get(net.minecraft.core.Registry.ITEM_REGISTRY);
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
                container.egg = items.registerSupplied(new ResourceLocation(container.getModId(), container.getEntityName().toLowerCase() + "_spawn_egg"), () -> new ModSpawnEggItem(container));
            }
        }
        Registry<BlockEntityType<?>> blockEntities = this.getRegistries().get(net.minecraft.core.Registry.BLOCK_ENTITY_TYPE_REGISTRY);
        blockEntities.register(new ResourceLocation(modid, "head"), () -> HeadBlockEntity.HEAD_TYPE);
        Registry<EntityType<?>> entityTypes = this.getRegistries().get(net.minecraft.core.Registry.ENTITY_TYPE_REGISTRY);
        //entity types
        for (EntityTypeContainer<?> container : ENTITIES.values()) {
            ResourceLocation rl = new ResourceLocation(modid, container.getEntityName());
            entityTypes.register(rl, container::getEntityType);
            EntityAttributes.register(container::getEntityType, container.getAttributeBuilder());
        }
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
        ((EntityTypeAccessor) type).setSerialize(true);
        return type;
    }


}
