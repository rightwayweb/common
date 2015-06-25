package com.zitego.http;

/**
 * Provides encoding of raw bytes to base64-encoded characters, and
 * decoding of base64 characters to raw bytes.
 *
 * @author Kevin Kelley (kelley@ruralnet.net)
 * @version 1.3
 */
public abstract class Base64 extends Object
{
    /** The number of characters per line for encode with newlines. It is 76. */
    public static final int CHARS_PER_LINE = 76;
    /** code characters for values 0..63 */
    private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

    /** lookup table for converting base64 characters to value in range 0..63 */
    static private byte[] codes = new byte[256];
    static
    {
        for (int i=0; i<256; i++) codes[i] = -1;
        for (int i = 'A'; i <= 'Z'; i++) codes[i] = (byte)(     i - 'A');
        for (int i = 'a'; i <= 'z'; i++) codes[i] = (byte)(26 + i - 'a');
        for (int i = '0'; i <= '9'; i++) codes[i] = (byte)(52 + i - '0');
        codes['+'] = 62;
        codes['/'] = 63;
    }

    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Usage: java Base64 <(en)code|(de)code> <string>");
            System.exit(1);
        }
        boolean encode = (args[0].equalsIgnoreCase("en") || args[0].equalsIgnoreCase("encode"));
        System.out.println("In: "+args[1]);
        System.out.println("Out: "+(encode?encode(args[1]):decode(args[1])));
    }

    /**
     * returns an array of base64-encoded characters to represent the
     * passed data array.
     *
     * @param String The string to encode.
     * @return String
     */
    public static String encode(String instr)
    {
        if (instr == null) return null;
        else return encode( instr.getBytes() );
    }

    /**
     * Returns an array of base64-encoded characters to represent the
     * passed data array.
     *
     * @param data the array of bytes to encode
     * @return String
     */
    public static String encode(byte[] data)
    {
    	char[] out = new char[((data.length + 2) / 3) * 4];

    	//
    	// 3 bytes encode to 4 chars.  Output is always an even
    	// multiple of 4 characters.
    	//
    	for (int i=0, index=0; i<data.length; i+=3, index+=4)
    	{
    	    boolean quad = false;
    	    boolean trip = false;

    	    int val = (0xFF & (int) data[i]);
    	    val <<= 8;
    	    if ( (i+1) < data.length )
    	    {
        		val |= (0xFF & (int) data[i+1]);
        		trip = true;
    	    }
    	    val <<= 8;
    	    if ( (i+2) < data.length )
    	    {
        		val |= (0xFF & (int) data[i+2]);
        		quad = true;
    	    }
    	    out[index+3] = alphabet[(quad? (val & 0x3F): 64)];
    	    val >>= 6;
    	    out[index+2] = alphabet[(trip? (val & 0x3F): 64)];
    	    val >>= 6;
    	    out[index+1] = alphabet[val & 0x3F];
    	    val >>= 6;
    	    out[index+0] = alphabet[val & 0x3F];
    	}
    	return new String(out);
    }

    /**
     * Returns an array of base64-encoded characters to represent the
     * passed data array.
     *
     * Also includes line breaks ('\n' characters) so that the maximum
     * length of a line is 76 characters (which is required by the RFC
     * for mail attachments.)
     *
     * The returned string alwaqys ends with a newline.
     *
     * @param data the array of bytes to encode.
     * @return String
     */
    public static String encodeWithNewlines(String data)
    {
        if (data == null) return null;
        else return encodeWithNewlines( data.getBytes() );
    }

    /**
     * Returns an array of base64-encoded characters to represent the
     * passed data array.
     *
     * Also includes line breaks ('\n' characters) so that the maximum
     * length of a line is 76 characters (which is required by the RFC
     * for mail attachments.)
     *
     * The returned string alwaqys ends with a newline.
     *
     * @param data the array of bytes to encode.
     * @return String
     */
    public static String encodeWithNewlines(byte[] data)
    {
        char[] raw = encode(data).toCharArray();
        // number of newlines is ceil(raw.length/CHARS_PER_LINE)
        int numNewLines = (raw.length % CHARS_PER_LINE == 0 ?
                                raw.length / CHARS_PER_LINE :
                                raw.length / CHARS_PER_LINE + 1);
        char[] result = new char[raw.length+numNewLines];
        // copy the segments preceding each newline (and the newline
        // itself) into result
        for (int i=0; i<numNewLines-1; i++)
        {
            // copy segment
            System.arraycopy
            (
                raw, i*CHARS_PER_LINE, result, i*(CHARS_PER_LINE+1), CHARS_PER_LINE
            );
            // copy newline
            result[i*(CHARS_PER_LINE+1) + CHARS_PER_LINE] = '\n';
        }
        // copy the remainder -- the last linefull
        int remainder = raw.length % CHARS_PER_LINE;
        // if we have a multiple of CHARS_PER_LINE, then remainder
        // will be 0 but we still want to copy over the last set of
        // newlines
        if (remainder == 0) { remainder = CHARS_PER_LINE; }
        System.arraycopy
        (
            raw, raw.length-remainder, result, result.length-remainder-1, remainder
        );

        // fill in the last newline
        result[result.length - 1] = '\n';
        return new String(result);
    }
    
    /**
     * Decodes a BASE-64 encoded stream to recover the original
     * data. White space before and after will be trimmed away,
     * but no other manipulation of the input will be performed.
     *
     * As of version 1.2 this method will properly handle input
     * containing junk characters (newlines and the like) rather
     * than throwing an error. It does this by pre-parsing the
     * input and generating from that a count of VALID input
     * characters.
     *
     * @param arr The bytes to decode.
     * @return String
     **/
    public static String decode(byte[] arr)
    {
        if (arr == null) return null;
        else return new String( decodeToBytes(arr) );
    }

    /**
     * Decodes a BASE-64 encoded stream to recover the original
     * data. White space before and after will be trimmed away,
     * but no other manipulation of the input will be performed.
     *
     * As of version 1.2 this method will properly handle input
     * containing junk characters (newlines and the like) rather
     * than throwing an error. It does this by pre-parsing the
     * input and generating from that a count of VALID input
     * characters.
     *
     * @param The string to decode.
     * @return String
     **/
    public static String decode(String in)
    {
        if (in == null) return null;
        else return new String( decodeToBytes(in) );
    }
    
    /**
     * Decodes a BASE-64 encoded stream to recover the original
     * data. White space before and after will be trimmed away,
     * but no other manipulation of the input will be performed.
     *
     * As of version 1.2 this method will properly handle input
     * containing junk characters (newlines and the like) rather
     * than throwing an error. It does this by pre-parsing the
     * input and generating from that a count of VALID input
     * characters.
     *
     * @param in The byte[] to decode.
     * @return byte[]
     **/
    public static byte[] decodeToBytes(byte[] in)
    {
        if (in == null) return null;
        else return decodeToBytes( new String(in) );
    }
    
    /**
     * Decodes a BASE-64 encoded stream to recover the original
     * data. White space before and after will be trimmed away,
     * but no other manipulation of the input will be performed.
     *
     * As of version 1.2 this method will properly handle input
     * containing junk characters (newlines and the like) rather
     * than throwing an error. It does this by pre-parsing the
     * input and generating from that a count of VALID input
     * characters.
     *
     * @param in The String to decode.
     * @return byte[]
     **/
    public static byte[] decodeToBytes(String in)
    {
        if (in == null) return null;
        char[] data = in.toCharArray();

    	// as our input could contain non-BASE64 data (newlines,
    	// whitespace of any sort, whatever) we must first adjust
    	// our count of USABLE data so that...
    	// (a) we don't misallocate the output array, and
    	// (b) think that we miscalculated our data length
    	//     just because of extraneous throw-away junk
    	int tempLen = data.length;
    	for(int ix=0; ix<data.length; ix++)
    	{
    	    // ignore non-valid chars and padding
    		if( (data[ix] > 255) || codes[ data[ix] ] < 0 ) --tempLen;
    	}

    	// calculate required length:
    	//  -- 3 bytes for every 4 valid base64 chars
    	//  -- plus 2 bytes if there are 3 extra base64 chars,
    	//     or plus 1 byte if there are 2 extra.
    	int len = (tempLen / 4) * 3;
    	if ( (tempLen % 4) == 3 ) len += 2;
    	if ( (tempLen % 4) == 2 ) len += 1;

    	byte[] out = new byte[len];

    	int shift = 0;   // # of excess bits stored in accum
    	int accum = 0;   // excess bits
    	int index = 0;

    	// we now go through the entire array (NOT using the 'tempLen' value)
    	for (int ix=0; ix<data.length; ix++)
    	{
    		int value = (data[ix]>255)? -1: codes[ data[ix] ];

    		if (value >= 0)             // skip over non-code
    		{
    			accum <<= 6;            // bits shift up by 6 each time thru
    			shift += 6;             // loop, with new bits being put in
    			accum |= value;         // at the bottom.
    			if (shift >= 8)         // whenever there are 8 or more shifted in,
    			{
    				shift -= 8;         // write them out (from the top, leaving any
    				out[index++] =      // excess at the bottom for next iteration.
    				    (byte) ((accum >> shift) & 0xff);
    			}
    		}

    		// we will also have skipped processing a padding null byte ('=') here;
    		// these are used ONLY for padding to an even length and do not legally
    		// occur as encoded data. for this reason we can ignore the fact that
    		// no index++ operation occurs in that special case: the out[] array is
    		// initialized to all-zero bytes to start with and that works to our
    		// advantage in this combination.
    	}

    	// if there is STILL something wrong we just have to throw up now!
    	if (index != out.length)
    	{
    		throw new Error("Miscalculated data length (wrote " + index + " instead of " + out.length + ")");
    	}
    	
    	return out;
    }
}