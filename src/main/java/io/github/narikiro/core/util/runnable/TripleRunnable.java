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

package io.github.narikiro.core.util.runnable;

import io.github.narikiro.api.command.annotation.RunAt;
import io.github.narikiro.api.command.runnable.CommandRunnable;

// Because Integer is final
@SuppressWarnings("all")
public class TripleRunnable<T extends CommandRunnable, U extends RunAt, V extends Integer>
        implements Comparable<TripleRunnable<CommandRunnable, RunAt, Integer>> {

    private CommandRunnable commandRunnable;
    private RunAt runAt;
    private Integer priority;

    public TripleRunnable(CommandRunnable a, RunAt b, Integer c) {
        this.commandRunnable = a;
        this.runAt = b;
        this.priority = c;
    }

    public CommandRunnable getCommandRunnable() {
        return commandRunnable;
    }

    public RunAt getRunAt() {
        return runAt;
    }

    public Integer getPriority() {
        return priority;
    }

    @Override
    public int compareTo(TripleRunnable<CommandRunnable, RunAt, Integer> o) {
        if (this.priority > o.priority) {
            return 1;
        } else if (this.priority < o.priority) {
            return -1;
        }
        return 0;
    }
}
