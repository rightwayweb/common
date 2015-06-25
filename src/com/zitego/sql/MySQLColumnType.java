package com.zitego.sql;

import com.zitego.util.Constant;
import java.util.Vector;

/**
 * This describes mysql sql database column types.
 *
 * @author John Glorioso
 * @version $Id: MySQLColumnType.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class MySQLColumnType extends Constant
{
    private static int n = 0;
    public static final MySQLColumnType INT = new MySQLColumnType("INT");
    public static final MySQLColumnType TINYINT = new MySQLColumnType("TINYINT");
    public static final MySQLColumnType CHAR = new MySQLColumnType("CHAR");
    public static final MySQLColumnType VARCHAR_255 = new MySQLColumnType("VARCHAR(255)");
    public static final MySQLColumnType TEXT = new MySQLColumnType("TEXT");
    public static final MySQLColumnType DATE = new MySQLColumnType("DATE");
    public static final MySQLColumnType DATE_TIME = new MySQLColumnType("DATETIME");
    public static final MySQLColumnType TIMESTAMP = new MySQLColumnType("TIMESTAMP");
    public static final MySQLColumnType FLOAT_12_2 = new MySQLColumnType("FLOAT(12,2)");
    public static final MySQLColumnType FLOAT_12_3 = new MySQLColumnType("FLOAT(12,3)");

    /** To keep track of each type. */
    private static Vector _types;

    /**
     * Creates a new MySQLColumnType given the description.
     *
     * @param desc The description.
     */
    private MySQLColumnType(String desc)
    {
        super(n++, desc);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns a MySQLColumnType based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param id The constant id.
     * @return MySQLColumnType
     */
    public static MySQLColumnType evaluate(int id)
    {
        return (MySQLColumnType)evaluate(id, _types);
    }

    /**
     * Returns a MySQLColumnType based on the description passed in. If the description does
     * not match the description of a constant, then we return null. If there are two constants
     * with the same description, then the first one is returned.
     *
     * @param desc The constant description.
     * @return MySQLColumnType
     */
    public static MySQLColumnType evaluate(String desc)
    {
        return (MySQLColumnType)evaluate(desc, _types);
    }

    public Vector getTypes()
    {
        return _types;
    }
}
