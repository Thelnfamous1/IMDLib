package dev.itsmeow.imdlib.util.config;

import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ConfigBuilder {

    private Consumer<MinecraftServer> onLoadMethod;

    protected ConfigBuilder(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        this.onLoadMethod = a -> onLoad.run();
    }

    protected ConfigBuilder(Consumer<ConfigBuilder> init, Consumer<MinecraftServer> onLoad) {
        this.onLoadMethod = onLoad;
    }

    public void onLoad(MinecraftServer t) {
        this.onLoadMethod.accept(t);
    }

    public abstract <T> Supplier<T> define(String path, T defaultValue);

    public abstract Supplier<Double> defineInRange(String path, double defaultValue, double min, double max);

    public abstract Supplier<Integer> defineInRange(String path, int defaultValue, int min, int max);

    public abstract Supplier<Long> defineInRange(String path, long defaultValue, long min, long max);

    public abstract <T> Supplier<List<? extends T>> defineList(String path, List<? extends T> defaultValue, T baseTypeValue, Predicate<Object> elementValidator);

    public abstract <T> Supplier<List<? extends T>> defineList(String path, Supplier<List<? extends T>> defaultSupplier, T baseTypeValue, Predicate<Object> elementValidator);

    public abstract <T> Supplier<T> define(String path, String comment, T defaultValue);

    public abstract Supplier<Double> defineInRange(String path, String comment, double defaultValue, double min, double max);

    public abstract Supplier<Integer> defineInRange(String path, String comment, int defaultValue, int min, int max);

    public abstract Supplier<Long> defineInRange(String path, String comment, long defaultValue, long min, long max);

    public abstract <T> Supplier<List<? extends T>> defineList(String path, String comment, List<? extends T> defaultValue, T baseTypeValue, Predicate<Object> elementValidator);

    public abstract <T> Supplier<List<? extends T>> defineList(String path, String comment, Supplier<List<? extends T>> defaultSupplier, T baseTypeValue, Predicate<Object> elementValidator);

    public abstract void push(String path);

    public abstract void pop();
}
