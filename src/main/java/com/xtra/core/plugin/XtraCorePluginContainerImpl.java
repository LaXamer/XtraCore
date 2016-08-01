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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xtra.api.command.Command;
import com.xtra.api.command.CommandHandler;
import com.xtra.api.command.runnable.CommandRunnable;
import com.xtra.api.config.ConfigHandler;
import com.xtra.api.listener.ListenerHandler;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.api.text.HelpPaginationHandler;
import com.xtra.core.util.ReflectionScanner;
import com.xtra.core.util.store.CommandStore;

public class XtraCorePluginContainerImpl implements XtraCorePluginContainer {

    // Internals
    public Set<CommandStore> commandStores = new HashSet<>();
    public Multimap<Class<? extends Command>, CommandRunnable> commandRunnables = ArrayListMultimap.create();
    public ReflectionScanner scanner;
    // API
    private Object plugin;
    private PluginContainer pluginContainer;
    private Logger logger;
    // Defaults
    private Optional<CommandHandler> commandHandler = Optional.empty();
    private Optional<ConfigHandler> configHandler = Optional.empty();
    private Optional<ListenerHandler> listenerHandler = Optional.empty();
    private Optional<HelpPaginationHandler> helpPaginationHandler = Optional.empty();

    public XtraCorePluginContainerImpl(Object plugin, PluginContainer pluginContainer) {
        this.plugin = plugin;
        this.pluginContainer = pluginContainer;
    }

    @Override
    public Object getPlugin() {
        return this.plugin;
    }

    @Override
    public PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }
    
    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public Optional<CommandHandler> getCommandHandler() {
        return this.commandHandler;
    }

    @Override
    public Optional<ConfigHandler> getConfigHandler() {
        return this.configHandler;
    }

    @Override
    public Optional<ListenerHandler> getListenerHandler() {
        return this.listenerHandler;
    }

    @Override
    public Optional<HelpPaginationHandler> getHelpPaginationHandler() {
        return this.helpPaginationHandler;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = Optional.of(commandHandler);
    }

    public void setConfigHandler(ConfigHandler configHandler) {
        this.configHandler = Optional.of(configHandler);
    }

    public void setListenerHandler(ListenerHandler listenerHandler) {
        this.listenerHandler = Optional.of(listenerHandler);
    }

    public void setHelpPaginationHandler(HelpPaginationHandler helpPaginationHandler) {
        this.helpPaginationHandler = Optional.of(helpPaginationHandler);
    }
}
