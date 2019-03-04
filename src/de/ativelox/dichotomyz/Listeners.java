package de.ativelox.dichotomyz;

import de.ativelox.dichotomyz.audio.AudioChatHandler;
import de.ativelox.dichotomyz.callbacks.IDayCallback;
import de.ativelox.dichotomyz.callbacks.IIntervalCallback;
import de.ativelox.dichotomyz.callbacks.TimeObserver;
import de.ativelox.dichotomyz.exceptions.UnexpectedGuildSizeException;
import de.ativelox.dichotomyz.logging.BufferedLogFormatter;
import de.ativelox.dichotomyz.logging.ELogType;
import de.ativelox.dichotomyz.logging.ILogger;
import de.ativelox.dichotomyz.logging.Logger;
import de.ativelox.dichotomyz.settings.SettingsProvider;
import de.ativelox.dichotomyz.utils.UserUtils;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 * Implementation for the {@link ListenerAdapter} which forwards specific events
 * and handles them to create logs of user activity and the like.
 * 
 * @author Ativelox {@literal<ativelox.dev@web.de>}
 *
 */
public class Listeners extends ListenerAdapter implements IDayCallback {

    /**
     * The audio chat handler, used to send and receive audio on this client.
     */
    private final AudioChatHandler mCaf;

    /**
     * The formatter used to format given log messages in a user-friendly fashion.
     * Every logging call should be made to this instance, since it forwards those
     * to the underlying {@link ILogger}.
     */
    private BufferedLogFormatter mFormatter;

    /**
     * The client this listener operates on.
     */
    private final Bot mClient;

    /**
     * Provides callbacks for implementations for {@link IDayCallback} and
     * {@link IIntervalCallback}.
     */
    private final TimeObserver mTimeObserver;

    /**
     * Creates a new implementation for the {@link ListenerAdapter} which forwards
     * specific events and handles them to create logs of user activity and the
     * like.
     * 
     * @param client The client this listener operates on.
     */
    public Listeners(final Bot client) {
	mCaf = new AudioChatHandler("unbenannt.raw");
	mTimeObserver = new TimeObserver();
	mTimeObserver.add(this);

	mFormatter = new BufferedLogFormatter();
	mClient = client;
    }

    /**
     * Provides a routine for which is called from both the
     * {@link Listeners#onGuildVoiceJoin(GuildVoiceJoinEvent) onGuildVoiceJoin} and
     * the {@link Listeners#onGuildVoiceMove(GuildVoiceMoveEvent) onGuildVoiceMove}
     * events.
     * 
     * @param event The generic guild voice event provided by the aforementioned
     *              events.
     */
    public void handleGuildVoiceJoin(final GenericGuildVoiceEvent event) {
	Logger.Get().log(ELogType.INFO,
		event.getMember().getEffectiveName() + " joined " + event.getVoiceState().getChannel().getName());

	if (!event.getMember().getEffectiveName().equals("Ativelox")) {
	    return;
	}

	final AudioManager am = event.getGuild().getAudioManager();
	am.openAudioConnection(event.getVoiceState().getChannel());
	am.setReceivingHandler(mCaf);
	am.setSendingHandler(mCaf);

    }

    /**
     * Initializes this listener, and is called after the
     * {@link Listeners#onReady(ReadyEvent) onReady} event is fired, allowing for
     * data-fetching which is server specific.
     */
    private void init() {

	try {
	    mFormatter.init(UserUtils.getAllUsers());

	} catch (final UnexpectedGuildSizeException e) {
	    Logger.Get().log(ELogType.WARNING, "Unexpected amount of guilds, might not work as intended");

	}
    }

    @Override
    public void onGuildVoiceJoin(final GuildVoiceJoinEvent event) {
	handleGuildVoiceJoin(event);
    }

    @Override
    public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event) {
	Logger.Get().log(ELogType.INFO,
		event.getMember().getEffectiveName() + " left " + event.getChannelLeft().getName());

	if (!event.getMember().getEffectiveName().equals(SettingsProvider.getPMUser())) {
	    return;
	}

	final AudioManager am = event.getGuild().getAudioManager();
	am.closeAudioConnection();
	mCaf.reset();
    }

    @Override
    public void onGuildVoiceMove(final GuildVoiceMoveEvent event) {
	handleGuildVoiceJoin(event);

    }

    @Override
    public void onPrivateMessageReceived(final PrivateMessageReceivedEvent event) {
	Logger.Get().log(ELogType.PM,
		" " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());

	if (event.getAuthor().getName().equals("Ativelox")
		&& event.getMessage().getContentDisplay().contains("logout")) {
	    mClient.logout();

	}
    }

    @Override
    public void onReady(final ReadyEvent event) {
	UserUtils.init(mClient);
	init();
	new Thread(mTimeObserver).start();

    }

    @Override
    public void onShutdown(final ShutdownEvent event) {
	mTimeObserver.stop();

	mFormatter.log();
//
//	// TODO: handle the audio file generation better.
//
//	int size = mCaf.numOfPackages();
//	byte[] sound = new byte[3840 * size];
//
//	int i = 0;
//	while (mCaf.hasNext()) {
//	    byte[] currentPackage = mCaf.get20MsAudio();
//
//	    for (int j = 0; j < currentPackage.length; j++) {
//		sound[i] = currentPackage[j];
//		i++;
//	    }
//
//	}
//
//	try {
//	    Files.write(Paths.get(SettingsProvider.getPath() + ProjectPaths.AUDIO_RECEIVE_PATH + "test.raw"), sound,
//		    StandardOpenOption.CREATE);
//	} catch (IOException e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//	}
    }

    @Override
    public void onUserUpdateGame(final UserUpdateGameEvent event) {
	String newName = null;
	String oldName = null;

	if (event.getNewGame() != null) {
	    newName = event.getNewGame().getName();

	}
	if (event.getOldGame() != null) {
	    oldName = event.getOldGame().getName();

	}
	mFormatter.addActivityChange(event.getMember().getEffectiveName(), oldName, newName);

    }

    @Override
    public void onUserUpdateOnlineStatus(final UserUpdateOnlineStatusEvent event) {
	mFormatter.addStatusChange(event.getMember().getEffectiveName(), event.getOldOnlineStatus(),
		event.getNewOnlineStatus());

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ativelox.dichotomyz.callbacks.IDayCallback#onDayPassed()
     */
    @Override
    public void onDayPassed() {
	mFormatter.log();

	mFormatter = new BufferedLogFormatter();
	try {
	    mFormatter.init(UserUtils.getAllUsers());

	} catch (UnexpectedGuildSizeException e) {
	    Logger.Get().log(ELogType.WARNING, e.getMessage());

	}
	mFormatter.updateDate();

    }
}
