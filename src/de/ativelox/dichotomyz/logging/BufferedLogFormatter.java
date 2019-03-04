package de.ativelox.dichotomyz.logging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.ativelox.dichotomyz.utils.Timestamp;
import de.ativelox.dichotomyz.utils.TimestampedEntry;
import de.ativelox.dichotomyz.utils.UserUtils;
import net.dv8tion.jda.core.OnlineStatus;
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
     * A mapping from user names to each of their online statuses associated with
     * their current active time, which is stored in its respective
     * {@link TimestampedEntry#getEntry()}.
     */
    private final Map<String, Map<OnlineStatus, TimestampedEntry<Long>>> mStatusMap;

    /**
     * A mapping from user names to each of their played games associated with their
     * current active time, which is stored in its respective
     * {@link TimestampedEntry#getEntry()}.
     */
    private final Map<String, Map<String, TimestampedEntry<Long>>> mActivityMap;

    /**
     * A list of all the members in the guild.
     */
    private List<Member> mMembers;

    /**
     * Creates a new {@link BufferedLogFormatter}.
     */
    public BufferedLogFormatter() {
	mStatusMap = new HashMap<>();
	mActivityMap = new HashMap<>();

    }

    /**
     * Adds a change in the activity of a user to this buffer.
     * 
     * @param affectedName The name of the affected user, not <tt>null</tt>
     * @param oldGameName  The name of the game this user has stopped playing, if
     *                     <tt>null</tt> the user has started playing a game.
     * @param newGameName  The name of the game this user has started playing, if
     *                     <tt>null</tt> the user has stopped playing a game.
     */
    public void addActivityChange(final String affectedName, final String oldGameName, final String newGameName) {
	final Map<String, TimestampedEntry<Long>> toUpdate = mActivityMap.get(affectedName);

	if (oldGameName == null && newGameName == null) {
	    return;
	}

	if (oldGameName == null) {
	    // user has started playing newGameName

	    if (toUpdate.get(newGameName) == null) {
		// the game was not played before in this session.
		toUpdate.put(newGameName, new TimestampedEntry<Long>(0L));

	    }
	    // update the current associated timestamp, so it starts with the game start.
	    final TimestampedEntry<Long> oldEntry = toUpdate.get(newGameName);
	    toUpdate.put(newGameName, new TimestampedEntry<Long>(oldEntry.getEntry()));

	} else if (oldGameName.equals(newGameName)) {
	    return;

	} else if (newGameName == null) {
	    // user has stopped playing oldGameName
	    final TimestampedEntry<Long> oldEntry = toUpdate.get(oldGameName);
	    toUpdate.put(oldGameName, new TimestampedEntry<Long>(oldEntry.getEntry() + oldEntry.getDifference()));

	}

    }

    /**
     * Updates the date of the underlying logger, if supported.
     */
    public void updateDate() {
	ILogger logger = Logger.Get();
	if (logger instanceof CombinedLogger) {
	    ((CombinedLogger) logger).updateDate();
	}
    }

    /**
     * Adds a change in the online status of a user to this buffer.
     * 
     * @param affectedName The name of the affected user, not <tt>null</tt>
     * @param oldStatus    The old online status of the user, not <tt>null</tt>.
     * @param newStatus    The new online status of the user, not <tt>null</tt>
     */
    public void addStatusChange(final String affectedName, final OnlineStatus oldStatus, final OnlineStatus newStatus) {
	final Map<OnlineStatus, TimestampedEntry<Long>> toUpdate = mStatusMap.get(affectedName);

	if (oldStatus.equals(OnlineStatus.UNKNOWN)) {
	    toUpdate.put(newStatus, new TimestampedEntry<Long>(0L));
	    return;

	}

	// here we assume that the toUpdate map has an entry for every OnlineStatus.
	// This is done by this class' init method.

	// this method got called with two valid parameters, thus the user status has
	// changed -> update values.

	final TimestampedEntry<Long> oldStatusTimestamp = toUpdate.get(oldStatus);
	toUpdate.put(oldStatus,
		new TimestampedEntry<Long>(oldStatusTimestamp.getDifference() + oldStatusTimestamp.getEntry()));

	toUpdate.put(newStatus, new TimestampedEntry<Long>(toUpdate.get(newStatus).getEntry()));

    }

    /**
     * Initializes this buffer, by passing a list of every member in the guild and
     * internally fetching their initial online statuses and their initial activity.
     * 
     * @param members A list of all the members in the guild.
     */
    public void init(final List<Member> members) {
	this.mMembers = members;

	for (final Member member : members) {
	    mActivityMap.put(member.getEffectiveName(), new HashMap<>());
	    mStatusMap.put(member.getEffectiveName(), new HashMap<>());

	    this.addActivityChange(member.getEffectiveName(), null, UserUtils.getUniformGameName(member.getGame()));

	    // set values for every online status.
	    for (final OnlineStatus status : OnlineStatus.values()) {
		this.addStatusChange(member.getEffectiveName(), OnlineStatus.UNKNOWN, status);

	    }
	}
    }

    /**
     * Logs everything that has been added to this buffer using the underlying
     * {@link ILogger} from {@link Logger#Get()}.
     */
    public void log() {

	final StringBuilder activityLog = new StringBuilder();
	final StringBuilder statusLog = new StringBuilder();

	// make sure to log the current ongoing statuses and activities aswell.
	for (final Member member : mMembers) {
	    this.addActivityChange(member.getEffectiveName(), UserUtils.getUniformGameName(member.getGame()), null);
	    this.addStatusChange(member.getEffectiveName(), member.getOnlineStatus(), OnlineStatus.UNKNOWN);

	}

	// generate the logs for the activity.
	for (final Entry<String, Map<String, TimestampedEntry<Long>>> entry : this.mActivityMap.entrySet()) {
	    for (final Entry<String, TimestampedEntry<Long>> secondEntry : entry.getValue().entrySet()) {
		activityLog.append(entry.getKey() + " played " + secondEntry.getKey() + " for "
			+ Timestamp.msToReadable(secondEntry.getValue().getEntry()) + "\r\n");

	    }

	}

	// generate the logs for the statuses.
	for (final Entry<String, Map<OnlineStatus, TimestampedEntry<Long>>> entry : this.mStatusMap.entrySet()) {
	    for (final Entry<OnlineStatus, TimestampedEntry<Long>> secondEntry : entry.getValue().entrySet()) {
		if (secondEntry.getValue().getEntry() > 0) {
		    statusLog.append(entry.getKey() + " was " + secondEntry.getKey() + " for "
			    + Timestamp.msToReadable(secondEntry.getValue().getEntry()) + "\r\n");
		}
	    }

	}

	final ILogger logger = Logger.Get();

	logger.log(ELogType.ACTIVITY, activityLog.toString());
	logger.log(ELogType.STATUS, statusLog.toString());

    }
}
