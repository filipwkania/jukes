package com.melloware.jukes.db.orm;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.melloware.jukes.db.audit.Auditable;

/**
 * Business POJO representing an ALBUM.
 * <p>
 * Implements Auditable so that the user and date information is updated when
 * this object is updated using a Hibernate Interceptor.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class Disc
    extends AbstractJukesObject
    implements Auditable,
               Comparable {

    public static final String PROPERTYNAME_CREATED_DATE = "createdDate";
    public static final String PROPERTYNAME_MODIFIED_DATE = "modifiedDate";
    public static final String PROPERTYNAME_ID = "id";
    public static final String PROPERTYNAME_LOCATION = "location";
    public static final String PROPERTYNAME_BITRATE = "bitrate";
    public static final String PROPERTYNAME_COVER_URL = "coverUrl";
    public static final String PROPERTYNAME_COVER_SIZE = "coverSize";
    public static final String PROPERTYNAME_CREATED_USER = "createdUser";
    public static final String PROPERTYNAME_GENRE = "genre";
    public static final String PROPERTYNAME_MODIFIED_USER = "modifiedUser";
    public static final String PROPERTYNAME_NAME = "name";
    public static final String PROPERTYNAME_YEAR = "year";
    public static final String PROPERTYNAME_DURATION = "duration";
    public static final String PROPERTYNAME_DURATION_TIME = "durationTime";
    public static final String PROPERTYNAME_ARTIST = "artist";
    public static final String PROPERTYNAME_NOTES = "notes";

    private Artist artist;
    private Date createdDate;
    private Date modifiedDate;
    private File file = null;
    private Long bitrate;
    private long coverSize;
    private long duration;
    private Long id;
    private Set tracks;
    private String coverUrl;
    private String createdUser;
    private String durationTime;
    private String genre;
    private String location;
    private String modifiedUser;
    private String name;
    private String notes;
    private String year;

    /** default constructor */
    public Disc() {
        super();
    }

    /** constructor with id */
    public Disc(Long id) {
        this.id = id;
    }

    public Artist getArtist() {
        return this.artist;
    }

    /**
     * This date determines how the NEW flag is checked in the isNew() function.
     * <p>
     * @return the created date is used to check in the isNew() function
     */
    public Date getAuditDate() {
        return this.createdDate;
    }

    /**
     * @return Returns the bitrate.
     */
    public Long getBitrate() {
        return this.bitrate;
    }

    /**
     * This is used to fool the tree for lazy loading of tree nodes.
     * <p>
     * @return the number of children this object has
     */
    public int getChildCount() {
        if (childCount == -1) {
            // childCount = HibernateDao.count("Track as track where track.disc = " + this.id);
            childCount = 10;
        }
        return childCount;
    }

    /**
     * Gets the coverSize.
     * <p>
     * @return Returns the coverSize.
     */
    public long getCoverSize() {
        return this.coverSize;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public String getCreatedUser() {
        return this.createdUser;
    }

    /**
     * Gets the display format based on a format from prefs.  The format is in
     * aFormat and can have values %b forbitrate, %y for year,
     * %r for running time, and %d for disc.
     *
     * Examples:
     * %y -%d = 2005 - Guero
     * <p>
     * @param aFormat the string format like %y -%d to rename 2005 - Guero
     * @return the value of the display text
     */
    public String getDisplayText(final String aFormat) {
        String display = aFormat;
        if (StringUtils.isBlank(display)) {
            display = "%y %d";
        }
        String result = "";
        display = StringUtils.replace(display, "%b", getBitrate().toString());
        display = StringUtils.replace(display, "%y", getYear());
        display = StringUtils.replace(display, "%r", getDurationTime());
        display = StringUtils.replace(display, "%d", getName());
        display = StringUtils.replace(display, "%D", getName().toUpperCase());
        result = display;

        return result;
    }

    /**
     * Gets the duration.
     * <p>
     * @return Returns the duration.
     */
    public long getDuration() {
        return this.duration;
    }

    /**
     * Gets the durationTime.
     * <p>
     * @return Returns the durationTime.
     */
    public String getDurationTime() {
        return this.durationTime;
    }

    public String getGenre() {
        return this.genre;
    }

    public Long getId() {
        return this.id;
    }

    /**
     * Gets the location.
     * <p>
     * @return Returns the location.
     */
    public String getLocation() {
        return this.location;
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

    public Set getTracks() {
        return this.tracks;
    }

    /**
     * Returns the release year.
     * <p>
     * @return the release year
     */
    public String getYear() {
        return this.year;
    }

    public void setArtist(final Artist artist) {
        this.artist = artist;
    }

    /**
     * @param aBitrate The bitrate to set.
     */
    public void setBitrate(final Long aBitrate) {
        final Long old = getBitrate();
        this.bitrate = aBitrate;
        firePropertyChange(PROPERTYNAME_BITRATE, old, aBitrate);

    }

    /**
     * Sets the coverSize.
     * <p>
     * @param aCoverSize The coverSize to set.
     */
    public void setCoverSize(final long aCoverSize) {
        final long old = getCoverSize();
        this.coverSize = aCoverSize;
        firePropertyChange(PROPERTYNAME_COVER_SIZE, old, aCoverSize);
    }

    public void setCoverUrl(final String coverUrl) {
        final String old = getCoverUrl();
        this.coverUrl = coverUrl;
        firePropertyChange(PROPERTYNAME_COVER_URL, old, coverUrl);
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

    /**
     * Sets the duration.
     * <p>
     * @param aDuration The duration to set.
     */
    public void setDuration(final long aDuration) {
        final long old = getDuration();
        this.duration = aDuration;
        firePropertyChange(PROPERTYNAME_DURATION, old, aDuration);
    }

    /**
     * Sets the durationTime.
     * <p>
     * @param aDurationTime The durationTime to set.
     */
    public void setDurationTime(final String aDurationTime) {
        final String old = getDurationTime();
        this.durationTime = aDurationTime;
        firePropertyChange(PROPERTYNAME_DURATION_TIME, old, aDurationTime);
    }

    public void setGenre(final String genre) {
        final String old = getGenre();
        this.genre = genre;
        firePropertyChange(PROPERTYNAME_GENRE, old, genre);
    }

    public void setId(final Long id) {
        final Long old = getId();
        this.id = id;
        firePropertyChange(PROPERTYNAME_ID, old, id);
    }

    /**
     * Sets the location.
     * <p>
     * @param aLocation The location to set.
     */
    public void setLocation(final String aLocation) {
        final String old = getLocation();
        this.location = aLocation;
        this.file = null;
        firePropertyChange(PROPERTYNAME_LOCATION, old, aLocation);
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

    public void setTracks(final Set tracks) {
        this.tracks = tracks;
    }

    /**
     * Sets the release year.
     * <p>
     * @param year the year of this release
     */
    public void setYear(final String year) {
        final String old = getYear();
        this.year = year;
        firePropertyChange(PROPERTYNAME_YEAR, old, year);
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.db.orm.AbstractJukesObject#isValid()
     */
    public boolean isValid() {
        boolean result = false;
        if (StringUtils.isNotBlank(getLocation())) {
            if (this.file == null) {
                this.file = new File(getLocation());
            }
            result = this.file.exists();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public void addTrack(final Track track) {
        if (this.tracks == null) {
            this.tracks = new HashSet();
        }
        this.tracks.add(track);
        track.setDisc(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object object) {
        final Disc disc = (Disc)object;
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(this.getName().toUpperCase(), disc.getName().toUpperCase());
        return builder.toComparison();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Disc)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final Disc rhs = (Disc)obj;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(artist, rhs.artist);
        builder.append(name, rhs.name);
        return builder.isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(artist).append(name).toHashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("id", id);
        builder.append("name", name);
        builder.append("year", year);
        builder.append("genre", genre);
        builder.append("bitrate", bitrate);
        builder.append("coverUrl", coverUrl);
        builder.append("location", location);
        return builder.toString();
    }

}