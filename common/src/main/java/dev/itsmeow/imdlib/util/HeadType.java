package dev.itsmeow.imdlib.util;

import dev.itsmeow.imdlib.block.GenericSkullBlock;
import dev.itsmeow.imdlib.blockentity.HeadBlockEntity;
import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.interfaces.IVariantTypes;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.entity.util.variant.EntityVariant;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.item.ItemBlockHeadType;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HeadType {

    protected static final Set<HeadType> HEADS = new HashSet<>();
    protected static final Map<String, HeadType> HEADS_MAP = new HashMap<>();
    protected static final Map<ResourceLocation, HeadType> HEADS_BLOCK_MAP = new HashMap<>();
    private final String name;
    private final PlacementType placement;
    private final EntityTypeContainer<? extends LivingEntity> container;
    private final String modid;
    @Environment(EnvType.CLIENT)
    public Supplier<Supplier<EntityModel<? extends Entity>>> modelSupplier;
    private float yOffset = 0F;
    private IVariant singletonVariant;
    private final Map<IVariant, Pair<RegistrySupplier<GenericSkullBlock>, RegistrySupplier<ItemBlockHeadType>>> heads = new HashMap<>();
    private final Set<RegistrySupplier<ItemBlockHeadType>> items = new HashSet<>();
    private final Set<RegistrySupplier<GenericSkullBlock>> blocks = new HashSet<>();
    private final Map<ResourceLocation, IVariant> reverseVariantMap = new HashMap<>();
    private final Consumer<Registries> registerVariants;

    public HeadType(String modid, CreativeModeTab group, String name, PlacementType placement, float yOffset, HeadIDMapping mapping, @Nullable Function<IVariant, String> variantMapper, @Nullable IVariant singletonVariant, @Nullable String singletonID, EntityTypeContainer<? extends LivingEntity> container) {
        this.name = name;
        this.modid = modid;
        this.placement = placement;
        this.yOffset = yOffset;
        this.container = container;
        if (!container.hasVariants() && mapping != HeadIDMapping.SINGLETON) {
            throw new RuntimeException("Tried to create non-singleton head type with a variantless entity!");
        }
        this.registerVariants = (registries) -> {
            switch (mapping) {
                case NAMES:
                    for (IVariant variant : container.getVariants()) {
                        if (variant.hasHead()) {
                            setupVariant(registries, variant, group, variant.getName());
                        }
                    }
                    break;
                case NUMBERS:
                    for (IVariant variant : container.getVariants()) {
                        if (variant.hasHead()) {
                            setupVariant(registries, variant, group, String.valueOf(container.getVariants().indexOf(variant) + 1));
                        }
                    }
                    break;
                case CUSTOM:
                    for (IVariant variant : container.getVariants()) {
                        if (variant.hasHead()) {
                            setupVariant(registries, variant, group, variantMapper.apply(variant));
                        }
                    }
                    break;
                case SINGLETON:
                    setupVariant(registries, singletonVariant, group, singletonID);
                    this.singletonVariant = singletonVariant;
                    break;
                default:
                    break;
            }
        };
        HEADS.add(this);
        HEADS_MAP.put(name, this);
    }

    public void register(Registries registries) {
        registerVariants.accept(registries);
    }

    protected void setupVariant(Registries registries, IVariant variant, CreativeModeTab group, String id) {
        ResourceLocation rl = new ResourceLocation(this.getMod(), this.getName() + "_" + id);
        RegistrySupplier<GenericSkullBlock> block = registries.get(Registry.BLOCK_REGISTRY).registerSupplied(rl, () -> new GenericSkullBlock(this, id));
        RegistrySupplier<ItemBlockHeadType> item = registries.get(Registry.ITEM_REGISTRY).registerSupplied(rl, () -> new ItemBlockHeadType(block.get(), this, id, variant, group));
        heads.put(variant, Pair.of(block, item));
        blocks.add(block);
        items.add(item);
        reverseVariantMap.put(rl, variant);
        HEADS_BLOCK_MAP.put(rl, this);
    }

    public static Set<HeadType> values() {
        return HEADS;
    }

    public static HeadType valueOf(String name) {
        return HEADS_MAP.get(name);
    }

    public static HeadType valueOf(Block block) {
        return HEADS_BLOCK_MAP.get(block);
    }

    public static GenericSkullBlock[] getAllBlocks() {
        return HeadType.values().stream().map(type -> type.getBlockObjects()).flatMap(Collection::stream).map(Supplier::get).collect(Collectors.toList()).toArray(new GenericSkullBlock[0]);
    }

    public float getYOffset() {
        return this.yOffset;
    }

    public IVariant getVariantForBlock(Block block) {
        return reverseVariantMap.get(Registry.BLOCK.getKey(block));
    }

    public Pair<RegistrySupplier<GenericSkullBlock>, RegistrySupplier<ItemBlockHeadType>> getPairForVariant(IVariant variant) {
        return heads.get(variant);
    }

    public RegistrySupplier<GenericSkullBlock> getBlockForVariant(IVariant variant) {
        if (getPairForVariant(variant) == null)
            return null;
        return getPairForVariant(variant).getLeft();
    }

    public RegistrySupplier<ItemBlockHeadType> getItemForVariant(IVariant variant) {
        if (getPairForVariant(variant) == null)
            return null;
        return getPairForVariant(variant).getRight();
    }

    public RegistrySupplier<GenericSkullBlock> getSingletonBlock() {
        if (getPairForVariant(singletonVariant) == null)
            return null;
        return getPairForVariant(singletonVariant).getLeft();
    }

    public RegistrySupplier<ItemBlockHeadType> getSingletonItem() {
        if (getPairForVariant(singletonVariant) == null)
            return null;
        return getPairForVariant(singletonVariant).getRight();
    }

    public Set<RegistrySupplier<ItemBlockHeadType>> getItemObjects() {
        return items;
    }

    public Set<RegistrySupplier<GenericSkullBlock>> getBlockObjects() {
        return blocks;
    }

    public HeadBlockEntity createTE() {
        return new HeadBlockEntity(this);
    }

    public String getName() {
        return name;
    }

    public PlacementType getPlacementType() {
        return placement;
    }

    @Environment(EnvType.CLIENT)
    public Supplier<Supplier<EntityModel<? extends Entity>>> getModelSupplier() {
        return modelSupplier;
    }

    public EntityTypeContainer<? extends LivingEntity> getContainer() {
        return this.container;
    }

    public IVariant getSingletonVariant() {
        return this.singletonVariant;
    }

    public void drop(Mob entity, int chance) {
        drop(entity, chance, getHeadID(entity).orElse(null));
    }

    public void drop(Mob entity, int chance, IVariant variant) {
        if (variant != null && !entity.level.isClientSide && !entity.isBaby()) {
            if (entity.getRandom().nextInt(chance) == 0) {
                ItemStack stack = new ItemStack(this.getItemForVariant(variant).get());
                entity.spawnAtLocation(stack, 0.5F);
            }
        }
    }

    private Optional<IVariant> getHeadID(Mob entity) {
        if (entity instanceof IVariantTypes<?> && this.container.hasVariants()) {
            IVariantTypes<?> ent = (IVariantTypes<?>) entity;
            return ent.getVariant();
        } else {
            return Optional.of(this.singletonVariant);
        }
    }

    public String getMod() {
        return this.modid;
    }

    public enum PlacementType {
        FLOOR_AND_WALL,
        WALL_ONLY
    }

    public enum HeadIDMapping {
        NAMES,
        NUMBERS,
        CUSTOM,
        SINGLETON
    }

    public static class Builder<T extends Mob, C extends EntityTypeContainer<T>, B extends IEntityBuilder<T, C, B>> {

        private final String name;
        private final B initial;
        private PlacementType placement;
        private float yOffset;
        @Environment(EnvType.CLIENT)
        private Supplier<Supplier<EntityModel<?>>> modelSupplier;
        private HeadIDMapping idMapping;
        private Function<IVariant, String> customMapper;
        private IVariant singletonVariant;
        private String singletonID;
        private CreativeModeTab group;

        public Builder(B initial, String name) {
            this.initial = initial;
            this.name = name;
            this.placement = PlacementType.WALL_ONLY;
            this.yOffset = 0.0F;
            this.idMapping = null;
        }

        public Builder<T, C, B> itemGroup(CreativeModeTab group) {
            this.group = group;
            return this;
        }

        public Builder<T, C, B> mapToNames() {
            this.idMapping = HeadIDMapping.NAMES;
            return this;
        }

        public Builder<T, C, B> mapToNumbers() {
            this.idMapping = HeadIDMapping.NUMBERS;
            return this;
        }

        public Builder<T, C, B> mapToCustom(Function<IVariant, String> customMapper) {
            this.idMapping = HeadIDMapping.CUSTOM;
            this.customMapper = customMapper;
            return this;
        }

        public Builder<T, C, B> singleton(String id, String texture) {
            this.idMapping = HeadIDMapping.SINGLETON;
            this.singletonID = id;
            this.singletonVariant = new EntityVariant(initial.getMod(), id, texture);
            return this;
        }

        public Builder<T, C, B> allowFloor() {
            this.placement = PlacementType.FLOOR_AND_WALL;
            return this;
        }

        public Builder<T, C, B> setModel(Supplier<Supplier<EntityModel<? extends Entity>>> modelSupplier) {
            if (Platform.getEnv() == EnvType.CLIENT) {
                this.modelSupplier = modelSupplier;
            }
            return this;
        }

        public Builder<T, C, B> offset(float yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        public B done() {
            initial.setHeadBuild(this::build);
            return initial;
        }

        public HeadType build(C container) {
            if (idMapping == null) {
                throw new RuntimeException("No ID mapping set for head builder " + name);
            }
            HeadType type = new HeadType(initial.getMod(), group, name, placement, yOffset, idMapping, customMapper, singletonVariant, singletonID, container);
            if (Platform.getEnv() == EnvType.CLIENT) {
                type.modelSupplier = modelSupplier;
            }
            return type;
        }

    }

}