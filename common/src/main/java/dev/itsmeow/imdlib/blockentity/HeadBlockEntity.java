package dev.itsmeow.imdlib.blockentity;

import dev.itsmeow.imdlib.block.AnimalSkullBlock;
import dev.itsmeow.imdlib.client.render.RenderGenericHead;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.util.HeadType;
import me.shedaniel.architectury.registry.BlockEntityRenderers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class HeadBlockEntity extends BlockEntity {

    public static final BlockEntityType<HeadBlockEntity> HEAD_TYPE = BlockEntityType.Builder.of(HeadBlockEntity::new, HeadType.getAllBlocks()).build(null);
    private HeadType cachedType = null;
    private IVariant cachedVariant = null;

    public HeadBlockEntity(HeadType type) {
        super(HEAD_TYPE);
        this.cachedType = type;
    }

    public HeadBlockEntity() {
        super(HEAD_TYPE);
    }

    @Environment(EnvType.CLIENT)
    public static void registerTypeRender() {
        BlockEntityRenderers.registerRenderer(HEAD_TYPE, RenderGenericHead::new);
    }

    @Environment(EnvType.CLIENT)
    public EntityModel<? extends Entity> getNewModel() {
        return this.getHeadType().getModelSupplier().get().get();
    }

    private Block getBlock() {
        return this.getBlockState().getBlock();
    }

    public HeadType getHeadType() {
        if (cachedType == null) {
            cachedType = HeadType.valueOf(this.getBlock());
        }
        return cachedType;
    }

    public ResourceLocation getTexture() {
        return this.getHeadVariant().getTexture(null);
    }

    public IVariant getHeadVariant() {
        if (cachedVariant == null) {
            cachedVariant = this.getHeadType().getVariantForBlock(this.getBlock());
        }
        return cachedVariant;
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(AnimalSkullBlock.FACING_EXCEPT_DOWN);
    }

    public Direction getTopDirection() {
        return this.getBlockState().getValue(AnimalSkullBlock.TOP_FACING);
    }

    public float getTopRotation() {
        return this.getTopDirection().toYRot();
    }

}
