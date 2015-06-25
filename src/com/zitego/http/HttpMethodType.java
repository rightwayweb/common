package com.zitego.http;

import java.util.Vector;
import com.zitego.util.Constant;

/**
 * This constant class defines http method types.
 *
 * @author John Glorioso
 * @version $Id: HttpMethodType.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class HttpMethodType extends Constant
{
    public static final HttpMethodType GET = new HttpMethodType("GET");
    public static final HttpMethodType POST = new HttpMethodType("POST");
    public static final HttpMethodType PUT = new HttpMethodType("PUT");
    public static final HttpMethodType DELETE = new HttpMethodType("DELETE");
    public static final HttpMethodType CHECKOUT = new HttpMethodType("CHECKOUT");
    public static final HttpMethodType SHOWMETHOD = new HttpMethodType("SHOWMETHOD");
    public static final HttpMethodType LINK = new HttpMethodType("LINK");
    public static final HttpMethodType UNLINK = new HttpMethodType("UNLINK");
    public static final HttpMethodType CHECKIN = new HttpMethodType("CHECKIN");
    public static final HttpMethodType TEXTSEARCH = new HttpMethodType("TEXTSEARCH");
    public static final HttpMethodType SPACEJUMP = new HttpMethodType("SPACEJUMP");
    public static final HttpMethodType SEARCH = new HttpMethodType("SEARCH");
    public static final HttpMethodType HEAD = new HttpMethodType("HEAD");

    /** To keep track of each type. */
    private static Vector _types;
    /** The next id to use. */
    private static int _nextId = 0;

    /**
     * Creates a new HttpMethodType given the description.
     *
     * @param String The description.
     */
    private HttpMethodType(String description)
    {
        super(_nextId++, description);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns an HttpMethodType based on the id passed in. If the id does not match the id of
     * a constant, then null is returned.
     *
     * @param int The id.
     * @return HttpMethodType
     */
    public static HttpMethodType evaluate(int id)
    {
        return (HttpMethodType)Constant.evaluate(id, _types);
    }

    /**
     * Returns an HttpMethodType based on the description passed in. If the description does not match the one of
     * a constant, then null is returned.
     *
     * @param String The description.
     * @return HttpMethodType
     */
    public static HttpMethodType evaluate(String desc)
    {
        return (HttpMethodType)Constant.evaluate(desc, _types);
    }

    public Vector getTypes()
    {
        return _types;
    }
}