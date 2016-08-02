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

package com.xtra.core.internal.command;

import java.net.MalformedURLException;
import java.net.URL;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.xtra.api.command.annotation.RegisterCommand;
import com.xtra.api.command.base.CommandBase;
import com.xtra.core.internal.Internals;

@RegisterCommand(childOf = XtraCoreCommand.class)
public class VersionCommand extends CommandBase<CommandSource> {

    private PaginationList info;

    public VersionCommand() {
        String url = "https://github.com/XtraStudio/XtraCore";
        try {
            this.info = PaginationList.builder()
                    .padding(Text.of(TextColors.GOLD, "-="))
                    .title(Text.of(TextColors.GREEN, "XtraCore"))
                    .contents(Text.of(TextColors.BLUE, "Author: ", TextColors.GREEN, "12AwesomeMan34"),
                            Text.of(TextColors.BLUE, "Version: ", TextColors.GREEN, Internals.VERSION),
                            Text.of(TextColors.BLUE, "GitHub: ", TextColors.GREEN, TextActions.openUrl(new URL(url)), url))
                    .build();
        } catch (MalformedURLException e) {
            Internals.globalLogger.error("A MalformedURLException has occured while attempting to construct a URL!", e);
        }
    }

    @Override
    public String[] aliases() {
        return new String[] {"version", "v"};
    }

    @Override
    public String permission() {
        return "xtracore.version";
    }

    @Override
    public String description() {
        return "Displays various XtraCore info.";
    }

    @Override
    public CommandElement[] args() {
        return null;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        info.sendTo(src);
        return CommandResult.success();
    }
}
