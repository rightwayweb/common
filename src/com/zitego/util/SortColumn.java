package com.zitego.util;

/**
 * An extension of Constant that allows one to sort on a Constant.
 *
 * @author John Glorioso
 * @version $Id: SortColumn.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class SortColumn extends Constant
{
    protected Constant _constant;

    public SortColumn(Constant c)
    {
        super( c.getValue(), c.getDescription() );
        _constant = c;
    }

    /**
     * Returns the constant behind this column.
     *
     * @return Constant
     */
    public Constant getConstant()
    {
        return _constant;
    }
}