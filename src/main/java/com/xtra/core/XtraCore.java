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

package com.xtra.core;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.xtra.core.config.Config;
import com.xtra.core.config.annotation.DoNotReload;
import com.xtra.core.internal.InternalCommands;
import com.xtra.core.internal.Internals;
import com.xtra.core.registry.ConfigRegistry;
import com.xtra.core.util.log.Logger;

@Plugin(name = "XtraCore", id = "xtracore", version = Internals.VERSION, authors = {"12AwesomeMan34"}, description = Internals.DESCRIPTION)
public class XtraCore {

    @Listener(order = Order.FIRST)
    public void onPreInit(GamePreInitializationEvent event) {
        Internals.globalLogger = new Logger();
        Internals.globalLogger.log("======================================================");
        Internals.globalLogger.log("Initializing XtraCore version " + Internals.VERSION);
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        InternalCommands.createCommands(this);
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        for (Config config : ConfigRegistry.getAllConfigs()) {
            // If there is no DoNotReload annotation, then reload.
            if (config.getClass().getAnnotation(DoNotReload.class) == null) {
                config.load();
            }
        }
    }
}