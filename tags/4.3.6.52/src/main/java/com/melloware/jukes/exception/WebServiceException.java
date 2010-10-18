package com.melloware.jukes.exception;

/**
 * Exception related to any SOAP errors accessing web services.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class WebServiceException extends JukesException {

    /**
     * Default constructor
     */
    public WebServiceException() {
        super();
    }

    /**
     * Constructor that takes a message.
     * @param aMessage the message to contain
     */
    public WebServiceException(String aMessage) {
        super(aMessage);
    }

    /**
     * Constructor takes a mesage and exception.
     * <p>
     * @param aMessage the message
     * @param aThrowable the wrapped exception
     */
    public WebServiceException(String aMessage, Throwable aThrowable) {
        super(aMessage, aThrowable);
    }

    /**
     * Constructor that wraps another exception.
     * <p>
     * @param aThrowable
     */
    public WebServiceException(Throwable aThrowable) {
        super(aThrowable);
    }

}
