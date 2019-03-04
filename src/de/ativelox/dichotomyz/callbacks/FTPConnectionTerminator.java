package de.ativelox.dichotomyz.callbacks;

/**
 * Provides a runnable that calls {@link ITimeOut#timeout()} on all passed
 * instances after the given time. Can be useful for connection terminations, by
 * utilizing a seperate thread.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class FTPConnectionTerminator implements Runnable {

    /**
     * The time in ms this instance will call {@link ITimeOut#timeout()} on its
     * passed callbacks.
     */
    private final long mTimeout;

    /**
     * All the callback implementing classes given.
     */
    private final ITimeOut[] mTimeoutCallbacks;

    /**
     * Instantiates a new {@link FTPConnectionTerminator}.
     * 
     * @param timeoutms        The time in ms this instance will call
     *                         {@link ITimeOut#timeout()} on its passed callbacks.
     * @param timeoutCallbacks All the callback implementing classes on which to
     *                         call {@link ITimeOut#timeout()}.
     */
    public FTPConnectionTerminator(final long timeoutms, final ITimeOut... timeoutCallbacks) {
	mTimeout = timeoutms;
	mTimeoutCallbacks = timeoutCallbacks;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	try {
	    Thread.sleep(mTimeout);

	} catch (final InterruptedException e) {
	    return;

	}
	for (final ITimeOut to : mTimeoutCallbacks) {
	    to.timeout();

	}
    }
}
