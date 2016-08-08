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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import com.xtra.api.ICore;
import com.xtra.api.ban.BanHandler;
import com.xtra.api.command.CommandHandler;
import com.xtra.api.command.annotation.CommandAnnotationHelper;
import com.xtra.api.config.ConfigHandler;
import com.xtra.api.config.annotation.ConfigAnnotationHelper;
import com.xtra.api.entity.EntityHandler;
import com.xtra.api.listener.ListenerHandler;
import com.xtra.api.logger.LoggerHandler;
import com.xtra.api.plugin.XtraCorePlugin;
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
import com.xtra.core.config.annotation.ConfigAnnotationHelperImpl;
import com.xtra.core.entity.EntityHandlerImpl;
import com.xtra.core.event.XtraCoreCommandHandlerInitializedEventImpl;
import com.xtra.core.event.XtraCoreConfigHandlerInitializedEventImpl;
import com.xtra.core.event.XtraCoreInitializedEventImpl;
import com.xtra.core.event.XtraCoreListenerHandlerInitializedEventImpl;
import com.xtra.core.event.XtraCorePluginInitializedEventImpl;
import com.xtra.core.internal.Internals;
import com.xtra.core.listener.ListenerHandlerImpl;
import com.xtra.core.logger.LoggerHandlerImpl;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;
import com.xtra.core.plugin.XtraCorePluginHandlerImpl;
import com.xtra.core.registry.CommandRegistryImpl;
import com.xtra.core.registry.ConfigRegistryImpl;
import com.xtra.core.text.HelpPaginationHandlerImpl;
import com.xtra.core.util.ReflectionScanner;
import com.xtra.core.world.direction.DirectionHandlerImpl;

public class CoreImpl implements ICore {

    public static CoreImpl instance;
    private BanHandlerImpl banHandler = new BanHandlerImpl();
    private CommandAnnotationHelperImpl commandAnnotationHelper = new CommandAnnotationHelperImpl();
    private ConfigAnnotationHelperImpl configAnnotationHelper = new ConfigAnnotationHelperImpl();
    private EntityHandlerImpl entityHandler = new EntityHandlerImpl();
    private XtraCorePluginHandlerImpl pluginHandler = new XtraCorePluginHandlerImpl();
    private CommandRegistryImpl commandRegistry = new CommandRegistryImpl();
    private ConfigRegistryImpl configRegistry = new ConfigRegistryImpl();
    private DirectionHandlerImpl directionHandler = new DirectionHandlerImpl();
    private LoggerHandlerImpl loggerHandler = new LoggerHandlerImpl();

