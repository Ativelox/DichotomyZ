package de.ativelox.dichotomyz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import de.ativelox.dichotomyz.audio.AudioChatHandler;
import de.ativelox.dichotomyz.exceptions.UnexpectedGuildSizeException;
import de.ativelox.dichotomyz.logging.BufferedLogFormatter;
import de.ativelox.dichotomyz.logging.ELogType;
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
public class Listeners extends ListenerAdapter {

    /**
     * The audio chat handler, used to send and receive audio on this client.
     */
    private final AudioChatHandler mCaf;

    /**
     * The formatter used to format given log messages in a user-friendly fashion.
     * Every logging call should be made to this instance, since it forwards those
     * to the underlying {@link ILogger}.
     */
    private final BufferedLogFormatter mFormatter;

    /**
     * The client this listener operates on.
     */
    private final Bot mClient;

    /**
     * Creates a new implementation for the {@link ListenerAdapter} which forwards
     * specific events and handles them to create logs of user activity and the
     * like.
     * 
     * @param client The client this listener operates on.
     */
    public Listeners(final Bot client) {
	mCaf = new AudioChatHandler("unbenannt.raw");

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
	mFormatter.addDebugLog(ELogType.INFO,
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
	    mFormatter.init(UserUtils.getAllUsers(mClient));

	} catch (final UnexpectedGuildSizeException e) {
	    mFormatter.addDebugLog(ELogType.WARNING, "Unexpected amount of guilds, might not work as intended");

	}
    }

    @Override
    public void onGuildVoiceJoin(final GuildVoiceJoinEvent event) {
	handleGuildVoiceJoin(event);
    }

    @Override
    public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event) {
	mFormatter.addDebugLog(ELogType.INFO,
		event.getMember().getEffectiveName() + " left " + event.getChannelLeft().getName());

	if (!event.getMember().getEffectiveName().equals("Ativelox")) {
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
	mFormatter.addPMLog(event.getAuthor().getName(), event.getMessage().getContentDisplay());

	if (event.getAuthor().getName().equals("Ativelox")
		&& event.getMessage().getContentDisplay().contains("logout")) {
	    mClient.logout();

	}
    }

    @Override
    public void onReady(final ReadyEvent event) {
	init();

    }

    @Override
    public void onShutdown(final ShutdownEvent event) {
	mFormatter.log();

	// TODO: handle the audio file generation better.

	int size = mCaf.numOfPackages();
	byte[] sound = new byte[3840 * size];

	int i = 0;
	while (mCaf.hasNext()) {
	    byte[] currentPackage = mCaf.get20MsAudio();

	    for (int j = 0; j < currentPackage.length; j++) {
		sound[i] = currentPackage[j];
		i++;
	    }

	}

	try {
	    Files.write(Paths.get(SettingsProvider.getPath() + ProjectPaths.AUDIO_RECEIVE_PATH + "test.raw"), sound,
		    StandardOpenOption.CREATE);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public void onUserUpdateGame(final UserUpdateGameEvent event) {
	String name = null;
	if (event.getNewGame() != null) {
	    name = event.getNewGame().getName();

	}
	mFormatter.addActivityChange(event.getEntity().getName(), name);
    }

    @Override
    public void onUserUpdateOnlineStatus(final UserUpdateOnlineStatusEvent event) {
	mFormatter.addStatusChange(event.getEntity().getName(), event.getNewOnlineStatus());

    }
}
