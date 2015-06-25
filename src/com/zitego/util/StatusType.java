package com.zitego.util;

import java.util.Vector;
import com.zitego.util.Constant;

/**
 * This constant class defines different status types.
 *
 * @author John Glorioso
 * @version $Id: StatusType.java,v 1.2 2008/03/21 20:18:08 jglorioso Exp $
 */
public class StatusType extends Constant
{
    public static final StatusType ACTIVE = new StatusType(1, "Active");
    public static final StatusType INACTIVE = new StatusType(0, "Inactive");
    /** To keep track of each type. */
    private static Vector _types;

    /**
     * For extending. It does nothing.
     */
    protected StatusType() {}

    /**
     * Creates a new StatusType given the id and description.
     *
     * @param int The id.
     * @param String The description.
     */
    private StatusType(int id, String desc)
    {
        super(id, desc);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns an StatusType based on the id passed in. If the id does not match the id of
     * a constant, then we return INACTIVE. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param int The constant id.
     * @return StatusType
     */
    public static StatusType evaluate(int id)
    {
        StatusType ret = (StatusType)Constant.evaluate(id, _types);
        if (ret == null) ret = StatusType.INACTIVE;
        return ret;
    }

    /**
     * Returns an StatusType based on the description passed in. If the description does not match one of
     * a constant, then we return INACTIVE. If there are two constants with the same description, then
     * the first one is returned.
     *
     * @param String The constant description.
     * @return StatusType
     */
    public static StatusType evaluate(String desc)
    {
        StatusType ret = (StatusType)Constant.evaluate(desc, _types);
        if (ret == null) ret = StatusType.INACTIVE;
        return ret;
    }

    public Vector getTypes()
    {
        return _types;
    }
}