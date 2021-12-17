package dev.itsmeow.imdlib.blockentity;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.itsmeow.imdlib.block.AnimalSkullBlock;
import dev.itsmeow.imdlib.client.render.RenderGenericHead;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.util.HeadType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HeadBlockEntity extends BlockEntity {

    public static final BlockEntityType<HeadBlockEntity> HEAD_TYPE = BlockEntityType.Builder.of(HeadBlockEntity::new, HeadType.getAllBlocks()).build(null);
    private HeadType cachedType = null;
    private IVariant cachedVariant = null;

    public HeadBlockEntity(HeadType type, BlockPos pos, BlockState state) {
        super(HEAD_TYPE, pos, state);
        this.cachedType = type;
    }

    public HeadBlockEntity(BlockPos pos, BlockState state) {
        super(HEAD_TYPE, pos, state);
    }

    @Environment(EnvType.CLIENT)
    public static void registerTypeRender() {
        BlockEntityRendererRegistry.register(HEAD_TYPE, RenderGenericHead::new);
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
