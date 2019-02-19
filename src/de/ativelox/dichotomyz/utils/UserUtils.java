package de.ativelox.dichotomyz.utils;

import java.util.List;

import de.ativelox.dichotomyz.Bot;
import de.ativelox.dichotomyz.exceptions.UnexpectedGuildSizeException;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

/**
 * Provides some helper functions to ease user-related access.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class UserUtils {

    /**
     * Gets all users which are currently connected to the same guild as this
     * client. Internally also knowns as {@link Member}.
     * 
     * @param client The client mentioned
     * @return A list of all the users in the same guild as the client
     * @throws UnexpectedGuildSizeException If there were too many, or too few
     *                                      guilds this client is connected to.
     */
    public static List<Member> getAllUsers(final Bot client) throws UnexpectedGuildSizeException {
	final List<Guild> guilds = client.getJDA().getGuilds();

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
