package dev.itsmeow.imdlib.entity.util;

import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.entity.MobEntity;

public interface IContainerEntity<T extends MobEntity> {

    /* Implemented */

    T getImplementation();

    EntityTypeContainer<?> getContainer();

    /* Default Methods */

    default boolean despawn(double range) {
        return getContainer().despawn && !this.getImplementation().hasCustomName();
    }

    default HeadType getHeadType() {
        return getContainer().getHeadType();
    }

    default void doHeadDrop() {
        getHeadType().drop(getImplementation(), 12);
    }

    public static <T extends MobEntity, C extends EntityTypeContainer<T>> boolean despawn(C container, T impl, double range) {
        return container.despawn && !impl.hasCustomName();
    }

}