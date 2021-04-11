package dev.itsmeow.imdlib.entity.interfaces;

import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;

public interface IContainerEntity<T extends MobEntity> {

    /* Implemented */

    T getImplementation();

    EntityTypeContainer<? extends T> getContainer();

    /* Default Methods */

    default boolean despawn(double range) {
        return getContainer().despawns() && !this.getImplementation().hasCustomName();
    }

    default HeadType getHeadType() {
        return getContainer().getHeadType();
    }

    default void doHeadDrop() {
        getHeadType().drop(getImplementation(), 12);
    }

    static <T extends MobEntity, C extends EntityTypeContainer<T>> boolean despawn(C container, T impl, double range) {
        return container.despawns() && !impl.hasCustomName();
    }

    @SuppressWarnings("unchecked")
    default EntityType<? extends T> type() {
        return getContainer().getEntityType();
    }

    default T createType() {
        return type().create(getImplementation().getEntityWorld());
    }
}