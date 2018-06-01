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

package io.github.narikiro.core.logger;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.narikiro.api.logger.LoggerHandler;
import io.github.narikiro.api.plugin.XtraCorePluginContainer;
import io.github.narikiro.core.CoreImpl;
import io.github.narikiro.core.internal.Internals;
import io.github.narikiro.core.plugin.XtraCorePluginContainerImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
                // Wipe the old log file.
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

        FileAppender appender = FileAppender.newBuilder().withFileName(logPath.toString()).withAppend(true).withLocking(false)
                .withName("XtraCore-FileAppender-" + name).withImmediateFlush(true).withIgnoreExceptions(false)
                .withLayout(PatternLayout.newBuilder().withPattern("\"[%d{HH:mm:ss.SSS}] [%t] [%p]: %m%n\"").withConfiguration(config)
                        .withCharset(StandardCharsets.UTF_8).withConfiguration(config).build())
                .setConfiguration(config).build();

        appender.start();
        config.addLoggerAppender(coreLogger, appender);

        ConsoleAppender appender2 = ConsoleAppender.newBuilder()
                .withLayout(PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss}] [%t/%level] [" + consoleName + "]: %msg%n")
                        .withConfiguration(config).withCharset(StandardCharsets.UTF_8).build())
                .withFilter(ThresholdFilter.createFilter(Level.WARN, Result.ACCEPT, Result.DENY))
                .setTarget(Target.SYSTEM_OUT).withName("XtraCore-ConsoleAppender-" + name).setFollow(false).withIgnoreExceptions(false).build();

        appender2.start();
        config.addLoggerAppender(coreLogger, appender2);

        AppenderRef[] refs = new AppenderRef[] {AppenderRef.createAppenderRef("XtraCore-FileAppender-" + name, Level.DEBUG, null),
                AppenderRef.createAppenderRef("XtraCore-ConsoleAppender-" + name, Level.ERROR, null)};

        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.DEBUG, "name", "true", refs, new Property[] {}, config,
                ThresholdFilter.createFilter(Level.DEBUG, Result.ACCEPT, Result.DENY));
        loggerConfig.addAppender(appender, Level.DEBUG, null);
    }
}
