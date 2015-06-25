package com.zitego.util;

import java.util.Vector;
import java.util.StringTokenizer;

/**
 * This class contains static methods used to perform operations on Strings.
 *
 * @author John Glorioso
 * @version $Id
 */
public class TextUtils
{
    public static void main(String[] args)
    {
        System.out.println(replaceHighAscii(args[0]));
    }

    /**
     * This method splits a String into a String[] of tokens given a character delimiter.
     * If a token is missing from the string, then an empty string is returned for that
     * token.
     *
     * @param String The String to parse.
     * @param char The delimiter.
     * @return String[]
     */
    public static String[] split(String in, char delim)
    {
        return split(in, delim, (char)0);
    }

    /**
     * This method splits a String into a String[] of tokens given a character delimiter and
     * a surrounder character. The surrounder character is used to surround an entire token
     * where the delimiter may exist. For example, if the String passed in contains a person's
     * name, address, city, state, and zipcode on a line delimited by a comma, an optional
     * surrounding character can be specified to avoid parsing too many tokens. Take for instance
     * that an address may have a comma in it. The address can be surrounded by a character
     * such as a double quote to enclose the token.<br>
     * Ex: John Glorioso,"123 Fake Street, Apt.201",Springfield,OH,12345<br><br>
     *
     * The tokens returned would be "John Glorioso", "123 Fake Street, Apt.201", "Springfield",
     * "OH", and "12345". Note that the surrounding character and the delimiter are not
     * returned.<br><br>
     *
     * If a token is missing from the string, then an empty string is returned for that token.
     * Note: A surrounding character with the integer value of 0 will be ignored and a surrounding
     * character will not be used.
     *
     * @param String The String to parse.
     * @param char The delimiter.
     * @param char The surrounding character.
     * @return String[]
     */
    public static String[] split(String in, char delim, char surroundChar)
    {
        if (delim == surroundChar) throw new IllegalArgumentException("Delimiter and surrounding character cannot be the same");

        if (in == null) return new String[0];

        boolean inSurround = false;
        char[] chars = in.toCharArray();
        Vector tokens = new Vector();
        StringBuffer token = new StringBuffer();
        for (int i=0; i<chars.length; i++)
        {
            if (chars[i] == delim && !inSurround)
            {
                tokens.add( token.toString() );
                token.setLength(0);
                inSurround = false;
            }
            else if (chars[i] == surroundChar && (int)surroundChar != 0)
            {
                inSurround = !inSurround;
            }
            else
            {
                token.append(chars[i]);
            }
        }
        if (token.length() > 0) tokens.add( token.toString() );
        String[] ret = new String[tokens.size()];
        tokens.copyInto(ret);
        return ret;
    }

    /**
	 * Truncates the right side of the string based on the number of characters to allow. If the string
	 * is shorter than the number of characters to show, then the entire string will be returned, otherwise
	 * the string will be returned as the string up to the number of characters specified plus the ...
	 *
	 * @param String The string to truncate.
	 * @param int The number of characters to allow.
	 */
	public static String trunc(String instr, int chars)
	{
		if (instr == null || instr.length() <= chars) return instr;

		return instr.substring(0, chars) + "...";
	}

	/**
	 * Truncates the a string based on the number of characters to allow in the front and end. If the string
	 * is shorter than the total number of characters to show, then the entire string will be returned, otherwise
	 * the string will be returned as the string up to the number of characters specified in the front, plus ...,
	 * plus the number of chars to show at the end.
	 *
	 * @param String The string to truncate.
	 * @param int The number of characters to allow from the front.
	 * @param int The number of characters to allow at the end.
	 */
	public static String trunc(String instr, int front, int end)
	{
		if (instr == null || instr.length() <= front+end) return instr;

		return instr.substring(0, front) + "..." + instr.substring( instr.length()-end );
	}

