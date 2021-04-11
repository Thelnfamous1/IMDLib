package dev.itsmeow.imdlib.entity.util.variant;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public interface IVariant {

    String getName();

    ResourceLocation getTexture(@Nullable Entity entity);

    boolean hasHead();

}
