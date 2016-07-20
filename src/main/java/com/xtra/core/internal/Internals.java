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

import com.xtra.api.command.Command;
import com.xtra.api.config.Config;
import com.xtra.api.logger.Logger;
import com.xtra.api.plugin.XtraCorePluginContainer;

/**
 * This is an internal class for storing various information that should only
 * ever be accessed by XtraCore. It is recommended to NOT touch or access these
 * values directly (unless you're XtraCore itself)!
 */
public class Internals {

    public static final String VERSION = "@project.version@";
    public static final String DESCRIPTION = "A unifying plugin utility backend.";
    public static final String LOG_DIRECTORY = "/logs/xtracore-logs";
    public static Logger globalLogger;

    /**
     * Checks if the specified class has already been instantiated and if so
     * then returns its object. If not, then this will instantiate a new object
     * for the specified class.
     * 
     * @param container The container
     * @param clazz The class to check
     * @return The object if it has already been instantiated, otherwise a new
     *         instance of the specified class
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object checkIfAlreadyExists(XtraCorePluginContainer container, Class<?> clazz)
            throws InstantiationException, IllegalAccessException {
        if (container.getCommandHandler().isPresent()) {
            for (Command command : container.getCommandHandler().get().getCommands()) {
                if (clazz.equals(command.getClass())) {
                    return command;
                }
            }
        }
        if (container.getConfigHandler().isPresent()) {
            for (Config config : container.getConfigHandler().get().getConfigs()) {
                if (clazz.equals(config.getClass())) {
                    return config;
                }
            }
        }
        return clazz.newInstance();
    }
}
