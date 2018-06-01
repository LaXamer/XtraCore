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

package io.github.narikiro.core.internal.config;

import java.util.Map;
import java.util.Optional;

import io.github.narikiro.api.command.Command;
import io.github.narikiro.api.command.annotation.RegisterCommand;
import io.github.narikiro.api.command.state.CommandState;
import io.github.narikiro.api.config.Config;
import io.github.narikiro.api.plugin.XtraCorePluginContainer;
import io.github.narikiro.api.util.command.EmptyCommand;
import io.github.narikiro.core.CoreImpl;
import io.github.narikiro.core.XtraCore;
import io.github.narikiro.core.internal.Internals;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class ConfigChecker {

    public static void commandConfig() {
        // Here we will initialize the command.conf file, for customizing the
        // commands.
        Config commandConfig = CoreImpl.instance.getConfigHandler(XtraCore.class).get().getConfig(CommandsConfig.class).get();
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
                    parentString = optionalParent.get().aliases()[0] + "$";
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
}
