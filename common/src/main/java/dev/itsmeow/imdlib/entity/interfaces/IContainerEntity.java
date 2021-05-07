package dev.itsmeow.imdlib.entity.interfaces;

import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public interface IContainerEntity<T extends Mob> {

    /* Implemented */

    static <T extends Mob, C extends EntityTypeContainer<T>> boolean despawn(C container, T impl, double range) {
        return container.despawns() && !impl.hasCustomName();
    }

    T getImplementation();

    /* Default Methods */

    EntityTypeContainer<? extends T> getContainer();

    default boolean despawn(double range) {
        return getContainer().despawns() && !this.getImplementation().hasCustomName();
    }

    default HeadType getHeadType() {
        return getContainer().getHeadType();
    }

    default void doHeadDrop() {
        getHeadType().drop(getImplementation(), 12);
    }

    @SuppressWarnings("unchecked")
    default EntityType<? extends T> type() {
        return getContainer().getEntityType();
    }

    default T createType() {
        return type().create(getImplementation().getCommandSenderWorld());
    }
}