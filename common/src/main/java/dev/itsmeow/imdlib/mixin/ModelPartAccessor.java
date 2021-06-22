package dev.itsmeow.imdlib.mixin;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {

    @Accessor("cubes")
    ObjectList<ModelPart.Cube> cubes();

}
