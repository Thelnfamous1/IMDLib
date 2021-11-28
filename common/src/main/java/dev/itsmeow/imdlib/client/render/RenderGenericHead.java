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

        this.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, dir, rotation, te.getTexture(), model);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, int packedOverlayIn, @Nullable Direction facing, float skullRotation, ResourceLocation texture, EntityModel<? extends Entity> model) {
        matrixStackIn.pushPose();
        if(model instanceof HeadModel) {
            matrixStackIn.translate(0F, ((HeadModel) model).globalOffsetY() * 0.0625F, 0F);
        }
        if (facing == Direction.UP) {
            matrixStackIn.translate(0.5D, 0.0D, 0.5D);
        } else {
            if(model instanceof HeadModel) {
                HeadModel m = (HeadModel) model;
                matrixStackIn.translate(0.5F - (float) facing.getStepX() * 0.25F - ((float) facing.getStepX() * m.wallOffsetX() * 0.0625F), 0.25D + m.wallOffsetY() * 0.0625F, 0.5F - (float) facing.getStepZ() * 0.25F - ((float) facing.getStepZ() * m.wallOffsetZ() * 0.0625F));
            } else {
                matrixStackIn.translate(0.5F - (float) facing.getStepX() * 0.25F, 0.25D, 0.5F - (float) facing.getStepZ() * 0.25F);
            }
        }
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
