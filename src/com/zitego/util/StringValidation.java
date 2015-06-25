package com.zitego.util;

import com.zitego.logging.Logger;
import java.lang.reflect.Method;
import java.io.*;
import java.util.regex.Pattern;

/**
 * A class of static methods for convenient string validations. This class has
 * multiple methods that will evaluate the passed in string value to determine
 * various properties about it.
 *
 * @author John Glorioso
 * @version $Id: StringValidation.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class StringValidation
{
    public static void main(String[] args) throws Exception
    {
        Method[] methods = StringValidation.class.getDeclaredMethods();
        if (args.length == 0)
        {
            System.out.println
            (
                "Usage: java com.zitego.util.StringValidation <method> <optional arguments>\n" +
                "\n" +
                "Methods:"
            );
            for (int i=0; i<methods.length; i++)
            {
                String name = methods[i].getName();
                if
                (
                    !name.equalsIgnoreCase("main") && !name.startsWith("class") && !name.equalsIgnoreCase("isNumeric") &&
                    !name.endsWith("ToJs") && !name.startsWith("createJs")

                ) System.out.println(name);
            }
            System.out.println("");
            System.exit(1);
        }

        for (int i=0; i<methods.length; i++)
        {
            String name = methods[i].getName();
            if ( name.equalsIgnoreCase(args[0]) )
            {
                Class[] paramTypes = methods[i].getParameterTypes();
                Object[] params = new Object[paramTypes.length];
                System.out.print(name+"(");
                for (int j=0; j<params.length; j++)
                {
                    System.out.print( (j>0?", ":"")+args[j+1] );
                    params[j] = args[j+1];
                }
                System.out.println(")");
                System.out.println( "Results: "+methods[i].invoke(null, params) );
            }
        }
    }

    /**
     * Returns true if the string is null.
     *
     * @param String The string to test.
     * @return boolean
     */
    public static boolean isNull(String in)
    {
        return (in == null);
    }

    /**
     * Returns true if the string is not null.
     *
     * @param String The string to test.
     * @return boolean
     */
    public static boolean isNotNull(String in)
    {
        return !isNull(in);
    }

    /**
     * Returns true if the string is null or is an empty string.
     *
     * @param String The string to test.
     * @return boolean
     */
    public static boolean isEmpty(String in)
    {
        return ( isNull(in) || "".equals(in) );
    }

    /**
     * Returns true if the string is longer than the specified amount.
     *
     * @param String The string to test.
     * @param int The length to test against.
     * @return boolean
     */
    public static boolean isLongerThen(String in, int len)
    {
        return (isNotNull(in) && in.length() > len);
    }

    /**
     * Returns true if the string is not null and is not an empty string.
     *
     * @param String The string to test.
     * @return boolean
     */
    public static boolean isNotEmpty(String in)
    {
        return !isEmpty(in);
    }

    /**
     * Returns true if the string is not null and is numeric.
     *
     * @param String The string to test.
     * @return boolean
     */
    public static boolean isNumericNotNull(String in)
    {
        return isNumeric(in);
    }

    /**
     * Returns true if the string is null or numeric.
     *
     * @param String The string to test.
     * @return boolean
     */
    public static boolean isNumericOrNull(String in)
    {
        return ( isNull(in) || isNumeric(in) );
    }

    /**
     * Returns true if the string is null or numeric.
     *
     * @param String The string to test.
     * @return boolean
     */
    public static boolean isNotNumeric(String in)
    {
        return ( !isNumeric(in) );
    }

    /**
     * Returns true if the string matches the given regular
     * expression. If either are null, then false is returned.
     *
     * @param String The string to test.
     * @param String The regular expression.
     * @return boolean
     */
    public static boolean matches(String in, String re)
    {
        return ( isNotNull(in) && isNotNull(re) && in.matches(re) );
    }

    /**
     * Returns true if the string does not match the given regular
     * expression. If either are null, then true is returned.
     *
     * @param String The string to test.
     * @param String The regular expression.
     * @return boolean
     */
    public static boolean doesNotMatch(String in, String re)
    {
        return !matches(in, re);
    }

    /**
     * Returns true if any subsequence of characters in the string matches the
     * given regular expression. If either are null, then false is returned.
     *
     * @param String The string to test.
     * @param String The regular expression.
     * @return boolean
     */
    public static boolean partiallyMatches(String in, String re)
    {
        if ( isNull(in) || isNull(re) ) return false;
        else return Pattern.compile(re).matcher(in).find();
    }

    /**
     * Returns true is no subsequence of characters in the string matches the
     * given regular expression. If either are null, then true is returned.
     *
     * @param String The string to test.
     * @param String The regular expression.
     * @return boolean
     */
    public static boolean doesNotPartiallyMatch(String in, String re)
    {
        return !partiallyMatches(in, re);
    }

    private static boolean isNumeric(String in)
    {
        if ( !isNull(in) )
        {
            try
            {
                Double.parseDouble(in);
                return true;
            }
            catch (NumberFormatException nfe)
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private static String writeJsFunction(String func, String[] args, String[] code)
    {
        StringBuffer ret = new StringBuffer();
        ret.append("function ").append(func).append("(");
        if (args != null)
        {
            for (int i=0; i<args.length; i++)
            {
                ret.append( (i>0?", ":"") ).append(args[i]);
            }
        }
        ret.append(")\r\n")
           .append("{\r\n");
        if (code != null)
        {
            for (int i=0; i<code.length; i++)
            {
                ret.append("    ").append(code[i]).append("\r\n");
            }
        }
        ret.append("}");

        return ret.toString();
    }

    /**
     * Writes out the isNull method as javascript.
     *
     * @return String
     */
    public static String isNullToJs()
    {
        return writeJsFunction
        (
            "isNull",
            new String[] { "val" },
            new String[] { "return ( val == null || val == void(0) );" }
        );
    }

    /**
     * Writes out the isNotNull method as javascript.
     *
     * @return String
     */
    public static String isNotNullToJs()
    {
        return writeJsFunction
        (
            "isNotNull",
            new String[] { "val" },
            new String[] { "return !isNull(val);" }
        );
    }

    /**
     * Writes out the isEmpty method as javascript.
     *
     * @return String
     */
    public static String isEmptyToJs()
    {
        return writeJsFunction
        (
            "isEmpty",
            new String[] { "val" },
            new String[] { "return (isNull(val) || val == \"\");" }
        );
    }

    /**
     * Writes out the isLongerThen method as javascript.
     *
     * @return String
     */
    public static String isLongerThenToJs()
    {
        return writeJsFunction
        (
            "isLongerThen",
            new String[] { "val", "len" },
            new String[] { "return (isNotNull(val) && val.length > len);" }
        );
    }

    /**
     * Writes out the isNotEmpty method as javascript.
     *
     * @return String
     */
    public static String isNotEmptyToJs()
    {
        return writeJsFunction
        (
            "isNotEmpty",
            new String[] { "val" },
            new String[] { "return !isEmpty(val);" }
        );
    }

    /**
     * Writes out the isNumericNotNull method as javascript.
     *
     * @return String
     */
    public static String isNumericNotNullToJs()
    {
        return writeJsFunction
        (
            "isNumericNotNull",
            new String[] { "val" },
            new String[] { "return isNumeric(val);" }
        );
    }

    /**
     * Writes out the isNumericOrNull method as javascript.
     *
     * @return String
     */
    public static String isNumericOrNullToJs()
    {
        return writeJsFunction
        (
            "isNumericOrNull",
            new String[] { "val" },
            new String[] { "return ( isNull(val) || isNumeric(val) );" }
        );
    }

    /**
     * Writes out the isNotNumeric method as javascript.
     *
     * @return String
     */
    public static String isNotNumericToJs()
    {
        return writeJsFunction
        (
            "isNotNumeric",
            new String[] { "val" },
            new String[] { "return isNaN(val);" }
        );
    }

    /**
     * Writes out the matches method as javascript.
     *
     * @return String
     */
    public static String matchesToJs()
    {
        return writeJsFunction
        (
            "matches",
            new String[] { "val", "re" },
            new String[] { "return ( isNotNull(val) && isNotNull(re) && val.match(re) != null );" }
        );
    }

    /**
     * Writes out the doesNotMatch method as javascript.
     *
     * @return String
     */
    public static String doesNotMatchToJs()
    {
        return writeJsFunction
        (
            "doesNotMatch",
            new String[] { "val", "re" },
            new String[] { "return !matches(val, re);" }
        );
    }

    /**
     * Writes out the isNumeric method as javascript.
     *
     * @return String
     */
    public static String isNumericToJs()
    {
        return writeJsFunction
        (
            "isNumeric",
            new String[] { "val" },
            new String[] { "return !isNaN(val);" }
        );
    }

    /**
     * Creates a javascript source file given the path. This is normally tied directly to a Servlet
     * container, therefore logError will be called to log where the file is being written out to.
     * Of course, if the path is null nothing happens.
     *
     * @param String The path.
     * @throws Exception if an error occurs creating the source file
     */
    public static void createJsSourceFile(String path) throws Exception
    {
        createJsSourceFile(path, null);
    }

    /**
     * Creates a javascript source file given the path. This is normally tied directly to a Servlet
     * container, therefore logError will be called to log where the file is being written out to.
     * Of course, if the path is null nothing happens. If a non-null Logger is provided, a message is
     * printed to it.
     *
     * @param String The path.
     * @param Logger An optional logger to log this is being created.
     * @throws Exception if an error occurs creating the source file
     */
    public static void createJsSourceFile(String path, Logger logger) throws Exception
    {
        if (path == null) return;

        if (logger != null) logger.log("Writing out StringValidation javascript source file to "+path);

        //Prepend the copywrite info
        StringBuffer src = new StringBuffer();
        File inFile = new File(path);
        if ( inFile.exists() )
        {
            BufferedReader in = new BufferedReader( new FileReader(inFile) );
            String line = null;
            while ( (line=in.readLine()) != null )
            {
                src.append(line).append("\r\n");
                if ( line.equals("") ) break;
            }
            in.close();
        }

        //Get the toJs methods.
        Method[] methods = StringValidation.class.getMethods();
        int count = 0;
        for (int i=0; i<methods.length; i++)
        {
            String name = methods[i].getName();
            if ( name.endsWith("ToJs") )
            {
                if (count++ > 0) src.append("\r\n\r\n");
                src.append( methods[i].invoke(null, new Object[] { }) );
            }
        }
        PrintWriter out = new PrintWriter( new BufferedWriter(new FileWriter(path)) );
        out.print( src.toString() );
        out.flush();
        out.close();
    }
}