    /**
     * Converts the array of Objects provided to a single delimited string.
     *
     * @param Object[] The array to convert.
     * @param String The delimiter to use, if null, then "," is used.
     */
    public static String join(Object[] arr, String delim)
    {
        if (arr == null || arr.length == 0) return "";
        if (delim == null) delim = ",";

        StringBuffer buf = new StringBuffer( arr[0].toString() );
        for (int i=1; i<arr.length; i++)
        {
            buf.append(delim).append(arr[i]);
        }

        return buf.toString();
    }

	/**
     * Converts the array of ints provided to a single delimited string.
     *
     * @param int[] The array to convert.
     * @param String The delimiter to use, if null, then "," is used.
     */
    public static String join(int[] arr, String delim)
    {
        return join(new ObjectArray(arr).toArray(), delim);
    }

    /**
     * Converts the array of longs provided to a single delimited string.
     *
     * @param long[] The array to convert.
     * @param String The delimiter to use, if null, then "," is used.
     */
    public static String join(long[] arr, String delim)
    {
        return join(new ObjectArray(arr).toArray(), delim);
    }

    /**
     * Converts the array of doubles provided to a single delimited string.
     *
     * @param double[] The array to convert.
     * @param String The delimiter to use, if null, then "," is used.
     */
    public static String join(double[] arr, String delim)
    {
        return join(new ObjectArray(arr).toArray(), delim);
    }

    /**
     * Converts the array of floats provided to a single delimited string.
     *
     * @param floats[] The array to convert.
     * @param String The delimiter to use, if null, then "," is used.
     */
    public static String join(float[] arr, String delim)
    {
        return join(new ObjectArray(arr).toArray(), delim);
    }

    /**
     * Converts the array of shorts provided to a single delimited string.
     *
     * @param short[] The array to convert.
     * @param String The delimiter to use, if null, then "," is used.
     */
    public static String join(short[] arr, String delim)
    {
        return join(new ObjectArray(arr).toArray(), delim);
    }

    /**
     * Converts the array of chars provided to a single delimited string.
     *
     * @param char[] The array to convert.
     * @param String The delimiter to use, if null, then "," is used.
     */
    public static String join(char[] arr, String delim)
    {
        return join(new ObjectArray(arr).toArray(), delim);
    }

    /**
     * Removes multiple spaces and leave only one space where there is more than one.
     * Example "a    b       c"  would be "a b c"
     *
     * @param String The string containing the extra spaces.
     * @return String
     */
    public static String removeExtraSpaces(String instr)
    {
        if (instr == null) return null;
        StringBuffer ret = new StringBuffer(instr);
        if (ret.length() == 0) return "";

        for (int i=1; i<ret.length(); i++)
        {
            if (ret.charAt(i) == ' ' && ret.charAt(i-1) == ' ') ret.deleteCharAt(i--);
        }
        return ret.toString();
    }

    /**
     * Removes the given leading character from the given string buffer.
     *
     * @param StringBuffer The buffer to cleanse.
     * @param char The character to remove.
     * @return StringBuffer
     */
    public static StringBuffer removeLeadingCharacter(StringBuffer in, char c)
    {
        return removeEndCharacters(in, new char[] { c }, 1);
    }

    /**
     * Removes the given leading characters from the given string buffer.
     *
     * @param StringBuffer The buffer to cleanse.
     * @param char[] The characters to remove.
     * @return StringBuffer
     */
    public static StringBuffer removeLeadingCharacters(StringBuffer in, char[] chars)
    {
        return removeEndCharacters(in, chars, 1);
    }

    /**
     * Removes the given trailing character from the given string buffer.
     *
     * @param StringBuffer The buffer to cleanse.
     * @param char The character to remove.
     * @return StringBuffer
     */
    public static StringBuffer removeTrailingCharacter(StringBuffer in, char c)
    {
        return removeEndCharacters(in, new char[] { c }, -1);
    }

    /**
     * Removes the given trailing characters from the given string buffer.
     *
     * @param StringBuffer The buffer to cleanse.
     * @param char[] The characters to remove.
     * @return StringBuffer
     */
    public static StringBuffer removeTrailingCharacters(StringBuffer in, char[] chars)
    {
        return removeEndCharacters(in, chars, -1);
    }

