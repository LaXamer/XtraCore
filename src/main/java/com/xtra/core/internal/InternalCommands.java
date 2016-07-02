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

package com.xtra.core.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.xtra.core.XtraCore;

public class InternalCommands {

    private static PaginationList info;

    public static void createCommands(XtraCore instance) {
        setInfo();

        CommandSpec versionCommand = CommandSpec.builder()
                .permission("xtracore.version")
                .description(Text.of("Displays various XtraCore info."))
                .executor((src, args) -> {
                    info.sendTo(src);
                    return CommandResult.success();
                })
                .build();

        CommandSpec mainCommand = CommandSpec.builder()
                .permission("xtracore.base")
                .description(Text.of("XtraCore base command."))
                .executor((src, args) -> {
                    info.sendTo(src);
                    return CommandResult.success();
                })
                .child(versionCommand, "version", "v")
                .build();
        
        Sponge.getCommandManager().register(instance, mainCommand, "xtracore", "xtra-core");
    }

    private static void setInfo() {
        String url = "https://github.com/XtraStudio/XtraCore";
        try {
            info = PaginationList.builder()
                    .padding(Text.of(TextColors.GOLD, "-="))
                    .title(Text.of(TextColors.GREEN, "XtraCore"))
                    .contents(Text.of(TextColors.BLUE, "Author: ", TextColors.GREEN, "12AwesomeMan34"),
                            Text.of(TextColors.BLUE, "Version: ", TextColors.GREEN, Internals.VERSION),
                            Text.of(TextColors.BLUE, "GitHub: ", TextColors.GREEN, TextActions.openUrl(new URL(url)), url))
                    .build();
        } catch (MalformedURLException e) {
            Internals.globalLogger.log(e);
        }
    }
}
