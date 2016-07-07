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

package com.xtra.core.listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.Sponge;

import com.xtra.api.listener.ListenerHandler;
import com.xtra.core.CoreImpl;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;

public class ListenerHandlerImpl implements ListenerHandler {

    private Set<Class<?>> listenerClasses = new HashSet<>();

    public void registerListeners(Object plugin) {
        Internals.globalLogger.log("Registering listeners for " + plugin.getClass().getName());
        XtraCorePluginContainerImpl container =
                (XtraCorePluginContainerImpl) CoreImpl.instance.getPluginHandler().getContainerUnchecked(plugin.getClass());
        container.getLogger().log("======================================================");
        for (Object listener : container.scanner.getPluginListeners()) {
            this.listenerClasses.add(listener.getClass());
            Sponge.getEventManager().registerListeners(container.getPlugin(), listener);
        }
    }

    @Override
    public Collection<Class<?>> getListenerClasses() {
        return this.listenerClasses;
    }
}