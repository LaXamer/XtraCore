/**
 * This file is part of XtraCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2016 XtraStudio <https://github.com/XtraStudio>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xtra.core.plugin;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.collect.Maps;
import com.xtra.core.internal.Internals;
import com.xtra.core.util.exceptions.XtraCoreException;
import com.xtra.core.util.log.LogHandler;

public class XtraCorePluginHandler {

    /**
     * Creates a new {@link XtraCorePluginContainer} and
     * {@link XtraCoreInternalPluginContainer} mapping entry for the specified
     * plugin. If the specified Object is not a plugin, then an
     * {@link XtraCoreException} is raised.
     * 
     * @param plugin The plugin to add
     * @return The added xtracore plugin container
     */
    public static Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> add(Object plugin) {
        Optional<PluginContainer> optional = Sponge.getPluginManager().fromInstance(plugin);
        if (!optional.isPresent()) {
            LogHandler.getGlobalLogger().log(
                    new XtraCoreException("Cannot find the plugin instance for " + plugin.getClass().getName() + "! Did you pass the wrong object?"));
        }
        XtraCorePluginContainer container = new XtraCorePluginContainer(plugin, optional.get());
        XtraCoreInternalPluginContainer internalContainer = new XtraCoreInternalPluginContainer(container);
        Internals.plugins.put(container, internalContainer);
        return Maps.immutableEntry(container, internalContainer);
    }

    /**
     * Gets the {@link XtraCorePluginContainer} for the specified plugin object.
     * 
     * @param plugin The plugin
     * @return The xtracore plugin container or {@link Optional#empty()} if the
     *         specified plugin object does not hold an xtracore plugin
     *         container
     */
    public static Optional<XtraCorePluginContainer> getContainer(Object plugin) {
        for (Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry : Internals.plugins.entrySet()) {
            if (entry.getKey().getPlugin().equals(plugin)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the {@link XtraCorePluginContainer} for the specified plugin class.
     * 
     * @param clazz The plugin class
     * @return The xtracore plugin container or {@link Optional#empty()} if the
     *         specified plugin object does not hold an xtracore plugin
     *         container
     */
    public static Optional<XtraCorePluginContainer> getContainer(Class<?> clazz) {
        for (Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry : Internals.plugins.entrySet()) {
            if (entry.getKey().getPlugin().getClass().equals(clazz)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the {@link XtraCorePluginContainer} for the specified plugin object.
     * Throws an {@link XtraCoreException} if the specified plugin's container
     * could not be found.
     * 
     * @param plugin The plugin
     * @return The xtracore plugin container
     */
    public static XtraCorePluginContainer getContainerUnchecked(Object plugin) {
        for (Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry : Internals.plugins.entrySet()) {
            if (entry.getKey().getPlugin().equals(plugin)) {
                return entry.getKey();
            }
        }
        Internals.globalLogger.log(
                new XtraCoreException("Cannot find the plugin container for " + plugin.getClass().getName() + "! Did you pass the wrong object?"));
        return null;
    }

    /**
     * Gets the {@link XtraCorePluginContainer} for the specified plugin class.
     * Throws an {@link XtraCoreException} if the specified plugin's container
     * could not be found.
     * 
     * @param clazz The plugin class
     * @return The xtracore plugin container
     */
    public static XtraCorePluginContainer getContainerUnchecked(Class<?> clazz) {
        for (Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry : Internals.plugins.entrySet()) {
            if (entry.getKey().getPlugin().getClass().equals(clazz)) {
                return entry.getKey();
            }
        }
        Internals.globalLogger.log(
                new XtraCoreException("Cannot find the plugin container for " + clazz.getName() + "! Did you pass the wrong class?"));
        return null;
    }

    /**
     * Gets the mapping entry of an {@link XtraCorePluginContainer} and a
     * {@link XtraCoreInternalPluginContainer}.
     * 
     * @param plugin The plugin
     * @return The mapping entry, or {@link Optional#empty()} if an entry could
     *         not be found
     */
    public static Optional<Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer>> getEntryContainer(Object plugin) {
        for (Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry : Internals.plugins.entrySet()) {
            if (entry.getKey().getPlugin().equals(plugin)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the mapping entry of an {@link XtraCorePluginContainer} and a
     * {@link XtraCoreInternalPluginContainer}.
     * 
     * @param clazz The plugin class
     * @return The mapping entry, or {@link Optional#empty()} if an entry could
     *         not be found
     */
    public static Optional<Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer>> getEntryContainer(Class<?> clazz) {
        for (Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry : Internals.plugins.entrySet()) {
            if (entry.getKey().getPlugin().getClass().equals(clazz)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the mapping entry of an {@link XtraCorePluginContainer} and a
     * {@link XtraCoreInternalPluginContainer}. Throws an
     * {@link XtraCoreException} if the specified plugin's entry could not be
     * found.
     * 
     * @param plugin The plugin
     * @return The mapping entry
     */
    public static Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> getEntryContainerUnchecked(Object plugin) {
        for (Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry : Internals.plugins.entrySet()) {
            if (entry.getKey().getPlugin().equals(plugin)) {
                return entry;
            }
        }
        Internals.globalLogger.log(new XtraCoreException(
                "Cannot find the plugin container entry for " + plugin.getClass().getName() + "! Did you pass the wrong object?"));
        return null;
    }

    /**
     * Gets the mapping entry of an {@link XtraCorePluginContainer} and a
     * {@link XtraCoreInternalPluginContainer}. Throws an
     * {@link XtraCoreException} if the specified plugin's entry could not be
     * found.
     * 
     * @param clazz The plugin class
     * @return The mapping entry
     */
    public static Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> getEntryContainerUnchecked(Class<?> clazz) {
        for (Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry : Internals.plugins.entrySet()) {
            if (entry.getKey().getPlugin().getClass().equals(clazz)) {
                return entry;
            }
        }
        Internals.globalLogger.log(new XtraCoreException(
                "Cannot find the plugin container entry for " + clazz.getName() + "! Did you pass the wrong object?"));
        return null;
    }
}
