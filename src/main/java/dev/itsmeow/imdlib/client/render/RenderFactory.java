package dev.itsmeow.imdlib.client.render;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderFactory {

    public final String modid;

    public RenderFactory(String modid) {
        this.modid = modid;
    }

    public <T extends MobEntity, M extends EntityModel<T>> ImplRenderer.Builder<T, M> r(float shadowSize) {
        return ImplRenderer.factory(modid, shadowSize);
    }

    public <T extends MobEntity, M extends EntityModel<T>> void addRender(EntityType<T> clazz, float shadowSize, Function<ImplRenderer.Builder<T, M>, ImplRenderer.Builder<T, M>> render) {
        RenderingRegistry.registerEntityRenderingHandler(clazz, render.apply(this.r(shadowSize)).done());
    }

    public static <T extends Entity> void addRender(EntityType<T> clazz, IRenderFactory<T> renderer) {
        RenderingRegistry.registerEntityRenderingHandler(clazz, renderer);
    }

    public static <T extends Entity & IRendersAsItem> IRenderFactory<T> sprite() {
        return mgr -> new SpriteRenderer<>(mgr, Minecraft.getInstance().getItemRenderer());
    }

    public static <T extends Entity> IRenderFactory<T> nothing() {
        return r -> new EntityRenderer<T>(r) {
            @Override
            public ResourceLocation getEntityTexture(T entity) {
                return null;
            }
        };
    }

}
