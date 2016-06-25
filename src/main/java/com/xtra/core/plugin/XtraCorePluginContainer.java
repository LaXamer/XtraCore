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

import org.spongepowered.api.plugin.PluginContainer;

import com.xtra.core.command.CommandHandler;
import com.xtra.core.command.runnable.CommandRunnableHandler;
import com.xtra.core.config.ConfigHandler;
import com.xtra.core.util.log.Logger;

public class XtraCorePluginContainer {

    private Object plugin;
    private PluginContainer pluginContainer;
    protected Logger logger;
    protected CommandHandler commandHandler;
    protected ConfigHandler configHandler;
    protected CommandRunnableHandler commandRunnableHandler;

    public XtraCorePluginContainer(Object plugin, PluginContainer pluginContainer) {
        this.plugin = plugin;
        this.pluginContainer = pluginContainer;
    }

    public Object getPlugin() {
        return this.plugin;
    }

    public PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }
    
    public Logger getLogger() {
        return this.logger;
    }

    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    public CommandRunnableHandler getCommandRunnableHandler() {
        return this.commandRunnableHandler;
    }
}
