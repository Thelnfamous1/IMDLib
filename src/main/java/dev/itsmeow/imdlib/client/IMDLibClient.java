package dev.itsmeow.imdlib.client;

import dev.itsmeow.imdlib.client.util.ModelReplacementHandler;

public class IMDLibClient {

    public static ModelReplacementHandler getReplacementHandler(String modid) {
        return new ModelReplacementHandler(modid);
    }

}
