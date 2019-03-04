package de.ativelox.dichotomyz.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import de.ativelox.dichotomyz.ProjectPaths;
import de.ativelox.dichotomyz.utils.Timestamp;

/**
 * Handles logs by creating associated log files for the given {@link ELogType}
 * passed in the respective log method from {@link ILogger}.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class LocalFileLogger implements ILogger {

    /**
     * The date this logger was created.
     */
    private final String mServiceStartTimestamp;

    /**
     * The time this logger was created
     */
    private final String mServiceStartTimeTimestamp;

    /**
     * A mapping from filenames to boolean values, which indicates whether this
     * logger has already written to said file.
     */
    private final Map<String, Boolean> mHasWritten;

    /**
     * The top path of the log files.
     */
    private final String mTopPath;

    /**
     * Creates a new {@link LocalFileLogger}.
     * 
     * @param topPath The top path for the log files.
     */
    public LocalFileLogger(final String topPath) {
	mTopPath = topPath;
	mServiceStartTimestamp = Timestamp.getCurrentDate();
	mServiceStartTimeTimestamp = Timestamp.getCurrentTime();
	mHasWritten = new HashMap<>();

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ativelox.dichotomyz.logger.ILogger#log(de.ativelox.dichotomyz.logger.
     * ELogType, java.lang.String)
     */
    @Override
    public void log(final ELogType type, final String message) {
	String fileName = mServiceStartTimestamp + " - ";
	String relativePath = "";

	switch (type) {
	case ACTIVITY:
	    fileName += "Activity.log";
	    relativePath = ProjectPaths.LOG_PATH;
	    break;
	case DEBUG:
	    fileName += "Debug.log";
	    relativePath = ProjectPaths.DEBUG_PATH;
	    break;

	case INFO:
	    fileName += "Debug.log";
	    relativePath = ProjectPaths.DEBUG_PATH;
	    break;

	case PM:
	    fileName += "PM.log";
	    relativePath = ProjectPaths.DEBUG_PATH;
	    break;

	case STATUS:
	    fileName += "Status.log";
	    relativePath = ProjectPaths.LOG_PATH;
	    break;

	case WARNING:
	    fileName += "Debug.log";
	    relativePath = ProjectPaths.DEBUG_PATH;
	    break;

	default:
	    break;

	}

	String timestampedMessage = message;

	if (mHasWritten.get(fileName) == null || !mHasWritten.get(fileName)) {
	    timestampedMessage = "System start on the " + mServiceStartTimestamp + " at " + mServiceStartTimeTimestamp
		    + "\r\n" + message;
	}

	final File file = new File(mTopPath + relativePath + fileName);

	if (!file.exists()) {
	    try {
		Files.write(Paths.get(file.getAbsolutePath()), timestampedMessage.getBytes(),
			StandardOpenOption.CREATE);

	    } catch (final IOException e) {
		e.printStackTrace();

	    }

	} else {
	    timestampedMessage = "\r\n\r\n" + timestampedMessage;

	    try {
		Files.write(Paths.get(file.getAbsolutePath()), timestampedMessage.getBytes(),
			StandardOpenOption.APPEND);

	    } catch (final IOException e) {
		e.printStackTrace();

	    }
	}
	mHasWritten.put(fileName, true);

    }
}
