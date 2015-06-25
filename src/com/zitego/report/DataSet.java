package com.zitego.report;

import com.zitego.util.Sortable;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class stores data of any type and performs comparisons on one
 * or several fields in the set. If more than one field is set for
 * comparision, then the fields are compared until a non-equal value is
 * encountered. If all values are equal, then the DataSet's are considered
 * equal. The fields are evaluated in the order in which they are set
 *
 * @author John Glorioso
 * @version $Id: DataSet.java,v 1.2 2008/05/11 22:08:39 jglorioso Exp $
 */
public class DataSet extends HashMap implements Comparable
{
    private static final int SHORT = 0;
    private static final int INT = 1;
    private static final int LONG = 2;
    private static final int BYTE = 3;
    private static final int FLOAT = 4;
    private static final int DOUBLE = 5;
    private static final int BOOLEAN = 6;
    private static final int CHAR = 7;
    /** To designate that nulls are treated as the smaller of two objects. */
    public static final int SMALL = 1;
    /** To designate that nulls are treated as the larger of two objects. */
    public static final int LARGE = 2;
    /** The list of fields to use in comparisions. */
    private LinkedList _compareFields = null;
    /** The current sort order, this is used if a sort order isn't specified. */
    private int _sortOrder = Sortable.ASCENDING;
    /** Whether String comparisions are case sensitive. */
    private boolean _case = false;
    /** Determines the behavior of a sort when a null value is encountered. SMALL is default. */
    private int _sortNull = SMALL;

    /**
     * Creates a new empty DataSet.
     */
    public DataSet()
    {
        super();
        clearSort();
    }

    /**
     * Sets the supplied int value.
     *
     * @param Object The key for this item.
     * @param int The value.
     */
    public void put(Object key, int value)
    {
        super.put( key, new Integer(value) );
    }

    /**
     * Sets the supplied long value.
     *
     * @param Object The key for this item.
     * @param int The value.
     */
    public void put(Object key, long value)
    {
        super.put( key, new Long(value) );
    }

    /**
     * Sets the supplied float value.
     *
     * @param Object The key for this item.
     * @param int a float value for this item
     */
    public void put(Object key, float value)
    {
        super.put( key, new Float(value) );
    }


    /**
     * Sets the supplied double value.
     *
     * @param Object The key for this item.
     * @param int The value.
     */
    public void put(Object key, double value)
    {
        super.put( key, new Double(value) );
    }

    /**
     * Sets the supplied short value.
     *
     * @param Object The key for this item.
     * @param int The value.
     */
    public void put(Object key, short value)
    {
        super.put( key, new Short(value) );
    }

    /**
     * Sets the supplied byte value.
     *
     * @param Object The key for this item.
     * @param int The value.
     */
    public void put(Object key, byte value)
    {
        super.put( key, new Byte(value) );
    }

    /**
     * Sets the supplied boolean value.
     *
     * @param Object The key for this item.
     * @param int The value.
     */
    public void put(Object key, boolean value)
    {
        super.put( key, new Boolean(value) );
    }

    /**
     * Retrieves the specified field.
     *
     * @param The field to retrieve.
     * @return Object
     */
    public Object get(Object field)
    {
        if (field != null) return super.get(field);
        else return null;
    }

    private Number getNumber(Object key) throws NumberFormatException
    {
        Object obj = get(key);
        if (obj == null)
        {
            return new Integer(0);
        }
        else if (obj instanceof Number)
        {
            return (Number)obj;
        }
        else if (obj instanceof Boolean)
        {
            if ( ((Boolean)obj) == Boolean.TRUE ) return new Integer(1);
            else return new Integer(0);
        }
        else if (obj instanceof String)
        {
            return new Double( (String)obj );
        }
        throw new NumberFormatException();
    }

    /**
     * Attempts to retrieve to requested item as an int. If the item is of any number type it will be converted to int.
     * If the item is boolean it will be converted to 1 if true, 0 if false. If the item is a string, an attempt will be
     * made to parse the string as an int.
     *
     * @param Object The key for the requested item.
     * @return int
     * @throws ClassCastException if the requested item cannot be converted to an int.
     */
    public int getInt(Object key)
    {
        try
        {
            return getNumber(key).intValue();
        }
        catch (NumberFormatException nfe)
        {
            throw new ClassCastException("Could not convert to an int: " + key + " = " + get(key));
        }
    }

