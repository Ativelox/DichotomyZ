package de.ativelox.dichotomyz.consumer;

import java.util.function.Consumer;

import net.dv8tion.jda.core.entities.PrivateChannel;

/**
 * Acts as a functional interface implementation of the {@link Consumer}
 * interface. Will queue a message to be sent to a given {@link PrivateChannel}
 * after its {@link AsynchroniousPCMessageSender#accept(PrivateChannel)} is
 * called.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class AsynchroniousPCMessageSender implements Consumer<PrivateChannel> {

    /**
     * The message this instance queues to the underlying http request buffer.
     */
    private final String mMessage;

    /**
     * Instantiates a new {@link AsynchroniousPCMessageSender} with the given
     * message.
     * 
     * @param message The message to queue.
     */
    public AsynchroniousPCMessageSender(final String message) {
	mMessage = message;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.function.Consumer#accept(java.lang.Object)
     */
    @Override
    public void accept(final PrivateChannel pc) {
	pc.sendMessage(mMessage).queue();

    }

}