    public CoreImpl(XtraCore core) {
        // Initialize XtraCore stuff
        instance = this;
        XtraCorePluginContainerImpl containerImpl = this.pluginHandler.add(core);
        containerImpl.scanner = ReflectionScanner.create(containerImpl);

        this.loggerHandler.createGlobal();
        Internals.globalLogger.info(Internals.LOG_HEADER);
        Internals.globalLogger.info("Initializing XtraCore version " + Internals.VERSION);
        containerImpl.setLogger(Internals.globalLogger);

        ConfigHandler configHandler = ConfigHandlerImpl.create(containerImpl);
        containerImpl.setConfigHandler(configHandler);
        Sponge.getEventManager().post(new XtraCoreConfigHandlerInitializedEventImpl(containerImpl, configHandler));

        CommandHandler commandHandler = CommandHandlerImpl.create(containerImpl);
        containerImpl.setCommandHandler(commandHandler);
        Sponge.getEventManager().post(new XtraCoreCommandHandlerInitializedEventImpl(containerImpl, commandHandler));

        Sponge.getEventManager().post(new XtraCoreInitializedEventImpl(containerImpl));

        // Initialize XtraCore plugins
        for (PluginContainer container : Sponge.getPluginManager().getPlugins()) {
            if (container.getInstance().isPresent()) {
                Object instance = container.getInstance().get();
                XtraCorePlugin annotation = instance.getClass().getAnnotation(XtraCorePlugin.class);
                if (annotation != null) {
                    // Now we have an XtraCore plugin, so create an XtraCore
                    // plugin container.
                    XtraCorePluginContainerImpl pluginContainerImpl = this.pluginHandler.add(instance);
                    // Create a logger for the plugin
                    Logger logger = this.loggerHandler.create(pluginContainerImpl);
                    logger.info(Internals.LOG_HEADER);
                    logger.info("Initializing with XtraCore version " + Internals.VERSION + "!");

                    Internals.globalLogger.info(Internals.LOG_HEADER);
                    Internals.globalLogger.info("Initializing plugin class " + instance.getClass().getName());

                    pluginContainerImpl.scanner = ReflectionScanner.create(pluginContainerImpl);
                    Sponge.getEventManager().post(new XtraCorePluginInitializedEventImpl(pluginContainerImpl));

                    // Now initialize other XtraCore specific handlers, if they
                    // have not been disabled.
                    if (!annotation.disableConfigHandler()) {
                        ConfigHandler handler = ConfigHandlerImpl.create(pluginContainerImpl);
                        Sponge.getEventManager().post(new XtraCoreConfigHandlerInitializedEventImpl(pluginContainerImpl, handler));
                    }

                    if (!annotation.disableCommandHandler()) {
                        CommandHandler handler = CommandHandlerImpl.create(pluginContainerImpl);
                        Sponge.getEventManager().post(new XtraCoreCommandHandlerInitializedEventImpl(pluginContainerImpl, handler));
                    }

                    if (!annotation.disableListenerHandler()) {
                        ListenerHandlerImpl handler = new ListenerHandlerImpl(pluginContainerImpl);
                        Sponge.getEventManager().post(new XtraCoreListenerHandlerInitializedEventImpl(pluginContainerImpl, handler));
                    }
                }
            }
        }
    }

    @Override
    public Optional<CommandHandler> getCommandHandler(Class<?> clazz) {
        checkNotNull(clazz, "Plugin class cannot be null!");
        Optional<XtraCorePluginContainer> container = this.pluginHandler.getContainer(clazz);
        if (!container.isPresent()) {
            return Optional.empty();
        }
        return container.get().getCommandHandler();
    }

    @Override
    public Optional<ConfigHandler> getConfigHandler(Class<?> clazz) {
        checkNotNull(clazz, "Plugin class cannot be null!");
        Optional<XtraCorePluginContainer> container = this.pluginHandler.getContainer(clazz);
        if (!container.isPresent()) {
            return Optional.empty();
        }
        return container.get().getConfigHandler();
    }

    @Override
    public Optional<ListenerHandler> getListenerHandler(Class<?> clazz) {
        checkNotNull(clazz, "Plugin class cannot be null!");
        Optional<XtraCorePluginContainer> container = this.pluginHandler.getContainer(clazz);
        if (!container.isPresent()) {
            return Optional.empty();
        }
        return container.get().getListenerHandler();
    }

    @Override
    public HelpPaginationHandler.Builder createHelpPaginationBuilder(Class<?> clazz) {
        checkNotNull(clazz, "Plugin class cannot be null!");
        HelpPaginationHandlerImpl impl = new HelpPaginationHandlerImpl();
        return impl.new Builder(impl, clazz);
    }

    @Override
    public Optional<HelpPaginationHandler> getHelpPaginationHandler(Class<?> clazz) {
        checkNotNull(clazz, "Plugin class cannot be null!");
        Optional<XtraCorePluginContainer> container = this.pluginHandler.getContainer(clazz);
        if (!container.isPresent()) {
            return Optional.empty();
        }
        return container.get().getHelpPaginationHandler();
    }

    @Override
    public BanHandler getBanHandler() {
        return this.banHandler;
    }

    @Override
    public CommandAnnotationHelper getCommandAnnotationHelper() {
        return this.commandAnnotationHelper;
    }

    @Override
    public ConfigAnnotationHelper getConfigAnnotationHelper() {
        return this.configAnnotationHelper;
    }

    @Override
    public EntityHandler getEntityHandler() {
        return this.entityHandler;
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
    public LoggerHandler getLoggerHandler() {
        return this.loggerHandler;
    }

    @Override
    public String getVersion() {
        return Internals.VERSION;
    }
}
