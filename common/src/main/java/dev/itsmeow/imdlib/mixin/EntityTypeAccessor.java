package dev.itsmeow.imdlib.mixin;

import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityType.class)
public interface EntityTypeAccessor {

    @Accessor("serialize")
    boolean getSerialize();

    @Accessor("serialize")
    void setSerialize(boolean serialize);

}
