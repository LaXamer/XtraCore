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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtra.api.logger.LoggerHandler;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.core.CoreImpl;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;

public class LoggerHandlerImpl implements LoggerHandler {

    public LoggerHandlerImpl() {
        try {
            if (Files.notExists(Internals.LOG_DIRECTORY)) {
                Files.createDirectories(Internals.LOG_DIRECTORY);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Logger> getLogger(Class<?> clazz) {
        checkNotNull(clazz, "Logger class cannot be null!");
        Optional<XtraCorePluginContainer> container = CoreImpl.instance.getPluginHandler().getContainer(clazz);
        if (container.isPresent()) {
            return Optional.of(container.get().getLogger());
        } else {
            return Optional.empty();
        }
    }

    public Logger create(XtraCorePluginContainer container) {
        XtraCorePluginContainerImpl impl = (XtraCorePluginContainerImpl) container;
        String name = "XtraCore-" + container.getPluginContainer().getName();
        Path logPath = Paths.get(Internals.LOG_DIRECTORY.toString(), container.getPluginContainer().getId() + ".log");
        this.checkExists(logPath);
        this.handleAppenders(logPath, name, container.getPluginContainer().getId());

        Logger logger = LoggerFactory.getLogger("XtraCore-" + name);
        impl.setLogger(logger);
        return logger;
    }

    public void createGlobal() {
        Path logPath = Paths.get(Internals.LOG_DIRECTORY.toString(), "xtracore.log");
        this.checkExists(logPath);
        this.handleAppenders(logPath, "XtraCoreLogger", "xtracore");
        Internals.globalLogger = LoggerFactory.getLogger("XtraCore-XtraCoreLogger");
    }

    private void checkExists(Path logPath) {
        try {
            if (Files.notExists(logPath)) {
                Files.createFile(logPath);
            } else {
                // Wipe the log file.
                new PrintWriter(logPath.toFile()).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAppenders(Path logPath, String name, String consoleName) {
        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger("XtraCore-" + name);
        coreLogger.setAdditive(false);

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        FileAppender appender = FileAppender.createAppender(logPath.toString(), "true", "false", "XtraCore-FileAppender-" + name, "true",
                "false", "4000", PatternLayout.createLayout("[%d{HH:mm:ss.SSS}] [%t] [%p]: %m%n", config, null, StandardCharsets.UTF_8.name(), null),
                null, "false", null, config);
        appender.start();
        config.addLoggerAppender(coreLogger, appender);

        ConsoleAppender appender2 =
                ConsoleAppender.createAppender(PatternLayout.createLayout("[%d{HH:mm:ss}] [%t/%level] [" + consoleName + "]: %msg%n", config, null,
                        StandardCharsets.UTF_8.name(), null), null, "SYSTEM_OUT", "XtraCore-ConsoleAppender-" + name, "false", "false");
        appender2.addFilter(ThresholdFilter.createFilter(Level.WARN.name(), "ACCEPT", "DENY"));
        appender2.start();
        config.addLoggerAppender(coreLogger, appender2);

        AppenderRef[] refs = new AppenderRef[] {AppenderRef.createAppenderRef("XtraCore-FileAppender-" + name, Level.DEBUG.name(), null),
                AppenderRef.createAppenderRef("XtraCore-ConsoleAppender-" + name, Level.ERROR.name(), null)};
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", null, "XtraCore-" + name, "true", refs, null, config, null);
        loggerConfig.addAppender(appender, Level.DEBUG, null);
    }
}
