package dev.itsmeow.imdlib;

import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.util.LazyLoadedValue;

public class IMDLib {

    public static final String MOD_ID = "imdlib";
    public static final LazyLoadedValue<Registries> REGISTRIES = new LazyLoadedValue<>(() -> Registries.get(MOD_ID));

    public static EntityRegistrarHandler entityHandler(String modid) {
        return new EntityRegistrarHandler(modid);
    }

}
