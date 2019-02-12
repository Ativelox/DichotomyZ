package de.ativelox.dichotomyz.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Utility class that provides methods to get timestamps of dates and time in
 * general.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class Timestamp {

    /**
     * The formatter used for dates, in the format dd.MM.yyyy
     */
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * The formatter used for time.
     */
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Fits the given string to the length given. If the given string is too short,
     * it will be filled with leading zeros, and if to short, it will be cut off.
     * 
     * @param toFit The String to fit
     * @param k     The number of characters in the string to trim to.
     * @return A string of length <tt>k</tt> in the mentioned format.
     */
    private static String fitStringToLength(final String toFit, final int k) {
	String modified = toFit;

	while (modified.length() < k) {
	    modified = "0" + modified;

	}

	if (modified.length() > k) {
	    modified = modified.substring(0, k);

	}
	return modified;

    }

    /**
     * Returns a formatted string, given a formatter, in the UTC+1 (Berlin) time
     * zone.
     * 
     * @param formatter The formatter to format the current {@link LocalDateTime}
     *                  object.
     * @return The formatted string mentioned.
     */
    private static String getByFormatter(final DateTimeFormatter formatter) {
	return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.ofOffset("UTC", ZoneOffset.ofHours(1)))
		.format(formatter);
    }

    /**
     * Gets the current date, where the time zone is specified by
     * {@link Timestamp#getByFormatter(DateTimeFormatter)}, formatted by
     * {@link Timestamp#DATE_FORMATTER}.
     * 
     * @return A formatted string representing the date
     */
    public static String getCurrentDate() {
	return getByFormatter(DATE_FORMATTER);

    }

    /**
     * Gets the current time, where the time zone is specified by
     * {@link Timestamp#getByFormatter(DateTimeFormatter)}, formatted by
     * {@link Timestamp#TIME_FORMATTER}.
     * 
     * @return A formatted string representing the time.
     */
    public static String getCurrentTime() {
	return getByFormatter(TIME_FORMATTER);

    }

    /**
     * Converts the given time in milliseconds to a human-readable time
     * representation (hh:mm:ss).
     * 
     * @param ms The milliseconds to convert.
     * @return A formatted string representing the given time.
     */
    public static String msToReadable(final long ms) {
	final int seconds = Math.round(ms / 1000f);
	final int minutes = Math.round(ms / (1000f * 60f));
	final int hours = Math.round(ms / (1000f * 60f * 60f));

	return fitStringToLength(String.valueOf(hours), 2) + ":" + fitStringToLength(String.valueOf(minutes), 2) + ":"
		+ fitStringToLength(String.valueOf(seconds), 2);

    }

    private Timestamp() {

    }
}
