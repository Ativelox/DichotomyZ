package de.ativelox.dichotomyz.logging;

import de.ativelox.dichotomyz.settings.SettingsProvider;

/**
 * A singleton for the current in-use logger for the project.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class Logger {

    /**
     * The current instance for the logger in use.
     */
    private static ILogger INSTANCE;

    /**
     * Gets the current logger used for this project. This should be the only way a
     * logger is accessed.
     * 
     * @return The current logger.
     */
    public static ILogger Get() {
	if (INSTANCE == null) {
	    INSTANCE = new FileLogger(SettingsProvider.getPath());

	}
	return INSTANCE;

    }

    private Logger() {

    }
}
