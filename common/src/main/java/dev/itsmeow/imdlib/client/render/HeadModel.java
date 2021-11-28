package dev.itsmeow.imdlib.client.render;

public interface HeadModel {

    default float wallOffsetX() {
        return 0F;
    }

    default float wallOffsetY() {
        return 0F;
    }

    default float wallOffsetZ() {
        return wallOffsetX();
    }

    default float globalOffsetY() {
        return 0F;
    }
}
