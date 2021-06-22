package dev.itsmeow.imdlib.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.itsmeow.imdlib.mixin.ModelPartAccessor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public class RenderUtil {

    private static final Map<ModelPart, Cube> cubeList = new WeakHashMap<>();

    public static Vec3 partLocation(ModelPart... parts) {
        float x = 0F;
        float y = 0F;
        float z = 0F;
        for (ModelPart part : parts) {
            x += part.xRot + xOffset(part);
            y += part.yRot + yOffset(part);
            z += part.zRot + zOffset(part);
        }
        return new Vec3(x, y, z);
    }

    public static void partTranslateRotate(PoseStack stack, ModelPart... parts) {
        for (ModelPart part : parts) {
            RenderUtil.pointTranslate(stack, part);
            stack.mulPose(Vector3f.XP.rotation(part.xRot));
            stack.mulPose(Vector3f.YP.rotation(part.yRot));
            stack.mulPose(Vector3f.ZP.rotation(part.zRot));
        }
    }

    public static void partTranslateOffsetRotate(PoseStack stack, ModelPart... parts) {
        for (ModelPart part : parts) {
            RenderUtil.offsetTranslate(stack, part);
            RenderUtil.pointTranslate(stack, part);
            stack.mulPose(Vector3f.XP.rotation(part.xRot));
            stack.mulPose(Vector3f.YP.rotation(part.yRot));
            stack.mulPose(Vector3f.ZP.rotation(part.zRot));
        }
    }

    public static void partScaleTranslate(PoseStack stack, ModelPart part, float scale) {
        RenderUtil.offsetTranslate(stack, part);
        RenderUtil.pointTranslate(stack, part);
        RenderUtil.scale(stack, scale);
        RenderUtil.negativeOffsetTranslate(stack, part);
        RenderUtil.negativePointTranslate(stack, part);
    }

    public static void partScaleTranslate(PoseStack stack, ModelPart part, double scale) {
        partScaleTranslate(stack, part, (float) scale);
    }

    public static void partScaleTranslate(PoseStack stack, ModelPart part, float scaleX, float scaleY, float scaleZ) {
        RenderUtil.offsetTranslate(stack, part);
        RenderUtil.pointTranslate(stack, part);
        stack.scale(scaleX, scaleY, scaleZ);
        RenderUtil.negativeOffsetTranslate(stack, part);
        RenderUtil.negativePointTranslate(stack, part);
    }

    public static void partScaleTranslate(PoseStack stack, ModelPart part, double scaleX, double scaleY, double scaleZ) {
        partScaleTranslate(stack, part, (float) scaleX, (float) scaleY, (float) scaleZ);
    }

    public static void offsetTranslate(PoseStack stack, ModelPart part) {
        stack.translate(xOffset(part), yOffset(part), zOffset(part));
    }

    public static void negativeOffsetTranslate(PoseStack stack, ModelPart part) {
        stack.translate(-xOffset(part), -yOffset(part), -zOffset(part));
    }

    public static void pointTranslate(PoseStack stack, ModelPart part) {
        stack.translate(part.x / 16, part.y / 16, part.z / 16);
    }

    public static void negativePointTranslate(PoseStack stack, ModelPart part) {
        stack.translate(-part.x / 16, -part.y / 16, -part.z / 16);
    }

    public static void scale(PoseStack stack, float scale) {
        stack.scale(scale, scale, scale);
    }

    public static void scale(PoseStack stack, double scale) {
        scale(stack, (float) scale);
    }

    public static float xOffset(ModelPart part) {
        return getPartBox(part).minX / 16;
    }

    public static float yOffset(ModelPart part) {
        return getPartBox(part).minY / 16;
    }

    public static float zOffset(ModelPart part) {
        return getPartBox(part).minZ / 16;
    }

    @SuppressWarnings("unchecked")
    private static Cube getPartBox(ModelPart part) {
        Cube res = cubeList.get(part);
        if (res == null) {
            res = ((ModelPartAccessor) part).cubes().get(0);
            cubeList.put(part, res);
        }
        return res;
    }

}
