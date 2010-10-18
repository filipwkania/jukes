package com.melloware.jukes.db.orm;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.melloware.jukes.db.audit.Auditable;

/**
 * Business POJO representing an ARTIST.
 * <p>
 * Implements Auditable so that the user and date information is updated when
 * this object is updated using a Hibernate Interceptor.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class Artist
    extends AbstractJukesObject
    implements Auditable, Comparable {

    public static final String PROPERTYNAME_CREATED_DATE = "createdDate";
    public static final String PROPERTYNAME_MODIFIED_DATE = "modifiedDate";
    public static final String PROPERTYNAME_ID = "id";
    public static final String PROPERTYNAME_CREATED_USER = "createdUser";
    public static final String PROPERTYNAME_MODIFIED_USER = "modifiedUser";
    public static final String PROPERTYNAME_NAME = "name"; 
    public static final String PROPERTYNAME_NOTES = "notes";

    private Date createdDate;
    private Date modifiedDate;
    private Long id;
    private Set discs;
    private String createdUser;
    private String modifiedUser;
    private String name; 
    private String notes;

    /** default constructor */
    public Artist() {
        super();
    }

    /** constructor with id */
    public Artist(Long id) {
        this.id = id;
    }

    /**
     * This date determines how the NEW flag is checked in the isNew() function.
     * <p>
     * @return the modified date is used to check in the isNew() function
     */
    public Date getAuditDate() {
        return this.modifiedDate;
    }

    /**
     * This is used to fool the tree for lazy loading of tree nodes.
     * <p>
     * @return the number of children this object has
     */
    public int getChildCount() {
        childCount = this.discs.size();
        return childCount;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public String getCreatedUser() {
        return this.createdUser;
    }

    public Set getDiscs() {
        return this.discs;
    }

    public Long getId() {
        return this.id;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public String getModifiedUser() {
        return this.modifiedUser;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Gets the notes.
     * <p>
     * @return Returns the notes.
     */
    public String getNotes() {
        return this.notes;
    }

    public void setCreatedDate(final Date createdDate) {
        final Date old = getCreatedDate();
        this.createdDate = createdDate;
        firePropertyChange(PROPERTYNAME_CREATED_DATE, old, createdDate);
    }

    public void setCreatedUser(final String createdUser) {
        final String old = getCreatedUser();
        this.createdUser = createdUser;
        firePropertyChange(PROPERTYNAME_CREATED_USER, old, createdUser);
    }

    public void setDiscs(final Set discs) {
        this.discs = discs;
    }

    public void setId(final Long id) {
        final Long old = getId();
        this.id = id;
        firePropertyChange(PROPERTYNAME_ID, old, id);
    }

    public void setModifiedDate(final Date modifiedDate) {
        final Date old = getModifiedDate();
        this.modifiedDate = modifiedDate;
        firePropertyChange(PROPERTYNAME_MODIFIED_DATE, old, modifiedDate);
    }

    public void setModifiedUser(final String modifiedUser) {
        final String old = getModifiedUser();
        this.modifiedUser = modifiedUser;
        firePropertyChange(PROPERTYNAME_MODIFIED_USER, old, modifiedUser);
    }

    public void setName(final String name) {
        final String old = getName();
        this.name = name;
        firePropertyChange(PROPERTYNAME_NAME, old, name);
    }

    /**
     * Sets the notes.
     * <p>
     * @param aNotes The notes to set.
     */
    public void setNotes(final String aNotes) {
        final String old = getNotes();
        this.notes = aNotes;
        firePropertyChange(PROPERTYNAME_NOTES, old, aNotes);
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.db.orm.AbstractJukesObject#isValid()
     */
    public boolean isValid() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public void addDisc(final Disc disc) {
        if (this.discs == null) {
            this.discs = new HashSet();
        }
        this.discs.add(disc);
        disc.setArtist(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Artist)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final Artist rhs = (Artist)obj;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(name, rhs.name);
        return builder.isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).toHashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("id", id);
        builder.append("name", name);
        return builder.toString();
    }

    /* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object object) {
		final Artist artist = (Artist)object;
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(this.getName().toUpperCase(), artist.getName().toUpperCase());
        return builder.toComparison();
	}

}