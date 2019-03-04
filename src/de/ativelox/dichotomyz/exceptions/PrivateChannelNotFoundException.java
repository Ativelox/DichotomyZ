package de.ativelox.dichotomyz.exceptions;

/**
 * An exception indicating that any private channel couldn't be found.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class PrivateChannelNotFoundException extends Exception {

    /**
     * The serial version UID used for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new {@link PrivateChannelNotFoundException}.
     * 
     * @param message The optional message further specifying the error.
     */
    public PrivateChannelNotFoundException(final String message) {
	super(message);
    }

    /**
     * Instantiazes a new {@link PrivateChannelNotFoundException} with no
     * explanation.
     */
    public PrivateChannelNotFoundException() {
	this("");
    }

}
