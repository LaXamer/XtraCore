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

package io.github.narikiro.core.util.store;

import javax.annotation.Nullable;

import org.spongepowered.api.command.spec.CommandSpec;
import io.github.narikiro.api.command.Command;
import io.github.narikiro.api.command.state.CommandState;

/**
 * Ties together a command and its appropriate command spec builder.
 */
public class CommandStore implements Comparable<CommandStore> {

    private Command command;
    private CommandSpec.Builder commandSpecBuilder;
    private Command childOf;
    private CommandState state = CommandState.ENABLED;

    public CommandStore(Command commandBase, CommandSpec.Builder commandSpecBuilder, @Nullable Command childOf) {
        this.command = commandBase;
        this.commandSpecBuilder = commandSpecBuilder;
        this.childOf = childOf;
    }

    public Command command() {
        return this.command;
    }

    public CommandSpec.Builder commandSpecBuilder() {
        return this.commandSpecBuilder;
    }

    public Command childOf() {
        return this.childOf;
    }

    public CommandState state() {
        return this.state;
    }

    public void setState(CommandState state) {
        this.state = state;
    }

    @Override
    public int compareTo(CommandStore other) {
        return this.command.aliases()[0].compareTo(other.command.aliases()[0]);
    }
}
