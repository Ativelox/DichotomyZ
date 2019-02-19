package de.ativelox.dichotomyz.utils;

/**
 * Provides a pair of a generic entry and the timestamp of its creation.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public final class TimestampedEntry<T> {

    /**
     * The value for this {@link TimestampedEntry}.
     */
    private final T mEntry;

    /**
     * The time (in ms) this object was created.
     */
    private final long mTimestamp;

    /**
     * Creates a new {@link TimestampedEntry}.
     * 
     * @param entry The value which is given a timestamp.
     */
    public TimestampedEntry(final T entry) {
	mEntry = entry;
	mTimestamp = System.currentTimeMillis();

    }

    /**
     * Gets the difference in time (ms) between the creation of this instant and
     * now.
     * 
     * @return The difference mentioned.
     */
    public long getDifference() {
	return System.currentTimeMillis() - mTimestamp;

    }

    /**
     * Gets the entry for this instance.
     * 
     * @return The entry mentioned
     */
    public T getEntry() {
	return mEntry;

    }

    /**
     * Gets the creation time for this instance in ms.
     * 
     * @return The creation time.
     */
    public long getTimestamp() {
	return mTimestamp;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((mEntry == null) ? 0 : mEntry.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	@SuppressWarnings("unchecked")
	TimestampedEntry<T> other = (TimestampedEntry<T>) obj;
	if (mEntry == null) {
	    if (other.mEntry != null)
		return false;
	} else if (!mEntry.equals(other.mEntry))
	    return false;
	return true;
    }
}
