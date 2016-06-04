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

package com.xtra.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xtra.core.command.Command;
import com.xtra.core.command.annotation.RegisterCommand;
import com.xtra.core.command.base.CommandBase;
import com.xtra.core.command.base.EmptyCommand;
import com.xtra.core.internal.Internals;
import com.xtra.core.text.HelpPaginationGen;
import com.xtra.core.util.store.CommandStore;

/**
 * A class that contains utility methods about commands.
 */
public class CommandHelper {

    /**
     * A convenience method for getting all of the child commands of the
     * specified command.
     * 
     * @param command The command to get children of
     * @return The child commands of the specified command
     */
    public static Set<Command> getChildCommands(Command command) {
        Set<Command> childCommands = new HashSet<>();
        for (Command cmd : Internals.commands) {
            try {
                Class<? extends Command> parentCommand = cmd.getClass().getAnnotation(RegisterCommand.class).childOf();
                Command parentCommand2 = parentCommand.newInstance();
                if (!(parentCommand2 instanceof EmptyCommand)) {
                    if (command.equals(getEquivalentCommand(parentCommand2))) {
                        childCommands.add(cmd);
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return childCommands;
    }

    /**
     * Gets the parent command of this command, or null if one isn't specified.
     * 
     * @param command The child command to get the parent of
     * @return The parent command
     */
    public static Command getParentCommand(Command command) {
        try {
            Class<? extends Command> parentCommand = command.getClass().getAnnotation(RegisterCommand.class).childOf();
            Command parentCommand2 = parentCommand.newInstance();
            if (!(parentCommand2 instanceof EmptyCommand)) {
                return getEquivalentCommand(parentCommand2);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Since calling {@link Class#newInstance()} isn't reliable for a proper
     * equals check, we need to match up the command properties such as the
     * aliases, description, etc. This way we can get the correct
     * {@link Command}.
     * 
     * @param command The command to get the equivalent of
     * @return The correct command for an equals check
     */
    private static Command getEquivalentCommand(Command command) {
        for (CommandBase<?> cmd : Internals.commands) {
            if (Arrays.equals(command.aliases(), cmd.aliases()) && command.permission().equals(cmd.permission())
                    && command.description().equals(cmd.description()) && Arrays.equals(command.args(), cmd.args())) {
                return cmd;
            }
        }
        return null;
    }

    public static List<CommandStore> orderContents(Set<CommandStore> contentsStore, HelpPaginationGen.CommandOrdering ordering) {
        List<CommandStore> commandStore = new ArrayList<>();
        commandStore.addAll(contentsStore);
        switch (ordering) {
            case A_Z:
                Collections.sort(commandStore);
                return commandStore;
            case Z_A:
                Collections.sort(commandStore);
                Collections.reverse(commandStore);
                return commandStore;
            case PARENT_COMMANDS_FIRST_A_Z:
                return sortParentTypes(commandStore, true);
            case PARENT_COMMANDS_FIRST_Z_A:
                return sortParentTypes(commandStore, false);
            case CHILD_COMMANDS_FIRST_A_Z:
                return sortChildTypes(commandStore, true);
            case CHILD_COMMANDS_FIRST_Z_A:
                return sortChildTypes(commandStore, false);
            case PARENT_AND_CHILD_FIRST_NON_LAST_A_Z:
                return sortParentAndChildThenNonTypes(commandStore, true);
            case PARENT_AND_CHILD_FIRST_NON_LAST_Z_A:
                return sortParentAndChildThenNonTypes(commandStore, false);
            case DEFAULT:
                return commandStore;
        }
        return null;
    }

    /**
     * Sorts the parent command types either a-z or z-a. Then appends the
     * non-parent commands.
     * 
     * @param contentsStore The contents store
     * @param a_z If these should be sorted a-z, or z-a
     * @return The sorted help list
     */
    private static List<CommandStore> sortParentTypes(List<CommandStore> contentsStore, boolean a_z) {
        Set<Command> parentCommands = new HashSet<>();
        for (CommandStore commandStore : contentsStore) {
            if (commandStore.childOf() != null) {
                parentCommands.add(commandStore.childOf());
            }
        }
        // Get the command stores for these parent commands
        List<CommandStore> parentCmds = new ArrayList<>();
        for (Command command : parentCommands) {
            for (CommandStore commandStore : contentsStore) {
                if (commandStore.command().equals(command)) {
                    parentCmds.add(commandStore);
                }
            }
        }
        // Remove the parent commands, so they will be at the top of the list
        contentsStore.removeAll(parentCommands);

        Collections.sort(parentCmds);
        Collections.sort(contentsStore);
        if (!a_z) {
            Collections.reverse(parentCmds);
            Collections.reverse(contentsStore);
        }
        // Append these to the end now
        parentCmds.addAll(contentsStore);
        return parentCmds;
    }

    /**
     * Sorts the child command types either a-z or z-a. Then appends the
     * non-child commands.
     * 
     * @param contentsStore The contents store
     * @param a_z If these should be sorted a-z, or z-a
     * @return The sorted help list
     */
    private static List<CommandStore> sortChildTypes(List<CommandStore> contentsStore, boolean a_z) {
        Set<Command> childCommands = new HashSet<>();
        for (CommandStore commandStore : contentsStore) {
            if (commandStore.childOf() != null) {
                childCommands.add(commandStore.command());
            }
        }
        // Get the command stores for these child commands
        List<CommandStore> childCmds = new ArrayList<>();
        for (Command command : childCommands) {
            for (CommandStore commandStore : contentsStore) {
                if (commandStore.command().equals(command)) {
                    childCmds.add(commandStore);
                }
            }
        }
        // Remove the child commands, so they will be at the top of the list
        contentsStore.removeAll(childCmds);

        Collections.sort(childCmds);
        Collections.sort(contentsStore);
        if (!a_z) {
            Collections.reverse(childCmds);
            Collections.reverse(contentsStore);
        }
        // Append these to the end now
        childCmds.addAll(contentsStore);
        return childCmds;
    }

    /**
     * Sort the commands with parent commands and their children first in the
     * list, followed by other parent commands and their children. At the end,
     * is a sorted (a-z, or z-a) list of non-parent and non-child commands.
     * 
     * @param contentsStore The contents store
     * @param a_z If these should be sorted a-z, or z-a
     * @return The sorted help list
     */
    private static List<CommandStore> sortParentAndChildThenNonTypes(List<CommandStore> contentsStore, boolean a_z) {
        Set<Command> parentAndChildCommands = new HashSet<>();
        for (CommandStore commandStore : contentsStore) {
            if (commandStore.childOf() != null) {
                // These will automatically sort anyway once we call
                // Collections#sort later on
                parentAndChildCommands.add(commandStore.command());
                parentAndChildCommands.add(commandStore.childOf());
            }
        }
        // Get the command stores for these child commands
        List<CommandStore> parentAndChildCmds = new ArrayList<>();
        for (Command command : parentAndChildCommands) {
            for (CommandStore commandStore : contentsStore) {
                if (commandStore.command().equals(command)) {
                    parentAndChildCmds.add(commandStore);
                }
            }
        }
        // Remove the child commands, so they will be at the top of the list
        contentsStore.removeAll(parentAndChildCmds);

        Collections.sort(parentAndChildCmds);
        Collections.sort(contentsStore);
        if (!a_z) {
            Collections.reverse(parentAndChildCmds);
            Collections.reverse(contentsStore);
        }
        // Append these to the end now
        parentAndChildCmds.addAll(contentsStore);
        return parentAndChildCmds;
    }
}
