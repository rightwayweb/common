package com.zitego.http;

import com.zitego.sql.DBHandle;
import com.zitego.http.Base64;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class can be used by any class wanting to retrieve a base 64 encoded username and password
 * from a request cookie. Pass the name of the cookie to look to getLoginInformation and it will
 * return it if it is there.
 *
 * @author John Glorioso
 * @version $Id: AutoLogin.java,v 1.2 2010/06/01 16:29:12 jglorioso Exp $
 */
public class AutoLogin
{
    private static AutoLogin _staticAutoClass;
    private AutoLogin() { }

	/**
	 * Returns the username and password information stored in the given cookie name. If the information
	 * is not present, then it returns null.
	 *
	 * @param request The servlet request.
	 * @param cookie The name of the cookie to look for information in.
	 * @return AutoLogin.LoginInfo
	 */
	public static AutoLogin.LoginInfo getLoginInformation(HttpServletRequest request, String cookie)
	{
		Cookie c = getCookie(request, cookie);
		if ( c != null && !"-1".equals(c.getValue()) )
		{
			try
			{
				//The information is in the format of username|password
				String val = c.getValue();
				if (val != null) val = Base64.decode(val);
				StringTokenizer st = new StringTokenizer( (val == null ? "" : val), ":");
				if (_staticAutoClass == null) _staticAutoClass = new AutoLogin();
				return _staticAutoClass.getLoginInfo
				(
				    st.nextToken(), st.nextToken()
				);
			}
			catch (NoSuchElementException nsee) { }
		}
		return null;
	}

	/**
	 * Removes a cookie.
	 *
	 * @param response The servlet response object.
	 * @param cookie The name of the cookie.
	 */
    public static void removeCookie(HttpServletResponse response, String cookie)
	{
	    setCookie(response, cookie, null, null, 0);
	}

	/**
	 * Sets a new cookie "permanently" into the servlet response. Permanently means for 1 year.
	 *
	 * @param response The servlet response object.
	 * @param cookie The name of the cookie.
	 * @param username The username.
	 * @param password The password.
	 */
    public static void setPermanentCookie(HttpServletResponse response, String cookie, String username, String password)
	{
	    setCookie(response, cookie, username, password, 31536000);
	}

	/**
	 * Sets a new cookie into the servlet response. This must be called for the response buffer is written or it
	 * will not set. If the cookie name is null, then nothing is done.
	 *
	 * @param response The servlet response object.
	 * @param cookie The name of the cookie.
	 * @param username The username.
	 * @param password The password.
	 * @param int The number of seconds the cookie will live.
	 */
	public static void setCookie(HttpServletResponse response, String cookie, String username, String password, int life)
	{
	    if (cookie == null) return;
		Cookie c = null;
		if (username != null && password != null)
		{
			c = new Cookie( cookie, Base64.encode(username+":"+password) );
		}
		else
		{
			c = new Cookie(cookie, "-1");
		}
		c.setMaxAge(life);
		c.setPath("/");
		response.addCookie(c);
	}

	/**
	 * Returns the requested cookie.
	 *
	 * @param HttpServletRequest The request object.
	 * @return User
	 */
	private static Cookie getCookie(HttpServletRequest request, String cookie)
	{
		if (cookie == null || request == null || request.getCookies() == null) return null;
		//Look for the specified cookie
		for (int i=0; i<request.getCookies().length; i++)
		{
			if ( cookie.equals(request.getCookies()[i].getName()) )
			{
				return request.getCookies()[i];
			}
		}
		return null;
	}

	private LoginInfo getLoginInfo(String user, String pass)
	{
        return new LoginInfo(user, pass);
	}

	public class LoginInfo
	{
	    private String _username;
	    private String _password;

	    private LoginInfo(String username, String password)
	    {
	        _username = username;
	        _password = password;
	    }

	    /**
	     * Returns the username.
	     *
	     * @return String
	     */
	    public String getUsername()
	    {
	        return _username;
	    }

	    /**
	     * Returns the password.
	     *
	     * @return String
	     */
	    public String getPassword()
	    {
	        return _password;
	    }
	}
}