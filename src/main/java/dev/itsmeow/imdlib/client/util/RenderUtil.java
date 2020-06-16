package dev.itsmeow.imdlib.client.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

import com.mojang.blaze3d.matrix.MatrixStack;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ModelRenderer.ModelBox;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class RenderUtil {

    private static final Map<ModelRenderer, ModelBox> cubeList = new WeakHashMap<ModelRenderer, ModelBox>();
    private static final Field cubeListField = ObfuscationReflectionHelper.findField(ModelRenderer.class, "field_78804_l");

    public static Vec3d partLocation(ModelRenderer... parts) {
        float x = 0F;
        float y = 0F;
        float z = 0F;
        for(ModelRenderer part : parts) {
            x += part.rotateAngleX + xOffset(part);
            y += part.rotateAngleY + yOffset(part);
            z += part.rotateAngleZ + zOffset(part);
        }
        return new Vec3d(x, y, z);
    }

    public static void partTranslateRotate(MatrixStack stack, ModelRenderer... parts) {
        for(ModelRenderer part : parts) {
            RenderUtil.offsetTranslate(stack, part);
            RenderUtil.pointTranslate(stack, part);
            stack.rotate(Vector3f.XP.rotation(part.rotateAngleX));
            stack.rotate(Vector3f.YP.rotation(part.rotateAngleY));
            stack.rotate(Vector3f.ZP.rotation(part.rotateAngleZ));
        }
    }

    public static void partScaleTranslate(MatrixStack stack, ModelRenderer part, float scale) {
        RenderUtil.offsetTranslate(stack, part);
        RenderUtil.pointTranslate(stack, part);
        RenderUtil.scale(stack, scale);
        RenderUtil.negativeOffsetTranslate(stack, part);
        RenderUtil.negativePointTranslate(stack, part);
    }

    public static void partScaleTranslate(MatrixStack stack, ModelRenderer part, double scale) {
        partScaleTranslate(stack, part, (float) scale);
    }

    public static void partScaleTranslate(MatrixStack stack, ModelRenderer part, float scaleX, float scaleY, float scaleZ) {
        RenderUtil.offsetTranslate(stack, part);
        RenderUtil.pointTranslate(stack, part);
        stack.scale(scaleX, scaleY, scaleZ);
        RenderUtil.negativeOffsetTranslate(stack, part);
        RenderUtil.negativePointTranslate(stack, part);
    }

    public static void partScaleTranslate(MatrixStack stack, ModelRenderer part, double scaleX, double scaleY, double scaleZ) {
        partScaleTranslate(stack, part, (float) scaleX, (float) scaleY, (float) scaleZ);
    }

    public static void offsetTranslate(MatrixStack stack, ModelRenderer part) {
        stack.translate(xOffset(part), yOffset(part), zOffset(part));
    }

    public static void negativeOffsetTranslate(MatrixStack stack, ModelRenderer part) {
        stack.translate(-xOffset(part), -yOffset(part), -zOffset(part));
    }

    public static void pointTranslate(MatrixStack stack, ModelRenderer part) {
        stack.translate(part.rotationPointX / 16, part.rotationPointY / 16, part.rotationPointZ / 16);
    }

    public static void negativePointTranslate(MatrixStack stack, ModelRenderer part) {
        stack.translate(-part.rotationPointX / 16, -part.rotationPointY / 16, -part.rotationPointZ / 16);
    }

    public static void scale(MatrixStack stack, float scale) {
        stack.scale(scale, scale, scale);
    }

    public static void scale(MatrixStack stack, double scale) {
        scale(stack, (float) scale);
    }

    public static float xOffset(ModelRenderer part) {
        return getPartBox(part).posX1 / 16;
    }

    public static float yOffset(ModelRenderer part) {
        return getPartBox(part).posY1 / 16;
    }

    public static float zOffset(ModelRenderer part) {
        return getPartBox(part).posZ1 / 16;
    }

    @SuppressWarnings("unchecked")
    private static ModelBox getPartBox(ModelRenderer part) {
        if(cubeList.containsKey(part)) {
            return cubeList.get(part);
        } else {
            try {
                ModelBox box = ((ObjectList<ModelBox>) cubeListField.get(part)).get(0);
                cubeList.put(part, box);
                return box;
            } catch(IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
