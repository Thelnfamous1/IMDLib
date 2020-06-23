package dev.itsmeow.imdlib.entity.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public interface IVariant {

    public String getName();

    public <T extends Entity> ResourceLocation getTexture(T entity);

    public boolean hasHead();

}