    /**
     * Removes the given characters from one end of the given string buffer. If
     * The end parameter is positive, then the characters are taken off of the
     * front of the buffer, otherwise, they are taken from the rear.
     *
     * @param StringBuffer The buffer to cleanse.
     * @param char[] The characters to remove.
     * @param int The end to take from.
     */
    private static StringBuffer removeEndCharacters(StringBuffer in, char[] chars, int end)
    {
        StringBuffer ret = new StringBuffer();
        if (in == null || chars == null || chars.length == 0) return ret;

        while (in.length() > 0)
        {
            int check = (end > 0 ? 0 : in.length()-1);
            char c = in.charAt(check);
            boolean notThere = true;
            for (int i=0; i<chars.length; i++)
            {
                if (c == chars[i])
                {
                    ret.append( in.charAt(check) );
                    in.deleteCharAt(check);
                    notThere = false;
                    break;
                }
            }
            if (notThere) break;
        }
        return ret;
    }

    /**
     * Returns a trimmed version of the given String. This removes spaces (char)20, newlines,
     * and carriage returns. To remove whitespace only, see String.trim().
     *
     * @param String The string to trim.
     * @return String The trimmed String.
     */
    public static String trim(String in)
    {
        StringBuffer ret = new StringBuffer(in);
        removeLeadingCharacters(ret, new char[]{' ','\r','\n'});
        removeTrailingCharacters(ret, new char[]{' ','\r','\n'});
        return ret.toString().trim();
    }

    /**
     * Returns the text from the given string buffer up to the point it
     * encounters the given character. This removes the text from the
     * string buffer as it goes.
     *
     * @param StringBuffer The buffer to get text from.
     * @param char The character to look for.
     * @return String
     */
    public static String getTextUpTo(StringBuffer in, char c)
    {
        return getTextUpTo(in, new char[] { c });
    }

    /**
     * Returns the text from the given string buffer up to the point it
     * encounters one of the given characters. This removes the text from the
     * string buffer as it goes.
     *
     * @param StringBuffer The buffer to get text from.
     * @param char[] The characters to look for.
     * @return String
     */
    public static String getTextUpTo(StringBuffer in, char[] chars)
    {
        if (in == null || in.length() == 0 || chars == null || chars.length == 0) return null;

        StringBuffer ret = new StringBuffer();
        while (in.length() > 0)
        {
            char c = in.charAt(0);
            boolean hitChar = false;
            for (int i=0; i<chars.length; i++)
            {
                if (c == chars[i])
                {
                    hitChar = true;
                    break;
                }
            }
            if (hitChar)
            {
                break;
            }
            else
            {
                ret.append(c);
                in.deleteCharAt(0);
            }
        }
        return ret.toString();
    }

    /**
     * Returns the text from the given string buffer up to the point it
     * encounters the given string. This removes the text from the
     * string buffer as it goes. If the string is not encountered, the
     * entire string buffer is returned.
     *
     * @param StringBuffer The buffer to get text from.
     * @param String The string to look for.
     * @return String
     */
    public static String getTextUpTo(StringBuffer in, String str)
    {
        if (in == null || in.length() == 0 || str == null || str.length() == 0) return null;

        StringBuffer ret = new StringBuffer();
        int index = ret.indexOf(str);
        if (index > -1)
        {
            ret.append( in.substring(0, index) );
            in.delete(0, index);
        }
        else
        {
            ret.append(in);
            in.setLength(0);
        }
        return ret.toString();
    }

