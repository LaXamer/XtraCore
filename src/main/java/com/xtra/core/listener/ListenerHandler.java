package com.xtra.core.listener;

import org.spongepowered.api.Sponge;

import com.xtra.core.internal.Internals;
import com.xtra.core.util.ReflectionScanner;

public class ListenerHandler {

    /**
     * Automatically scans and registers plugin listeners.
     */
    public static void registerListeners() {
        for (Object listener : ReflectionScanner.getPluginListeners()) {
            Sponge.getEventManager().registerListeners(Internals.plugin, listener);
        }
    }
}
