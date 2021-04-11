package dev.itsmeow.imdlib.entity.util;

import dev.itsmeow.imdlib.entity.AbstractEntityBuilder;
import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.EntityTypeDefinition;
import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.item.IContainerItem;
import dev.itsmeow.imdlib.item.IContainerItem.ITooltipFunction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityTypeContainerContainable<T extends MobEntity & IContainable, I extends Item & IContainerItem<T>> extends EntityTypeContainer<T> {

    protected DataParameter<Boolean> fromContainerDataKey;
    protected I containerItem;
    protected Item emptyContainerItem;

    protected EntityTypeContainerContainable(ContainableEntityTypeDefinition<T, I, EntityTypeContainerContainable<T, I>> def) {
        super(def);
        this.containerItem = def.getContainerSupplier().apply(this, def.getTooltipFunction());
        this.emptyContainerItem = def.getEmptyContainerSupplier().apply(this);
    }

    protected static class ContainableEntityTypeDefinition<T extends MobEntity & IContainable, I extends Item & IContainerItem<T>, C extends EntityTypeContainerContainable<T, I>> extends EntityTypeDefinition<T> {
        AbstractEntityBuilderContainable<T, I, C, ?> builder;

        public ContainableEntityTypeDefinition(AbstractEntityBuilderContainable<T, I, C, ?> builder) {
            super(builder);
            this.builder = builder;
        }

        public ITooltipFunction getTooltipFunction() {
            return builder.tooltipFinal;
        }

        public BiFunction<C, ITooltipFunction, I> getContainerSupplier() {
            return builder.containerSupplier;
        }

        public Function<C, Item> getEmptyContainerSupplier() {
            return builder.emptyContainerSupplier;
        }

    }

    public static abstract class AbstractEntityBuilderContainable<T extends MobEntity & IContainable, I extends Item & IContainerItem<T>, C extends EntityTypeContainerContainable<T, I>, B extends AbstractEntityBuilderContainable<T, I, C, B>> extends AbstractEntityBuilder<T, C, B> {

        protected ITooltipFunction tooltip;
        protected ITooltipFunction tooltipFinal;
        protected BiFunction<C, ITooltipFunction, I> containerSupplier;
        protected Function<C, Item> emptyContainerSupplier;

        protected AbstractEntityBuilderContainable(Class<T> EntityClass, EntityType.IFactory<T> factory, String entityNameIn, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, String modid) {
            super(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        public B containers(BiFunction<C, ITooltipFunction, I> containerSupplier, Function<C, Item> emptyContainerSupplier) {
            return containers(containerSupplier, emptyContainerSupplier, null);
        }

        public B containers(BiFunction<C, ITooltipFunction, I> containerSupplier, Function<C, Item> emptyContainerSupplier, ITooltipFunction tooltip) {
            this.containerSupplier = containerSupplier;
            this.emptyContainerSupplier = emptyContainerSupplier;
            this.tooltip = tooltip;
            return getImplementation();
        }

        @Override
        public void preBuild() {
            if(variantCount > 0) {
                if(this.tooltip == null) {
                    this.tooltipFinal = IContainerItem.VARIANT_TOOLTIP;
                } else {
                    this.tooltipFinal = (container, stack, worldIn, tooltip) -> {
                        IContainerItem.VARIANT_TOOLTIP.addInformation(container, stack, worldIn, tooltip);
                        this.tooltip.addInformation(container, stack, worldIn, tooltip);
                    };
                }
            } else if(this.tooltip != null) {
                this.tooltipFinal = this.tooltip;
            } else {
                this.tooltipFinal = (container, stack, world, tooltip) -> {
                };
            }
        }
    }

    public static class Builder<T extends MobEntity & IContainable, I extends Item & IContainerItem<T>> extends AbstractEntityBuilderContainable<T, I, EntityTypeContainerContainable<T, I>, Builder<T, I>> {

        protected Builder(Class<T> EntityClass, EntityType.IFactory<T> factory, String entityNameIn, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, String modid) {
            super(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        @Override
        public EntityTypeContainerContainable<T, I> rawBuild() {
            return new EntityTypeContainerContainable<>(new ContainableEntityTypeDefinition<>(this));
        }

        @Override
        public Builder<T, I> getImplementation() {
            return this;
        }

        public static <T extends MobEntity & IContainable, I extends Item & IContainerItem<T>> Builder<T, I> create(Class<T> EntityClass, EntityType.IFactory<T> factory, String entityNameIn, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, String modid) {
            return new Builder<>(EntityClass, factory, entityNameIn, attributeMap, modid);
        }
    }

    public DataParameter<Boolean> getFromContainerDataKey() {
        if(this.fromContainerDataKey == null) {
            this.fromContainerDataKey = EntityDataManager.createKey(this.getEntityClass(), DataSerializers.BOOLEAN);
        }
        return this.fromContainerDataKey;
    }

    public I getContainerItem() {
        return this.containerItem;
    }

    public Item getEmptyContainerItem() {
        return this.emptyContainerItem;
    }

}
