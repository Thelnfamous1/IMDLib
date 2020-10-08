package dev.itsmeow.imdlib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import dev.itsmeow.imdlib.block.BlockGenericSkull;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.util.EntityVariant;
import dev.itsmeow.imdlib.entity.util.IVariant;
import dev.itsmeow.imdlib.entity.util.IVariantTypes;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.item.ItemBlockHeadType;
import dev.itsmeow.imdlib.tileentity.TileEntityHead;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class HeadType {

    protected static final Set<HeadType> HEADS = new HashSet<HeadType>();
    protected static final Map<String, HeadType> HEADS_MAP = new HashMap<String, HeadType>();
    protected static final Map<Block, HeadType> HEADS_BLOCK_MAP = new HashMap<Block, HeadType>();

    public static Set<HeadType> values() {
        return HEADS;
    }

    public static HeadType valueOf(String name) {
        return HEADS_MAP.get(name);
    }

    public static HeadType valueOf(Block block) {
        return HEADS_BLOCK_MAP.get(block);
    }

    private final String name;
    private final PlacementType placement;
    private final Map<IVariant, Pair<BlockGenericSkull, ItemBlockHeadType>> heads = new HashMap<IVariant, Pair<BlockGenericSkull, ItemBlockHeadType>>();
    private final Set<ItemBlockHeadType> items = new HashSet<ItemBlockHeadType>();
    private final Set<BlockGenericSkull> blocks = new HashSet<BlockGenericSkull>();
    @OnlyIn(Dist.CLIENT)
    public Supplier<Supplier<EntityModel<? extends Entity>>> modelSupplier;
    private final EntityTypeContainer<? extends LivingEntity> container;
    private float yOffset = 0F;
    private IVariant singletonVariant;
    private final Map<Block, IVariant> reverseVariantMap = new HashMap<Block, IVariant>();
    private final String modid;

    public HeadType(String modid, ItemGroup group, String name, PlacementType placement, float yOffset, HeadIDMapping mapping, @Nullable Function<IVariant, String> variantMapper, @Nullable IVariant singletonVariant, @Nullable String singletonID, EntityTypeContainer<? extends LivingEntity> container) {
        this.name = name;
        this.modid = modid;
        this.placement = placement;
        this.yOffset = yOffset;
        this.container = container;
        if(!container.hasVariants() && mapping != HeadIDMapping.SINGLETON) {
            throw new RuntimeException("Tried to create non-singleton head type with a variantless entity!");
        }
        switch(mapping) {
        case NAMES:
            for(IVariant variant : container.getVariants()) {
                if(variant.hasHead()) {
                    BlockGenericSkull block = new BlockGenericSkull(this, variant.getName());
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
            for(IVariant variant : container.getVariants()) {
                if(variant.hasHead()) {
                    int index = container.getVariants().indexOf(variant) + 1;
                    BlockGenericSkull block = new BlockGenericSkull(this, String.valueOf(index));
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
            for(IVariant variant : container.getVariants()) {
                if(variant.hasHead()) {
                    String id = variantMapper.apply(variant);
                    BlockGenericSkull block = new BlockGenericSkull(this, id);
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
            BlockGenericSkull block = new BlockGenericSkull(this, singletonID);
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

    public float getYOffset() {
        return this.yOffset;
    }

    public IVariant getVariant(Block block) {
        return reverseVariantMap.get(block);
    }

    public Pair<BlockGenericSkull, ItemBlockHeadType> getPair(IVariant variant) {
        return heads.get(variant);
    }

    public BlockGenericSkull getBlock(IVariant variant) {
        if(getPair(variant) == null)
            return null;
        return getPair(variant).getLeft();
    }

    public ItemBlockHeadType getItem(IVariant variant) {
        if(getPair(variant) == null)
            return null;
        return getPair(variant).getRight();
    }

    public BlockGenericSkull getBlock() {
        if(getPair(singletonVariant) == null)
            return null;
        return getPair(singletonVariant).getLeft();
    }

    public ItemBlockHeadType getItem() {
        if(getPair(singletonVariant) == null)
            return null;
        return getPair(singletonVariant).getRight();
    }

    public Set<ItemBlockHeadType> getItemSet() {
        return items;
    }

    public Set<BlockGenericSkull> getBlockSet() {
        return blocks;
    }

    public TileEntityHead createTE() {
        return new TileEntityHead(this);
    }

    public String getName() {
        return name;
    }

    public PlacementType getPlacementType() {
        return placement;
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<Supplier<EntityModel<? extends Entity>>> getModelSupplier() {
        return modelSupplier;
    }

    public static BlockGenericSkull[] getAllBlocks() {
        ArrayList<BlockGenericSkull> blocks = new ArrayList<>();
        for(HeadType type : HeadType.values()) {
            blocks.addAll(type.getBlockSet());
        }
        BlockGenericSkull[] list = new BlockGenericSkull[blocks.size()];
        list = blocks.toArray(list);
        return list;
    }

    public EntityTypeContainer<? extends LivingEntity> getContainer() {
        return this.container;
    }

    public IVariant getSingletonVariant() {
        return this.singletonVariant;
    }

    public void drop(MobEntity entity, int chance) {
        drop(entity, chance, getHeadID(entity));
    }

    public void drop(MobEntity entity, int chance, Optional<IVariant> variant) {
        if(variant.isPresent() && !entity.world.isRemote && !entity.isChild()) {
            if(entity.getRNG().nextInt(chance) == 0) {
                ItemStack stack = new ItemStack(this.getItem(variant.get()));
                entity.entityDropItem(stack, 0.5F);
            }
        }
    }

    private Optional<IVariant> getHeadID(MobEntity entity) {
        if(entity instanceof IVariantTypes<?> && this.container.hasVariants()) {
            IVariantTypes<?> ent = (IVariantTypes<?>) entity;
            return ent.getVariant();
        } else {
            return Optional.of(this.singletonVariant);
        }
    }

    public static enum PlacementType {
        FLOOR_AND_WALL,
        WALL_ONLY;
    }

    public static enum HeadIDMapping {
        NAMES,
        NUMBERS,
        CUSTOM,
        SINGLETON;
    }

    public static class Builder<T extends MobEntity, C extends EntityTypeContainer<T>, B extends IEntityBuilder<T, C, B>> {

        private final String name;
        private PlacementType placement;
        private float yOffset;
        @OnlyIn(Dist.CLIENT)
        private Supplier<Supplier<EntityModel<?>>> modelSupplier;
        private HeadIDMapping idMapping;
        private Function<IVariant, String> customMapper;
        private IVariant singletonVariant;
        private String singletonID;
        private final B initial;
        private ItemGroup group;

        public Builder(B initial, String name) {
            this.initial = initial;
            this.name = name;
            this.placement = PlacementType.WALL_ONLY;
            this.yOffset = 0.0F;
            this.idMapping = null;
        }

        public Builder<T, C, B> itemGroup(ItemGroup group) {
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
            if(FMLEnvironment.dist == Dist.CLIENT) {
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
            if(idMapping == null) {
                throw new RuntimeException("No ID mapping set for head builder " + name);
            }
            HeadType type = new HeadType(initial.getMod(), group, name, placement, yOffset, idMapping, customMapper, singletonVariant, singletonID, container);
            if(FMLEnvironment.dist == Dist.CLIENT) {
                type.modelSupplier = modelSupplier;
            }
            return type;
        }

    }

    public String getMod() {
        return this.modid;
    }

}