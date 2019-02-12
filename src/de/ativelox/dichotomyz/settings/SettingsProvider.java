package de.ativelox.dichotomyz.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import de.ativelox.dichotomyz.logging.ELogType;
import de.ativelox.dichotomyz.logging.Logger;

/**
 * A utility class which is used to read settings from a file, and if not
 * present prompts the user for the needed information to create said file. Then
 * provides functions such as {@link SettingsProvider#getToken()} to retrieve
 * the settings.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class SettingsProvider {

    /**
     * The name of the file from which to get the settings data.
     */
    private static final String CONFIG_NAME = "config.ini";

    /**
     * The identifier used within the config file to identify the token setting.
     */
    private static final String TOKEN_IDENTIFIER = "token";

    /**
     * The identifier used withing the config file to identiy the path setting.
     */
    private static final String LOG_FOLDER = "path";

    /**
     * The map, which represents the settings file as structure.
     */
    private static Map<String, String> _Settings;

    /**
     * Writes a config file, given the user input, in the correct format.
     * 
     * @throws IOException when there was an IOException writing the config file
     */
    private static void generateFileFromUserInput() throws IOException {
	final Scanner scanner = new Scanner(System.in);

	System.out.print("Enter the value for the token: ");
	final String tokenValue = scanner.nextLine();
	System.out.println();

	System.out.print("Enter the value for the general log folder path: ");
	final String logFolderValue = scanner.nextLine();
	System.out.println();

	scanner.close();

	final String toWrite = TOKEN_IDENTIFIER + "=" + tokenValue + "\r\n" + LOG_FOLDER + "=" + logFolderValue;
	Files.write(Paths.get(CONFIG_NAME), toWrite.getBytes(), StandardOpenOption.CREATE);
    }

    /**
     * Sets up the internally used {@link SettingsProvider#_Settings} map from a
     * given path. It's assumed that the file associated with the path does exist.
     * 
     * @param path The path to an existing config file
     * @throws IOException when there was an IOException trying to access the config
     *                     file described by the given path.
     */
    private static void generateFromExistingFile(final Path path) throws IOException {
	final List<String> lines = Files.readAllLines(path);

	for (final String line : lines) {
	    String[] split = line.split("=");
	    _Settings.put(split[0], split[1]);

	}
    }

    /**
     * Gets the value for the given key in the settings file.
     * 
     * @param key The key for which to fetch its value.
     * @return The value associated with the key.
     */
    private static String get(final String key) {
	return _Settings.get(key);

    }

    /**
     * Gets the default path from the settings file.
     * 
     * @return The path mentioned.
     */
    public static String getPath() {
	return get(LOG_FOLDER);
    }

    /**
     * Gets the token from the settings file.
     * 
     * @return The token mentioned.
     */
    public static String getToken() {
	return get(TOKEN_IDENTIFIER);
    }

    /**
     * Initializes this {@link SettingsProvider}. Checks whether the config file is
     * present, and if it isn't it creates one by prompting the user for needed
     * input.
     */
    public static void init() {
	_Settings = new HashMap<>();

	final Path configPath = Paths.get(CONFIG_NAME);

	try {
	    if (Files.exists(configPath)) {
		generateFromExistingFile(configPath);

	    } else {
		generateFileFromUserInput();
		generateFromExistingFile(configPath);

	    }
	} catch (final IOException e) {
	    Logger.Get().log(ELogType.WARNING, "An IOException occured when trying to access the file: " + CONFIG_NAME);

	}
    }
}
