package com.melloware.jukes.db.orm;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.melloware.jukes.db.audit.Auditable;

/**
 * Business POJO representing an TRACK.
 * <p>
 * Implements Auditable so that the user and date information is updated when
 * this object is updated using a Hibernate Interceptor.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class Track
    extends AbstractJukesObject
    implements Auditable,
               Comparable {

    public static final String PROPERTYNAME_CREATED_DATE = "createdDate";
    public static final String PROPERTYNAME_MODIFIED_DATE = "modifiedDate";
    public static final String PROPERTYNAME_DURATION = "duration";
    public static final String PROPERTYNAME_ID = "id";
    public static final String PROPERTYNAME_BITRATE = "bitrate";
    public static final String PROPERTYNAME_COMMENT = "comment";
    public static final String PROPERTYNAME_CREATED_USER = "createdUser";
    public static final String PROPERTYNAME_DURATION_TIME = "durationTime";
    public static final String PROPERTYNAME_MODIFIED_USER = "modifiedUser";
    public static final String PROPERTYNAME_NAME = "name";
    public static final String PROPERTYNAME_TRACK_NUMBER = "trackNumber";
    public static final String PROPERTYNAME_TRACK_URL = "trackUrl";
    public static final String PROPERTYNAME_TRACK_SIZE = "trackSize";

    private Date createdDate;
    private Date modifiedDate;
    private Disc disc;
    private File file = null;
    private Long bitrate;
    private long duration;
    private Long id;
    private long trackSize;
    private String comment;
    private String createdUser;
    private String durationTime;
    private String modifiedUser;
    private String name;
    private String trackNumber;
    private String trackUrl;

    /** default constructor */
    public Track() {
        super();
    }

    /** constructor with id */
    public Track(Long id) {
        this.id = id;
    }

    /**
     * This date determines how the NEW flag is checked in the isNew() function.
     * <p>
     * @return the created date is used to check in the isNew() function
     */
    public Date getAuditDate() {
        return this.createdDate;
    }

    public Long getBitrate() {
        return this.bitrate;
    }

    /**
     * This is used to fool the tree for lazy loading of tree nodes.
     * <p>
     * @return the number of children this object has
     */
    public int getChildCount() {
        // tracks have no children so return 0
        return NO_CHILDREN;
    }

    /**
     * Gets the comment.
     * <p>
     * @return Returns the comment.
     */
    public String getComment() {
        return this.comment;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public String getCreatedUser() {
        return this.createdUser;
    }

    public Disc getDisc() {
        return this.disc;
    }

    /**
     * Gets the display format based on a format from prefs.  The format is in
     * aFormat and can have values %b for bitrate, %n for track number,
     * %r for duration, and %t for title.
     *
     * Examples:
     * %n -%t = 01 - Track.mp3
     * <p>
     * @param aFormat the string format like %n -%t to display 01 - Track.mp3
     * @return the value of the display text
     */
    public String getDisplayText(final String aFormat) {
        String display = aFormat;
        if (StringUtils.isBlank(display)) {
            display = "%n. %t";
        }
        String result = "";
        display = StringUtils.replace(display, "%b", getBitrate().toString());
        display = StringUtils.replace(display, "%n", getTrackNumber());
        display = StringUtils.replace(display, "%r", getDurationTime());
        display = StringUtils.replace(display, "%t", getName());
        display = StringUtils.replace(display, "%T", getName().toUpperCase());
        result = display;

        return result;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getDurationTime() {
        return this.durationTime;
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

    public String getTrackNumber() {
        return this.trackNumber;
    }

    /**
     * Gets the trackSize.
     * <p>
     * @return Returns the trackSize.
     */
    public long getTrackSize() {
        return this.trackSize;
    }

    public String getTrackUrl() {
        return this.trackUrl;
    }

    public void setBitrate(final Long bitrate) {
        final Long old = getBitrate();
        this.bitrate = bitrate;
        firePropertyChange(PROPERTYNAME_BITRATE, old, bitrate);
    }

    /**
     * Sets the comment.
     * <p>
     * @param aComment The comment to set.
     */
    public void setComment(final String aComment) {
        final String old = getComment();
        this.comment = aComment;
        firePropertyChange(PROPERTYNAME_COMMENT, old, aComment);
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

    public void setDisc(final Disc disc) {
        this.disc = disc;
    }

    public void setDuration(final long duration) {
        final long old = getDuration();
        this.duration = duration;
        firePropertyChange(PROPERTYNAME_DURATION, old, duration);
    }

    public void setDurationTime(final String durationTime) {
        final String old = getDurationTime();
        this.durationTime = durationTime;
        firePropertyChange(PROPERTYNAME_DURATION_TIME, old, durationTime);
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

    public void setTrackNumber(final String trackNumber) {
        final String old = getTrackNumber();
        this.trackNumber = trackNumber;
        firePropertyChange(PROPERTYNAME_TRACK_NUMBER, old, trackNumber);
    }

    /**
     * Sets the trackSize.
     * <p>
     * @param aTrackSize The trackSize to set.
     */
    public void setTrackSize(final long aTrackSize) {
        final long old = getTrackSize();
        this.trackSize = aTrackSize;
        firePropertyChange(PROPERTYNAME_TRACK_SIZE, old, aTrackSize);
    }

    public void setTrackUrl(final String trackUrl) {
        final String old = getTrackUrl();
        this.trackUrl = trackUrl;
        this.file = null;
        firePropertyChange(PROPERTYNAME_TRACK_URL, old, trackUrl);
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.db.orm.AbstractJukesObject#isValid()
     */
    public boolean isValid() {
        boolean result = false;
        if (StringUtils.isNotBlank(getTrackUrl())) {
            if (this.file == null) {
                this.file = new File(getTrackUrl());
            }
            result = this.file.exists();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object object) {
        final Track track = (Track)object;
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(this.getTrackNumber(), track.getTrackNumber());
        return builder.toComparison();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Track)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final Track rhs = (Track)obj;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(disc, rhs.disc);
        builder.append(trackNumber, rhs.trackNumber);
        builder.append(name, rhs.name);
        return builder.isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(disc).append(trackNumber).append(name).toHashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("id", id);
        builder.append("name", name);
        builder.append("trackNumber", trackNumber);
        builder.append("bitrate", bitrate);
        builder.append("duration", duration);
        builder.append("durationTime", durationTime);
        builder.append("trackUrl", trackUrl);
        return builder.toString();
    }

}