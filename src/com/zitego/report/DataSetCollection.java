package com.zitego.report;

import com.zitego.util.Sortable;
import com.zitego.util.SortColumn;
import java.util.*;
import java.lang.reflect.Array;

/**
 * A class which holds multiple DataSet objects. This class supports sorting of the
 * DataSetCollection using one or more fields in the DataSet's for comparision. If
 * more than one field is set for comparision, then the fields are compared until a
 * non-equal value is encountered. If all values are equal, then the DataSet's are
 * considered equal. The fields are evaluated in the order in which they are set.
 * <br>
 * For example, if the following DataSetCollection is created:
 * <p>
 * DataSetCollection dsc = new DataSetCollection();<br>
 * DataSet ds = new DataSet();<br>
 * ds.put("name", "Bob");<br>
 * ds.put("balance", 10d);<br>
 * ds.put("lastdeposit", 0d);<br>
 * dsc.add(ds);<br>
 * <p>
 * ds = new DataSet();<br>
 * ds.put("name", "Joe");<br>
 * ds.put("balance", 10d);<br>
 * ds.put("lastdeposit", 5d);<br>
 * dsc.add(ds);<br>
 * ds = new DataSet();<br>
 * ds.put("name", "Bill");<br>
 * ds.put("balance", 12d);<br>
 * ds.put("lastdeposit", 2d);<br>
 * dsc.add(ds);<br>
 * <p>
 * Then it could be sorted by a single field:
 * <p>
 * dsc.setSortDescending();<br>
 * dsc.addSort("balance");<br>
 * <p>
 * with the results being (the order of Bob and Joe is not guaranteed because
 * they are equal):<br>
 * Name, Balance, Last Desposit<br>
 * Bill, $12, $2<br>
 * Bob, $10, $0<br>
 * Joe, $10, $5<br>
 * <p>
 * Or, it could be sorted by more than one field:
 * <p>
 * dsc.setSortDescending();<br>
 * dsc.addSort("balance");<br>
 * dsc.addSort("name");<br>
 * <p>
 * with the results being (the order of Bob and Joe is not guaranteed because
 * they are equal):<br>
 * Name, Balance, Last Desposit<br>
 * Bill, $12, $2<br>
 * Joe, $10, $5<br>
 * Bob, $10, $0<br>
 *
 * @author John Glorioso
 * @version $Id: DataSetCollection.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class DataSetCollection extends Vector implements Sortable
{
    /** Whether or not sort direction has been set yet. */
	private boolean _sortSet = false;
	/** Whether or not a sort column has just been set. */
	private boolean _sortUpdated = false;
	/** Whether to sort ascending or not. */
	private boolean _sortAsc = false;
	/** How to handle nul comparisons. DataSet.UNCOMPARABLE by default. */
    private int _sortNull = DataSet.SMALL;
    /** An id for this collection. */
	private long _id;
	/** If we are currently sorting or not. */
	private boolean _sorting = false;
	/** A private property to keep track of sort columns (in case there is no data). */
	private Vector _sortColumns = new Vector();

	/**
	 * Creates a new data set collection.
	 */
	public DataSetCollection()
	{
		super();
		Random random = new Random( System.currentTimeMillis() );
		_id = (long)Math.abs(random.nextFloat()*99999999f);
	}

	/**
	 * Returns the id of this DataSetCollection. The id is a randomly generated number created upon
	 * object construction.
	 *
	 * @return long
	 */
	public long getId()
	{
		return _id;
	}

	/**
	 * Returns the contents of the DataSetCollection as an array of DataSet's.
	 *
	 * @throws ClassCastException if all of the items in the DataSetCollection cannot be cast to DataSet.
	 */
	public DataSet[] getDataSets() throws ClassCastException
	{
		DataSet[] items = new DataSet[size()];
		copyInto(items);
		return items;
	}

    /**
     * Sets a single sort column in the given order.
     *
     * @param SortColumn The constant that denotes which field to use.
     * @param int The sort order.
     */
    public void setSort(SortColumn field, int order)
    {
        clearSort();
        addSort(field, order);
    }

	/**
	 * Adds a SortColumn to be used to sort the objects in this DataSetCollection.
	 *
	 * @param SortColumn The constant that denotes which field to use.
	 */
	public void addSort(SortColumn field)
	{
		addSort(field, Sortable.ASCENDING);
	}

    /**
	 * Adds a sort column to be used and the order to use to sort the objects in this DataSetCollection.
	 *
	 * @param SortColumn The constant that denotes which field to use.
     * @param int The direction to sort. >= 0 for ascending and < 0 for descending.
	 */
	public void addSort(SortColumn field, int order)
	{
		_sortUpdated = true;
		int count = size();
		for (int i=0; i<count; i++)
		{
			( (DataSet)get(i) ).setCompareField(field.getConstant(), order);
			_sortColumns.add(field);
		}
	}

    /**
	 * Clears any sort settings for the DataSetCollection.
	 */
	public void clearSort()
    {
		_sortUpdated = true;
        int count = size();
		for (int i=0; i<count; i++)
        {
			( (DataSet)get(i) ).clearSort();
		}
		_sortColumns.clear();
	}

	public void setSortAscending()
	{
		if (!_sortSet || !_sortAsc)
		{
            int count = size();
			for (int i=0; i<count; i++)
			{
				( (DataSet)get(i) ).setCompareAscending();
			}
			_sortSet = true;
			_sortUpdated =true;
			_sortAsc = true;
		}
	}

	public void setSortDescending()
	{
		if (!_sortSet || _sortAsc)
		{
			int count = size();
			for (int i=0; i<count; i++)
			{
				( (DataSet)get(i) ).setCompareDescending();
			}
			_sortSet = true;
			_sortUpdated =true;
			_sortAsc = false;
		}
	}

	public int getSortDirection()
	{
	    return (_sortAsc ? Sortable.ASCENDING : Sortable.DESCENDING);
	}

	public void setSortColumn(SortColumn col)
	{
	    setSort( col, (_sortAsc ? Sortable.ASCENDING : Sortable.DESCENDING) );
	}

	public SortColumn getSortColumn()
	{
	    //Return the first for this interface
	    return (SortColumn)_sortColumns.get(0);
	}

    public void sort()
    {
		if (_sortUpdated )
		{
			_sorting = true;
			DataSet[] sets = getDataSets();
			if (sets != null)
			{
				Arrays.sort(sets);
				removeAllElements();
				for (int i=0; i<sets.length; i++)
				{
					addElement(sets[i]);
				}
			}
			_sortUpdated = false;
			_sorting = false;
		}
	}

    /**
     * Sets that when sorting to treat null as the smaller value.
     */
    public void setSortNullSmall()
    {
         _sortNull = DataSet.SMALL;
         updateNullSort();
    }

    /**
     * Sets that when sorting to treat null as the larger value.
     */
    public void setSortNullLarge()
    {
         _sortNull = DataSet.LARGE;
         updateNullSort();
    }

    private void updateNullSort()
    {
        int count = size();
        for(int i=0; i<count; i++)
        {
            ( (DataSet)get(i) ).setSortNull(_sortNull);
        }
        _sortUpdated =true;
    }

	/**
	 * Adds an element to the collection.
	 *
	 * @param DataSet The DataSet to add.
	 */
	public void add(DataSet data)
	{
		_sortUpdated = true;
        if (data != null) data.setSortNull(_sortNull);
		super.addElement(data);
	}

	/**
	 * Returns whether we are currently sorting or not.
	 *
	 * @return boolean
	 */
	protected boolean isSorting()
	{
		return _sorting;
	}
}