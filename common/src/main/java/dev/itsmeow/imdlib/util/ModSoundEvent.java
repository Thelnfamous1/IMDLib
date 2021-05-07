package dev.itsmeow.imdlib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSoundEvent extends SoundEvent {

    public ModSoundEvent(String modid, String name) {
        super(new ResourceLocation(modid, name));
        //this.setRegistryName(new ResourceLocation(modid, name.replaceAll("\\.", "_")));
    }

}
