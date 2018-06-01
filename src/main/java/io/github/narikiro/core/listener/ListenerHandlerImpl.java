/**
 * This file is part of XtraCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2018 LaXamer <https://github.com/LaXamer>
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

package io.github.narikiro.core.listener;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spongepowered.api.Sponge;
import io.github.narikiro.api.listener.ListenerHandler;
import io.github.narikiro.api.plugin.XtraCorePluginContainer;
import io.github.narikiro.core.internal.Internals;
import io.github.narikiro.core.plugin.XtraCorePluginContainerImpl;

public class ListenerHandlerImpl implements ListenerHandler {

    private Set<Object> listenerObjects = new HashSet<>();
    private Set<Method> listenerMethods = new HashSet<>();

    public ListenerHandlerImpl(XtraCorePluginContainer container) {
        this.registerListeners((XtraCorePluginContainerImpl) container);
    }

    public void registerListeners(XtraCorePluginContainerImpl container) {
        Internals.globalLogger.info("Registering listeners for " + container.getPluginContainer().getId());
        for (Map.Entry<Class<?>, Method> listener : container.scanner.getPluginListeners().entries()) {
            this.listenerMethods.add(listener.getValue());
            // To prevent duplicate classes getting instantiated, we need to
            // check if the current class already exists as a listener object.
            boolean skip = false;
            for (Object o : this.listenerObjects) {
                if (o.getClass().equals(listener.getKey())) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

            try {
                Object o = Internals.checkIfAlreadyExists(container, listener.getKey());
                this.listenerObjects.add(o);
                Sponge.getEventManager().registerListeners(container.getPlugin(), o);
            } catch (InstantiationException | IllegalAccessException e) {
                container.getLogger().error("An error has occurred while attempting to instantiate the listeners!", e);
            }
        }
        container.setListenerHandler(this);
    }

    @Override
    public Collection<Object> getListenerObjects() {
        return this.listenerObjects;
    }

    @Override
    public Collection<Method> getListenerMethods() {
        return this.listenerMethods;
    }
}
