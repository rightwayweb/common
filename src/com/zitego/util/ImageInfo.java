package com.zitego.util;

import java.io.*;

/**
 * This class handles determining the width and height of a given image.
 * Provide the class with an absolute path to an image file. If the file
 * is not an image then the check() method will return false. If it returns
 * true, then the number of pixels can be retrieved by getWidth and getHeight.
 *
 * This class is a scaled down version of ImageInfo by Marco Schmidt
 * <http://www.geocities.com/marcoschmidt.geo/contact.html>.
 *
 * @author John Glorioso
 * @version $Id: ImageInfo.java,v 1.2 2012/12/30 07:01:19 jglorioso Exp $
 */
public class ImageInfo
{
    public static final int FORMAT_JPEG = 0;
    public static final int FORMAT_GIF = 1;
    public static final int FORMAT_PNG = 2;
    public static final int FORMAT_BMP = 3;
    public static final int FORMAT_PCX = 4;
    public static final int FORMAT_IFF = 5;
    public static final int FORMAT_RAS = 6;
    public static final int FORMAT_PBM = 7;
    public static final int FORMAT_PGM = 8;
    public static final int FORMAT_PPM = 9;
    public static final int FORMAT_PSD = 10;
    public static final int FORMAT_SWF = 11;
    final byte[] GIF_MAGIC_87A = { 0x46, 0x38, 0x37, 0x61 };
    final byte[] GIF_MAGIC_89A = { 0x46, 0x38, 0x39, 0x61 };
    final byte[] APP0_ID = { 0x4a, 0x46, 0x49, 0x46, 0x00 };
    final byte[] PNG_MAGIC = { 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };
    final int[] PNM_FORMATS = { FORMAT_PBM, FORMAT_PGM, FORMAT_PPM };
    final byte[] PSD_MAGIC = { 0x50, 0x53 };
    final byte[] RAS_MAGIC = { 0x6a, (byte)0x95 };

    /** The image width. */
    private int _width = -1;
    /** The image height. */
    private int _height = -1;

    public static void main(String[] args) throws Exception
    {
        ImageInfo info = new ImageInfo();
        if ( info.check(args[0]) )
        {
            System.out.println( "width: " + info.getWidth() );
            System.out.println( "height: " + info.getHeight() );
        }
        else
        {
            System.out.println("Unsupported file");
        }
    }

    /**
     * Creates a new ImageInfo.
     */
    public ImageInfo() { }

    /**
     * Returns the image width in pixels.
     *
     * @return int
     */
    public int getWidth()
    {
        return _width;
    }

    /**
     * Returns the image height in pixels.
     *
     * @return int
     */
    public int getHeight()
    {
        return _height;
    }

