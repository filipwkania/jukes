package com.melloware.jukes.db.orm;

import java.io.Serializable;
import java.util.Date;

import com.jgoodies.binding.beans.Model;
import com.melloware.jukes.util.TimeSpan;

/**
 * Base class for all domain ORM objects.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 *
 * @see com.jgoodies.binding.beans.Model
 */
abstract public class AbstractJukesObject
    extends Model
    implements Serializable {

    private static final Date TODAY = new Date();

    /*
     * These are used for lazy loading of tree nodes.
     */
    public static final int NO_CHILDREN = 0;
    protected boolean newFile = false;
    protected boolean valid = true;
    protected int childCount = -1;
    
    /**
     * All objects must implement a name function to describe this object.
     * <p>
     * @return the name of the object
     */
    abstract public String getName();
    
    /**
     * All objects must implement a unique identifier.
     * <p>
     * @return the id of the object
     */
    abstract public Long getId();

    /**
     * Method implemented in each ORM class. If it has some sort of child return
     * the count else return 0. This is used for lazy loading the tree nodes.
     * <p>
     * @return the count of the children or 0 if no children
     */
    abstract public int getChildCount();
    
    /**
     * Date will be used to determine new-ness.  
     * <p>
     * @return the audit date to determine new-ness
     */
    abstract public Date getAuditDate();

    /**
     * Method implemented in each ORM class. This returns the audit modified 
     * date.
     * <p>
     * @return the modification date
     */
    abstract public Date getModifiedDate();
    
    /**
     * Method implemented in each ORM class. This returns the audit created 
     * date.
     * <p>
     * @return the created date
     */
    abstract public Date getCreatedDate();

    /**
     * Gets the valid flag.
     * <p>
     * @return Returns the valid flag.
     */
    abstract public boolean isValid();
    
    /**
     * Gets the valid flag.
     * <p>
     * @return Returns the valid flag.
     */
    public boolean isNotValid() {
        return !isValid();
    }

    /**
     * Sets the Child count.
     * <p>
     * @param count the count to set it to.
     */
    public void setChildCount(final int count) {
        this.childCount = count;
    }

    /**
     * Sets the newFile.
     * <p>
     * @param aNewFile The newFile to set.
     */
    public void setNewFile(final boolean aNewFile) {
        this.newFile = aNewFile;
    }

    /**
     * Sets the valid.
     * <p>
     * @param aValid The valid to set.
     */
    public void setValid(final boolean aValid) {
        this.valid = aValid;
    }

    /**
     * Gets whether this file is considered new by checking the number of days
     * against aDays.
     * <p>
     * @param aDays the number of days past a file is considered new
     * @return Returns the newFile.
     */
    public boolean isNewFile(final int aDays) {
        // subtract today from the modified date and check against the pref
        final TimeSpan timespan = TimeSpan.subtract(TODAY, this.getAuditDate());
        this.newFile = (timespan.getDays() < aDays);
        return this.newFile;
    }

}