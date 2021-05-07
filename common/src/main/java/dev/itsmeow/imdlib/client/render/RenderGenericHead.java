package dev.itsmeow.imdlib.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.itsmeow.imdlib.block.GenericSkullBlock;
import dev.itsmeow.imdlib.blockentity.HeadBlockEntity;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RenderGenericHead extends BlockEntityRenderer<HeadBlockEntity> {

    public static HashMap<HeadType, EntityModel<?>> modelMap = new HashMap<>();

    public RenderGenericHead(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private static void translateHead(PoseStack matrixStackIn, Direction face, float yOffset) {
        if (face == null) {
            matrixStackIn.translate(0.5F, 0.25F + yOffset + 0.3F, 1.0F);
            return;
        }
        switch (face) {
            case NORTH:
                matrixStackIn.translate(0.5F, 0.25F + yOffset + 0.3F, 1.0F);
                break;
            case EAST:
                matrixStackIn.translate(0F, 0.25F + yOffset + 0.3F, 0.5F);
                break;
            case SOUTH:
                matrixStackIn.translate(0.5F, 0.25F + yOffset + 0.3F, 0F);
                break;
            case WEST:
                matrixStackIn.translate(1F, 0.25F + yOffset + 0.3F, 0.5F);
                break;
            case UP:
                matrixStackIn.translate(0.5F, 0.18F + yOffset, 0.5F);
                break;
            default:
                matrixStackIn.translate(0F, 0.25F + yOffset, 0F);
                break;
        }
    }

    @Override
    public void render(HeadBlockEntity te, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState state = te.getBlockState();
        if (!(state.getBlock() instanceof GenericSkullBlock)) {
            return;
        }
        Direction dir = te.getDirection();
        dir = dir == null ? Direction.NORTH : dir;
        float rotation = -dir.toYRot();
        rotation = (dir == Direction.NORTH || dir == Direction.SOUTH) ? dir.getOpposite().toYRot() : rotation;
        rotation = (dir == Direction.UP) ? te.getTopRotation() : rotation;

        EntityModel<? extends Entity> model = modelMap.get(te.getHeadType());
        if (model == null) {
            EntityModel<? extends Entity> newModel = te.getNewModel();
            modelMap.put(te.getHeadType(), newModel);
            model = newModel;
        }

        this.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, dir, rotation, te.getTexture(), model, te.getOffset());
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, int packedOverlayIn, @Nullable Direction facing, float skullRotation, ResourceLocation texture, EntityModel<? extends Entity> model, float yOffset) {
        matrixStackIn.pushPose();
        translateHead(matrixStackIn, facing, 1.5F + yOffset);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        float rotX = 0F;
        if (facing != null) {
            rotX = facing == Direction.UP ? -90F : 0.0F;
        }
        model.setupAnim(null, skullRotation, rotX, 0.0F, 0.0F, 0.0F);
        model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(texture)), packedLightIn, packedOverlayIn, 1F, 1F, 1F, 1F);
        matrixStackIn.popPose();

    }

}
