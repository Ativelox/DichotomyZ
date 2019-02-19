package de.ativelox.dichotomyz.callbacks;

/**
 * Provides a callback method for time interval related stuff.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public interface IIntervalCallback {

    /**
     * Fires once when the time interval this instance was registered with has been
     * passed. This is checked in an interval specified by {@link TimeObserver} and
     * thus can result in precision loss.
     * 
     * @param intervalMs The interval this instance was registered with. This can be
     *                   used to distinguish the interval when registered with
     *                   multiple intervals.
     */
    void onIntervalPassed(final long intervalMs);

}
