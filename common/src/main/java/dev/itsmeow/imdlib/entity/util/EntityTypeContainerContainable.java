package dev.itsmeow.imdlib.entity.util;

import dev.itsmeow.imdlib.entity.AbstractEntityBuilder;
import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.EntityTypeDefinition;
import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.item.IContainerItem;
import dev.itsmeow.imdlib.item.IContainerItem.ITooltipFunction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityTypeContainerContainable<T extends Mob & IContainable, I extends Item & IContainerItem<T>> extends EntityTypeContainer<T> {

    protected EntityDataAccessor<Boolean> fromContainerDataKey;
    protected LazyLoadedValue<I> containerItem;
    protected LazyLoadedValue<Item> emptyContainerItem;
    protected String containerItemName;
    protected String emptyContainerItemName;

    protected EntityTypeContainerContainable(ContainableEntityTypeDefinition<T, I, EntityTypeContainerContainable<T, I>> def) {
        super(def);
        this.containerItem = new LazyLoadedValue(() -> def.getContainerSupplier().apply(this, def.getTooltipFunction()));
        this.emptyContainerItem = new LazyLoadedValue(() -> def.getEmptyContainerSupplier().apply(this));
        this.containerItemName = def.getContainerNameSupplier().apply(this);
        this.emptyContainerItemName = def.getEmptyContainerNameSupplier().apply(this);
    }

    public EntityDataAccessor<Boolean> getFromContainerDataKey() {
        if (this.fromContainerDataKey == null) {
            this.fromContainerDataKey = SynchedEntityData.defineId(this.getEntityClass(), EntityDataSerializers.BOOLEAN);
        }
        return this.fromContainerDataKey;
    }

    public I getContainerItem() {
        return this.containerItem.get();
    }

    public Item getEmptyContainerItem() {
        return this.emptyContainerItem.get();
    }

    public String getEmptyContainerItemName() {
        return emptyContainerItemName;
    }

    public String getContainerItemName() {
        return containerItemName;
    }

    protected static class ContainableEntityTypeDefinition<T extends Mob & IContainable, I extends Item & IContainerItem<T>, C extends EntityTypeContainerContainable<T, I>> extends EntityTypeDefinition<T> {
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

        public Function<C, String> getContainerNameSupplier() {
            return builder.containerNameSupplier;
        }

        public Function<C, String> getEmptyContainerNameSupplier() {
            return builder.emptyContainerNameSupplier;
        }

    }

    public static abstract class AbstractEntityBuilderContainable<T extends Mob & IContainable, I extends Item & IContainerItem<T>, C extends EntityTypeContainerContainable<T, I>, B extends AbstractEntityBuilderContainable<T, I, C, B>> extends AbstractEntityBuilder<T, C, B> {

        protected ITooltipFunction tooltip;
        protected ITooltipFunction tooltipFinal;
        protected BiFunction<C, ITooltipFunction, I> containerSupplier;
        protected Function<C, Item> emptyContainerSupplier;
        protected Function<C, String> containerNameSupplier;
        protected Function<C, String> emptyContainerNameSupplier;

        protected AbstractEntityBuilderContainable(Class<T> EntityClass, EntityType.EntityFactory<T> factory, String entityNameIn, Supplier<AttributeSupplier.Builder> attributeMap, String modid) {
            super(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        public B containers(String containerNameFormat, BiFunction<C, ITooltipFunction, I> containerSupplier, String emptyContainerNameFormat, Function<C, Item> emptyContainerSupplier) {
            return containers(containerNameFormat, containerSupplier, emptyContainerNameFormat, emptyContainerSupplier, null);
        }

        public B containers(String containerNameFormat, BiFunction<C, ITooltipFunction, I> containerSupplier, String emptyContainerNameFormat, Function<C, Item> emptyContainerSupplier, ITooltipFunction tooltip) {
            this.containerSupplier = containerSupplier;
            this.emptyContainerSupplier = emptyContainerSupplier;
            this.tooltip = tooltip;
            this.containerNameSupplier = c -> String.format(containerNameFormat, c.getEntityName());
            this.emptyContainerNameSupplier = c -> String.format(emptyContainerNameFormat, c.getEntityName());
            return getImplementation();
        }

        @Override
        public void preBuild() {
            if (variantCount > 0) {
                if (this.tooltip == null) {
                    this.tooltipFinal = IContainerItem.VARIANT_TOOLTIP;
                } else {
                    this.tooltipFinal = (container, stack, worldIn, tooltip) -> {
                        IContainerItem.VARIANT_TOOLTIP.addInformation(container, stack, worldIn, tooltip);
                        this.tooltip.addInformation(container, stack, worldIn, tooltip);
                    };
                }
            } else if (this.tooltip != null) {
                this.tooltipFinal = this.tooltip;
            } else {
                this.tooltipFinal = (container, stack, world, tooltip) -> {
                };
            }
        }
    }

    public static class Builder<T extends Mob & IContainable, I extends Item & IContainerItem<T>> extends AbstractEntityBuilderContainable<T, I, EntityTypeContainerContainable<T, I>, Builder<T, I>> {

        protected Builder(Class<T> EntityClass, EntityType.EntityFactory<T> factory, String entityNameIn, Supplier<AttributeSupplier.Builder> attributeMap, String modid) {
            super(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        public static <T extends Mob & IContainable, I extends Item & IContainerItem<T>> Builder<T, I> create(Class<T> EntityClass, EntityType.EntityFactory<T> factory, String entityNameIn, Supplier<AttributeSupplier.Builder> attributeMap, String modid) {
            return new Builder<>(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        @Override
        public EntityTypeContainerContainable<T, I> rawBuild() {
            return new EntityTypeContainerContainable<>(new ContainableEntityTypeDefinition<>(this));
        }

        @Override
        public Builder<T, I> getImplementation() {
            return this;
        }
    }

}
