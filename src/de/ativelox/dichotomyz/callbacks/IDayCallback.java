package de.ativelox.dichotomyz.callbacks;

/**
 * Provides callbacks for day related changes.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public interface IDayCallback {

    /**
     * Fires once when a new day starts. This is backed by
     * {@link de.ativelox.dichotomyz.utils.Timestamp#getCurrentDate()
     * getCurrentDate} and uses its timezone and the like. This is checked in an
     * interval specified by {@link TimeObserver}.
     */
    void onDayPassed();

}
