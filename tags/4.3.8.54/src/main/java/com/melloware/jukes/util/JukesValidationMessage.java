package com.melloware.jukes.util;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;

/**
 * Basic validation message to assign an icon, and object, a tooltip,
 * and message.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class JukesValidationMessage
    implements ValidationMessage {
    private Object domainObject;

    private Severity severity;
    private String message;
    private String toolTip;

    /**
     * Default Constructor
     */
    public JukesValidationMessage(String aMessage, Severity aSeverity) {
        super();
        this.message = aMessage;
        this.severity = aSeverity;
        this.domainObject = null;
    }

    public JukesValidationMessage(String aMessage, Severity aSeverity, Object aObject) {
        super();
        this.message = aMessage;
        this.severity = aSeverity;
        this.domainObject = aObject;
    }

    /**
     * Gets the domainObject.
     * <p>
     * @return Returns the domainObject.
     */
    public Object getDomainObject() {
        return this.domainObject;
    }

    /**
     * Gets the message.
     * <p>
     * @return Returns the message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets the severity.
     * <p>
     * @return Returns the severity.
     */
    public Severity getSeverity() {
        return this.severity;
    }

    /**
     * Gets the toolTip.
     * <p>
     * @return Returns the toolTip.
     */
    public String getToolTip() {
        return this.toolTip;
    }

    /**
     * Sets the domainObject.
     * <p>
     * @param aDomainObject The domainObject to set.
     */
    public void setDomainObject(Object aDomainObject) {
        this.domainObject = aDomainObject;
    }

    /**
     * Sets the message.
     * <p>
     * @param aMessage The message to set.
     */
    public void setMessage(String aMessage) {
        this.message = aMessage;
    }

    /**
     * Sets the severity.
     * <p>
     * @param aSeverity The severity to set.
     */
    public void setSeverity(Severity aSeverity) {
        this.severity = aSeverity;
    }

    /**
     * Sets the toolTip.
     * <p>
     * @param aToolTip The toolTip to set.
     */
    public void setToolTip(String aToolTip) {
        this.toolTip = aToolTip;
    }

    /* (non-Javadoc)
     * @see com.jgoodies.validation.ValidationMessage#formattedText()
     */
    public String formattedText() {
        return this.message.trim();
    }

    /* (non-Javadoc)
     * @see com.jgoodies.validation.ValidationMessage#key()
     */
    public Object key() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.jgoodies.validation.ValidationMessage#severity()
     */
    public Severity severity() {
        return this.severity;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.message.trim();
    }
}