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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

import com.xtra.api.logger.Logger;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;

public class LoggerImpl implements Logger {

    private File logFile;
    private XtraCorePluginContainerImpl container;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private String lineSeparator = System.getProperty("line.separator");

    public LoggerImpl(XtraCorePluginContainerImpl container) {
        try {
            this.container = container;
            File directory = new File(System.getProperty("user.dir") + Internals.LOG_DIRECTORY);
            this.logFile = new File(directory + "/" + container.getPluginContainer().getId() + ".log");
            if (!logFile.exists()) {
                logFile.createNewFile();
            } else {
                FileUtils.writeStringToFile(logFile, "", StandardCharsets.UTF_8, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a global XtraCore logger.
     */
    public LoggerImpl() {
        try {
            File directory = new File(System.getProperty("user.dir") + Internals.LOG_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            this.logFile = new File(directory + "/xtracore.log");
            if (!logFile.exists()) {
                logFile.createNewFile();
            } else {
                FileUtils.writeStringToFile(logFile, "", StandardCharsets.UTF_8, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(Level level, String message) {
        try {
            FileUtils.writeStringToFile(this.logFile,
                    "[" + this.format.format(new Date()) + "] " + "[" + level + "]: " + message + this.lineSeparator, StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (level.equals(Level.WARNING)) {
            if (container != null) {
                this.container.getPluginContainer().getLogger().warn(message);
            } else {
                Sponge.getServer().getConsole().sendMessage(Text.of(message));
            }
        } else if (level.equals(Level.ERROR)) {
            if (container != null) {
                this.container.getPluginContainer().getLogger().error(message);
            } else {
                Sponge.getServer().getConsole().sendMessage(Text.of(message));
            }
        }
    }

    @Override
    public void log(Level level, Throwable cause) {
        String stackTrace = ExceptionUtils.getStackTrace(cause);
        try {
            FileUtils.writeStringToFile(this.logFile,
                    "[" + this.format.format(new Date()) + "] " + "[" + level + "]: " + cause.getMessage() + this.lineSeparator,
                    StandardCharsets.UTF_8, true);
            FileUtils.writeStringToFile(this.logFile, stackTrace, StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (level.equals(Level.WARNING)) {
            if (container != null) {
                this.container.getPluginContainer().getLogger().warn(cause.getMessage());
            } else {
                Sponge.getServer().getConsole().sendMessage(Text.of(cause.getMessage()));
            }
        } else if (level.equals(Level.ERROR)) {
            if (container != null) {
                this.container.getPluginContainer().getLogger().error(cause.getMessage());
                this.container.getPluginContainer().getLogger().error(stackTrace);
            } else {
                Sponge.getServer().getConsole().sendMessage(Text.of(cause.getMessage()));
                Sponge.getServer().getConsole().sendMessage(Text.of(stackTrace));
            }
        }
    }
}
