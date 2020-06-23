package dev.itsmeow.imdlib.entity.util;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public interface IVariant {

    public String getName();

    public ResourceLocation getTexture(@Nullable Entity entity);

    public boolean hasHead();

}