    /**
     * Checks to see if the provided file exists and is a supported image.
     * This returns true if the width and height are able to be loaded.
     *
     * @param String The absolute path of the image.
     * @return boolean
     */
    public boolean check(String path)
    {
        _width = -1;
        _height = -1;
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(path);
            int b1 = read(in) & 0xff;
            int b2 = read(in) & 0xff;
            if (b1 == 0x47 && b2 == 0x49) return checkGif(in);
            else if (b1 == 0x89 && b2 == 0x50) return checkPng(in);
            else if (b1 == 0xff && b2 == 0xd8) return checkJpeg(in);
            else if (b1 == 0x42 && b2 == 0x4d) return checkBmp(in);
            else if (b1 == 0x0a && b2 < 0x06) return checkPcx(in);
            else if (b1 == 0x46 && b2 == 0x4f) return checkIff(in);
            else if (b1 == 0x59 && b2 == 0xa6) return checkRas(in);
            else if (b1 == 0x50 && b2 >= 0x31 && b2 <= 0x36) return checkPnm(in, b2 - '0');
            else if (b1 == 0x38 && b2 == 0x42) return checkPsd(in);
            else if (b1 == 0x46 && b2 == 0x57) return checkSwf(in);
            else return false;
        }
        catch (FileNotFoundException fnfe)
        {
            return false;
        }
        catch (IOException ioe)
        {
            return false;
        }
        finally
        {
            if (in != null)
            try
            {
                in.close();
            }
            catch (IOException ioe) {}
            finally
            {
                in = null;
            }
        }
    }

    private boolean checkBmp(FileInputStream in) throws IOException
    {
        byte[] a = new byte[44];
        if (read(in, a) != a.length) return false;

        _width = getIntLittleEndian(a, 16);
        _height = getIntLittleEndian(a, 20);
        if (_width < 1 || _height < 1) return false;

        return true;
    }

    private boolean checkGif(FileInputStream in) throws IOException
    {
        byte[] a = new byte[11];
        if (read(in, a) != 11) return false;

        if ( (!equals(a, 0, GIF_MAGIC_89A, 0, 4)) && (!equals(a, 0, GIF_MAGIC_87A, 0, 4)) ) return false;

        _width = getShortLittleEndian(a, 4);
        _height = getShortLittleEndian(a, 6);

        return true;
    }

    private boolean checkIff(FileInputStream in) throws IOException
    {
        byte[] a = new byte[10];
        if (read(in, a, 0, 10) != 10) return false;

        final byte[] IFF_RM = {0x52, 0x4d};
        if ( !equals(a, 0, IFF_RM, 0, 2) ) return false;

        int type = getIntBigEndian(a, 6);
        if (type != 0x494c424d && type != 0x50424d20) return false;

        do
        {
            if (read(in, a, 0, 8) != 8) return false;

            int chunkId = getIntBigEndian(a, 0);
            int size = getIntBigEndian(a, 4);
            if ( (size & 1) == 1 ) size++;

            if (chunkId == 0x424d4844)
            {
                if (read(in, a, 0, 9) != 9) return false;
                _width = getShortBigEndian(a, 0);
                _height = getShortBigEndian(a, 2);
                int bitsPerPixel = a[8] & 0xff;
                return (_width > 0 && _height > 0 && bitsPerPixel > 0 && bitsPerPixel < 33);
            }
            else
            {
                skip(in, size);
            }
        }
        while (true);
    }

    private boolean checkJpeg(FileInputStream in) throws IOException
    {
        byte[] data = new byte[12];
        while (true)
        {
            if (read(in, data, 0, 4) != 4) return false;

            int marker = getShortBigEndian(data, 0);
            int size = getShortBigEndian(data, 2);
            if ( (marker & 0xff00) != 0xff00 ) return false;

            if (marker == 0xffe0)
            {
                if (size < 14) return false;

                if (read(in, data, 0, 12) != 12) return false;

                skip(in, size - 14);
            }
            else
            {
                if (size > 2 && marker == 0xfffe)
                {
                    size -= 2;
                    byte[] chars = new byte[size];
                    if (read(in, chars, 0, size) != size) return false;
                }
                else if (marker >= 0xffc0 && marker <= 0xffcf && marker != 0xffc4 && marker != 0xffc8)
                {
                    if (read(in, data, 0, 6) != 6) return false;

                    _width = getShortBigEndian(data, 3);
                    _height = getShortBigEndian(data, 1);
                    return true;
                }
                else
                {
                    skip(in, size - 2);
                }
            }
        }
    }

    private boolean checkPcx(FileInputStream in) throws IOException
    {
        byte[] a = new byte[64];
        if (read(in, a) != a.length) return false;

        if (a[0] != 1) return false;

        // width / height
        int x1 = getShortLittleEndian(a, 2);
        int y1 = getShortLittleEndian(a, 4);
        int x2 = getShortLittleEndian(a, 6);
        int y2 = getShortLittleEndian(a, 8);
        if (x1 < 0 || x2 < x1 || y1 < 0 || y2 < y1) return false;

        _width = x2 - x1 + 1;
        _height = y2 - y1 + 1;

        return true;
    }

    private boolean checkPng(FileInputStream in) throws IOException
    {
        byte[] a = new byte[24];
        if (read(in, a) != 24) return false;

        if ( !equals(a, 0, PNG_MAGIC, 0, 6) ) return false;

        _width = getIntBigEndian(a, 14);
        _height = getIntBigEndian(a, 18);

        return true;
    }

    private boolean checkPnm(FileInputStream in, int id) throws IOException
    {
        if (id < 1 || id > 6) return false;

        int format = PNM_FORMATS[(id - 1) % 3];
        boolean hasPixelResolution = false;
        String s;
        while (true)
        {
            s = readLine(in);
            if (s != null) {
                s = s.trim();
            }
            if (s == null || s.length() < 1) {
                continue;
            }
            if (s.charAt(0) == '#')
            {
                continue;
            }
            if (!hasPixelResolution)
            {
                int spaceIndex = s.indexOf(' ');
                if (spaceIndex == -1) return false;

                String widthString = s.substring(0, spaceIndex);
                spaceIndex = s.lastIndexOf(' ');
                if (spaceIndex == -1) return false;

                String heightString = s.substring(spaceIndex + 1);
                try
                {
                    _width = Integer.parseInt(widthString);
                    _height = Integer.parseInt(heightString);
                }
                catch (NumberFormatException nfe)
                {
                    return false;
                }
                if (_width < 1 || _height < 1) return false;

                if (format == FORMAT_PBM)
                {
                    return true;
                }
                hasPixelResolution = true;
            }
            else
            {
                int maxSample;
                try
                {
                    maxSample = Integer.parseInt(s);
                }
                catch (NumberFormatException nfe)
                {
                    return false;
                }
                if (maxSample < 0) return false;

                for (int i = 0; i < 25; i++)
                {
                    if ( maxSample < (1 << (i + 1)) )
                    {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private boolean checkPsd(FileInputStream in) throws IOException
    {
        byte[] a = new byte[24];
        if (read(in, a) != a.length) return false;

        if ( !equals(a, 0, PSD_MAGIC, 0, 2) ) return false;

        _width = getIntBigEndian(a, 16);
        _height = getIntBigEndian(a, 12);
        return (_width > 0 && _height > 0);
    }

    private boolean checkRas(FileInputStream in) throws IOException
    {
        byte[] a = new byte[14];
        if (read(in, a) != a.length) return false;

        if ( !equals(a, 0, RAS_MAGIC, 0, 2) ) return false;

        _width = getIntBigEndian(a, 2);
        _height = getIntBigEndian(a, 6);
        int bitsPerPixel = getIntBigEndian(a, 10);
        return (_width > 0 && _height > 0 && bitsPerPixel > 0 && bitsPerPixel <= 24);
    }

    private boolean checkSwf(FileInputStream in) throws IOException
    {
        byte[] a = new byte[6];
        if (read(in, a) != a.length) return false;

        int bitSize = (int)readUBits(in, 5);
        int minX = (int)readSBits(in, bitSize);
        int maxX = (int)readSBits(in, bitSize);
        int minY = (int)readSBits(in, bitSize);
        int maxY = (int)readSBits(in, bitSize);
        _width = maxX/20;
        _height = maxY/20;
        return (_width > 0 && _height > 0);
    }

    private int read(FileInputStream in) throws IOException
    {
        if (in != null) return in.read();
        else return -1;
    }

    private int read(FileInputStream in, byte[] a) throws IOException
    {
        if (in != null) return in.read(a);
        else return -1;
    }

    private int read(FileInputStream in, byte[] a, int offset, int num) throws IOException
    {
        if (in != null) return in.read(a, offset, num);
        else return -1;
    }

    private String readLine(FileInputStream in) throws IOException
    {
        return readLine( in, new StringBuffer() );
    }

    private String readLine(FileInputStream in, StringBuffer sb) throws IOException
    {
        boolean finished;
        do
        {
            int value = read(in);
            finished = (value == -1 || value == 10);
            if (!finished) sb.append( (char)value );

        }
        while (!finished);
        return sb.toString();
    }

    public long readUBits(FileInputStream in, int numBits) throws IOException
    {
        if (numBits == 0) return 0;

        int bitsLeft = numBits;
        int bitPos = 0;
        int bitBuf = 0;
        long result = 0;
        if (bitPos == 0)
        {
            if (in != null) bitBuf = in.read();
            bitPos = 8;
        }

        while (true)
        {
            int shift = bitsLeft - bitPos;
            if (shift > 0)
            {
                result |= bitBuf << shift;
                bitsLeft -= bitPos;

                if (in != null) bitBuf = in.read();

                bitPos = 8;
            }
            else
            {
                 result |= bitBuf >> -shift;
                bitPos -= bitsLeft;
                bitBuf &= 0xff >> (8 - bitPos);

                return result;
            }
        }
    }

    private int readSBits(FileInputStream in, int numBits) throws IOException
    {
        long uBits = readUBits(in, numBits);

        if ( (uBits & (1L << (numBits - 1))) != 0 )
        {
            uBits |= -1L << numBits;
        }

        return (int)uBits;
    }

    private void skip(FileInputStream in, int num) throws IOException
    {
        while (num > 0)
        {
            long result = -1;
            if (in != null) result = in.skip(num);
            if (result > 0) num -= result;
        }
    }

    private boolean equals(byte[] a1, int offs1, byte[] a2, int offs2, int num)
    {
        while (num-- > 0)
        {
            if (a1[offs1++] != a2[offs2++]) return false;
        }
        return true;
    }

    private int getIntBigEndian(byte[] a, int offs)
    {
        return (a[offs] & 0xff) << 24 | (a[offs + 1] & 0xff) << 16 | (a[offs + 2] & 0xff) << 8 | a[offs + 3] & 0xff;
    }

    private int getIntLittleEndian(byte[] a, int offs)
    {
        return (a[offs + 3] & 0xff) << 24 | (a[offs + 2] & 0xff) << 16 | (a[offs + 1] & 0xff) << 8 | a[offs] & 0xff;
    }

    private int getShortBigEndian(byte[] a, int offs)
    {
        return (a[offs] & 0xff) << 8 | (a[offs + 1] & 0xff);
    }

    private int getShortLittleEndian(byte[] a, int offs)
    {
        return (a[offs] & 0xff) | (a[offs + 1] & 0xff) << 8;
    }
}