package de.ativelox.dichotomyz.logging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.ativelox.dichotomyz.utils.Timestamp;
import de.ativelox.dichotomyz.utils.TimestampedEntry;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;

/**
 * Formats the given logs in a user-friendly reading fashion. Also buffers the
 * log messages to make the logs even more intuitive. For example, waits for a
 * user to change its online-status and then proceeds to log the time the user
 * had the former status. All logging should be passed to this formatter, which
 * in turn then forwards its buffered logs to the underlying {@link ILogger} by
 * calling {@link BufferedLogFormatter#log}.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class BufferedLogFormatter {

    /**
     * A mapping from user names to their respective (current) online status, and
     * the time at which that status was acquired. An "empty" online status is
     * symbolized by having the {@link TimestampedEntry} hold a value of
     * {@link OnlineStatus.OFFLINE} with its respective timestamp.
     */
    private final Map<String, TimestampedEntry<OnlineStatus>> mStatusMap;

    /**
     * A mapping from user names to their respective (current) playing game names,
     * and the time at which that game was started playing. An "empty" game name is
     * symbolized by having the {@link TimestampedEntry} hold a value of
     * <tt>null</tt> with its respective timestamp.
     */
    private final Map<String, TimestampedEntry<String>> mActivityMap;

    /**
     * Holds the current log of all the users online status, formatted as a
     * user-friendly string.
     */
    private StringBuilder mCurrentStatusLog;

    /**
     * Holds the current log of all the users activity (rich presence), formatted as
     * a user-friendly string.
     */
    private StringBuilder mCurrentActivityLog;

    /**
     * Holds the current log of all the PMs this client has received, formatted as a
     * user-friendly string.
     */
    private StringBuilder mCurrentPMLog;

    /**
     * Holds the current log of all the debug messages that occured during runtime,
     * formatted as a user-friendly string.
     */
    private StringBuilder mCurrentDebugLog;

    /**
     * Creates a new {@link BufferedLogFormatter}.
     */
    public BufferedLogFormatter() {
	mStatusMap = new HashMap<>();
	mActivityMap = new HashMap<>();

	mCurrentStatusLog = new StringBuilder();
	mCurrentActivityLog = new StringBuilder();
	mCurrentPMLog = new StringBuilder();
	mCurrentDebugLog = new StringBuilder();

    }

    /**
     * Adds a change in the activity of a user to this buffer.
     * 
     * @param affectedName The name of the affected user, not <tt>null</tt>
     * @param gameName     The name of the game this user has started playing, if
     *                     <tt>null</tt> the user has stopped playing the previous
     *                     game
     */
    public void addActivityChange(final String affectedName, final String gameName) {
	final TimestampedEntry<String> oldEntry = mActivityMap.get(affectedName);

	if (oldEntry.getEntry() != null && !oldEntry.getEntry().equals("null")) {
	    if (oldEntry.getEntry().equals(gameName)) {
		return;

	    }

	    mCurrentActivityLog.append(affectedName + " played " + oldEntry.getEntry() + " for "
		    + Timestamp.msToReadable(oldEntry.getDifference()) + ".\r\n");
	}

	mActivityMap.put(affectedName, new TimestampedEntry<String>(gameName));
    }

    /**
     * Adds a debug log to this buffer.
     * 
     * @param type    The type of the debug log, should only be
     *                {@link ELogType#DEBUG}, {@link ELogType#INFO} or
     *                {@link ELogType#WARNING}
     * @param message The message that describes the debug log
     */
    public void addDebugLog(final ELogType type, final String message) {
	mCurrentDebugLog.append("[" + type + "] " + message + "\r\n");

    }

    /**
     * Adds a PM to this buffer.
     * 
     * @param name    The name of the author that wrote the PM to this client
     * @param message The message written.
     */
    public void addPMLog(final String name, final String message) {
	mCurrentPMLog.append("[" + Timestamp.getCurrentTime() + "] " + name + ": " + message + "\r\n");

    }

    /**
     * Adds a change in the online status of a user to this buffer.
     * 
     * @param affectedName The name of the affected user, not <tt>null</tt>
     * @param newStatus    The new online status of the user, not <tt>null</tt>
     */
    public void addStatusChange(final String affectedName, final OnlineStatus newStatus) {
	final TimestampedEntry<OnlineStatus> oldEntry = mStatusMap.get(affectedName);

	if (oldEntry != null && oldEntry.getEntry() != OnlineStatus.OFFLINE) {
	    mCurrentStatusLog.append(affectedName + " was " + oldEntry.getEntry() + " for "
		    + Timestamp.msToReadable(oldEntry.getDifference()) + ".\r\n");
	}

	mStatusMap.put(affectedName, new TimestampedEntry<OnlineStatus>(newStatus));

    }

    /**
     * Initializes this buffer, by passing a list of every member in the guild and
     * internally fetching their initial online statuses and their initial activity.
     * 
     * @param members A list of all the members in the guild.
     */
    public void init(final List<Member> members) {
	for (final Member member : members) {
	    final Game game = member.getGame();
	    String gameName = null;

	    if (game != null) {
		gameName = game.getName();
	    }
	    mStatusMap.put(member.getEffectiveName(), new TimestampedEntry<OnlineStatus>(member.getOnlineStatus()));
	    mActivityMap.put(member.getEffectiveName(), new TimestampedEntry<String>(gameName));
	}
    }

    /**
     * Logs everything that has been added to this buffer using the underlying
     * {@link ILogger} from {@link Logger#Get()}.
     */
    public void log() {
	for (final Entry<String, TimestampedEntry<OnlineStatus>> entry : mStatusMap.entrySet()) {
	    addStatusChange(entry.getKey(), entry.getValue().getEntry());

	}

	for (final Entry<String, TimestampedEntry<String>> entry : mActivityMap.entrySet()) {
	    addActivityChange(entry.getKey(), entry.getValue().getEntry());

	}

	final ILogger logger = Logger.Get();

	logger.log(ELogType.ACTIVITY, mCurrentActivityLog.toString());
	logger.log(ELogType.STATUS, mCurrentStatusLog.toString());
	logger.log(ELogType.DEBUG, mCurrentDebugLog.toString());
	logger.log(ELogType.PM, mCurrentPMLog.toString());

    }
}
