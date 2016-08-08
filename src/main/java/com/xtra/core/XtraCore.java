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

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.xtra.api.Core;
import com.xtra.api.command.Command;
import com.xtra.api.command.annotation.RegisterCommand;
import com.xtra.api.command.base.CommandBase;
import com.xtra.api.command.base.CommandBaseLite;
import com.xtra.api.command.state.CommandState;
import com.xtra.api.config.Config;
import com.xtra.api.config.annotation.DoNotReload;
import com.xtra.api.config.base.ConfigBase;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.api.text.HelpPaginationHandler.ChildBehavior;
import com.xtra.api.util.command.EmptyCommand;
import com.xtra.core.command.base.CommandBaseImpl;
import com.xtra.core.command.base.CommandBaseLiteImpl;
import com.xtra.core.config.base.ConfigBaseImpl;
import com.xtra.core.internal.Internals;
import com.xtra.core.internal.config.CommandsConfig;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

@Plugin(name = "XtraCore", id = "xtracore", version = Internals.VERSION, authors = {"12AwsomeMan34"}, description = Internals.DESCRIPTION)
public class XtraCore {

    // Provide initial services to Core.
    @Listener(order = Order.FIRST)
    public void onPreInit(GameConstructionEvent event) {
        this.provideImplementations();
        this.loadCommandConfig();
    }

    // Now register internal XtraCore things during init. If plugins wish to
    // modify these, they may listen at a later order.
    @Listener
    public void onInit(GameInitializationEvent event) {
        CoreImpl.instance.createHelpPaginationBuilder(this.getClass()).childBehavior(ChildBehavior.IGNORE_PARENT).build();
    }

    // For automatic configuration reloading.
    @Listener
    public void onReload(GameReloadEvent event) {
        this.loadCommandConfig();
        for (Config config : CoreImpl.instance.getConfigRegistry().getAllConfigs()) {
            // If there is no DoNotReload annotation, then reload.
            if (config.getClass().getAnnotation(DoNotReload.class) == null) {
                config.load();
            }
        }
    }

    private void loadCommandConfig() {
        // Here we will initialize the command.conf file, for customizing the
        // commands.
        Config commandConfig = CoreImpl.instance.getConfigHandler(this.getClass()).get().getConfig(CommandsConfig.class).get();
        commandConfig.load();
        for (Map.Entry<Command, XtraCorePluginContainer> entry : CoreImpl.instance.getCommandRegistry().getAllCommandMappings().entrySet()) {
            // Get a potential parent command, as we will include that in the
            // config option
            Class<? extends Command> parentCommand = entry.getKey().getClass().getAnnotation(RegisterCommand.class).childOf();
            // Default to empty string if no parent
            String parentString = "";

            if (!parentCommand.equals(EmptyCommand.class)) {
                Optional<Command> optionalParent = CoreImpl.instance.getCommandRegistry().getCommand(parentCommand);
                if (optionalParent.isPresent()) {
                    parentString = optionalParent.get().aliases()[0] + "-";
                }
            }

            CommentedConfigurationNode node = commandConfig.rootNode().getNode(entry.getValue().getPluginContainer().getId())
                    .getNode(parentString + entry.getKey().aliases()[0]);
            if (node.isVirtual()) {
                node.setValue("ENABLED");
            } else {
                try {
                    CommandState state = CommandState.valueOf(node.getString());
                    entry.getValue().getCommandHandler().get().getCommandStateHandler().setState(entry.getKey().getClass(), state);
                } catch (IllegalArgumentException e) {
                    Internals.globalLogger.warn("Config node '" + entry.getKey().aliases()[0]
                            + "' in the commands.conf file has been set to an unknown '" + node.getString() + "' value! Defaulting to enabled!");
                    entry.getValue().getCommandHandler().get().getCommandStateHandler().setState(entry.getKey().getClass(), CommandState.ENABLED);
                }
            }
        }
        commandConfig.save();
    }

    private void provideImplementations() {
        try {
            FieldUtils.writeStaticField(CommandBase.class, "BASE", new CommandBaseImpl(), true);
            FieldUtils.writeStaticField(CommandBaseLite.class, "BASE", new CommandBaseLiteImpl(), true);
            FieldUtils.writeStaticField(ConfigBase.class, "BASE", new ConfigBaseImpl(), true);
            FieldUtils.writeStaticField(Core.class, "CORE", new CoreImpl(this), true);
        } catch (Exception e) {
            Internals.globalLogger.error("An error has occurred while attempting to set the static API fields!", e);
        }
    }
}
