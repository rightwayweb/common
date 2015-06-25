package com.zitego.util;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.SocketException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPFile;
import org.w3c.dom.Document;

/**
 *
 * @author John Glorioso
 * @version $Id: FTPUtils.java,v 1.1 2009/05/05 01:47:07 jglorioso Exp $
 */
public class FTPUtils
{
    /**
     * Runs the client from the command line by parsing an xml file and performing the
     * given actions. The xml file has the following form:
     * <pre>
     * <ftp>
     *  <host>199.199.199.199</host>
     *  <port>21</port>
     *  <username>some_user</username>
     *  <password>some_pass</password>
     *  <commands>
     *   <retrieve>
     *    <remote>ftpgeek/ftpinven</remote>
     *    <local>/home/httpd/domains/wave/inventory_file.txt</local>
     *   </retrieve>
     *  </commands>
     * </ftp>
     * </pre>
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length != 6)
        {
            System.out.println("Retrieves a file from a remote ftp server");
            System.out.println("Usage: java FTPUtils <host> <port> <username> <password> <remote_path> <local_path>");
            System.exit(1);
        }
        FTPClient client = connect(args[0], Integer.parseInt(args[1]), args[2], args[3]);
        retrieveFile(client, args[4], args[5]);
    }

    /**
     * Retrieves a file given a connected ftp client, a remote file path, and the local path
     * to where the file should be placed.
     *
     * @param client The connected ftp client.
     * @param remoteFile The remote file path.
     * @param localFile The local file path to copy to.
     * @throws IOException if an error occurs.
     * @throws FileNotFoundException if the destination is invalid.
     */
    public static File retrieveFile(FTPClient client, String remoteFile, String localFile) throws IOException, FileNotFoundException
    {
        File ret = null;
        FTPFile files[] = client.listFiles(remoteFile);
        if (files != null && files.length == 1)
        {
            ret = new File(localFile);
            String file = remoteFile;
            int idx = file.lastIndexOf("/");
            if (idx > -1)
            {
                client.changeWorkingDirectory( file.substring(0, idx) );
                file = file.substring(idx + 1);
            }
            FileOutputStream outfile = new FileOutputStream(ret);
            boolean success = client.retrieveFile(file, outfile);
            if (!success) throw new IOException("ERROR: Could not retrieve "+remoteFile+" to "+localFile+". msg: "+client.getReplyString() );
            outfile.flush();
            outfile.close();
            client.changeWorkingDirectory("/");
        }
        return ret;
    }

    /**
     * Connects to the ftp server and returns a client given a host on port 21, username,
     * and password.
     *
     * @param host The host to connect to.
     * @param username The username to connect as.
     * @param pass The password to connect with.
     * @return FTPClient
     * @throws IOException if an IO error occurs.
     * @throws SocketException if a socket exception occurs.
     * @throws InvalidLoginException if a login error occurs.
     */
    public static FTPClient connect(String host, String username, String pass)
    throws SocketException, IOException, InvalidLoginException
    {
        return connect(host, 21, username, pass);
    }

    /**
     * Connects to the ftp server and returns a client given a host, port (optional), username,
     * and password.
     *
     * @param host The host to connect to.
     * @param port The port to connect to.
     * @param username The username to connect as.
     * @param pass The password to connect with.
     * @return FTPClient
     * @throws IOException if an IO error occurs.
     * @throws SocketException if a socket exception occurs.
     * @throws InvalidLoginException if a login error occurs.
     */
    public static FTPClient connect(String host, int port, String username, String pass)
    throws SocketException, IOException, InvalidLoginException
    {
        FTPClient ret = new FTPClient();
        ret.connect(host);
        if ( !FTPReply.isPositiveCompletion(ret.getReplyCode()) )
        {
            ret.disconnect( );
            System.err.println("FTP server refused connection.");
            System.exit(1);
        }
        boolean connected = ret.login(username, pass);
        if (!connected) throw new InvalidLoginException("Could not connect to "+host+" as "+username+" with password "+pass);

        return ret;
    }
}