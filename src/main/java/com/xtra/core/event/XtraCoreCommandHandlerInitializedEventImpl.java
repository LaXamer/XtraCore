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

package com.xtra.core.event;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;

import com.xtra.api.command.CommandHandler;
import com.xtra.api.event.XtraCoreCommandHandlerInitializedEvent;
import com.xtra.api.plugin.XtraCorePluginContainer;

public class XtraCoreCommandHandlerInitializedEventImpl extends XtraCorePluginContainerEventImpl
        implements XtraCoreCommandHandlerInitializedEvent {

    private CommandHandler handler;
    private Cause cause;

    public XtraCoreCommandHandlerInitializedEventImpl(XtraCorePluginContainer container, CommandHandler handler) {
        super(container);
        this.handler = handler;
        this.cause = Cause.of(NamedCause.owner(container), NamedCause.source(handler));
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public CommandHandler getCommandHandler() {
        return this.handler;
    }
}
