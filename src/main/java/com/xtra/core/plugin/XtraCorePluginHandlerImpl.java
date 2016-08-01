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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.api.plugin.XtraCorePluginHandler;
import com.xtra.api.util.exceptions.XtraCoreException;
import com.xtra.core.internal.Internals;

public class XtraCorePluginHandlerImpl implements XtraCorePluginHandler {

    private Set<XtraCorePluginContainer> containers = new HashSet<>();

    public XtraCorePluginContainer add(Object plugin) {
        Optional<PluginContainer> optional = Sponge.getPluginManager().fromInstance(plugin);
        if (!optional.isPresent()) {
            Internals.globalLogger.error("Cannot find the plugin instance for " + plugin.getClass().getName() + "! Did you pass the wrong object?",
                    new XtraCoreException());
        }
        XtraCorePluginContainerImpl container = new XtraCorePluginContainerImpl(plugin, optional.get());
        this.containers.add(container);
        return container;
    }

    @Override
    public Optional<XtraCorePluginContainer> getContainer(Class<?> clazz) {
        checkNotNull(clazz, "Container class cannot be null!");
        for (XtraCorePluginContainer container : this.containers) {
            if (container.getPlugin().getClass().equals(clazz)) {
                return Optional.of(container);
            }
        }
        return Optional.empty();
    }

    @Override
    public XtraCorePluginContainer getContainerUnchecked(Class<?> clazz) {
        checkNotNull(clazz, "Container class cannot be null!");
        for (XtraCorePluginContainer container : this.containers) {
            if (container.getPlugin().getClass().equals(clazz)) {
                return container;
            }
        }
        Internals.globalLogger.error("Cannot find the plugin container for " + clazz.getName() + "! Did you pass the wrong class?",
                new XtraCoreException());
        return null;
    }

    @Override
    public Collection<XtraCorePluginContainer> getContainers() {
        return this.containers;
    }
}
