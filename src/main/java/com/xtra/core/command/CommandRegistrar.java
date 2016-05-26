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

import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandRegistrar {

    private Object plugin;
    private Set<CommandStore> commands = new HashSet<>();
    private Set<CommandStore> parentCommands = new HashSet<>();

    public CommandRegistrar(Object plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        Set<CommandBase<?>> commands = getCommands();
        for (CommandBase<?> command : commands) {
            initializeCommandSpec(command);
        }
        addChildCommands();
        for (CommandStore command : this.commands) {
            buildAndRegisterCommand(command.commandSpecBuilder(), command.command());
        }
    }

    private Set<CommandBase<?>> getCommands() {
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName(), new SubTypesScanner(false), new TypeAnnotationsScanner());
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RegisterCommand.class);
        Set<CommandBase<?>> commands = new HashSet<>();
        for (Class<?> oneClass : classes) {
            try {
                Object o = oneClass.newInstance();
                if (o instanceof CommandBase<?>) {
                    commands.add((CommandBase<?>) o);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return commands;
    }

    private void initializeCommandSpec(CommandBase<?> command) {
        CommandSpec.Builder specBuilder = CommandSpec.builder()
                .permission(command.permission())
                .description(Text.of(command.description()))
                .executor(command);
        if (command.args().length != 0) {
            specBuilder.arguments(command.args());
        }
        try {
            Class<? extends Command> parentCommand = command.getClass().getAnnotation(RegisterCommand.class).childOf();
            Command parentCommand2 = parentCommand.newInstance();
            if (!(parentCommand2 instanceof EmptyCommand)) {
                parentCommands.add(new CommandStore(parentCommand2, command, specBuilder));
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        commands.add(new CommandStore(command, command, specBuilder));
    }
    
    private void addChildCommands() {
        for (CommandStore parentStore : parentCommands) {
            for (CommandStore commandStore : commands) {
                Class<? extends Command> parentCommand = commandStore.commandBase().getClass().getAnnotation(RegisterCommand.class).childOf();
                try {
                    Command parentCommand2 = parentCommand.newInstance();
                    if (!(parentCommand2 instanceof EmptyCommand)) {
                        if (parentCommand2.equals(parentStore.command())) {
                            parentStore.commandSpecBuilder().child(commandStore.commandSpecBuilder().build(), commandStore.command().aliases());
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Builds a {@link CommandSpec} and registers it.
     * 
     * @param commandSpec The command spec to build and register
     * @param command The command
     */
    private void buildAndRegisterCommand(CommandSpec.Builder commandSpec, Command command) {
        Sponge.getCommandManager().register(plugin, commandSpec.build(), command.aliases());
    }
}
