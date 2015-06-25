package com.zitego.util;

import java.io.*;

/**
 * Static methods to process files.
 *
 * @author John Glorioso
 * @version $Id: FileUtils.java,v 1.2 2008/07/06 23:42:03 jglorioso Exp $
 */
public class FileUtils
{
    public static void main(String[] args) throws Exception
    {
        System.out.println( FileUtils.getSizeOfDirectory(new File(args[0])) );
    }

    /**
     * Returns the contents of the given filename as a String or null if the
     * filename is null.
     *
     * @param String The file path.
     * @return String
     * @throws IOException when an io error occurs reading the file.
     * @throws FileNotFoundException if the file does not exist.
     */
    public static String getFileContents(String path) throws IOException, FileNotFoundException
    {
        return getFileContents( new File(path) );
    }

    /**
     * Returns the contents of the given file as a String or null if the
     * file is null.
     *
     * @param File The file.
     * @return String
     * @throws IOException when an io error occurs reading the file.
     * @throws FileNotFoundException if the file does not exist.
     */
    public static String getFileContents(File file) throws IOException, FileNotFoundException
    {
        if (file == null) return null;
        StringBuffer ret = new StringBuffer();
        BufferedReader in = new BufferedReader( new FileReader(file) );
        char[] buffer = new char[1024];
        int charsRead = -1;
        while ( (charsRead=in.read(buffer, 0, buffer.length)) != -1 )
        {
            ret.append(buffer, 0, charsRead);
        }
        in.close();
        return ret.toString();
    }

    /**
     * Writes out a file given the file path and text contents.
     *
     * @param String The file path.
     * @param String The file contents.
     * @throws IOException
     */
    public static void writeFileContents(String path, String contents) throws IOException
    {
        writeFileContents( path, (contents == null ? null : contents.getBytes()) );
    }

    /**
     * Writes out a file given the file path and a byte array of contents.
     *
     * @param String The path.
     * @param byte[] The contents.
     * @throws IOException
     */
    public static void writeFileContents(String path, byte[] contents) throws IOException
    {
        FileOutputStream out = new FileOutputStream( new File(path) );
        if (contents == null) contents = new byte[0];
        out.write(contents, 0, contents.length);
        out.close();
    }

    /**
     * Returns the size of a directory in bytes including all subdirectories.
     *
     * @param File The directory.
     * @return long The size.
     */
    public static long getSizeOfDirectory(File dir)
    {
        long size = 0;
        if ( dir == null || !dir.exists() ) return size;
        File[] files = dir.listFiles();
        if (files == null) files = new File[0];
        for (int i=0; i<files.length; i++)
        {
            if ( files[i].isDirectory() ) size += getSizeOfDirectory(files[i]);
            else size += files[i].length();
        }
        return size;
    }
}