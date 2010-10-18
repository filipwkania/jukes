package com.melloware.jukes.exception;

/**
 * Exception related to any windows tray icon errors.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class WindowsTrayException extends JukesException {

    /**
     * Default constructor
     */
    public WindowsTrayException() {
        super();
    }

    /**
     * Constructor that takes a message.
     * @param aMessage the message to contain
     */
    public WindowsTrayException(String aMessage) {
        super(aMessage);
    }

    /**
     * Constructor takes a mesage and exception.
     * <p>
     * @param aMessage the message
     * @param aThrowable the wrapped exception
     */
    public WindowsTrayException(String aMessage, Throwable aThrowable) {
        super(aMessage, aThrowable);
    }

    /**
     * Constructor that wraps another exception.
     * <p>
     * @param aThrowable
     */
    public WindowsTrayException(Throwable aThrowable) {
        super(aThrowable);
    }

}
