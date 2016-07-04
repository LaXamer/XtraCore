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

package com.xtra.core.command.annotation;

import java.util.Optional;

import com.xtra.core.command.Command;
import com.xtra.core.command.base.EmptyCommand;
import com.xtra.core.registry.CommandRegistry;

/**
 * A helper class that gets information from any command annotations.
 */
public class CommandAnnotationInfo {

    /**
     * Returns if the command is an async command.
     * 
     * @param clazz The command class to check
     * @return If the command is async
     */
    public static boolean isAsync(Class<? extends Command> clazz) {
        return clazz.getAnnotation(RegisterCommand.class).async();
    }

    /**
     * Gets the parent command of the specified command.
     * 
     * @param clazz The command class to check
     * @return The parent command or {@link Optional#empty()} if the specified
     *         command does not have a parent command
     */
    public static Optional<Class<? extends Command>> getParent(Class<? extends Command> clazz) {
        Class<? extends Command> parent = clazz.getAnnotation(RegisterCommand.class).childOf();
        if (!parent.equals(EmptyCommand.class)) {
            return Optional.of(parent);
        }
        return Optional.empty();
    }

    /**
     * Gets the parent command object of the specified command.
     * 
     * @param clazz The command class to check
     * @return The parent command object or {@link Optional#empty()} if the
     *         specified command does not have a parent command, or if the
     *         parent command could not be found.
     */
    public static Optional<Command> getParentObject(Class<? extends Command> clazz) {
        Optional<Class<? extends Command>> parentClass = getParent(clazz);
        if (parentClass.isPresent()) {
            return CommandRegistry.getCommand(parentClass.get());
        }
        return Optional.empty();
    }
}
