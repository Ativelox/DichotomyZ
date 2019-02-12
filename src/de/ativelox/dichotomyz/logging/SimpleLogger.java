package de.ativelox.dichotomyz.logging;

/**
 * Provides a simple logger implementation, which logs everything to the systems
 * standard output.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class SimpleLogger implements ILogger {

    /*
     * (non-Javadoc)
     * 
     * @see de.ativelox.dichotomyz.ILogger#log(de.ativelox.dichotomyz.ELogType,
     * java.lang.String)
     */
    @Override
    public void log(final ELogType type, final String message) {
	System.out.println("[" + type.toString() + "]: " + message);

    }

}
