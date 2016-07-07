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

import com.xtra.api.command.Command;
import com.xtra.api.command.annotation.CommandAnnotationHelper;
import com.xtra.api.command.annotation.RegisterCommand;
import com.xtra.api.util.command.EmptyCommand;
import com.xtra.core.CoreImpl;

/**
 * A helper class that gets information from any command annotations.
 */
public class CommandAnnotationHelperImpl implements CommandAnnotationHelper {

    @Override
    public boolean isAsync(Class<? extends Command> clazz) {
        return clazz.getAnnotation(RegisterCommand.class).async();
    }

    @Override
    public boolean hasParent(Class<? extends Command> clazz) {
        return clazz.getAnnotation(RegisterCommand.class).childOf() != EmptyCommand.class;
    }

    @Override
    public Optional<Class<? extends Command>> getParent(Class<? extends Command> clazz) {
        Class<? extends Command> parent = clazz.getAnnotation(RegisterCommand.class).childOf();
        if (!parent.equals(EmptyCommand.class)) {
            return Optional.of(parent);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Command> getParentObject(Class<? extends Command> clazz) {
        Optional<Class<? extends Command>> parentClass = getParent(clazz);
        if (parentClass.isPresent()) {
            return CoreImpl.instance.getCommandRegistry().getCommand(parentClass.get());
        }
        return Optional.empty();
    }
}