    /**
     * Wraps the given text at the specified number of characters. It will wrap at the
     * last space encountered. If there was no last space, then we keep reading until
     * we reach one and start the next line after it.
     *
     * @param String The string to wrap.
     * @param int The number of characters per line.
     * @return String
     * @throws IllegalArgumentException if the number of characters is < 1.
     */
    public static String wrap(String in, int cols)
    {
        if (in == null) return "";

        if (cols < 1) throw new IllegalArgumentException("Characters per line must be greater than 0");

        StringBuffer ret = new StringBuffer();
        StringTokenizer st = new StringTokenizer(in, " ");
        String token = null;
        int count = 0, index = 0;
        while( st.hasMoreTokens() )
        {
            token = st.nextToken();
            index = token.indexOf("\n");
            if (index == -1) index = 0;
            else count = 0;
            count += token.length() - index + 1;
            if (count > cols)
            {
                ret.append("\n");
                count = token.length() + 1;
            }
            ret.append(token).append(" ");
        }
        return ret.toString();
    }

    /**
     * Repeats the given string the supplied number of times and returns it.
     *
     * @param String The string to repeat.
     * @param int The number of times to repeat.
     */
    public static String repeat(String str, int numTimes)
    {
        StringBuffer ret = new StringBuffer();
        for (int i=0; i<numTimes; i++)
        {
            ret.append(str);
        }
        return ret.toString();
    }

    /**
     * Attempts to replace high ascii characters with low text equivalents. If it cannot be mapped,
     * then a space character is used.
     *
     * @param in The string to convert.
     * @return String
     */
    public static String replaceHighAscii(String in)
    {
        if (in == null) return null;

        StringBuffer ret = new StringBuffer( in.length() ) ;

        for (int i=0; i<in.length(); i++)
        {
            int c ;
            switch ( (c=(int)in.charAt(i)) )
            {
                case 0x82 : ret.append(","); break;
                case 0x83 : ret.append("f"); break;
                case 0x84 : ret.append(",,"); break;
                case 0x85 : ret.append("..."); break;
                case 0x88 : ret.append("^"); break;
                case 0x89 : ret.append("ppt"); break;
                case 0x8B : ret.append("<"); break;
                case 0x8C : ret.append("Oe"); break;
                case 0x91 : ret.append("'"); break;
                case 0x92 : ret.append("'"); break;
                case 0x93 : ret.append("\""); break;
                case 0x94 : ret.append("\""); break;
                case 0x95 : ret.append("*"); break;
                case 0x96 : ret.append("-"); break;
                case 0x97 : ret.append("--"); break;
                case 0x98 : ret.append("~"); break;
                case 0x99 : ret.append("TM"); break;
                case 0x9B : ret.append(">"); break;
                case 0x9C : ret.append("oe"); break;
                case 0xA9 : ret.append("(c)"); break;
                case 0xAE : ret.append("(r)"); break;
                case 0xBC : ret.append("1/4"); break;
                case 0xBD : ret.append("1/2"); break;
                case 0xBE : ret.append("3/4"); break;
                case 8208 : ret.append("-"); break;
                case 8209 : ret.append("-"); break;
                case 8211 : ret.append("--"); break;
                case 8212 : ret.append("--"); break;
                case 8213 : ret.append("--"); break;
                case 8214 : ret.append("||"); break;
                case 8215 : ret.append("_"); break;
                case 8216 : ret.append("'"); break;
                case 8217 : ret.append("'"); break;
                case 8218 : ret.append(","); break;
                case 8219 : ret.append("'"); break;
                case 8220 : ret.append("\""); break;
                case 8221 : ret.append("\""); break;
                case 8222 : ret.append(",,"); break;
                case 8223 : ret.append("\""); break;
                case 8226 : ret.append("*"); break;
                case 8227 : ret.append(">"); break;
                case 8228 : ret.append("*"); break;
                case 8229 : ret.append(".."); break;
                case 8230 : ret.append("..."); break;
                case 8231 : ret.append("-"); break;
                case 61514 : ret.append(":-)"); break;
                case 61515 : ret.append(":-|"); break;
                case 61516 : ret.append(":-("); break;
                case 65533 : ret.append(" "); break;
                default : ret.append( (char)c );
            }
        }
        return ret.toString() ;
    }
}