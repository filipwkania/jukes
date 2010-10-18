package com.melloware.jukes.db.audit;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 * Audit inteceptor so any Hibernate insert or update will update the
 * CREATED_USER and MODIFIED_USER as well as CREATED_DATE on inserts.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * @see com.melloware.jukes.db.audit.Auditable
 */
public class AuditInterceptor
    implements Interceptor,
               Serializable {

    private static final Log LOG = LogFactory.getLog(AuditInterceptor.class);
    private final String user;

    /**
     * Default constructor
     */
    public AuditInterceptor() {
        super();
        LOG.info("AuditInterceptor is created.");
        user = StringUtils.defaultIfEmpty(SystemUtils.USER_NAME, "java");
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#getEntity(java.lang.String, java.io.Serializable)
     */
    public Object getEntity(String arg0, Serializable arg1)
                     throws CallbackException {
        return null;
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#getEntityName(java.lang.Object)
     */
    public String getEntityName(Object arg0)
                         throws CallbackException {
        return null;
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#isTransient(java.lang.Object)
     */
    public Boolean isTransient(Object arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#afterTransactionBegin(org.hibernate.Transaction)
     */
    public void afterTransactionBegin(Transaction arg0) {
        // do nothing

    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#afterTransactionCompletion(org.hibernate.Transaction)
     */
    public void afterTransactionCompletion(Transaction arg0) {
        // do nothing

    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#beforeTransactionCompletion(org.hibernate.Transaction)
     */
    public void beforeTransactionCompletion(Transaction arg0) {
        // do nothing

    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#findDirty(java.lang.Object, java.io.Serializable, java.lang.Object[],
     * java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    public int[] findDirty(Object arg0, Serializable arg1, Object[] arg2, Object[] arg3, String[] arg4, Type[] arg5) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#instantiate(java.lang.String, org.hibernate.EntityMode, java.io.Serializable)
     */
    public Object instantiate(String arg0, EntityMode arg1, Serializable arg2)
                       throws CallbackException {
        return null;
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#onCollectionRecreate(java.lang.Object, java.io.Serializable)
     */
    public void onCollectionRecreate(Object arg0, Serializable arg1)
                              throws CallbackException {
        // do nothing

    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#onCollectionRemove(java.lang.Object, java.io.Serializable)
     */
    public void onCollectionRemove(Object arg0, Serializable arg1)
                            throws CallbackException {
        // do nothing
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#onCollectionUpdate(java.lang.Object, java.io.Serializable)
     */
    public void onCollectionUpdate(Object arg0, Serializable arg1)
                            throws CallbackException {
        // do nothing

    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#onDelete(java.lang.Object, java.io.Serializable, java.lang.Object[],
     * java.lang.String[], org.hibernate.type.Type[])
     */
    public void onDelete(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4)
                  throws CallbackException {
        // do nothing

    }

    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types)
                         throws CallbackException {

        boolean result = false;

        if (entity instanceof Auditable) {
            for (int i = 0; i < propertyNames.length; i++) {
                if ("modifiedUser".equals(propertyNames[i])) {
                    currentState[i] = this.user;
                    result = true;
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#onLoad(java.lang.Object, java.io.Serializable, java.lang.Object[],
     * java.lang.String[], org.hibernate.type.Type[])
     */
    public boolean onLoad(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4)
                   throws CallbackException {
        return false;
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#onPrepareStatement(java.lang.String)
     */
    public String onPrepareStatement(String arg0) {
        return arg0;
    }

    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
                   throws CallbackException {

        boolean result = false;

        if (entity instanceof Auditable) {
            for (int i = 0; i < propertyNames.length; i++) {
                if ("createdUser".equals(propertyNames[i])) {
                    state[i] = this.user;
                    result = true;
                }
                if ("modifiedUser".equals(propertyNames[i])) {
                    state[i] = this.user;
                    result = true;
                }
                if (("createdDate".equals(propertyNames[i])) && (state[i] == null)) {
                    state[i] = new Date();
                    result = true;
                }
                if ("modifiedDate".equals(propertyNames[i])) {
                    state[i] = new Date();
                    result = true;
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#postFlush(java.util.Iterator)
     */
    public void postFlush(Iterator arg0)
                   throws CallbackException {
        // do nothing

    }

    /* (non-Javadoc)
     * @see org.hibernate.Interceptor#preFlush(java.util.Iterator)
     */
    public void preFlush(Iterator arg0)
                  throws CallbackException {
        // do nothing

    }

}