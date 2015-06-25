package com.zitego.util;

/**
 * Encodes and decodes text.
 *
 * @author John Glorioso
 * @version $Id: Hex.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class Hex
{
    private static char[] digits =
    {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f'
    };

    public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage: java Hex <(en)code|(de)code> <string>");
			System.exit(1);
		}
		boolean encode = (args[0].equalsIgnoreCase("en") || args[0].equalsIgnoreCase("encode"));
		System.out.println("In: "+args[1]);
		System.out.println("Out: "+(encode?encode(args[1]):decode(args[1])));
	}

    /**
     * Decodes a string of text.
     *
     * @param String The text to decode.
     * @return String
     */
    public static String decode(String text)
    {
        return decode( text.toCharArray() );
    }

    /**
     * Decodes an array of characters.
     *
     * @param char[] The characters.
     * @return String
     */
    public static String decode(char[] data)
    {
        return new String( decodeToByteArray(data) );
    }

    /**
     * Decodes an array of characters to bytes.
     *
     * @param char[] The characters.
     * @return byte[]
     */
    public static byte[] decodeToByteArray(char[] data)
    {
        int l = data.length;

        if ( (l&0x01) != 0 ) throw new RuntimeException("Odd number of characters.");

        byte[] out = new byte[l >> 1];

        // two characters form the hex value.
        for (int i=0, j=0; j<l; i++)
        {
            int f = Character.digit(data[j++], 16) << 4;
            f = f | Character.digit(data[j++], 16);
            out[i] = (byte)(f & 0xFF);
        }
        return out;
    }

    /**
     * Encodes a string of text.
     *
     * @param String The text to encode.
     * @return String
     */
    public static String encode(String text)
    {
        return encode( text.getBytes() );
    }

    /**
     * Encodes an array of bytes.
     *
     * @param byte[] The bytes.
     * @return String
     */
    public static String encode(byte[] data)
    {
        int l = data.length;

        char[] out = new char[l << 1];

        // two characters form the hex value.
        for (int i=0, j=0; i<l; i++)
        {
            out[j++] = digits[(0xF0 & data[i]) >>> 4 ];
            out[j++] = digits[ 0x0F & data[i] ];
        }
        return new String(out);
    }
}