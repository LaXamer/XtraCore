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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.xtra.api.command.Command;
import com.xtra.api.command.annotation.RegisterCommand;
import com.xtra.api.plugin.XtraCorePluginContainer;

public class CommandGetter {

    public static Optional<Command> getCommand(String primaryAlias, Collection<Command> commands, XtraCorePluginContainer container) {
        Map<Command, XtraCorePluginContainer> map = new HashMap<>();
        for (Command command : commands) {
            map.put(command, container);
        }
        Optional<Map.Entry<Command, XtraCorePluginContainer>> optionalEntry = getEntry(primaryAlias, map);
        if (optionalEntry.isPresent()) {
            return Optional.of(optionalEntry.get().getKey());
        }
        return Optional.empty();
    }

    public static Optional<Map.Entry<Command, XtraCorePluginContainer>> getEntry(String primaryAlias, Map<Command, XtraCorePluginContainer> map) {
        checkNotNull(primaryAlias, "Primary alias cannot be null!");
        // If there is a dollar sign, we will need to check for child commands,
        // so the logic is a bit more complicated.
        if (primaryAlias.contains("$")) {
            String[] aliases = primaryAlias.split("\\$");
            // We only need the Command, so a regular Set is fine.
            Set<Command> parentCommandCandidates = new HashSet<>();
            // Here we need the Command and the container, so we'll create a new
            // map for these.
            Map<Command, XtraCorePluginContainer> childCommandCandidates = new HashMap<>();
            // Iterate through to find aliases for the command candidates.
            for (Map.Entry<Command, XtraCorePluginContainer> entry : map.entrySet()) {
                for (String alias : entry.getKey().aliases()) {
                    if (alias.equals(aliases[0])) {
                        parentCommandCandidates.add(entry.getKey());
                    }
                    if (alias.equals(aliases[1])) {
                        childCommandCandidates.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            // Now we'll iterate through the child command candidates.
            for (Map.Entry<Command, XtraCorePluginContainer> entry : childCommandCandidates.entrySet()) {
                Optional<Command> optionalCommand = Optional.empty();
                for (Command command : map.keySet()) {
                    if (entry.getKey().getClass().getAnnotation(RegisterCommand.class).childOf().isInstance(command)) {
                        optionalCommand = Optional.of(command);
                        break;
                    }
                }
                // So if we found the command object for the parent command,
                // continue. Else we end up returning Optional#empty().
                if (optionalCommand.isPresent()) {
                    Command command = optionalCommand.get();
                    for (Command parentCommand : parentCommandCandidates) {
                        // We'll need to see if any of the command's aliases
                        // match the parent command's aliases. If they do, then
                        // we've verified the parent command.
                        for (String alias : command.aliases()) {
                            for (String parentAlias : parentCommand.aliases()) {
                                if (alias.equals(parentAlias)) {
                                    return Optional.of(entry);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Else we can just check the first alias.
            for (Map.Entry<Command, XtraCorePluginContainer> entry : map.entrySet()) {
                // We will check all of the aliases.
                for (String alias : entry.getKey().aliases()) {
                    if (alias.equals(primaryAlias)) {
                        return Optional.of(entry);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
