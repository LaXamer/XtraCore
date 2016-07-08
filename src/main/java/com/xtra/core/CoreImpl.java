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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.xtra.api.ICore;
import com.xtra.api.ban.BanHandler;
import com.xtra.api.command.CommandHandler;
import com.xtra.api.command.annotation.CommandAnnotationHelper;
import com.xtra.api.config.ConfigHandler;
import com.xtra.api.entity.EntityHandler;
import com.xtra.api.listener.ListenerHandler;
import com.xtra.api.logger.LogHandler;
import com.xtra.api.logger.Logger;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.api.plugin.XtraCorePluginHandler;
import com.xtra.api.registry.CommandRegistry;
import com.xtra.api.registry.ConfigRegistry;
import com.xtra.api.text.HelpPaginationHandler;
import com.xtra.api.world.direction.DirectionHandler;
import com.xtra.core.ban.BanHandlerImpl;
import com.xtra.core.command.CommandHandlerImpl;
import com.xtra.core.command.annotation.CommandAnnotationHelperImpl;
import com.xtra.core.config.ConfigHandlerImpl;
import com.xtra.core.entity.EntityHandlerImpl;
import com.xtra.core.internal.Internals;
import com.xtra.core.listener.ListenerHandlerImpl;
import com.xtra.core.logger.LogHandlerImpl;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;
import com.xtra.core.plugin.XtraCorePluginHandlerImpl;
import com.xtra.core.registry.CommandRegistryImpl;
import com.xtra.core.registry.ConfigRegistryImpl;
import com.xtra.core.text.HelpPaginationHandlerImpl;
import com.xtra.core.util.ReflectionScanner;
import com.xtra.core.world.direction.DirectionHandlerImpl;

public class CoreImpl implements ICore {

    public static CoreImpl instance;
    public Map<XtraCorePluginContainer, HelpPaginationHandler> paginationHandlers = new HashMap<>();
    private BanHandler banHandler = new BanHandlerImpl();
    private CommandAnnotationHelper annotationHelper = new CommandAnnotationHelperImpl();
    private EntityHandler entityHandler = new EntityHandlerImpl();
    private Map<XtraCorePluginContainer, ListenerHandler> listenerHandlers = new HashMap<>();
    private LogHandler logHandler = new LogHandlerImpl();
    private XtraCorePluginHandler pluginHandler = new XtraCorePluginHandlerImpl();
    private CommandRegistry commandRegistry = new CommandRegistryImpl();
    private ConfigRegistry configRegistry = new ConfigRegistryImpl();
    private DirectionHandler directionHandler = new DirectionHandlerImpl();

    public CoreImpl() {
        instance = this;
    }

    @Override
    public XtraCorePluginContainer initialize(Object plugin) {
        // Create a plugin container
        XtraCorePluginHandlerImpl handlerImpl = (XtraCorePluginHandlerImpl) this.pluginHandler;
        XtraCorePluginContainerImpl containerImpl = (XtraCorePluginContainerImpl) handlerImpl.add(plugin);
        LogHandlerImpl log = (LogHandlerImpl) this.logHandler;
        // Create a logger for the plugin
        Logger logger = log.create(containerImpl);
        logger.log("======================================================");
        logger.log("Initializing with XtraCore version " + Internals.VERSION + "!");

        Internals.globalLogger.log("======================================================");
        Internals.globalLogger.log("Initializing plugin class " + plugin.getClass().getName());

        containerImpl.scanner = ReflectionScanner.create(containerImpl);
        return containerImpl;
    }

    @Override
    public CommandHandler createCommandHandler(Class<?> clazz) {
        XtraCorePluginContainer container = this.pluginHandler.getContainer(clazz).get();
        if (container.getCommandHandler().isPresent()) {
            return container.getCommandHandler().get();
        }
        return CommandHandlerImpl.create(clazz);
    }

    @Override
    public Optional<CommandHandler> getCommandHandler(Class<?> clazz) {
        XtraCorePluginContainer container = this.pluginHandler.getContainer(clazz).get();
        if (container.getCommandHandler().isPresent()) {
            return Optional.of(container.getCommandHandler().get());
        }
        return Optional.empty();
    }

    @Override
    public ConfigHandler createConfigHandler(Class<?> clazz) {
        XtraCorePluginContainer container = this.pluginHandler.getContainer(clazz).get();
        if (container.getConfigHandler().isPresent()) {
            return container.getConfigHandler().get();
        }
        return ConfigHandlerImpl.create(clazz);
    }

    @Override
    public Optional<ConfigHandler> getConfigHandler(Class<?> clazz) {
        XtraCorePluginContainer container = this.pluginHandler.getContainer(clazz).get();
        if (container.getConfigHandler().isPresent()) {
            return Optional.of(container.getConfigHandler().get());
        }
        return Optional.empty();
    }

    @Override
    public ListenerHandler createListenerHandler(Class<?> clazz) {
        for (Map.Entry<XtraCorePluginContainer, ListenerHandler> listenerHandler : this.listenerHandlers.entrySet()) {
            if (listenerHandler.getKey().getPlugin().getClass().equals(clazz)) {
                return listenerHandler.getValue();
            }
        }
        ListenerHandlerImpl listenerHandler = new ListenerHandlerImpl();
        listenerHandler.registerListeners(clazz);
        return listenerHandler;
    }

    @Override
    public Optional<ListenerHandler> getListenerHandler(Class<?> clazz) {
        for (Map.Entry<XtraCorePluginContainer, ListenerHandler> listenerHandler : this.listenerHandlers.entrySet()) {
            if (listenerHandler.getKey().getPlugin().getClass().equals(clazz)) {
                return Optional.of(listenerHandler.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public HelpPaginationHandler.Builder createHelpPaginationBuilder(Object plugin) {
        HelpPaginationHandlerImpl impl = new HelpPaginationHandlerImpl();
        return impl.new Builder(impl, plugin);
    }

    @Override
    public Optional<HelpPaginationHandler> getHelpPaginationHandler(Class<?> clazz) {
        for (Map.Entry<XtraCorePluginContainer, HelpPaginationHandler> entry : this.paginationHandlers.entrySet()) {
            if (entry.getKey().getPlugin().getClass().equals(clazz)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public BanHandler getBanHandler() {
        return this.banHandler;
    }

    @Override
    public CommandAnnotationHelper getCommandAnnotationHelper() {
        return this.annotationHelper;
    }

    @Override
    public EntityHandler getEntityHandler() {
        return this.entityHandler;
    }

    @Override
    public LogHandler getLogHandler() {
        return this.logHandler;
    }

    @Override
    public XtraCorePluginHandler getPluginHandler() {
        return this.pluginHandler;
    }

    @Override
    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    @Override
    public ConfigRegistry getConfigRegistry() {
        return this.configRegistry;
    }

    @Override
    public DirectionHandler getDirectionHandler() {
        return this.directionHandler;
    }

    @Override
    public String getVersion() {
        return Internals.VERSION;
    }
}
