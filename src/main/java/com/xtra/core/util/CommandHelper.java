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

import org.spongepowered.api.text.Text;

import com.xtra.core.Core;
import com.xtra.core.command.Command;
import com.xtra.core.command.annotation.RegisterCommand;
import com.xtra.core.command.base.CommandBase;
import com.xtra.core.command.base.EmptyCommand;
import com.xtra.core.text.HelpPaginationGen;
import com.xtra.core.util.store.HelpContentsStore;

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
        for (Command cmd : Core.commands()) {
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
        for (CommandBase<?> cmd : Core.commands()) {
            if (Arrays.equals(command.aliases(), cmd.aliases()) && command.permission().equals(cmd.permission())
                    && command.description().equals(cmd.description()) && Arrays.equals(command.args(), cmd.args())) {
                return cmd;
            }
        }
        return null;
    }

    public static List<Text> orderContents(List<Text> contents, List<HelpContentsStore> contentsStore, HelpPaginationGen.CommandOrdering ordering) {
        switch (ordering) {
            case A_Z:
                Collections.sort(contents);
                return contents;
            case Z_A:
                Collections.sort(contents);
                Collections.reverse(contents);
                return contents;
            case PARENT_COMMANDS_FIRST_A_Z:
                return sortParentTypes(contentsStore, true);
            case PARENT_COMMANDS_FIRST_Z_A:
                return sortParentTypes(contentsStore, false);
            case CHILD_COMMANDS_FIRST_A_Z:
                return sortChildTypes(contentsStore, true);
            case CHILD_COMMANDS_FIRST_Z_A:
                return sortChildTypes(contentsStore, false);
            case DEFAULT:
                return contents;
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
    private static List<Text> sortParentTypes(List<HelpContentsStore> contentsStore, boolean a_z) {
        // HashSet in case we get duplication with these methods
        Set<Text> parentCommands = new HashSet<>();
        Set<HelpContentsStore> nonParentCommands = new HashSet<>();
        // Add these as we remove the parent commands later
        nonParentCommands.addAll(contentsStore);
        for (HelpContentsStore contentStore : contentsStore) {
            Command cmd = getParentCommand(contentStore.command());
            if (cmd != null) {
                parentCommands.add(Text.of(cmd.aliases()[0]));
                nonParentCommands.remove(cmd);
            }
        }

        // Convert back to list to be sorted
        List<Text> parentCmds = new ArrayList<>();
        List<Text> nonParentCmds = new ArrayList<>();
        parentCmds.addAll(parentCommands);

        // Get the aliases for the non parent commands
        for (HelpContentsStore nonParentCommand : nonParentCommands) {
            nonParentCmds.add(Text.of(nonParentCommand.command().aliases()[0]));
        }
        Collections.sort(parentCmds);
        Collections.sort(nonParentCmds);
        if (!a_z) {
            Collections.reverse(parentCmds);
            Collections.reverse(nonParentCmds);
        }
        // Append these to the end now
        parentCmds.addAll(nonParentCmds);
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
    private static List<Text> sortChildTypes(List<HelpContentsStore> contentsStore, boolean a_z) {
        // HashSet in case we get duplication with these methods
        Set<Text> childCommands = new HashSet<>();
        Set<HelpContentsStore> nonChildCommands = new HashSet<>();
        nonChildCommands.addAll(contentsStore);
        for (HelpContentsStore contentStore : contentsStore) {
            Set<Command> cmds = getChildCommands(contentStore.command());
            for (Command command : cmds) {
                childCommands.add(Text.of(command.aliases()[0]));
                // We can ignore commands that already have been removed
                nonChildCommands.removeAll(cmds);
            }
        }

        List<Text> childCmds = new ArrayList<>();
        List<Text> nonChildCmds = new ArrayList<>();
        childCmds.addAll(childCommands);

        for (HelpContentsStore nonChildCommand : nonChildCommands) {
            nonChildCmds.add(Text.of(nonChildCommand.command().aliases()[0]));
        }
        Collections.sort(childCmds);
        Collections.sort(nonChildCmds);
        if (!a_z) {
            Collections.reverse(childCmds);
            Collections.reverse(nonChildCmds);
        }
        childCmds.addAll(nonChildCmds);
        return childCmds;
    }
}
