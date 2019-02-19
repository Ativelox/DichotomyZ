/**
 * 
 */
package de.ativelox.dichotomyz.callbacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.ativelox.dichotomyz.logging.ELogType;
import de.ativelox.dichotomyz.logging.Logger;
import de.ativelox.dichotomyz.utils.Timestamp;

/**
 * Fires callbacks for instances of {@link IDayCallback} and
 * {@link IIntervalCallback} when registered to this instance. Checks every
 * {@link TimeObserver#WAIT_TIME_MS} if any callback should be fired and does so
 * if true.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class TimeObserver implements Runnable {

    /**
     * The frequency in which to check whether callbacks should be fired in
     * milliseconds.
     * 
     */
    private static final int WAIT_TIME_MS = 1000;

    /**
     * The maximum amount of difference between registered intervals and the current
     * value to check against. This should never exceed
     * {@link TimeObserver#WAIT_TIME_MS}.
     */
    private static final int DELTA_MS = 900;

    /**
     * The date that was present the last {@link TimeObserver#run} call.
     */
    private String mOldDate;

    /**
     * The time in ms specified by {@link System#currentTimeMillis()} when
     * {@link TimeObserver#run()} was called.
     */
    private long mRunnableStart;

    /**
     * Whether {@link TimeObserver#run()} is currently being executed or not.
     */
    private boolean running;

    /**
     * A mapping from each registered {@link IIntervalCallback} to its intervals.
     */
    private final Map<IIntervalCallback, long[]> mCallbacks;

    /**
     * A list for each registered {@link IDayCallback}.
     */
    private final List<IDayCallback> mDayCallbacks;

    /**
     * Creates a new {@link TimeObserver} instance.
     */
    public TimeObserver() {
	mDayCallbacks = new ArrayList<>();
	mCallbacks = new HashMap<>();
	running = true;

    }

    /**
     * Adds the given {@link IIntervalCallback} instance to this
     * {@link TimeObserver} with its given intervals.
     * 
     * @param cb        The instance for which to fire its callbacks specified by
     *                  {@link IIntervalCallback}.
     * @param intervals The intervals in which to fire the associated callback.
     */
    public void add(final IIntervalCallback cb, final long... intervals) {
	mCallbacks.put(cb, intervals);

    }

    /**
     * Removes the given instance from the callback-cycle.
     * 
     * @param cb The instance to remove.
     */
    public void remove(final IIntervalCallback cb) {
	mCallbacks.remove(cb);

    }

    /**
     * Adds the given {@link IDayCallback} instance to this {@link TimeObserver}s
     * callback-cycle.
     * 
     * @param dc The instance for which to fire its callbacks specified by
     *           {@link IDayCallback}.
     */
    public void add(final IDayCallback dc) {
	mDayCallbacks.add(dc);

    }

    /**
     * Removes the given instance from the callback-cycle.
     * 
     * @param dc The instance to remove.
     */
    public void remove(final IDayCallback dc) {
	mDayCallbacks.remove(dc);

    }

    /**
     * Stops this instance to execute its {@link TimeObserver#run()} method. Safely
     * stops this runnable.
     * 
     */
    public void stop() {
	running = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	mRunnableStart = System.currentTimeMillis();
	mOldDate = Timestamp.getCurrentDate();

	while (running) {

	    final String newDate = Timestamp.getCurrentDate();

	    if (!newDate.equals(mOldDate)) {
		for (final IDayCallback callback : mDayCallbacks) {
		    callback.onDayPassed();

		}
	    }

	    mOldDate = newDate;

	    for (final Entry<IIntervalCallback, long[]> entry : mCallbacks.entrySet()) {
		for (final long interval : entry.getValue()) {
		    System.out.println("checking against: " + interval);

		    if ((System.currentTimeMillis() - mRunnableStart) % interval <= DELTA_MS) {
			System.out.println((System.currentTimeMillis() - mRunnableStart) % DELTA_MS);
			entry.getKey().onIntervalPassed(interval);

		    }
		}
	    }

	    try {
		Thread.sleep(WAIT_TIME_MS);

	    } catch (final InterruptedException e) {
		Logger.Get().log(ELogType.WARNING, "Current TimeObserver Thread got interrupted: " + e.getMessage());

	    }
	}
    }
}
