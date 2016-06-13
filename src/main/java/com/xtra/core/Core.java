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

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import com.xtra.core.internal.Internals;
import com.xtra.core.util.ReflectionScanner;
import com.xtra.core.util.exceptions.XtraCoreException;
import com.xtra.core.util.log.Logger;

public class Core {

    /**
     * Initializes the base core class for function.
     * 
     * <p>CALL THIS BEFORE ATTEMPTING TO DO ANYTHING ELSE WITH XTRACORE OR
     * EVERYTHING WILL BREAK.</p>
     * 
     * @param plugin The plugin class
     * @return The core class
     */
    public static Core initialize(Object plugin) {
        return initialize(plugin, true);
    }

    /**
     * Initializes the base core class for function.
     * 
     * <p>CALL THIS BEFORE ATTEMPTING TO DO ANYTHING ELSE WITH XTRACORE OR
     * EVERYTHING WILL BREAK.</p>
     * 
     * @param plugin The plugin class
     * @param log Whether or not to create a log of various XtraCore actions
     * @return The core class
     */
    public static Core initialize(Object plugin, boolean log) {
        Optional<PluginContainer> optional = Sponge.getPluginManager().fromInstance(plugin);
        if (!optional.isPresent()) {
            try {
                throw new XtraCoreException("Cannot find the plugin instance! Did you pass the wrong object?");
            } catch (XtraCoreException e) {
                e.printStackTrace();
            }
        }
        Internals.pluginContainer = optional.get();
        Internals.plugin = plugin;

        Internals.logger = new Logger(log);
        Internals.logger.log("======================================================");
        Internals.logger.log("Initializing XtraCore version " + Internals.VERSION + "!");
        Internals.logger.log("======================================================");

        Internals.commands = ReflectionScanner.getCommands();
        Internals.configs = ReflectionScanner.getConfigs();
        Internals.initialized = true;
        return new Core();
    }
}
