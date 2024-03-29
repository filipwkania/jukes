package com.melloware.jukes.exception;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * This exception is used to mark (fatal) failures in infrastructure
 * and system code.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @author Christian Bauer <christian@hibernate.org>
 * @version 4.0
 * @see org.apache.commons.lang.exception.NestableRuntimeException
*/
public class InfrastructureException
    extends NestableRuntimeException {

    /**
     * Default Constructor.
     */
    public InfrastructureException() {
        // empty constructor
    }

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(Throwable cause) {
        super(cause);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}