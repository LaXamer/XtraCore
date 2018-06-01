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

package io.github.narikiro.core.text;

import java.util.Optional;

import org.spongepowered.api.text.Text;
import io.github.narikiro.api.command.Command;
import io.github.narikiro.api.text.ContentEntry;

public class ContentEntryImpl implements ContentEntry {

    private Command command;
    private Text completeText;
    private Text commandText;
    private Optional<Text> descriptionText;

    public ContentEntryImpl(Command command, Text completeText, Text commandText, Text descriptionText) {
        this.command = command;
        this.completeText = completeText;
        this.commandText = commandText;
        if (descriptionText != null) {
            this.descriptionText = Optional.of(descriptionText);
        } else {
            this.descriptionText = Optional.empty();
        }
    }

    @Override
    public Command getCommand() {
        return this.command;
    }

    @Override
    public Text getCompleteText() {
        return this.completeText;
    }

    @Override
    public Text getCommandText() {
        return this.commandText;
    }

    @Override
    public Optional<Text> getDescriptionText() {
        return this.descriptionText;
    }
}
