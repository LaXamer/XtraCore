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

package com.xtra.core;

import java.util.Map;

import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCoreInternalPluginContainer;
import com.xtra.core.plugin.XtraCorePluginContainer;
import com.xtra.core.plugin.XtraCorePluginHandler;
import com.xtra.core.util.log.LogHandler;
import com.xtra.core.util.log.Logger;

public class Core {

    public static XtraCorePluginContainer initialize(Object plugin) {
        Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry = XtraCorePluginHandler.add(plugin);
        Logger logger = LogHandler.create(entry);
        logger.log("======================================================");
        logger.log("Initializing with XtraCore version " + Internals.VERSION + "!");

        Internals.globalLogger.log("======================================================");
        LogHandler.getGlobalLogger().log("Initializing plugin class " + plugin.getClass().getName());
        return entry.getKey();
    }

    /**
     * Gets the XtraCore version.
     * 
     * @return The XtraCore version
     */
    public static String version() {
        return Internals.VERSION;
    }
}
