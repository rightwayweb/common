package com.zitego.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

/**
 * A static class for working with http cookies.
 *
 * @author John Glorioso
 * @version $Id: CookieUtils.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class CookieUtils
{
    /**
     * Sets a new cookie into the servlet response. By the nature of how cookies are set,
     * this will only work when called before the servlet response has been committed. In other
     * words, you must set the cookie before writing out data back to the client.
     *
     * The life parameter specifies how long the cookie will live from the point of creation.
     * If the life value is positive, it is interpreted as the number of seconds until the cookie
     * expires. If the value is negative, then the cookie has no persistance and is erased when
     * the browser session is killed. If the value is zero, then the cookie is deleted.
     *
     * @param response The servlet response object.
     * @param name The cookie name.
     * @param value The cookie value.
     * @param life The number of seconds the cookie will live.
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int life)
    {
        setCookie(response, name, value, null, life);
    }

    /**
     * Sets a new cookie into the servlet response. By the nature of how cookies are set,
     * this will only work when called before the servlet response has been committed. In other
     * words, you must set the cookie before writing out data back to the client.
     *
     * The life parameter specifies how long the cookie will live from the point of creation.
     * If the life value is positive, it is interpreted as the number of seconds until the cookie
     * expires. If the value is negative, then the cookie has no persistance and is erased when
     * the browser session is killed. If the value is zero, then the cookie is deleted.
     *
     * @param response The servlet response object.
     * @param name The cookie name.
     * @param value The cookie value.
     * @param path The path on the webserver to which the cookie is visible. Null is /.
     * @param life The number of seconds the cookie will live.
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String path, int life)
    {
        setCookie(response, name, value, null, path, life);
    }

    /**
     * Sets a new cookie into the servlet response. By the nature of how cookies are set,
     * this will only work when called before the servlet response has been committed. In other
     * words, you must set the cookie before writing out data back to the client.
     *
     * The life parameter specifies how long the cookie will live from the point of creation.
     * If the life value is positive, it is interpreted as the number of seconds until the cookie
     * expires. If the value is negative, then the cookie has no persistance and is erased when
     * the browser session is killed. If the value is zero, then the cookie is deleted.
     *
     * @param response The servlet response object.
     * @param name The cookie name.
     * @param value The cookie value.
     * @param domain The domain of the cookie.
     * @param path The path on the webserver to which the cookie is visible. Null is /.
     * @param life The number of seconds the cookie will live.
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String domain, String path, int life)
    {
        Cookie c = null;
        if (name != null && value != null)
        {
            c = new Cookie(name, value);
            c.setMaxAge(life);
            if(domain != null) c.setDomain(domain);
            if (path == null) path = "/";
            c.setPath(path);
            response.addCookie(c);
        }
    }

    /**
     * Removes the cookie given a name. A path of / is used.
     *
     * @param response The servlet response.
     * @param name The name of the cookie.
     */
    public static void removeCookie(HttpServletResponse response, String name)
    {
        setCookie(response, name, null, 0);
    }

    /**
     * Removes the cookie given a name and path. If the path is null then / is used.
     *
     * @param response The servlet response.
     * @param name The name of the cookie.
     * @param path The path of the cookie.
     */
    public static void removeCookie(HttpServletResponse response, String name, String path)
    {
        setCookie(response, name, null, path, 0);
    }

    /**
     * Removes the cookie given a name, domain, and path. If the path is null then / is used.
     *
     * @param response The servlet response.
     * @param name The name of the cookie.
     * @param domain The domain of the cookie.
     * @param path The path of the cookie.
     */
    public static void removeCookie(HttpServletResponse response, String name, String domain, String path)
    {
        setCookie(response, name, null, domain, path, 0);
    }

    /**
     * Returns the requested cookie given a name and path. If the path is null, then /
     * is used.
     *
     * @param request The request object.
     * @param name The cookie name.
     * @return Cookie
     */
    public static Cookie getCookie(HttpServletRequest request, String name)
    {
        if (name != null)
        {
            //Look for the specified cookie
            Cookie[] cookies = request.getCookies();
            if (cookies == null) return null;
            for (int i=0; i<cookies.length; i++)
            {
                if ( name.equals(cookies[i].getName()) )
                {
                    return cookies[i];
                }
            }
        }
        return null;
    }
}
