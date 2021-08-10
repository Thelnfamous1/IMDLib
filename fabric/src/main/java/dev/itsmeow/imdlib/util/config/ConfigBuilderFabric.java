package dev.itsmeow.imdlib.util.config;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigBuilderFabric extends ConfigBuilder {

    public ConfigBuilderFabric(CommonConfigAPI.ConfigType type, Consumer<ConfigBuilder> init, Runnable onLoad) {
        super(type, init, onLoad);
    }

    @Override
    public <T> Supplier<T> define(String path, T defaultValue) {
        return () -> defaultValue;
    }

    @Override
    public <T extends Comparable<? super T>> Supplier<T> defineInRange(List<String> path, T defaultValue, T min, T max, Class<T> clazz) {
        return () -> defaultValue;
    }

    @Override
    public Supplier<Double> defineInRange(String path, double defaultValue, double min, double max) {
        return () -> defaultValue;
    }

    @Override
    public Supplier<Integer> defineInRange(String path, int defaultValue, int min, int max) {
        return () -> defaultValue;
    }

    @Override
    public Supplier<Long> defineInRange(String path, long defaultValue, long min, long max) {
        return () -> defaultValue;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return () -> defaultValue;
    }

    @Override
    public <T> Supplier<T> define(String path, String comment, T defaultValue) {
        return () -> defaultValue;
    }

    @Override
    public <T extends Comparable<? super T>> Supplier<T> defineInRange(List<String> path, String comment, T defaultValue, T min, T max, Class<T> clazz) {
        return () -> defaultValue;
    }

    @Override
    public Supplier<Double> defineInRange(String path, String comment, double defaultValue, double min, double max) {
        return () -> defaultValue;
    }

    @Override
    public Supplier<Integer> defineInRange(String path, String comment, int defaultValue, int min, int max) {
        return () -> defaultValue;
    }

    @Override
    public Supplier<Long> defineInRange(String path, String comment, long defaultValue, long min, long max) {
        return () -> defaultValue;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(String path, String comment, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return () -> defaultValue;
    }

    @Override
    public void push(String path) {

    }

    @Override
    public void pop() {

    }
}
