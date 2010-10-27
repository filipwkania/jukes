package com.melloware.jukes.exception;

import org.apache.commons.lang.exception.NestableException;

/**
 * Base Exception for all wrapped exceptions in PUYJ.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class JukesException extends NestableException {

    /**
     * Default Constructor
     */
    public JukesException() {
        super();
    }

    /**
     * Constructor that takes a message.
     * @param aMessage the message to contain
     */
    public JukesException(String aMessage) {
        super(aMessage);
    }

    /**
     * Constructor takes a message and exception.
     * <p>
     * @param aMessage the message
     * @param aThrowable the wrapped exception
     */
    public JukesException(String aMessage, Throwable aThrowable) {
        super(aMessage, aThrowable);
    }

    /**
     * Constructor that wraps another exception.
     * <p>
     * @param aThrowable
     */
    public JukesException(Throwable aThrowable) {
        super(aThrowable);
    }

}
