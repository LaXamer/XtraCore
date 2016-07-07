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

package com.xtra.core.logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.xtra.api.logger.LogHandler;
import com.xtra.api.logger.Logger;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;

public class LogHandlerImpl implements LogHandler {

    private Map<Logger, XtraCorePluginContainer> loggers = new HashMap<>();

    public Logger create(XtraCorePluginContainer container) {
        // We know what we pass in will be an implementation
        XtraCorePluginContainerImpl impl = (XtraCorePluginContainerImpl) container;
        Logger logger = new LoggerImpl(impl);
        impl.setLogger(logger);
        loggers.put(logger, impl);
        return logger;
    }

    @Override
    public Optional<Logger> getLogger(Class<?> clazz) {
        for (Map.Entry<Logger, XtraCorePluginContainer> logger : this.loggers.entrySet()) {
            if (logger.getValue().getPlugin().getClass().equals(clazz)) {
                return Optional.of(logger.getKey());
            }
        }
        return Optional.empty();
    }
}
