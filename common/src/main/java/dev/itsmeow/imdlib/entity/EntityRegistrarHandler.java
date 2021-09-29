package dev.itsmeow.imdlib.entity;

import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.blockentity.HeadBlockEntity;
import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.item.IContainerItem;
import dev.itsmeow.imdlib.item.ItemModFishBucket;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import dev.itsmeow.imdlib.mixin.EntityTypeAccessor;
import dev.itsmeow.imdlib.mixin.SpawnSettingsAccessor;
import dev.itsmeow.imdlib.util.HeadType;
import dev.itsmeow.imdlib.util.config.CommonConfigAPI;
import me.shedaniel.architectury.registry.Registry;
import me.shedaniel.architectury.registry.entity.EntityAttributes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityRegistrarHandler {

    public final String modid;
    public final LinkedHashMap<String, EntityTypeContainer<? extends Mob>> ENTITIES = new LinkedHashMap<>();

    public EntityRegistrarHandler(String modid) {
        this.modid = modid;
    }

    public void init() {
        BiomeTypes.init();
        for (HeadType type : HeadType.values()) {
            type.register(IMDLib.getRegistries());
        }

        // Containers & eggs
        Registry<Item> items = IMDLib.getRegistry(net.minecraft.core.Registry.ITEM_REGISTRY);
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
        Registry<BlockEntityType<?>> blockEntities = IMDLib.getRegistry(net.minecraft.core.Registry.BLOCK_ENTITY_TYPE_REGISTRY);
        blockEntities.register(new ResourceLocation(modid, "head"), () -> HeadBlockEntity.HEAD_TYPE);
        Registry<EntityType<?>> entityTypes = IMDLib.getRegistry(net.minecraft.core.Registry.ENTITY_TYPE_REGISTRY);
        //entity types
        for (EntityTypeContainer<?> container : ENTITIES.values()) {
            ResourceLocation rl = new ResourceLocation(modid, container.getEntityName());
            entityTypes.register(rl, container::getEntityType);
            EntityAttributes.register(container::getEntityType, container.getAttributeBuilder());
        }

        CommonConfigAPI.createServerConfig(builder -> {
            builder.push("entities");
            {
                ENTITIES.values().forEach(c -> c.createConfiguration(builder));
            }
            builder.pop();
        }, server -> {
            ENTITIES.values().forEach(e -> e.getConfiguration().load());

            WritableRegistry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(net.minecraft.core.Registry.BIOME_REGISTRY);
            for (ResourceLocation key : biomeRegistry.keySet()) {
                Biome biome = biomeRegistry.get(key);
                MobSpawnSettings spawnInfo = biome.getMobSettings();
                SpawnSettingsAccessor spawnInfoA = (SpawnSettingsAccessor) spawnInfo;
                // make spawns mutable
                spawnInfoA.setSpawners(new HashMap<>(spawnInfoA.getSpawners()));
                // make spawner lists mutable
                for (MobCategory classification : MobCategory.values()) {
                    ArrayList<MobSpawnSettings.SpawnerData> newList = new ArrayList<>();
                    List<MobSpawnSettings.SpawnerData> oldList = spawnInfoA.getSpawners().get(classification);
                    if (oldList != null) {
                        newList.addAll(oldList);
                    }
                    spawnInfoA.getSpawners().put(classification, newList);
                }
                // make costs mutable
                spawnInfoA.setMobSpawnCosts(new HashMap<>(spawnInfoA.getMobSpawnCosts()));
                for (EntityTypeContainer<?> entry : ENTITIES.values()) {
                    EntityTypeContainer<?>.EntityConfiguration config = entry.getConfiguration();
                    if (config.doSpawning.get() && config.spawnWeight.get() > 0 && entry.getBiomeIDs().contains(key.toString())) {
                        entry.registerPlacement();
                        List<MobSpawnSettings.SpawnerData> list = spawnInfoA.getSpawners().get(entry.getDefinition().getSpawnClassification());
                        if (list != null) {
                            list.add(entry.getSpawnEntry());
                        }
                        if (config.spawnCostPer.get() != 0 && config.spawnMaxCost.get() != 0 && entry.getSpawnCostBiomeIDs().contains(key.toString())) {
                            // private constructors be like
                            MobSpawnSettings.MobSpawnCost costs = ((SpawnSettingsAccessor) new MobSpawnSettings.Builder().addMobCharge(entry.getEntityType(), config.spawnCostPer.get(), config.spawnMaxCost.get()).build()).getMobSpawnCosts().get(entry.getEntityType());
                            spawnInfoA.getMobSpawnCosts().put(entry.getEntityType(), costs);
                        }
                    }
                }
            }
        });

        CommonConfigAPI.createConfig(CommonConfigAPI.ConfigType.CLIENT, builder -> {
            builder.push("entities");
            {
                ENTITIES.values().forEach(c -> c.clientCustomConfigurationInit(builder));
            }
            builder.pop();
        }, () -> ENTITIES.values().forEach(EntityTypeContainer::clientCustomConfigurationLoad));
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
