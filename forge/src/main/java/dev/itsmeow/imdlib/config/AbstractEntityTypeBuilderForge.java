package dev.itsmeow.imdlib.config;

import dev.itsmeow.imdlib.entity.AbstractEntityBuilder;
import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public abstract class AbstractEntityTypeBuilderForge<T extends Mob, C extends EntityTypeContainer<T>, B extends AbstractEntityBuilder<T, C, B>> extends AbstractEntityBuilder<T, C, B> {

    protected ForgeEntityTypeContainerConfigHandler.CustomConfigurationLoad customConfigLoad;
    protected ForgeEntityTypeContainerConfigHandler.CustomConfigurationInit customConfigInit;
    protected ForgeEntityTypeContainerConfigHandler.CustomConfigurationLoad customClientConfigLoad;
    protected ForgeEntityTypeContainerConfigHandler.CustomConfigurationInit customClientConfigInit;

    protected AbstractEntityTypeBuilderForge(Class<T> EntityClass, EntityType.EntityFactory<T> factory, String entityNameIn, Supplier<AttributeSupplier.Builder> attributeMap, String modid) {
        super(EntityClass, factory, entityNameIn, attributeMap, modid);
    }

    public B config(ForgeEntityTypeContainerConfigHandler.CustomConfigurationInit configurationInit) {
        return config(configurationInit, holder -> {
        });
    }

    public B clientConfig(ForgeEntityTypeContainerConfigHandler.CustomConfigurationInit configurationInit) {
        return clientConfig(configurationInit, holder -> {
        });
    }

    public B config(ForgeEntityTypeContainerConfigHandler.CustomConfigurationInit configurationInit, ForgeEntityTypeContainerConfigHandler.CustomConfigurationLoad configurationLoad) {
        this.customConfigLoad = configurationLoad;
        this.customConfigInit = configurationInit;
        return getImplementation();
    }


    public B clientConfig(ForgeEntityTypeContainerConfigHandler.CustomConfigurationInit configurationInit, ForgeEntityTypeContainerConfigHandler.CustomConfigurationLoad configurationLoad) {
        this.customClientConfigLoad = configurationLoad;
        this.customClientConfigInit = configurationInit;
        return getImplementation();
    }
}
