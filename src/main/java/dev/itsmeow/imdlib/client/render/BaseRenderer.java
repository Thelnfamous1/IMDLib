package dev.itsmeow.imdlib.client.render;

import java.util.ArrayList;
import java.util.function.Function;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.MobEntity;

public abstract class BaseRenderer<T extends MobEntity, A extends EntityModel<T>> extends MobRenderer<T, A> {

    public BaseRenderer(EntityRendererManager renderManagerIn, A entityModelIn, float shadowSizeIn) {
        super(renderManagerIn, entityModelIn, shadowSizeIn);
    }

    public BaseRenderer<T, A> layer(Function<BaseRenderer<T, A>, LayerRenderer<T, A>> layer) {
        this.addLayer(layer.apply(this));
        return this;
    }

    public BaseRenderer<T, A> layers(ArrayList<Function<BaseRenderer<T, A>, LayerRenderer<T, A>>> layers) {
        layers.forEach(layer -> this.addLayer(layer.apply(this)));
        return this;
    }

}
