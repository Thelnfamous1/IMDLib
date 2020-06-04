package dev.itsmeow.imdlib;

import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;

public class IMDLib {
    
    public static final String ID = "imdlib";

    public static EntityRegistrarHandler entityHandler(String modid) {
        return new EntityRegistrarHandler(modid);
    }

}
