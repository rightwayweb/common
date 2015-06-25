package com.zitego.util.getopts;

import java.util.Vector;

/**
 * Main class to parse command line options.
 *
 * Example:<br>
 * <pre><code>
 *
 * GetOpts options = new GetOpts(new String[] {"properties::",
 *                                             "base:",
 *                                             "backup"},
 *                               args,
 *                               GetOpts.OPTION_CASE_INSENSITIVE);
 *
 * int index = -1;
 * boolean propFileSupplied = false;
 * try
 * {
 *     while ((index = options.getOptions()) != -1)
 *     {
 *         String argName = options.getOptionString(index);
 *         String arg = options.getOptarg();
 *         if ("properties".equalsIgnoreCase(argName))
 *         {
 *             Properties props = new Properties();
 *             props.load(new FileInputStream(arg));
 *             initializeConfiguration(props);
 *             propFileSupplied = true;
 *         }
 *         else if ("property1".equalsIgnoreCase(argName))
 *         {
 *             m_property1 = arg;
 *         }
 *         else if ("property2".equalsIgnoreCase(argName))
 *         {
 *             m_property2 = Integer.parseInt(arg);
 *         }
 *         else
 *         {
 *             System.err.println("unknown option: " + argName);
 *             System.exit(-1);
 *         }
 *     }
 * }
 * catch(GetOptException ge)
 * {
 *     System.err.println(ge.getMessage());
 *     System.exit(-1);
 * }
 * </code></pre>
 * @version $Id: GetOpts.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class GetOpts
{
    /**
     * Option to do case-insensitive option matching.  Default is
     * case-sensitive.
     */
    public static final int OPTION_CASE_INSENSITIVE = 0x0001;

    /**
     * Option to use double-dash as option prefix (GNU style).
     * Default is single dash (Java style).
     */
    public static final int OPTION_USE_DOUBLE_DASH  = 0x0002;

    private static final int OPTIONFLAG_PARAMS_NONE = 0;
    private static final int OPTIONFLAG_PARAMS_REQ  = 1;
    private static final int OPTIONFLAG_PARAMS_OPT  = 2;

    private final String[] _args;
    private final String[] _options;
    private final int[] _optionFlags;

    private final boolean _caseInsensitive;
    private final String _dashString;

    private int _currentArg;
    private Vector _otherArgs;
    private String _optarg;

    /**
     * Create a <code>GetOpts</code> object, with specified flags.
     * <p>
     * Format of the entries in the <code>options<code> array:<br>
     *   <code>"name"&nbsp;&nbsp;</code> -- matches "name", no parameters<br>
     *   <code>"name:"&nbsp;</code> -- matches "name", required parameter<br>
     *   <code>"name::"</code> -- matches "name", optional parameter<br>
     * <p>
     * @param options Array of options to match (see above)
     * @param args Command-line arguments passed to <code>main()</code>
     * @param flags Flags (bitmask of <code>OPTION_XXX</code> flags)
     */
    public GetOpts(String[] options, String[] args, int flags)
    {
        _currentArg = 0;
        _args = (String[])args.clone();
        _otherArgs = new Vector();

        _caseInsensitive = ( (flags & OPTION_CASE_INSENSITIVE) != 0 );
        if ( (flags & OPTION_USE_DOUBLE_DASH) != 0 ) _dashString = "--";
        else _dashString = "-";

        _options = new String[options.length];
        _optionFlags = new int[options.length];
        for (int i=0; i<options.length; i++)
        {
            String option = options[i];
            if ( option.endsWith("::") )
            {
                option = option.substring(0, option.length() - 2);
                _optionFlags[i] = OPTIONFLAG_PARAMS_OPT;
            }
            else if ( option.endsWith(":") )
            {
                option = option.substring(0, option.length() - 1);
                _optionFlags[i] = OPTIONFLAG_PARAMS_REQ;
            }
            else
            {
                _optionFlags[i] = OPTIONFLAG_PARAMS_NONE;
            }
            if ( option.endsWith(":") )
            {
                throw new IllegalArgumentException("invalid option specifier: \"" + options[i] + "\"");
            }
            _options[i] = option;
        }
    }


    /**
     * Create a <code>GetOpts</code> object, with default flags.
     * This is the same as calling:
     * <code>GetOpts(options, args, 0)</code>
     * <p>
     * @param options Array of options to match
     * @param args Command-line arguments passed to <code>main()</code>
     */
    public GetOpts(String[] options, String args[])
    {
        this(options, args, 0);
    }


    /**
     * See if an argument matches this option, based on
     * <code>caseInsensitive</code> flag
     */
    private boolean optionEquals(String arg1, String arg2)
    {
        if (_caseInsensitive) return arg1.equalsIgnoreCase(arg2);
        else return arg1.equals(arg2);
    }


    /**
     * Get the next option.  This should be called repeatedly until it
     * returns -1.
     * <p>
     * @return the index into the <code>options</code> array that was
     * passed to the constructor.  Use
     * {@link #getOptionString(int) getOptionString()}
     * to get the string equivalent.
     * @exception GetOptException if an unknown option was
     * encountered, or if an option with required parameters does not
     * have one.
     */
    public int getOptions() throws GetOptException
    {
        _optarg = null;
        while (_currentArg < _args.length)
        {
            if ( _args[_currentArg].startsWith(_dashString) )
            {
                String arg = _args[_currentArg++].substring( _dashString.length() );

                for (int i=0; i<_options.length; i++)
                {
                    if ( optionEquals(arg, _options[i]) )
                    {
                        if (_optionFlags[i] == OPTIONFLAG_PARAMS_NONE) return i;

                        if (_optionFlags[i] == OPTIONFLAG_PARAMS_OPT)
                        {
                            if ( (_currentArg < _args.length) && (!_args[_currentArg].startsWith("-")) )
                            {
                                _optarg = _args[_currentArg++];
                            }
                            return i;
                        }

                        if (_optionFlags[i] == OPTIONFLAG_PARAMS_REQ)
                        {
                            if ( (_currentArg < _args.length) && (!_args[_currentArg].startsWith("-")) )
                            {
                                _optarg = _args[_currentArg++];
                            }
                            else
                            {
                                throw new MissingParameterException
                                (
                                    "Required parameter for \"" + _options[i] + "\" not found"
                                );
                            }
                            return i;
                        }
                    }
                }
                throw new OptionNotFoundException("unknown option \"" + arg + "\"");
            }
            else
            {
                _otherArgs.add(_args[_currentArg++]);
            }
        }
        return -1;
    }


    /**
     * Get the name of the specified option.
     *
     * @param int The index into the <code>options</code> array.
     * @return String
     */
    public String getOptionString(int i)
    {
        return _options[i];
    }


    /**
     * Get the command-line parameter associated with the most
     * recently returned option.  If no parameter is available -- none
     * was specified on the command line (for an optional parameter),
     * or the option takes no parameters -- <code>null</code> is returned.
     * <p>
     * @return the parameter, or <code>null</code>

     */
    public String getOptarg()
    {
        return _optarg;
    }


    /**
     * Get any non-option arguments.  This method should only be
     * called after {@link #getOptions() getOptions()} returns -1.
     * <p>
     * @return An array of Strings containing all remaining
     * command-line arguments
     */
    public String[] getOtherArgs()
    {
        String otherArgsArray[] = new String[_otherArgs.size()];
        for (int i=0; i<otherArgsArray.length; i++)
        {
            otherArgsArray[i] = (String)_otherArgs.get(i);
        }
        return otherArgsArray;
    }

}