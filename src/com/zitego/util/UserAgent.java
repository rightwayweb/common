package com.zitego.util;

/**
 * Converts the user-agent header string into an object with properties such as browser
 * type, version, etc.
 *
 * @author John Glorioso
 * @version $Id: UserAgent.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class UserAgent
{
    private static int n = 0;
    public static final int MOZILLA = n++;
    public static final int IE = n++;
    public static final int OPERA = n++;
    public static final int WEB_TV = n++;
    public static final int AOL = n++;
    public static final int OTHER = n++;
    public static final int FIREFOX = n++;
    protected static final String[] _names = { "Mozilla", "Internet Explorer", "Opera", "WebTV", "AOL", "Other", "Firefox" };

    /** The browser type. Default is IE. */
    protected int _browserType = IE;
    /** The browser version. Default is 4.0. */
    protected double _version = 4.0;
    /** The raw user-agent header string. */
    protected String _userAgentString;

    public static void main(String[] args) throws Exception
    {
        UserAgent a = new UserAgent(args[0]);
        System.out.println("User-Agent: "+a.getUserAgent());
        System.out.println("Browser: "+a.getBrowserName());
        System.out.println("Version: "+a.getVersion());
    }

    /**
     * Constructs a new UserAgent object based off of the user-agent header.
     *
     * @param String The header string.
     */
    public UserAgent(String header)
    {
        _userAgentString = (header == null ? "" : header);

        //Parse it to get the browser type and version.
        int index = -1;
        if ( (index=_userAgentString.indexOf("WebTV")) > -1 )
        {
            _browserType = WEB_TV;
            String tmp = _userAgentString.substring( index+6, _userAgentString.indexOf(" ", index) );
            _version = getVersion(tmp, 0);
        }
        else if ( (index=_userAgentString.indexOf("AOL")) > -1 )
        {
            _browserType = AOL;
            index = _userAgentString.indexOf(" ", index);
            int index2 = _userAgentString.indexOf(";", index);
            String tmp = (index2 > -1 ? _userAgentString.substring(index+1, index2) : "0");
            _version = getVersion(tmp, 0);
        }
        else if ( (index=_userAgentString.indexOf("MSIE")) > -1 )
        {
            _browserType = IE;
            int index2 = _userAgentString.indexOf(";", index);
            if (index2 == -1) index2 = _userAgentString.indexOf(")", index);
            String tmp = (index2 > -1 ? _userAgentString.substring(index+5, index2) : "2.0");
            //Make sure this is a parseable number
            _version = getVersion(tmp, 2.0);
        }
        else if ( (index=_userAgentString.indexOf("Opera")) > -1 )
        {
            _browserType = OPERA;
            int index2 = _userAgentString.indexOf("/", index);
            if (index2 == -1) index2 = _userAgentString.indexOf(" ", index);
            int index3 = _userAgentString.indexOf(";", index2);
            if (index3 == -1) index3 = _userAgentString.indexOf(" ", index2+1);
            String tmp = (index2 > -1 && index3 > -1 ? _userAgentString.substring(index2, index3) : "3.0");
            //See if the last char is an "x"
            if (tmp.charAt(tmp.length()-1) == 'x') tmp = tmp.substring(tmp.length()-1);
            //Make sure this is a parseable number
            _version = getVersion(tmp, 3.0);
        }
        else if ( (index=_userAgentString.indexOf("Mozilla")) > -1 )
        {
            _browserType = MOZILLA;
            index = _userAgentString.indexOf("/", index) + 1;
            int index2 = _userAgentString.indexOf(" ", index);
            String tmp = (index > -1 && index2 > -1 ? _userAgentString.substring(index, index2) : "3.01");
            _version = getVersion(tmp, 3.0);
            if (_userAgentString.indexOf("Firefox") > -1) _browserType = FIREFOX;
        }
        else
        {
            _browserType = OTHER;
            _version = getVersion(_userAgentString, 0);
        }
    }

    /**
     * Returns the browser type.
     *
     * @return int
     */
    public int getBrowserType()
    {
        return _browserType;
    }

    /**
     * Returns the browser name.
     *
     * @return String
     */
    public String getBrowserName()
    {
        return _names[_browserType];
    }

    /**
     * Returns the user-agent header property.
     *
     * @return String
     */
    public String toString()
    {
        return _userAgentString;
    }

    /**
     * Returns the user agent string.
     *
     * @return String
     */
    public String getUserAgent()
    {
        return _userAgentString;
    }

    /**
     * Returns the version of the browser.
     *
     * @return double
     */
    public double getVersion()
    {
        return _version;
    }

    private double getVersion(String str, double def)
    {
        //Step through looking for numbers
        if (str == null) return def;
        char[] chars = str.toCharArray();
        StringBuffer ret = new StringBuffer();
        boolean startedVer = false;
        for (int i=0; i<chars.length; i++)
        {
            if ( (chars[i] >= '0' && chars[i] <= '9') || chars[i] == '.' )
            {
                ret.append(chars[i]);
                startedVer = true;
            }
            else if (startedVer)
            {
                break;
            }
        }
        try
        {
            return Double.parseDouble( ret.toString() );
        }
        catch (NumberFormatException nfe)
        {
            return def;
        }
    }
}