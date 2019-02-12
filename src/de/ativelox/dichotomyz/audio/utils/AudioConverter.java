package de.ativelox.dichotomyz.audio.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import de.ativelox.dichotomyz.ProjectPaths;
import de.ativelox.dichotomyz.logging.ELogType;
import de.ativelox.dichotomyz.logging.Logger;
import de.ativelox.dichotomyz.settings.SettingsProvider;

/**
 * Provides methods to convert audio formats.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class AudioConverter {

    /**
     * Does the same as {@link AudioConverter#byteConcatenation4(byte, byte)
     * byteConcatenation4} by leaving first and second empty.
     * 
     * @see AudioConverter#byteConcatenation4(byte, byte)
     */
    private static int byteConcatenation2(byte first, byte second) {
	int firstShift = cutOffLeadingBits(first, 24) << 8;
	int secondShift = cutOffLeadingBits(second, 24);

	return firstShift | secondShift;
    }

    /**
     * Concatenates 4 bytes into a (32 bit) integer. Where first fill occupy the
     * highest 8 bits and fourth the lowest 8 bits.
     * 
     * @param first  The byte to fill the highest 8 bits, starting from 2^31 to 2^24
     * @param second The byte to fill the next 8 bits, starting from 2^23 to 2^16
     * @param third  The byte to fill the next 8 bits, starting from 2^15 to 2^8
     * @param fourth The byte to fill the lowest 8 bits, starting from 2^7 to 2^0
     * 
     * @return The integer associated with the byte concatenation mentioned.
     */
    private static int byteConcatenation4(byte first, byte second, byte third, byte fourth) {

	int firstShift = cutOffLeadingBits(first, 24) << 24;
	int secondShift = cutOffLeadingBits(second, 24) << 16;
	int thirdShift = cutOffLeadingBits(third, 24) << 8;
	int fourthShift = cutOffLeadingBits(fourth, 24);

	return firstShift | secondShift | thirdShift | fourthShift;
    }

    /**
     * Cuts off the highest k bits from the given integer.
     * 
     * @param x The integer from which to cut high bits.
     * @param k The number of bits to cut from x.
     * @return An integer, which represents x with k high bits cut off.
     */
    private static int cutOffLeadingBits(final int x, int k) {
	return x & (int) Math.pow(2, 32 - k) - 1;
    }

    /**
     * Generates a .wav file from the given data.
     * 
     * @param data          The array of bytes containing the data of the audio.
     * @param sampleRate    The sample rate, given in Hertz.
     * @param numOfChannels The number of channels used in the audio data, 1 for
     *                      mono, 2 for stereo, etc.
     * @param bitDepth      The number of bits used per sample.
     * @param type          The type of the audio to be converted to a .wav file.
     * 
     * @return A byte array which is a can be read as a .wav file.
     */
    public static byte[] generateWAVFile(final byte[] data, final int sampleRate, final int numOfChannels,
	    final int bitDepth, final EAudioType type) {

	int typeIdenfifier = 0;

	if (type == EAudioType.PCM) {
	    typeIdenfifier = 1;

	} else {
	    Logger.Get().log(ELogType.WARNING, "Unsupported audio type: " + type.toString());
	    return null;

	}

	final int frameSize = numOfChannels * ((int) Math.floor((bitDepth + 7) / 8));

	// 12 bytes for the riff-header, 24 for the format-header and 8 for the
	// data-header.
	final byte[] resultData = new byte[data.length + 12 + 24 + 8];

	// generate RIFF-header
	resultData[0] = 'R';
	resultData[1] = 'I';
	resultData[2] = 'F';
	resultData[3] = 'F';

	final byte[] chunkSize = getBytesFrom(resultData.length - 8, 4);
	resultData[4] = chunkSize[0];
	resultData[5] = chunkSize[1];
	resultData[6] = chunkSize[2];
	resultData[7] = chunkSize[3];

	resultData[8] = 'W';
	resultData[9] = 'A';
	resultData[10] = 'V';
	resultData[11] = 'E';

	// generate Format-header
	resultData[12] = 'f';
	resultData[13] = 'm';
	resultData[14] = 't';
	resultData[15] = ' ';

	final byte[] remainingHeaderLength = getBytesFrom(16, 4);
	resultData[16] = remainingHeaderLength[0];
	resultData[17] = remainingHeaderLength[1];
	resultData[18] = remainingHeaderLength[2];
	resultData[19] = remainingHeaderLength[3];

	final byte[] formatTag = getBytesFrom(typeIdenfifier, 2);
	resultData[20] = formatTag[0];
	resultData[21] = formatTag[1];

	final byte[] channels = getBytesFrom(numOfChannels, 2);
	resultData[22] = channels[0];
	resultData[23] = channels[1];

	final byte[] sampleRateByte = getBytesFrom(sampleRate, 4);
	resultData[24] = sampleRateByte[0];
	resultData[25] = sampleRateByte[1];
	resultData[26] = sampleRateByte[2];
	resultData[27] = sampleRateByte[3];

	final byte[] bytesPerSecond = getBytesFrom(sampleRate * frameSize, 4);
	resultData[28] = bytesPerSecond[0];
	resultData[29] = bytesPerSecond[1];
	resultData[30] = bytesPerSecond[2];
	resultData[31] = bytesPerSecond[3];

	final byte[] blockAlign = getBytesFrom(frameSize, 2);
	resultData[32] = blockAlign[0];
	resultData[33] = blockAlign[1];

	final byte[] bitsPerSample = getBytesFrom(bitDepth, 2);
	resultData[34] = bitsPerSample[0];
	resultData[35] = bitsPerSample[1];

	// generate data-header
	resultData[36] = 'd';
	resultData[37] = 'a';
	resultData[38] = 't';
	resultData[39] = 'a';

	final byte[] remainingLength = getBytesFrom(resultData.length - 44, 4);
	resultData[40] = remainingLength[0];
	resultData[41] = remainingLength[1];
	resultData[42] = remainingLength[2];
	resultData[43] = remainingLength[3];

	// conversion from little-endian to big-endian
	// TODO: make it more dynamic given the bitDepth, only works for 16 bit atm.
	for (int i = 0; i < data.length; i += bitDepth / 2) {
	    resultData[44 + i] = data[i + 1];
	    resultData[44 + i + 1] = data[i];
	}

	return resultData;

    }

    /**
     * Gets up to 4 bytes from the given integer x, such that the concatenation of
     * the entries of the returned result will yield x in byte representation.
     * 
     * @param x The integer which to dissect into k bytes.
     * @param k The number of bytes to fetch from x.
     * @return An array containing the byte representation of x.
     */
    private static byte[] getBytesFrom(final int x, final int k) {

	byte[] result = new byte[k];

	for (int i = 0; i < k; i++) {
	    result[i] = (byte) ((x >> 8 * i) & (int) Math.pow(2, 8) - 1);
	}

	return result;
    }

    public static void main(final String[] main) {

	// TODO: remove this later, this is solely used for debugging currently.

	try {
	    byte[] toWrite = generateWAVFile(
		    Files.readAllBytes(
			    Paths.get(SettingsProvider.getPath() + ProjectPaths.AUDIO_RECEIVE_PATH + "test.raw")),
		    44800, 2, 16, EAudioType.PCM);

	    Files.write(Paths.get(SettingsProvider.getPath() + ProjectPaths.AUDIO_RECEIVE_PATH + "test.wav"), toWrite,
		    StandardOpenOption.CREATE);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Prints the RIFF-header, Format-header and the data-header of the byte array,
     * which are given in the .wav specifications.
     * 
     * @param data The .wav data from which to get the headers.
     */
    public static void printWAVHeader(final byte[] data) {
	System.out.println("RIFF-Header:");
	System.out.println("\tchunckID: " + (char) data[0] + (char) data[1] + (char) data[2] + (char) data[3]);
	System.out.println("\tChunckSize: " + byteConcatenation4(data[7], data[6], data[5], data[4]));
	System.out.println("\triffType: " + (char) data[8] + (char) data[9] + (char) data[10] + (char) data[11]);

	System.out.println();

	System.out.println("Format-Header:");
	System.out.println("\tsignature: " + (char) data[12] + (char) data[13] + (char) data[14] + (char) data[15]);
	System.out.println("\tremaining header length: " + byteConcatenation4(data[19], data[18], data[17], data[16]));
	System.out.println("\twFormatTag: " + byteConcatenation2(data[21], data[20]));
	System.out.println("\twChannels: " + byteConcatenation2(data[23], data[22]));
	System.out.println("\twSampleRate: " + byteConcatenation4(data[27], data[26], data[25], data[24]));
	System.out.println("\tBytes/Second: " + byteConcatenation4(data[31], data[30], data[29], data[28]));
	System.out.println("\tblock-align: " + byteConcatenation2(data[33], data[32]));
	System.out.println("\tBits/Sample: " + byteConcatenation2(data[35], data[34]));

	System.out.println();

	System.out.println("Data-Header:");
	System.out.println("\tsignature: " + (char) data[36] + (char) data[37] + (char) data[38] + (char) data[39]);
	System.out.println("\tdata-length: " + byteConcatenation4(data[43], data[42], data[41], data[40]));
    }

    /**
     * Prints the RIFF-header, Format-header and the data-header of the given file,
     * which are given in the .wav specifications.
     * 
     * @param filename The name of the file from which to get the headers.
     */
    public static void printWAVHeader(final String filename) {
	byte[] test = new byte[0];

	try {
	    test = Files.readAllBytes(Paths.get(ProjectPaths.DEBUG_PATH + filename));

	} catch (IOException e) {
	    Logger.Get().log(ELogType.WARNING,
		    "An IO exception occured while trying to open the file: " + ProjectPaths.DEBUG_PATH + filename);

	}
	printWAVHeader(test);

    }
}
