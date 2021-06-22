package dev.itsmeow.imdlib.util;

import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;

public class ClassLoadHacksForge {

    public static void subscribeInstanceIf(boolean condition, IEventBus bus, String classNameActive) {
        try {
            if (condition) {
                bus.register(Class.forName(classNameActive).newInstance());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LogManager.getLogger().error("Error retrieving compatibility class. This is a bug.");
        }
    }

}