    /**
     * Attempts to retrieve requested item as a long. If the item is of any number type it will be converted to long.
     * If the item is boolean it will be converted to 1 if true, 0 if false. If the item is a string, an attempt will be
     * made to parse the string as an long.
     *
     * @param Object the key for the requested item
     * @return long
     * @throws ClassCastException if the requested item cannot be converted to an long
     */
    public long getLong(Object key)
    {
        try
        {
            return getNumber(key).longValue();
        }
        catch (NumberFormatException nfe)
        {
            throw new ClassCastException("Could not convert to a long: " + key + " = " + get(key));
        }
    }

    /**
     * Attempts to retrieve requested item as a short. If the item is of any number type it will be converted to short.
     * If the item is boolean it will be converted to 1 if true, 0 if false. If the item is a string, an attempt will be
     * made to parse the string as an short.
     *
     * @param Object the key for the requested item.
     * @throws ClassCastException if the requested item cannot be converted to an short.
     */
    public short getShort(Object key)
    {
        try
        {
            return getNumber(key).shortValue();
        }
        catch (NumberFormatException nfe)
        {
            throw new ClassCastException("Could not convert to a short: " + key + " = " + get(key));
        }
    }

    /**
     * Attempts to retrieve requested item as a byte. If the item is of any number type it will be converted to byte.
     * If the item is boolean it will be converted to 1 if true, 0 if false. If the item is a string, an attempt will be
     * made to parse the string as an byte.
     *
     * @param Object The key for the requested item.
     * @throws ClassCastException if the requested item cannot be converted to an byte.
     */
    public byte getByte(Object key)
    {
        try
        {
            return getNumber(key).byteValue();
        }
        catch (NumberFormatException nfe)
        {
            throw new ClassCastException("Could not convert to a byte: " + key + " = " + get(key));
        }
    }

    /**
     * Attempts to retrieve requested item as a float. If the item is of any number type it will be converted to float.
     * If the item is boolean it will be converted to 1.0 if true, 0.0 if false. If the item is a string, an attempt will be
     * made to parse the string as an float.
     *
     * @param Object The key for the requested item.
     * @throws ClassCastException if the requested item cannot be converted to a float.
     */
    public float getFloat(Object key)
    {
        try
        {
            return getNumber(key).floatValue();
        }
        catch (NumberFormatException nfe)
        {
            throw new ClassCastException("Could not convert to a float: " + key + " = " + get(key));
        }
    }

    /**
     * Attempts to retrieve requested item as a double. If the item is of any number type it will be converted to double.
     * If the item is boolean it will be converted to 1.0 if true, 0.0 if false. If the item is a string, an attempt will be
     * made to parse the string as an double.
     *
     * @param Object The key for the requested item.
     * @throws ClassCastException if the requested item cannot be converted to an double.
     */
    public double getDouble(Object key)
    {
        try
        {
            return getNumber(key).doubleValue();
        }
        catch (NumberFormatException nfe)
        {
            throw new ClassCastException("Could not convert to a double: " + key + " = " + get(key));
        }
    }

    /**
     * Attempts to retrieve requested item as a boolean. If the item is of type boolean, it's value is returned.
     * If the item is a number, then true is returned if the number is > 0, otherwise false is returned.
     *
     * @param Object The key for the requested item.
     * @throws ClassCastException if the requested item cannot be converted to an boolean.
     */
    public boolean getBoolean(Object key)
    {
        Object obj = get(key);
        if (obj == null) return false;
        else if (obj instanceof Number) return ( ((Number)obj).longValue() > 0 );
        else if (obj instanceof Boolean) return ( (Boolean)obj ).booleanValue();
        else if (obj instanceof String) return new Boolean( (String)obj ).booleanValue();

        throw new ClassCastException("Requested item could not be converted to a boolean: " + key + " = " + get(key));
    }

    /**
     * Resets all compare fields. Does not change the current order of Data.
     */
    public void clearSort()
    {
        _compareFields = new LinkedList();
    }

    /**
     * Adds a field to be used in the comparable interface. When evaluating
     * comparisions, all of the configured compareFields are checked in the
     * order they were set until non-equal values are found. The sort order is
     * the order that was last set for this DataSet (using setCompareAscending
     * or setCompareDescending.
     *
     * @param Object The key for which field to use.
     */
    public void setCompareField(Object field)
    {
        setCompareField(field, _sortOrder);
    }

