package de.ativelox.dichotomyz.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

/**
 * Provides simplification for the most basic file operations, on underlying FTP
 * clients.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class FTPUtils {

    private FTPUtils() {

    }

    /**
     * Checks whether the given file under the path given does exist. Assumes that
     * the client is logged in, otherwise the behavior is undefined.
     * 
     * @param client   The currently logged in client.
     * @param path     The path to the directory the file is in.
     * @param filename The filename of the file.
     * @return <tt>true</tt> if the file exists in the given path, <tt>false</tt>
     *         otherwise.
     * @throws IllegalStateException    If the client is not connected or not
     *                                  authenticated.
     * @throws IOException              If an I/O error occurs.
     * @throws FTPIllegalReplyException If the server replies in an illegal way.
     * @throws FTPException             If the operation fails.
     * @throws FTPDataTransferException If a I/O occurs in the data transfer
     *                                  connection. If you receive this exception
     *                                  the transfer failed, but the main connection
     *                                  with the remote FTP server is in theory
     *                                  still working.
     * @throws FTPAbortedException      If operation is aborted by another thread.
     * @throws FTPListParseException    If none of the registered parsers can handle
     *                                  the response sent by the server.
     */
    public static boolean fileExists(final FTPClient client, final String path, final String filename)
	    throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException,
	    FTPAbortedException, FTPListParseException {
	FTPUtils.changeDir(client, path);

	for (final FTPFile file : client.list()) {
	    if (file.getName().equals(filename)) {
		return true;

	    }
	}
	return false;

    }

    /**
     * Changes the directory of the given client to the given directoy. Assumes that
     * the given client is already logged in, behavior is unspecified if not.
     * 
     * @param client The currently logged in client.
     * @param dir    The directory to switch to.
     * @throws IllegalStateException    If the client is not connected or not
     *                                  authenticated.
     * @throws IOException              If an I/O error occurs.
     * @throws FTPIllegalReplyException If the server replies in an illegal way.
     * @throws FTPException             If the operation fails.
     */
    public static void changeDir(final FTPClient client, final String dir)
	    throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException {
	String curDir = client.currentDirectory();

	while (true) {
	    client.changeDirectoryUp();

	    if (curDir.equals(client.currentDirectory())) {
		break;

	    }
	    curDir = client.currentDirectory();

	}

	// the client is currently at the top level.

	final String[] dirs = dir.split("//");

	for (int i = 0; i < dirs.length; i++) {
	    client.changeDirectory(dirs[i] + "/");

	}
    }

    /**
     * Writes the given data under the given filename into the given path. Assumes
     * that the client is already logged in, the behavior is unspecified if
     * otherwise.
     * 
     * @param client   The currently logged in client.
     * @param path     The path to the directoy the file should be written to.
     * @param filename The name of the file.
     * @param data     The data to write.
     * @throws IllegalStateException    If the client is not connected or not
     *                                  authenticated.
     * @throws IOException              If an I/O error occurs.
     * @throws FTPIllegalReplyException If the server replies in an illegal way.
     * @throws FTPException             If the operation fails.
     * @throws FTPDataTransferException If a I/O occurs in the data transfer
     *                                  connection. If you receive this exception
     *                                  the transfer failed, but the main connection
     *                                  with the remote FTP server is in theory
     *                                  still working.
     * @throws FTPAbortedException      If operation is aborted by another thread.
     */
    public static void writeFile(final FTPClient client, final String path, final String filename, final byte[] data)
	    throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException,
	    FTPAbortedException {
	FTPUtils.changeDir(client, path);
	client.upload(filename, new ByteArrayInputStream(data), 0, 0, null);

    }
}
