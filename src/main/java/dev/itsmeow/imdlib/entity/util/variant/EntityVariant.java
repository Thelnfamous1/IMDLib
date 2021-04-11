package dev.itsmeow.imdlib.entity.util.variant;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityVariant implements IVariant {

    protected final String name;
    protected final ResourceLocation texture;
    protected final boolean hasHead;

    public EntityVariant(String modid, String nameTexture) {
        this.texture = new ResourceLocation(modid, "textures/entity/" + nameTexture + ".png");
        this.name = nameTexture;
        this.hasHead = true;
    }

    public EntityVariant(String modid, String nameTexture, boolean hasHead) {
        this.texture = new ResourceLocation(modid, "textures/entity/" + nameTexture + ".png");
        this.name = nameTexture;
        this.hasHead = hasHead;
    }

    public EntityVariant(String modid, String name, String texture) {
        this.texture = new ResourceLocation(modid, "textures/entity/" + texture + ".png");
        this.name = name;
        this.hasHead = true;
    }

    public EntityVariant(String modid, String name, String texture, boolean hasHead) {
        this.texture = new ResourceLocation(modid, "textures/entity/" + texture + ".png");
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
