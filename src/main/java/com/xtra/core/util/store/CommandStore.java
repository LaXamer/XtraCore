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

package com.xtra.core.util.store;

import javax.annotation.Nullable;

import org.spongepowered.api.command.spec.CommandSpec;

import com.xtra.core.command.Command;

/**
 * Ties together a command and its appropriate command spec builder.
 */
public class CommandStore {

    private Command command;
    private CommandSpec.Builder commandSpecBuilder;
    private Command childOf;

    public CommandStore(Command commandBase, CommandSpec.Builder commandSpecBuilder, @Nullable Command childOf) {
        this.command = commandBase;
        this.commandSpecBuilder = commandSpecBuilder;
        this.childOf = childOf;
    }

    public Command command() {
        return command;
    }

    public CommandSpec.Builder commandSpecBuilder() {
        return commandSpecBuilder;
    }

    public Command childOf() {
        return childOf;
    }
}