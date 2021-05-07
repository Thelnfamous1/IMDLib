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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class HeadType {

    protected static final Set<HeadType> HEADS = new HashSet<>();
    protected static final Map<String, HeadType> HEADS_MAP = new HashMap<>();
    protected static final Map<Block, HeadType> HEADS_BLOCK_MAP = new HashMap<>();
    private final String name;
    private final PlacementType placement;
    private final Map<IVariant, Pair<GenericSkullBlock, ItemBlockHeadType>> heads = new HashMap<>();
    private final Set<ItemBlockHeadType> items = new HashSet<>();
    private final Set<GenericSkullBlock> blocks = new HashSet<>();
    private final EntityTypeContainer<? extends LivingEntity> container;
    private final Map<Block, IVariant> reverseVariantMap = new HashMap<>();
    private final String modid;
    @Environment(EnvType.CLIENT)
    public Supplier<Supplier<EntityModel<? extends Entity>>> modelSupplier;
    private float yOffset = 0F;
    private IVariant singletonVariant;

    public HeadType(String modid, CreativeModeTab group, String name, PlacementType placement, float yOffset, HeadIDMapping mapping, @Nullable Function<IVariant, String> variantMapper, @Nullable IVariant singletonVariant, @Nullable String singletonID, EntityTypeContainer<? extends LivingEntity> container) {
        this.name = name;
        this.modid = modid;
        this.placement = placement;
        this.yOffset = yOffset;
        this.container = container;
        if (!container.hasVariants() && mapping != HeadIDMapping.SINGLETON) {
            throw new RuntimeException("Tried to create non-singleton head type with a variantless entity!");
        }
        switch (mapping) {
            case NAMES:
                for (IVariant variant : container.getVariants()) {
                    if (variant.hasHead()) {
                        GenericSkullBlock block = new GenericSkullBlock(this, variant.getName());
                        ItemBlockHeadType item = new ItemBlockHeadType(block, this, variant.getName(), variant, group);
                        heads.put(variant, Pair.of(block, item));
                        blocks.add(block);
                        items.add(item);
                        reverseVariantMap.put(block, variant);
                        HEADS_BLOCK_MAP.put(block, this);
                    }
                }
                break;
            case NUMBERS:
                for (IVariant variant : container.getVariants()) {
                    if (variant.hasHead()) {
                        int index = container.getVariants().indexOf(variant) + 1;
                        GenericSkullBlock block = new GenericSkullBlock(this, String.valueOf(index));
                        ItemBlockHeadType item = new ItemBlockHeadType(block, this, String.valueOf(index), variant, group);
                        heads.put(variant, Pair.of(block, item));
                        blocks.add(block);
                        items.add(item);
                        reverseVariantMap.put(block, variant);
                        HEADS_BLOCK_MAP.put(block, this);
                    }
                }
                break;
            case CUSTOM:
                for (IVariant variant : container.getVariants()) {
                    if (variant.hasHead()) {
                        String id = variantMapper.apply(variant);
                        GenericSkullBlock block = new GenericSkullBlock(this, id);
                        ItemBlockHeadType item = new ItemBlockHeadType(block, this, id, variant, group);
                        heads.put(variant, Pair.of(block, item));
                        blocks.add(block);
                        items.add(item);
                        reverseVariantMap.put(block, variant);
                        HEADS_BLOCK_MAP.put(block, this);
                    }
                }
                break;
            case SINGLETON:
                GenericSkullBlock block = new GenericSkullBlock(this, singletonID);
                ItemBlockHeadType item = new ItemBlockHeadType(block, this, singletonID, singletonVariant, group);
                heads.put(singletonVariant, Pair.of(block, item));
                blocks.add(block);
                items.add(item);
                reverseVariantMap.put(block, singletonVariant);
                HEADS_BLOCK_MAP.put(block, this);
                this.singletonVariant = singletonVariant;
                break;
            default:
                break;
        }
        HEADS.add(this);
        HEADS_MAP.put(name, this);
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
        ArrayList<GenericSkullBlock> blocks = new ArrayList<>();
        for (HeadType type : HeadType.values()) {
            blocks.addAll(type.getBlockSet());
        }
        GenericSkullBlock[] list = new GenericSkullBlock[blocks.size()];
        list = blocks.toArray(list);
        return list;
    }

    public float getYOffset() {
        return this.yOffset;
    }

    public IVariant getVariant(Block block) {
        return reverseVariantMap.get(block);
    }

    public Pair<GenericSkullBlock, ItemBlockHeadType> getPair(IVariant variant) {
        return heads.get(variant);
    }

    public GenericSkullBlock getBlock(IVariant variant) {
        if (getPair(variant) == null)
            return null;
        return getPair(variant).getLeft();
    }

    public ItemBlockHeadType getItem(IVariant variant) {
        if (getPair(variant) == null)
            return null;
        return getPair(variant).getRight();
    }

    public GenericSkullBlock getBlock() {
        if (getPair(singletonVariant) == null)
            return null;
        return getPair(singletonVariant).getLeft();
    }

    public ItemBlockHeadType getItem() {
        if (getPair(singletonVariant) == null)
            return null;
        return getPair(singletonVariant).getRight();
    }

    public Set<ItemBlockHeadType> getItemSet() {
        return items;
    }

    public Set<GenericSkullBlock> getBlockSet() {
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
                ItemStack stack = new ItemStack(this.getItem(variant));
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