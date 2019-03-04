package de.ativelox.dichotomyz.logging;

/**
 * Provides a combination of multiple loggers, where one logger logs
 * {@link ELogType#STATUS} and {@link ELogType#ACTIVITY}, and the other one the
 * remaining log types. This can be useful, since the remaining log types can be
 * seen as debug only, and thus seperating them is advised.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class CombinedLogger implements ILogger {

    /**
     * The first logger.
     */
    private final ILogger mFirst;

    /**
     * The second logger.
     */
    private final ILogger mSecond;

    /**
     * Constructs a new {@link CombinedLogger} for the given loggers.
     * 
     * @param first  The first logger.
     * @param second The second logger.
     */
    public CombinedLogger(final ILogger first, final ILogger second) {
	mFirst = first;
	mSecond = second;

    }

    /**
     * Updates the date of the underlying loggers, if supported.
     */
    public void updateDate() {
	if (mFirst instanceof FTPLogger) {
	    ((FTPLogger) mFirst).updateDate();

	} else if (mSecond instanceof FTPLogger) {
	    ((FTPLogger) mSecond).updateDate();

	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ativelox.dichotomyz.logging.ILogger#log(de.ativelox.dichotomyz.logging.
     * ELogType, java.lang.String)
     */
    @Override
    public void log(ELogType type, String message) {
	switch (type) {
	case ACTIVITY:
	    mSecond.log(type, message);
	    break;

	case DEBUG:
	    mFirst.log(type, message);
	    break;

	case INFO:
	    mFirst.log(type, message);
	    break;

	case PM:
	    mFirst.log(type, message);
	    break;

	case STATUS:
	    mSecond.log(type, message);
	    break;

	case WARNING:
	    mFirst.log(type, message);
	    break;

	default:
	    break;

	}

    }

}
