package dev.itsmeow.imdlib.client;

import dev.itsmeow.imdlib.client.render.RenderFactory;
//import dev.itsmeow.imdlib.client.util.ModelReplacementHandler;

public class IMDLibClient {

    /*
    public static ModelReplacementHandler getReplacementHandler(String modid) {
        return new ModelReplacementHandler(modid);
    }
    */

    public static RenderFactory getRenderRegistry(String modid) {
        return new RenderFactory(modid);
    }

}
