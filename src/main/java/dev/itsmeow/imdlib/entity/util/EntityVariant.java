package dev.itsmeow.imdlib.entity.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityVariant implements IVariant {

    protected String name;
    protected ResourceLocation texture;
    protected boolean hasHead = true;

    public EntityVariant(String modid, String nameTexture) {
        this.texture = new ResourceLocation(modid, "textures/entities/" + nameTexture + ".png");
        this.name = nameTexture;
    }

    public EntityVariant(String modid, String nameTexture, boolean hasHead) {
        this.texture = new ResourceLocation(modid, "textures/entities/" + nameTexture + ".png");
        this.hasHead = hasHead;
    }

    public EntityVariant(String modid, String name, String texture) {
        this.texture = new ResourceLocation(modid, "textures/entities/" + texture + ".png");
        this.name = name;
    }

    public EntityVariant(String modid, String name, String texture, boolean hasHead) {
        this.texture = new ResourceLocation(modid, "textures/entities/" + texture + ".png");
        this.name = name;
        this.hasHead = hasHead;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceLocation getTexture(Entity entity) {
        return texture;
    }

    @Override
    public boolean hasHead() {
        return hasHead;
    }

}
