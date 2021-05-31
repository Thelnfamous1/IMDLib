package dev.itsmeow.imdlib.forge;

import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;
import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.util.ClassLoadHacks;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.beans.EventHandler;

public class EntityRegistrarHandlerImpl {

    public void platformInit(boolean useAttributeEvents, EntityRegistrarHandler handler) {
        if (!useAttributeEvents) {
            handler.ENTITIES.forEach(((s, entityTypeContainer) -> DefaultAttributes.put(entityTypeContainer.getEntityType(), entityTypeContainer.getDefinition().getAttributeMap().get().build())));
        } else {
            //Bottom method? TODO
        }
    }

    public void subscribe(IEventBus modBus, boolean useAttributeEvents, EntityRegistrarHandler handler) {
        modBus.register(new EventHandler(handler));
        ClassLoadHacks.runIf(useAttributeEvents, () -> () -> modBus.register(new EntityAttributeRegistrar(handler)));
    }


    public static class EntityAttributeRegistrar {
        private final EntityRegistrarHandler handler;

        public EntityAttributeRegistrar(EntityRegistrarHandler handler) {
            this.handler = handler;
        }

        @SubscribeEvent
        public void attributeCreate(EntityAttributeCreationEvent event) {
            for (EntityTypeContainer<?> container : handler.ENTITIES.values()) {
                event.put(container.getEntityType(), container.getAttributeBuilder().get().create());
            }
        }
    }
}



