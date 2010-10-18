package com.melloware.jukes.db.audit;

import java.util.Date;

/**
 * A marker interface for auditable persistent domain classes.  Classes that
 * implement this interface will have their user and date information updated
 * on creation or modification in the database using the Hibernate Interceptor
 * called AuditInterceptor.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * @see com.melloware.jukes.db.audit.AuditInterceptor
 */
public interface Auditable {

    public Date getCreatedDate();

    public String getCreatedUser();

    public Long getId();

    public Date getModifiedDate();

    public String getModifiedUser();

    public void setCreatedDate(Date createdDate);

    public void setCreatedUser(String createdUser);

    public void setModifiedDate(Date modifiedDate);

    public void setModifiedUser(String modifiedUser);
}