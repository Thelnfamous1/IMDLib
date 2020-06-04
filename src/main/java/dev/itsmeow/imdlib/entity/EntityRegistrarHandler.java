package dev.itsmeow.imdlib.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EntityRegistrarHandler {

    private static Field type$serializable = null;
    public final String modid;

    public EntityRegistrarHandler(String modid) {
        this.modid = modid;
    }

    public <T extends MobEntity> EntityType<T> createEntityType(EntityTypeContainer<T> container) {
        return createEntityType(container.entityClass, container.factory, container.entityName, container.spawnType, 64, 1, true, container.width, container.height);
    }

    public <T extends Entity> EntityType<T> createEntityType(Class<T> EntityClass, Function<World, T> func, String entityNameIn, EntityClassification classification, int trackingRange, int updateInterval, boolean velUpdates, float width, float height) {
        EntityType<T> type = EntityType.Builder.<T>create((etype, world) -> func.apply(world), classification).setTrackingRange(trackingRange).setUpdateInterval(updateInterval).setShouldReceiveVelocityUpdates(velUpdates).size(width, height).setCustomClientFactory((e, world) -> func.apply(world)).disableSerialization().build(modid + ":" + entityNameIn.toLowerCase());
        type.setRegistryName(modid + ":" + entityNameIn.toLowerCase());

        // It's okay, I hate it too
        try {
            if(type$serializable == null) {
                type$serializable = ObfuscationReflectionHelper.findField(EntityType.class, "field_200733_aL");
            }
            setFinalField(type$serializable, type, true);
        } catch(Exception e) {
            LogManager.getLogger().warn("Unable to set serializable for {}. This could result in possible saving issues with entities!", entityNameIn);
        }
        return type;
    }

    private static void setFinalField(Field field, Object object, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(object, newValue);
    }

}
