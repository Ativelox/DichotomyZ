package de.ativelox.dichotomyz.exceptions;

/**
 * An Exception which describes that there were more or less guilds than
 * expected.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class UnexpectedGuildSizeException extends Exception {

    /**
     * The ID used for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new UnexpectedGuildSizeException, which is thrown when there were
     * more or less guilds than expected.
     * 
     * @param number The number of guilds.
     */
    public UnexpectedGuildSizeException(final int number) {
	super("Unexpected number of guilds this bot is connected to: " + number);

    }

}
