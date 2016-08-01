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

package com.xtra.core.command.base;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.TextMessageException;

import com.xtra.api.command.annotation.RegisterCommand;
import com.xtra.api.command.base.CommandBaseLite;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.api.util.command.CommandBaseLiteExecutor;
import com.xtra.core.CoreImpl;

public class CommandBaseLiteImpl implements CommandBaseLiteExecutor {

    @Override
    public CommandResult execute(CommandBaseLite base, CommandSource source, CommandContext args) throws CommandException {
        XtraCorePluginContainer container = CoreImpl.instance.getCommandRegistry().getEntry(base.getClass()).get().getValue();
        if (base.getClass().getAnnotation(RegisterCommand.class).async()) {
            Sponge.getScheduler().createTaskBuilder().execute(
                    task -> {
                        try {
                            base.executeCommand(source, args);
                        } catch (TextMessageException e) {
                            source.sendMessage(e.getText());
                        } catch (Exception e2) {
                            source.sendMessage(Text.of(TextColors.RED, "An error has occurred while attempting to execute this command."));
                            container.getLogger()
                                    .error("An exception has occurred while attempting to execute the command " + base.aliases()[0] + "!", e2);
                        }
                    }).async().submit(container.getPlugin());
            return CommandResult.success();
        } else {
            try {
                return base.executeCommand(source, args);
            } catch (TextMessageException e) {
                source.sendMessage(e.getText());
            } catch (Exception e2) {
                source.sendMessage(Text.of(TextColors.RED, "An error has occurred while attempting to execute this command."));
                container.getLogger().error("An exception has occurred while attempting to execute the command " + base.aliases()[0] + "!", e2);
            }
        }
        return CommandResult.empty();
    }
}
