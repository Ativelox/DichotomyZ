package de.ativelox.dichotomyz.logging;

import de.ativelox.dichotomyz.consumer.AsynchroniousPCMessageSender;
import de.ativelox.dichotomyz.exceptions.PrivateChannelNotFoundException;
import de.ativelox.dichotomyz.utils.Timestamp;
import de.ativelox.dichotomyz.utils.UserUtils;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.requests.RestAction;

/**
 * This logger provides the possibility to log its logs as private messages to
 * the given user.
 * 
 * @author Ativelox {@literal ativelox.dev@web.de}
 *
 */
public class PMLogger implements ILogger {

    /**
     * The name of the user.
     */
    private final String mName;

    /**
     * The discriminator of the user, e.g. the numbers after the #.
     */
    private final String mDiscriminator;

    /**
     * Creates a new {@link PMLogger}.
     * 
     * @param name          The name of the user to log its logs to.
     * @param discriminator The discriminator of the user to log its logs to.
     */
    public PMLogger(final String name, final String discriminator) {
	mName = name;
	mDiscriminator = discriminator;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ativelox.dichotomyz.logging.ILogger#log(de.ativelox.dichotomyz.logging.
     * ELogType, java.lang.String)
     */
    @Override
    public void log(final ELogType type, final String message) {
	if (type == ELogType.PM) {
	    final String author = message.split(":")[0].trim().toLowerCase();

	    if (author.equals(mName.toLowerCase()) || author.equals(UserUtils.getOwnName().toLowerCase())) {
		return;

	    }
	}

	try {
	    final RestAction<PrivateChannel> channel = UserUtils.getPrivateChannelFromName(mName, mDiscriminator);
	    if (channel == null) {
		// bot is not yet fully logged in, thus we cannot send private messages
		return;

	    }

	    channel.queue(new AsynchroniousPCMessageSender(
		    "[" + Timestamp.getCurrentTime() + " | " + type.toString() + "]: " + message));

	} catch (final PrivateChannelNotFoundException e) {
	    e.printStackTrace();

	}
    }
}
