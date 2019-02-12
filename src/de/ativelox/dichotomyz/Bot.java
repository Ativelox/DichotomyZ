package de.ativelox.dichotomyz;

import javax.security.auth.login.LoginException;

import de.ativelox.dichotomyz.settings.SettingsProvider;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

/**
 * Provides a container for a built JDA client.
 * 
 * @author Ativelox {@literal<ativelox.dev@web.de>}
 *
 */
public class Bot {

    public static void main(String[] args) {
	new Bot();

    }

    /**
     * The underlying JDA client.
     */
    public JDA mClient;

    /**
     * Builds a {@link JDA} client and initializes the {@link SettingsProvider}.
     */
    public Bot() {
	SettingsProvider.init();

	JDABuilder a = new JDABuilder(SettingsProvider.getToken());
	mClient = null;
	try {
	    a.addEventListener(new Listeners(this));
	    mClient = a.build();

	} catch (final LoginException e) {
	    e.printStackTrace();

	}
    }

    /**
     * Gets the underlying JDA client.
     * 
     * @return The JDA client.
     */
    public JDA getJDA() {
	return mClient;

    }

    /**
     * Logs this client out of discords service.
     */
    public void logout() {
	mClient.shutdown();

    }
}
