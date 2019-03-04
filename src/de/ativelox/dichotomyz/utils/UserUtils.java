package de.ativelox.dichotomyz.utils;

import java.util.List;

import de.ativelox.dichotomyz.Bot;
import de.ativelox.dichotomyz.exceptions.PrivateChannelNotFoundException;
import de.ativelox.dichotomyz.exceptions.UnexpectedGuildSizeException;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;

/**
 * Provides some helper functions to ease user-related access.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class UserUtils {

    /**
     * A reference to the bot.
     */
    private static Bot _Client;

    /**
     * Whether {@link UserUtils#init(Bot)} has already been called or not.
     */
    private static boolean _initiated = false;

    /**
     * Initiates this class and makes it ready for use. This should be called as
     * soon as {@link Bot#getJDA()} is ready.
     * 
     * @param client The client that is ready.
     */
    public static void init(final Bot client) {
	_initiated = true;

	_Client = client;
    }

    /**
     * Gets the name of this bot if {@link UserUtils#init(Bot)} has been called,
     * otherwise returns an empty string.
     * 
     * @return The string mentioned.
     */
    public static String getOwnName() {
	if (!_initiated) {
	    return "";
	}

	return _Client.getJDA().getSelfUser().getName();
    }

    /**
     * Gets a rest action for the private channel associated with the given name.
     * This is due to the fact that a private channel does not have to exists.
     * 
     * @param name          The name for which to fetch its associated private
     *                      channel.
     * @param discriminator The users discriminator.
     * @return The rest action mentioned.
     * @throws PrivateChannelNotFoundException If the private channel wasn't found
     *                                         or couldn't be established.
     */
    public static RestAction<PrivateChannel> getPrivateChannelFromName(final String name, final String discriminator)
	    throws PrivateChannelNotFoundException {
	if (!_initiated) {
	    return null;

	}

	final List<User> users = _Client.getJDA().getUsersByName(name, true);

	for (final User user : users) {
	    if (user.getDiscriminator().equals(discriminator)) {
		return user.openPrivateChannel();

	    }
	}

	throw new PrivateChannelNotFoundException(
		"A private channel associated with the user name: " + name + " couldn't be found.");
    }

    /**
     * Gets all users which are currently connected to the same guild as this
     * client. Internally also knowns as {@link Member}.
     * 
     * @return A list of all the users in the same guild as the client
     * @throws UnexpectedGuildSizeException If there were too many, or too few
     *                                      guilds this client is connected to.
     */
    public static List<Member> getAllUsers() throws UnexpectedGuildSizeException {
	if (!_initiated) {
	    return null;

	}

	final List<Guild> guilds = _Client.getJDA().getGuilds();

	if (guilds.size() != 1) {
	    throw new UnexpectedGuildSizeException(guilds.size());

	}
	return guilds.get(0).getMembers();

    }

    /**
     * Gets the name of the current game, or returns <tt>null</tt> if the given game
     * was <tt>null</tt>.
     * 
     * @param game The game for which to fetch its name.
     * @return The name of the game, or <tt>null</tt> if the given game was
     *         <tt>null</tt>.
     */
    public static String getUniformGameName(final Game game) {
	String gameName = null;

	if (game != null) {
	    gameName = game.getName();

	}
	return gameName;
    }
}
