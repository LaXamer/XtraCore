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

package io.github.narikiro.core;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.narikiro.api.ICore;
import io.github.narikiro.api.command.CommandHandler;
import io.github.narikiro.api.command.annotation.CommandAnnotationHelper;
import io.github.narikiro.api.config.ConfigHandler;
import io.github.narikiro.api.config.annotation.ConfigAnnotationHelper;
import io.github.narikiro.api.listener.ListenerHandler;
import io.github.narikiro.api.logger.LoggerHandler;
import io.github.narikiro.api.plugin.XtraCorePlugin;
import io.github.narikiro.api.plugin.XtraCorePluginContainer;
import io.github.narikiro.api.plugin.XtraCorePluginHandler;
import io.github.narikiro.api.registry.CommandRegistry;
import io.github.narikiro.api.registry.ConfigRegistry;
import io.github.narikiro.api.text.HelpPaginationHandler;
import io.github.narikiro.core.command.CommandHandlerImpl;
import io.github.narikiro.core.command.annotation.CommandAnnotationHelperImpl;
import io.github.narikiro.core.config.ConfigHandlerImpl;
import io.github.narikiro.core.config.annotation.ConfigAnnotationHelperImpl;
import io.github.narikiro.core.event.XtraCoreCommandHandlerInitializedEventImpl;
import io.github.narikiro.core.event.XtraCoreConfigHandlerInitializedEventImpl;
import io.github.narikiro.core.event.XtraCoreInitializedEventImpl;
import io.github.narikiro.core.event.XtraCoreListenerHandlerInitializedEventImpl;
import io.github.narikiro.core.event.XtraCorePluginInitializedEventImpl;
import io.github.narikiro.core.internal.Internals;
import io.github.narikiro.core.listener.ListenerHandlerImpl;
import io.github.narikiro.core.logger.LoggerHandlerImpl;
import io.github.narikiro.core.plugin.XtraCorePluginContainerImpl;
import io.github.narikiro.core.plugin.XtraCorePluginHandlerImpl;
import io.github.narikiro.core.registry.CommandRegistryImpl;
import io.github.narikiro.core.registry.ConfigRegistryImpl;
import io.github.narikiro.core.text.HelpPaginationHandlerImpl;
import io.github.narikiro.core.util.PluginInfo;
import io.github.narikiro.core.util.ReflectionScanner;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;

public class CoreImpl implements ICore {

    public static CoreImpl instance;
    private CommandAnnotationHelperImpl commandAnnotationHelper = new CommandAnnotationHelperImpl();
    private ConfigAnnotationHelperImpl configAnnotationHelper = new ConfigAnnotationHelperImpl();
    private XtraCorePluginHandlerImpl pluginHandler = new XtraCorePluginHandlerImpl();
    private CommandRegistryImpl commandRegistry = new CommandRegistryImpl();
    private ConfigRegistryImpl configRegistry = new ConfigRegistryImpl();
    private LoggerHandlerImpl loggerHandler = new LoggerHandlerImpl();

    public CoreImpl(XtraCore core) {
        // Initialize XtraCore stuff
        instance = this;
        XtraCorePluginContainerImpl containerImpl = this.pluginHandler.add(core);
        containerImpl.scanner = ReflectionScanner.create(containerImpl);

        this.loggerHandler.createGlobal();
        Internals.globalLogger.info(Internals.LOG_HEADER);
        Internals.globalLogger.info("Initializing XtraCore version " + PluginInfo.VERSION);
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
                    logger.info("Initializing with XtraCore version " + PluginInfo.VERSION + "!");

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
    public CommandAnnotationHelper getCommandAnnotationHelper() {
        return this.commandAnnotationHelper;
    }

    @Override
    public ConfigAnnotationHelper getConfigAnnotationHelper() {
        return this.configAnnotationHelper;
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
    public LoggerHandler getLoggerHandler() {
        return this.loggerHandler;
    }

    @Override
    public String getVersion() {
        return PluginInfo.VERSION;
    }
}
