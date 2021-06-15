package dev.itsmeow.imdlib.forge;

import dev.itsmeow.imdlib.blockentity.HeadBlockEntity;
import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;
import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.util.ClassLoadHacks;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static class EventHandler {
        private final EntityRegistrarHandler handler;

        public EventHandler(EntityRegistrarHandler handler) {
            this.handler = handler;
        }

        @SubscribeEvent
        public void gatherData(GatherDataEvent event) {
            //event.getGenerator().addProvider(new ModSpawnEggItem.DataProvider(handler, event.getGenerator(), event.getExistingFileHelper()));
        }

        @SubscribeEvent
        public void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
            /*
            for(EntityTypeContainer<?> container : handler.ENTITIES.values()) {
                event.getRegistry().register(container.entityType);
                if(!useAttributeEvents) {
                    container.registerAttributes();
                }
            }
            */
        }

        @SubscribeEvent
        public void registerBlocks(RegistryEvent.Register<Block> event) {
            for (HeadType type : HeadType.values()) {
                event.getRegistry().registerAll(type.getBlockSet().toArray(new Block[0]));
            }
        }

        @SubscribeEvent
        public void registerItems(RegistryEvent.Register<Item> event) {
            // Heads
            for (HeadType type : HeadType.values()) {
                event.getRegistry().registerAll(type.getItemSet().toArray(new Item[0]));
            }

            // Containers & eggs
            for (EntityTypeContainer<?> container : handler.ENTITIES.values()) {
                if (container instanceof EntityTypeContainerContainable<?, ?>) {
                    EntityTypeContainerContainable<?, ?> c = (EntityTypeContainerContainable<?, ?>) container;
                    if (!ForgeRegistries.ITEMS.containsValue(c.getContainerItem()) && handler.modid.equals(c.getContainerItem().getRegistryName().getNamespace())) {
                        event.getRegistry().register(c.getContainerItem());
                    }
                    if (!ForgeRegistries.ITEMS.containsValue(c.getEmptyContainerItem()) && handler.modid.equals(c.getEmptyContainerItem().getRegistryName().getNamespace())) {
                        event.getRegistry().register(c.getEmptyContainerItem());
                    }
                }
                if (container.hasEgg()) {
                    event.getRegistry().register(container.getEggItem());
                }
            }
        }

        @SubscribeEvent
        public void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
            HeadBlockEntity.HEAD_TYPE.setRegistryName(handler.modid, "head");
            event.getRegistry().register(HeadBlockEntity.HEAD_TYPE);
        }
    }
}



