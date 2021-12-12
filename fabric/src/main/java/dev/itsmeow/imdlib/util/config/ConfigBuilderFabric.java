package dev.itsmeow.imdlib.util.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ListConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.NumberConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigBuilderFabric extends ConfigBuilder {

    private ConfigTreeBuilder builder = ConfigTree.builder();

    public ConfigBuilderFabric(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        super(type, init, onLoad);
    }

    public ConfigBuilderFabric(Consumer<ConfigBuilder> init, Consumer<MinecraftServer> onLoad) {
        super(init, onLoad);
    }

    public ConfigTreeBuilder getBuilder() {
        return builder;
    }

    public static <T, A extends SerializableType<T>> ConfigType<T, T, A> typeLookup(T value) {
        if (Boolean.class.isInstance(value)) {
            return (ConfigType<T, T, A>) ConfigTypes.BOOLEAN;
        } else if (Integer.class.isInstance(value)) {
            return (ConfigType<T, T, A>) ConfigTypes.INTEGER;
        } else if (String.class.isInstance(value)) {
            return (ConfigType<T, T, A>) ConfigTypes.STRING;
        } else if (Double.class.isInstance(value)) {
            return (ConfigType<T, T, A>) ConfigTypes.DOUBLE;
        } else if (Float.class.isInstance(value)) {
            return (ConfigType<T, T, A>) ConfigTypes.FLOAT;
        } else if (Long.class.isInstance(value)) {
            return (ConfigType<T, T, A>) ConfigTypes.LONG;
        } else if (Byte.class.isInstance(value)) {
            return (ConfigType<T, T, A>) ConfigTypes.BYTE;
        } else if (Character.class.isInstance(value)) {
            return (ConfigType<T, T, A>) ConfigTypes.CHARACTER;
        } else if (Short.class.isInstance(value)) {
            return (ConfigType<T, T, A>) ConfigTypes.SHORT;
        }
        throw new RuntimeException("Define config for unknown type: " + value.getClass().getName());
    }

    @Override
    public <T> Supplier<T> define(String path, T defaultValue) {
        ConfigType<T, T, SerializableType<T>> type = typeLookup(defaultValue);
        PropertyMirror<T> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, defaultValue).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public Supplier<Double> defineInRange(String path, double defaultValue, double min, double max) {
        NumberConfigType<Double> type = ConfigTypes.DOUBLE.withMinimum(min).withMaximum(max);
        PropertyMirror<Double> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, defaultValue).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public Supplier<Integer> defineInRange(String path, int defaultValue, int min, int max) {
        NumberConfigType<Integer> type = ConfigTypes.INTEGER.withMinimum(min).withMaximum(max);
        PropertyMirror<Integer> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, defaultValue).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public Supplier<Long> defineInRange(String path, long defaultValue, long min, long max) {
        NumberConfigType<Long> type = ConfigTypes.LONG.withMinimum(min).withMaximum(max);
        PropertyMirror<Long> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, defaultValue).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(String path, List<? extends T> defaultValue, T baseTypeValue, Predicate<Object> elementValidator) {
        ListConfigType<List<T>, T> type = ConfigTypes.makeList(typeLookup(baseTypeValue));
        PropertyMirror<List<T>> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, (List<T>) defaultValue).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(String path, Supplier<List<? extends T>> defaultSupplier, T baseTypeValue, Predicate<Object> elementValidator) {
        ListConfigType<List<T>, T> type = ConfigTypes.makeList(typeLookup(baseTypeValue));
        PropertyMirror<List<T>> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, (List<T>) defaultSupplier.get()).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public <T> Supplier<T> define(String path, String comment, T defaultValue) {
        ConfigType<T, T, SerializableType<T>> type = typeLookup(defaultValue);
        PropertyMirror<T> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, defaultValue).withComment(comment).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public Supplier<Double> defineInRange(String path, String comment, double defaultValue, double min, double max) {
        NumberConfigType<Double> type = ConfigTypes.DOUBLE.withMinimum(min).withMaximum(max);
        PropertyMirror<Double> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, defaultValue).withComment(comment).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public Supplier<Integer> defineInRange(String path, String comment, int defaultValue, int min, int max) {
        NumberConfigType<Integer> type = ConfigTypes.INTEGER.withMinimum(min).withMaximum(max);
        PropertyMirror<Integer> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, defaultValue).withComment(comment).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public Supplier<Long> defineInRange(String path, String comment, long defaultValue, long min, long max) {
        NumberConfigType<Long> type = ConfigTypes.LONG.withMinimum(min).withMaximum(max);
        PropertyMirror<Long> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, defaultValue).withComment(comment).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(String path, String comment, List<? extends T> defaultValue, T baseTypeValue, Predicate<Object> elementValidator) {
        ListConfigType<List<T>, T> type = ConfigTypes.makeList(typeLookup(baseTypeValue));
        PropertyMirror<List<T>> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, (List<T>) defaultValue).withComment(comment).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(String path, String comment, Supplier<List<? extends T>> defaultSupplier, T baseTypeValue, Predicate<Object> elementValidator) {
        ListConfigType<List<T>, T> type = ConfigTypes.makeList(typeLookup(baseTypeValue));
        PropertyMirror<List<T>> mirror = PropertyMirror.create(type);
        builder = builder.beginValue(path, type, (List<T>) defaultSupplier.get()).withComment(comment).finishValue(mirror::mirror);
        return mirror::getValue;
    }

    @Override
    public void push(String path) {
        builder = builder.fork(path);
    }

    @Override
    public void pop() {
        builder = builder.finishBranch();
    }
}
