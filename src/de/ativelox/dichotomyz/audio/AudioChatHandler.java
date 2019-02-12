package de.ativelox.dichotomzy.audio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;

import de.ativelox.dichotomyz.ProjectPaths;
import de.ativelox.dichotomyz.logging.ELogType;
import de.ativelox.dichotomyz.logging.ILogger;
import de.ativelox.dichotomyz.logging.Logger;
import de.ativelox.dichotomyz.settings.SettingsProvider;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.AudioChannel;

/**
 * This class allows clients to send and receive audio from a
 * {@linkplain AudioChannel}. Currently, it will record everything as combined
 * audio, and send some noise, since otherwise the API won't return audio to
 * this class.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class AudioChatHandler implements AudioReceiveHandler, AudioSendHandler {

    /**
     * A queue representing the packages of audio received.
     */
    private final ArrayDeque<byte[]> mAudio;

    /**
     * The audio sequence to send as noise.
     */
    private byte[] mToSend;

    /**
     * If <tt>true</tt> the client will send 20ms of audio to the AudioChat.
     */
    private boolean mSending;

    /**
     * The logger used for logging.
     */
    private ILogger mLogger;

    /**
     * Creates a new {@link AudioChatHandler}, reading the audio to send as noise
     * from the filename.
     * 
     * @param audioSendName The name of the file used to send audio to the channel
     */
    public AudioChatHandler(final String audioSendName) {
	mAudio = new ArrayDeque<>();
	mLogger = Logger.Get();

	mSending = true;

	try {
	    mToSend = Files
		    .readAllBytes(Paths.get(SettingsProvider.getPath() + ProjectPaths.AUDIO_SEND_PATH + audioSendName));

	} catch (IOException e) {
	    mLogger.log(ELogType.WARNING, "Encountered an IO Exception, when trying to open: "
		    + ProjectPaths.AUDIO_SEND_PATH + audioSendName);

	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.dv8tion.jda.core.audio.AudioSendHandler#canProvide()
     */
    @Override
    public boolean canProvide() {
	if (mSending) {
	    mSending = false;
	    return true;

	}
	return false;

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#canReceiveCombined()
     */
    @Override
    public boolean canReceiveCombined() {
	return true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#canReceiveUser()
     */
    @Override
    public boolean canReceiveUser() {
	return false;

    }

    /**
     * Gets 20ms of combined audio as specified by
     * {@link de.ativelox.dichotomzy.audio.AudioChatHandler#handleCombinedAudio(CombinedAudio)
     * handleCombinedAudio} in the form of bytes. The encoding is specified by
     * {@link net.dv8tion.jda.core.audio.AudioReceiveHandler#OUTPUT_FORMAT
     * OUTPUT_FORMAT}. Using this method until {@link AudioChatHandler#hasNext()
     * hasNext} returns <tt>false</tt>, and concatenating the arrays, will result in
     * a complete audio file for the recorded voice activity.
     * 
     * @return
     */
    public byte[] get20MsAudio() {

	return mAudio.poll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#handleCombinedAudio(net.
     * dv8tion.jda.core.audio.CombinedAudio)
     */
    @Override
    public void handleCombinedAudio(final CombinedAudio combinedAudio) {
	mAudio.add(combinedAudio.getAudioData(1f));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.dv8tion.jda.core.audio.AudioReceiveHandler#handleUserAudio(net.dv8tion.
     * jda.core.audio.UserAudio)
     */
    @Override
    public void handleUserAudio(final UserAudio userAudio) {
	// Nothing todo, since we don't receive user audio.

    }

    /**
     * Whether there's still 20ms of audio to retrieve from this structure or not.
     * 
     * @return <tt>true</tt> if there's still audio to retrieve, <tt>false</tt>
     *         otherwise.
     */
    public boolean hasNext() {
	return !mAudio.isEmpty();

    }

    @Override
    public boolean isOpus() {
	return true;

    }

    /**
     * The number of packages of 20ms audio this structure currently holds.
     * 
     * @return The number specified.
     */
    public int numOfPackages() {
	return mAudio.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.dv8tion.jda.core.audio.AudioSendHandler#provide20MsAudio()
     */
    @Override
    public byte[] provide20MsAudio() {
	return mToSend;

    }

    /**
     * Resets this handler, by resetting the flag {@link AudioChatHandler#mSending}
     * to <tt>true</tt> allowing for rejoining of AudioChannels and receiving audio.
     */
    public void reset() {
	mSending = true;

    }
}
