package de.ativelox.dichotomyz.logging;

import java.io.IOException;

import de.ativelox.dichotomyz.ProjectPaths;
import de.ativelox.dichotomyz.callbacks.FTPConnectionTerminator;
import de.ativelox.dichotomyz.callbacks.ITimeOut;
import de.ativelox.dichotomyz.utils.FTPUtils;
import de.ativelox.dichotomyz.utils.Timestamp;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

/**
 * Provides a logger that is able to log files to a given FTP host.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class FTPLogger implements ILogger, ITimeOut {

    /**
     * The time in milliseconds the client disconnects from the server.
     */
    private static final long FTP_TIMEOUT = 5000;

    /**
     * The underlying ftp client.
     */
    private final FTPClient mClient;

    /**
     * The user name credentials used to log in to the given server.
     */
    private final String mUser;

    /**
     * The password credentials used to log in to the given server.
     */
    private final String mPassword;

    /**
     * The host of the server the ftp service is located on.
     */
    private final String mHost;

    /**
     * The thread used to timeout the FTP connection.
     */
    private Thread mInterruptionThread;

    /**
     * The current date.
     */
    private String mCurrentDate;

    /**
     * Whether the underlying client is currently logged into the FTP service or
     * not.
     */
    private boolean mLoggedIn;

    /**
     * The top level path used for the files.
     */
    private final String mTopLevelPath;

    /**
     * Creates a new {@link FTPLogger} for the given credentials.
     * 
     * @param topLevel The top level path used for the files.
     * @param user     The user name used for credentials.
     * @param password The password used for credentials.
     * @param host     The host of the server the ftp service is located on.
     */
    public FTPLogger(final String topLevel, final String user, final String password, final String host) {
	mClient = new FTPClient();
	mPassword = password;
	mUser = user;
	mHost = host;

	mTopLevelPath = topLevel;

	mLoggedIn = false;

	mCurrentDate = Timestamp.getCurrentDate();

	mInterruptionThread = new Thread(new FTPConnectionTerminator(FTP_TIMEOUT, this));

    }

    /**
     * Updates the current date for this logger.
     */
    public void updateDate() {
	mCurrentDate = Timestamp.getCurrentDate();

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
	mInterruptionThread.interrupt();
	mInterruptionThread = new Thread(new FTPConnectionTerminator(FTP_TIMEOUT, this));

	if (!mLoggedIn) {
	    try {
		mClient.connect(mHost);
		mClient.login(mUser, mPassword);
		mLoggedIn = true;

	    } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
		e.printStackTrace();
		return;

	    }
	}
	// TODO: make sure file path is valid, and serve data
	String path = mTopLevelPath;
	String filename = mCurrentDate + " - ";

	switch (type) {
	case ACTIVITY:
	    path += ProjectPaths.LOG_PATH;
	    filename += "Activity";
	    break;

	case DEBUG:
	    path += ProjectPaths.DEBUG_PATH;
	    filename += "Debug";
	    break;

	case INFO:
	    path += ProjectPaths.DEBUG_PATH;
	    filename += "Info";
	    break;

	case PM:
	    path += ProjectPaths.DEBUG_PATH;
	    filename += "PM";
	    break;

	case STATUS:
	    path += ProjectPaths.LOG_PATH;
	    filename += "Status";
	    break;

	case WARNING:
	    path += ProjectPaths.DEBUG_PATH;
	    filename += "Warning";
	    break;

	default:
	    break;

	}

	filename += ".log";

	try {
	    FTPUtils.writeFile(mClient, path, filename, message.getBytes());

	} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException
		| FTPDataTransferException | FTPAbortedException e) {
	    e.printStackTrace();

	}

	mInterruptionThread.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ativelox.dichotomyz.callbacks.ITimeOut#timeout()
     */
    @Override
    public void timeout() {
	try {
	    mClient.disconnect(true);
	    mLoggedIn = false;

	} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
	    Logger.Get().log(ELogType.WARNING, e.getMessage());

	}

    }
}
