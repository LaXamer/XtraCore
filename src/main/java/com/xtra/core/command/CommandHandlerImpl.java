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

package com.xtra.core.command;

import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.xtra.api.command.Command;
import com.xtra.api.command.CommandHandler;
import com.xtra.api.command.annotation.RegisterCommand;
import com.xtra.api.command.runnable.CommandRunnableHandler;
import com.xtra.api.command.state.CommandStateHandler;
import com.xtra.api.util.command.EmptyCommand;
import com.xtra.core.CoreImpl;
import com.xtra.core.command.runnable.CommandRunnableHandlerImpl;
import com.xtra.core.command.state.CommandStateHandlerImpl;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;
import com.xtra.core.registry.CommandRegistryImpl;
import com.xtra.core.util.CommandHelper;
import com.xtra.core.util.store.CommandStore;

/**
 * A simple utility class for automatically retrieving, building and registering
 * the commands within a plugin.
 */
public class CommandHandlerImpl implements CommandHandler {

    private XtraCorePluginContainerImpl container;
    private Set<Command> commands;
    private CommandHelper helper;
    private CommandRunnableHandler runnableHandler;
    private CommandStateHandler stateHandler;

    private CommandHandlerImpl() {
    }

    public static CommandHandlerImpl create(Class<?> clazz) {
        return new CommandHandlerImpl()
                .init((XtraCorePluginContainerImpl) CoreImpl.instance.getPluginHandler().getContainerUnchecked(clazz));
    }

    private CommandHandlerImpl init(XtraCorePluginContainerImpl entry) {
        this.container = entry;
        Internals.globalLogger.info(Internals.LOG_HEADER);
        Internals.globalLogger.info("Initializing command handler for " + entry.getPluginContainer().getName());

        this.commands = this.container.scanner.getCommands();
        this.container.setCommandHandler(this);
        this.helper = new CommandHelper(this.container);
        this.container.getLogger().info(Internals.LOG_HEADER);
        this.container.getLogger().info("Initializing the command handler!");
        this.container.getLogger().info("Initializing the command specs for the commands...");

        for (Command command : this.commands) {
            this.initializeCommandSpec(command);
        }
        this.container.getLogger().info(Internals.LOG_HEADER);
        this.container.getLogger().info("Adding any necessary child commands to the command specs!");
        this.addChildCommands();
        this.container.getLogger().info(Internals.LOG_HEADER);
        this.container.getLogger().info("Building and registering the commands!");
        for (CommandStore command : this.container.commandStores) {
            this.buildAndRegisterCommand(command.commandSpecBuilder(), command.command());
            CommandRegistryImpl commandImpl = (CommandRegistryImpl) CoreImpl.instance.getCommandRegistry();
            commandImpl.add(command.command(), this.container);
        }

        this.runnableHandler = CommandRunnableHandlerImpl.create(entry);
        this.stateHandler = CommandStateHandlerImpl.create(entry);
        return this;
    }

    private void initializeCommandSpec(Command command) {
        // Create the initial CommandSpec builder
        CommandSpec.Builder specBuilder = CommandSpec.builder().executor(command);

        this.container.getLogger().info(Internals.LOG_HEADER);
        this.container.getLogger().info("Initializing the command spec for the command '" + command.aliases()[0] + "'.");

        // In case null, do not use
        if (command.permission() != null) {
            specBuilder.permission(command.permission());
            this.container.getLogger().info("Command permission: '" + command.permission() + "'");
        }
        if (command.description() != null) {
            specBuilder.description(Text.of(command.description()));
            this.container.getLogger().info("Command description: '" + command.description() + "'");
        }
        if (command.args() != null) {
            if (command.args().length != 0) {
                specBuilder.arguments(command.args());
                this.container.getLogger().info("Command has " + command.args().length + " argument(s).");
            }
        }

        Command parentCommand = this.helper.getParentCommand(command);
        if (parentCommand != null) {
            this.container.getLogger().info("Adding the command and its parent command to the command stores.");
            this.container.commandStores.add(new CommandStore(command, specBuilder, parentCommand));
        } else {
            this.container.getLogger().info("Parent command not found. Presuming command does not have one.");
            this.container.commandStores.add(new CommandStore(command, specBuilder, null));
        }
    }

    private void addChildCommands() {
        // Go through the commands to find any child commands
        for (CommandStore commandStore : this.container.commandStores) {
            if (commandStore.childOf() != null) {
                if (!(commandStore.childOf() instanceof EmptyCommand)) {
                    // Iterate through to find the parent
                    for (CommandStore commandStore2 : this.container.commandStores) {
                        if (commandStore2.command().equals(commandStore.childOf())) {
                            this.container.getLogger().info("Adding '" + commandStore.command().aliases()[0] + "' as a child command of '"
                                    + commandStore2.command().aliases()[0] + "'");
                            commandStore2.commandSpecBuilder().child(commandStore.commandSpecBuilder().build(), commandStore.command().aliases());
                        }
                    }
                }
            }
        }
    }

    private void buildAndRegisterCommand(CommandSpec.Builder commandSpec, Command command) {
        if (command.getClass().getAnnotation(RegisterCommand.class).childOf().equals(EmptyCommand.class)) {
            this.container.getLogger().info("Building and registering the command: '" + command.aliases()[0] + "'");
            Sponge.getCommandManager().register(this.container.getPlugin(), commandSpec.build(), command.aliases());
        }
    }

    @Override
    public Optional<Command> getCommand(Class<? extends Command> clazz) {
        for (Command command : this.commands) {
            if (clazz.isInstance(command)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Command> getCommands() {
        return this.commands;
    }

    @Override
    public CommandRunnableHandler getCommandRunnableHandler() {
        return this.runnableHandler;
    }

    @Override
    public CommandStateHandler getCommandStateHandler() {
        return this.stateHandler;
    }
}
