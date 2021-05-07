package dev.itsmeow.imdlib.entity.util.variant;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface IVariant {

    String getName();

    ResourceLocation getTexture(@Nullable Entity entity);

    boolean hasHead();

}
