package de.ativelox.dichotomyz.logging;

/**
 * An interface for loggers, providing a log function.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public interface ILogger {

    /**
     * Logs the given message with the given type.
     * 
     * @param type    The type of the log
     * @param message The message to log
     */
    void log(final ELogType type, final String message);

}