    /**
     * Adds a field to be used in the comparable interface. When evaluating
     * comparisions, all of the configured compareFields are checked in the
     * order they were set until non-equal values are found.
     *
     * @param Object The key for which field to use.
     * @param int The sort order. >= 0 for ascending and < 0 for descending.
     */
    public void setCompareField(Object field, int dir)
    {
        CompareField compareField = new CompareField( field, (dir >= 0 ? Sortable.ASCENDING : Sortable.DESCENDING) );
        _compareFields.add(compareField);
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero, or a
     * positive integer as this object is less than, equal to, or greater than the specified object.
     * If both items are null, 0 is returned.
     *
     * @param Object The Object to be compared.
     * @return int
     * @throws ClassCastException if the specified object cannot be compared to this object.
     */
    public int compareTo(Object obj) throws ClassCastException
    {
        if (obj == null) throw new ClassCastException("Object cannot be null");
        if ( !(obj instanceof DataSet) ) throw new ClassCastException("Object must be of type DataSet");

        DataSet si = (DataSet)obj;
        if (_compareFields == null || _compareFields.size() == 0) return 0;

        int size = _compareFields.size();
        for (int i=0; i<size; i++)
        {
            CompareField compareField = (CompareField)_compareFields.get(i);
            Object thisItem = get(compareField.field);
            Object compareItem = si.get(compareField.field);
            if (compareItem == null || thisItem == null)
            {
                if (thisItem == null && compareItem == null)
                {
                    //nulls are equal, continue to the next compareField
                }
                else
                {
                    if (_sortNull == SMALL)
                    {
                        if (thisItem == null) return compareField.sortOrder*-1;
                        else return compareField.sortOrder*1;
                    }
                    else if (_sortNull == LARGE)
                    {
                        if (thisItem == null) return compareField.sortOrder*1;
                        else return compareField.sortOrder*-1;
                    }
                    throw new ClassCastException("_sortNull is invalid: "+_sortNull);
                }
            }

            if (!_case && thisItem instanceof String && compareItem instanceof String)
            {
                int compare = ( (String)thisItem ).compareToIgnoreCase( (String)compareItem );
                if (compare != 0) return compareField.sortOrder*compare;
                //Objects are equal, continue to next compareField
            }
            else if (thisItem instanceof Comparable)
            {
                int compare = ( (Comparable)thisItem ).compareTo(compareItem);
                if (compare != 0) return compareField.sortOrder*compare;
                //Objects are equal, continue to next compareField
            }
        }
        //Objects are equal
        return 0;
    }

    /**
     * Sets that string comparison is case sensitive.
     */
    public void setCompareCaseSensitive()
    {
        _case = true;
    }

    /**
     * Sets that string comparison is not case sensitive.
     */
    public void setCompareCaseInSensitive()
    {
        _case = false;
    }

    /**
     * Sets to sort data in ascending order.
     */
    public void setCompareAscending()
    {
        _sortOrder = Sortable.ASCENDING;
        applySortOrder();
    }

    /**
     * Sets to sort data in descending order.
     */
    public void setCompareDescending()
    {
        _sortOrder = Sortable.DESCENDING;
        applySortOrder();
    }

    /**
     * Applies the sort order to all compare fields.
     */
    private void applySortOrder()
    {
        if (_compareFields == null) return;

        for (int i=0; i<_compareFields.size(); i++)
        {
            CompareField field = (CompareField)_compareFields.get(i);
            if (field != null) field.sortOrder = _sortOrder;
        }
    }

    /**
     * Sets how to evaluate null to not null comparisons.
     *
     * @param int A constant indicating how nulls are treated.
     * @throws IllegalArgumentException if type is not LARGE or SMALL.
     */
    protected void setSortNull(int sorttype)
    {
        _sortNull = sorttype;
    }

    /**
     * An internal class to determine how to compare two objects.
     */
    private class CompareField
    {
        Object field;
        int sortOrder = Sortable.ASCENDING;

        CompareField(Object field, int sortOrder)
        {
            this.field = field;
            this.sortOrder = sortOrder;
        }

        public String toString()
        {
            return field + "(" + (sortOrder == Sortable.ASCENDING ? "ASC)" : "DESC)");
        }
    }
}