package dev.itsmeow.imdlib.mixin;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ClothConfigScreen.class)
public interface ClothConfigScreenAccessor {

    @Accessor("tabButtons")
    List<ClothConfigTabButton> getTabButtons();

}
