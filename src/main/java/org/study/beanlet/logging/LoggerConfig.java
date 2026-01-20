package org.study.beanlet.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;

public class LoggerConfig {
    public static void setupLogger(Level level) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

        rootLogger.detachAndStopAllAppenders();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setName("console");
        consoleAppender.setWithJansi(true);

        PatternLayoutEncoder encoder = getPatternLayoutEncoder(loggerContext);

        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        rootLogger.addAppender(consoleAppender);
        rootLogger.setLevel(level);
    }

    private static PatternLayoutEncoder getPatternLayoutEncoder(LoggerContext loggerContext) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);

        encoder.setPattern(
                "%boldCyan(%d{yyyy-MM-dd HH:mm:ss.SSS}) " +
                        "%highlight(%-5level){ERROR=red bold, WARN=yellow bold, INFO=green bold, DEBUG=blue bold, TRACE=magenta bold} " +
                        "%yellow([%thread]) " +
                        "%boldMagenta(%logger{36}) " +
                        "- %highlight(%msg){ERROR=red, WARN=yellow, INFO=white, DEBUG=cyan, TRACE=blue}%n"
        );
        encoder.start();
        return encoder;
    }
}