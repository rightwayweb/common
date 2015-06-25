package com.zitego.util;

import com.zitego.util.Sortable;
import com.zitego.report.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an extension to the group navigator class that also sorts. You must define
 * setCollection which gets called in the constructors. It must populate the DataSetCollection
 * based on the objects in the original list and the sort columns.
 *
 * @author John Glorioso
 * @version $Id: SortableGroupNavigator.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public abstract class SortableGroupNavigator extends GroupNavigator implements Sortable
{
    /** The sort columns. */
    private SortColumn[] _sortCols;
    /** The sort direction. Default is descending. */
    private int _sortDirection = Sortable.DESCENDING;
    /** The sort column. */
    private SortColumn _sortColumn;
    /** The data set collection of the current group. */
    protected DataSetCollection _collection;

    /**
     * Creates a new navigator with a list the group/chunk size.
     *
     * @param Object The list.
     * @param int The group/chunk size.
     * @param SortColumn[] The sort columns.
     * @throws IllegalArgumentException if the list is null.
     */
    public SortableGroupNavigator(List l, int size, SortColumn[] cols) throws IllegalArgumentException
    {
        this(null, l, size, cols);
    }

    /**
     * Creates a new navigator with an id, list, and the group/chunk size.
     *
     * @param String The id for this navigator.
     * @param Object The list.
     * @param int The group/chunk size.
     * @param SortColumn[] The sort columns.
     * @throws IllegalArgumentException if the list is null.
     */
    public SortableGroupNavigator(String id, List l, int size, SortColumn[] cols) throws IllegalArgumentException
    {
        super(id, l, size);
        _sortCols = cols;
        setCollection();
    }

    /**
     * Creates a new navigator with an array and the group/chunk size.
     *
     * @param Object[] The array.
     * @param int The group/chunk size.
     * @param SortColumn[] The sort columns.
     * @throws IllegalArgumentException if the array is null.
     */
    public SortableGroupNavigator(Object[] a, int size, SortColumn[] cols) throws IllegalArgumentException
    {
        this(null, a, size, cols);
    }

    /**
     * Creates a new navigator with an id, array, and the group/chunk size.
     *
     * @param String The id.
     * @param Object[] The array.
     * @param int The group/chunk size.
     * @param SortColumn[] The sort columns.
     * @throws IllegalArgumentException if the array is null.
     */
    public SortableGroupNavigator(String id, Object[] a, int size, SortColumn[] cols) throws IllegalArgumentException
    {
        super(id, a, size);
        _sortCols = cols;
        setCollection();
    }

    /**
     * Sets the dataset collection for sorting.
     *
     * @throws RuntimeException if an error occurs setting the collection.
     */
    protected abstract void setCollection() throws RuntimeException;

    /**
     * Returns the entire data set collection.
     *
     * @return DataSetCollection
     */
    public DataSetCollection getDataSetCollection()
    {
        return _collection;
    }

    public void setSortAscending()
    {
        _sortDirection = Sortable.ASCENDING;
        _collection.setSortAscending();
    }

    public void setSortDescending()
    {
        _sortDirection = Sortable.DESCENDING;
        _collection.setSortDescending();
    }

    public int getSortDirection()
    {
        return _sortDirection;
    }

    public void setSortColumn(SortColumn col)
    {
        _sortColumn = col;
        _collection.setSortColumn(_sortColumn);
    }

    public SortColumn getSortColumn()
    {
        return _sortColumn;
    }

    public void sort()
    {
        _collection.sort();
    }

    /**
     * Returns the current sorted group as a list of DataSet objects. If you want the list
     * of actual objects, use the getCurrentGroup method.
     *
     * @return List
     */
    public List getCurrentSortedGroup()
    {
        int start = getStartIndex();
        if (start < 0) return new ArrayList(0);
        return _collection.subList( start, getEndIndex()+1 );
    }

    /**
     * Returns the sort columns for this navigator.
     *
     * @return SortColumn[]
     */
    public SortColumn[] getSortColumns()
    {
        return _sortCols;
    }
}