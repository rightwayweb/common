package com.zitego.util;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.text.Format;
import java.text.FieldPosition;
import java.text.ParsePosition;

/**
 * Formats a block of text by indenting each line the supplied number of spaces.
 * It can also parse an indented block of text back to aligned.
 *
 * @author John Glorioso
 * @version $Id: TextIndenter.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class TextIndenter extends Format
{
    private int ALIGN = 0;
    private int INDENT = 1;
    private static final int DEFAULT_INDENT = 4;
    /** The number of spaces to indent. Default is 4. */
    private int _indent = DEFAULT_INDENT;
    /** The string spacer. */
    private String _spacer;

    /**
     * Creates a new indenter with a default of four spaces to indent.
     */
    public TextIndenter()
    {
        this(DEFAULT_INDENT, null);
    }

    /**
     * Creates a new indenter with a default of four spaces to indent
     * and a leading character string.
     *
     * @param leading The leading string of character.
     */
    public TextIndenter(String leading)
    {
        this(DEFAULT_INDENT, leading);
    }

    /**
     * Creates a new indenter with a default of four spaces to indent
     * and a leading character string.
     *
     * @param spaces The number of spaces to indent.
     * @param leading The leading String of characters.
     */
    public TextIndenter(int spaces, String leading)
    {
        super();
        _indent = spaces;
        StringBuffer buffer = new StringBuffer();
        if (leading != null) buffer.append(leading);
        for (int i=0; i<spaces; i++)
        {
            buffer.append(" ");
        }
        _spacer = buffer.toString();
    }

    /**
     * Formats the given object if it is a string, array of characters, or an array of bytes,
     * and returns the indented text.
     *
     * @param obj The object to format.
     * @param toAppendTo What to append the formatted value to.
     * @param notUsed Not used.
     * @return String
     */
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition notUsed)
    {
        String txt = null;
        if (obj instanceof String) txt = (String)obj;
        else if (obj instanceof char[]) txt = new String( (char[])obj );
        else if (obj instanceof byte[]) txt = new String( (byte[])obj );

        return toAppendTo.append( indent(txt) );
    }

    /**
     * Returns the original text from an indented block of text.
     *
     * Note: pos is ignored and exists for proper implementation of the base class
     * @param source The String to parse.
     * @param pos Updated to the length of the source string.
     * @return Object
     */
    public Object parseObject(String source, ParsePosition pos)
    {
        if (source != null) pos.setIndex( source.length() );
        return align(source);
    }

    private String align(String in)
    {
        return applyOperationTo(in, ALIGN);
    }

    private String indent(String in)
    {
        return applyOperationTo(in, INDENT);
    }

    private String applyOperationTo(String in, int op)
    {
        StringBuffer ret = new StringBuffer();
        try
        {
            if (in == null) in = "";
            BufferedReader inString = new BufferedReader( new StringReader(in) );
            String line = null;
            while ( (line=inString.readLine()) != null )
            {
                if (op == INDENT) ret.append(_spacer).append(line).append("\r\n");
                else ret.append( (line.startsWith(_spacer) ? line.substring(_indent) : line) ).append("\r\n");
            }
        }
        catch (IOException ioe) { }
        return ret.toString();
    }
}