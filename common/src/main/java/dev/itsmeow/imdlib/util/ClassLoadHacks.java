package dev.itsmeow.imdlib.util;

import me.shedaniel.architectury.platform.Platform;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

public final class ClassLoadHacks {

    /**
     * @param clientTarget - Supplier to get and execute runnable of if on CLIENT
     * @param serverTarget - Supplier to get and execute runnable of if on DEDICATED_SERVER
     */
    public static void runOnDist(Supplier<Runnable> clientTarget, Supplier<Runnable> serverTarget) {
        switch (Platform.getEnv()) {
            case CLIENT:
                clientTarget.get().run();
                break;
            case SERVER:
                serverTarget.get().run();
                break;
            default:
                throw new IllegalArgumentException("UNSIDED?");
        }
    }

    /**
     * Checks if a modid is loaded and executes a runnable if it is
     *
     * @param modid  - Modid to check if loaded
     * @param target - Supplier to get and execute the runnable of if loaded
     */
    public static void runWhenLoaded(String modid, Supplier<Runnable> target) {
        runIf(Platform.isModLoaded(modid) || modid.equals("minecraft"), target);
    }

    public static void runIf(boolean condition, Supplier<Runnable> target) {
        if (condition) {
            target.get().run();
        }
    }

    /**
     * Get the compatibility proxy for a given modid, uses reflection.
     *
     * @param baseType          - The class/interface shared by the two classes
     * @param modid             - modid to check if loaded
     * @param classNameActive   - The class name to return if the mod is active
     * @param classNameInactive - The class name to return if the mod is not present
     * @return The proper proxy class for whether the mod is loaded or not
     **/
    public static <T> T getInteropProxy(Class<T> baseType, String modid, String classNameActive, String classNameInactive) {
        T proxy = null;
        try {
            if (Platform.isModLoaded(modid)) {
                proxy = Class.forName(classNameActive).asSubclass(baseType).newInstance();
            } else {
                proxy = Class.forName(classNameInactive).asSubclass(baseType).newInstance();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LogManager.getLogger().error("Error retrieving compatibility class. This is a bug.");
        }
        return proxy;
    }

    /**
     * Get the compatibility proxy for a given modid, uses reflection.
     *
     * @param baseType        - An interface or class that classNameActive
     *                        extends/implements. Using the type of classNameActive
     *                        may load it, so be careful.
     * @param modid           - modid to check if loaded
     * @param classNameActive - The class name to return if the mod is active
     * @return An instance of classNameActive (as an Object) if active, null if
     * inactive
     **/
    public static <T> T getClassIfActive(Class<T> baseType, String modid, String classNameActive) {
        T proxy = null;
        try {
            if (Platform.isModLoaded(modid)) {
                proxy = Class.forName(classNameActive).asSubclass(baseType).newInstance();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LogManager.getLogger().error("Error retrieving compatibility class. This is a bug.");
        }
        return proxy;
    }

}
