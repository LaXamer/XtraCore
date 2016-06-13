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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xtra.core.command.Command;
import com.xtra.core.command.annotation.RegisterCommand;
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
        Internals.logger.log("Getting the child commands for the command: " + command.aliases()[0]);
        Set<Command> childCommands = new HashSet<>();
        for (Command cmd : Internals.commands) {
            Command parentCommand = getParentCommand(cmd);
            if (parentCommand != null && parentCommand.equals(command)) {
                Internals.logger.log("Child command found! Child command is: " + cmd.aliases()[0]);
                childCommands.add(cmd);
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
        Internals.logger.log("Getting the parent command for the command: '" + command.aliases()[0] + "'.");
        Class<? extends Command> parentCommand = command.getClass().getAnnotation(RegisterCommand.class).childOf();
        Command parentCommand2 = getEquivalentCommand(parentCommand);
        return parentCommand2;
    }

    /**
     * Gets the 'equivalent' command of this class. This is useful to prevent
     * many instances of the same object being passed around XtraCore.
     * 
     * @param clazz The class
     * @return The command object for the specified class
     */
    public static Command getEquivalentCommand(Class<? extends Command> clazz) {
        for (Command cmd : Internals.commands) {
            if (clazz.isInstance(cmd)) {
                return cmd;
            }
        }
        return null;
    }

    public static List<CommandStore> orderContents(Set<CommandStore> contentsStore, HelpPaginationGen.CommandOrdering ordering) {
        List<CommandStore> commandStore = new ArrayList<>();
        commandStore.addAll(contentsStore);
        if (ordering.equals(HelpPaginationGen.CommandOrdering.A_Z)) {
            Collections.sort(commandStore);
            return commandStore;
        }
        if (ordering.equals(HelpPaginationGen.CommandOrdering.Z_A)) {
            Collections.sort(commandStore);
            Collections.reverse(commandStore);
            return commandStore;
        }

        Set<Command> topCommands = new HashSet<>();
        for (CommandStore commandStore2 : commandStore) {
            if (commandStore2.childOf() != null) {
                if (ordering.equals(HelpPaginationGen.CommandOrdering.PARENT_COMMANDS_FIRST_A_Z)
                        || ordering.equals(HelpPaginationGen.CommandOrdering.PARENT_COMMANDS_FIRST_Z_A)) {
                    topCommands.add(commandStore2.childOf());
                } else if (ordering.equals(HelpPaginationGen.CommandOrdering.CHILD_COMMANDS_FIRST_A_Z)
                        || ordering.equals(HelpPaginationGen.CommandOrdering.PARENT_COMMANDS_FIRST_Z_A)) {
                    topCommands.add(commandStore2.command());
                } else {
                    topCommands.add(commandStore2.childOf());
                    topCommands.add(commandStore2.command());
                }
            }
        }
        // Get the command stores for these commands
        List<CommandStore> topCmds = new ArrayList<>();
        for (Command command : topCommands) {
            for (CommandStore commandStore2 : commandStore) {
                if (commandStore2.command().equals(command)) {
                    topCmds.add(commandStore2);
                }
            }
        }
        // Remove the top commands, we will append the contents store without
        // the top commands to the list later, as the top commands are to come
        // first in the list
        commandStore.removeAll(topCommands);

        // Sort them
        Collections.sort(topCmds);
        Collections.sort(commandStore);
        // If z-a, reverse the sorting
        if (ordering.equals(HelpPaginationGen.CommandOrdering.PARENT_COMMANDS_FIRST_Z_A)
                || ordering.equals(HelpPaginationGen.CommandOrdering.CHILD_COMMANDS_FIRST_Z_A)
                || ordering.equals(HelpPaginationGen.CommandOrdering.PARENT_AND_CHILD_FIRST_NON_LAST_Z_A)) {
            Collections.reverse(topCmds);
            Collections.reverse(commandStore);
        }
        // Append these to the end now
        topCmds.addAll(commandStore);
        return topCmds;
    }
}
