package com.melloware.jukes.gui.view.dialogs;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import org.hibernate.stat.Statistics;

import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;
import com.l2fprod.common.model.DefaultBeanInfoResolver;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.gui.tool.Resources;

/**
 * A <code>PropertySheet</code> for the <code>Hibernate</code> statistics.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class PropertySheetHibernateStats
    extends JPanel {
	
	private static final String ENTITY = "Entity";
	private static final String CACHE = "Cache";
	private static final String COLLECTIONS = "Collections";
	private static final String SESSION = "Session";
	private static final String TRANSACTION = "Transaction";
	private static final String QUERY = "Query";

    public PropertySheetHibernateStats() {
        setLayout(LookAndFeelTweaks.createVerticalPercentLayout());

        final Bean data = new Bean();

        Statistics stats = HibernateUtil.getSessionFactory().getStatistics();
        data.setSessionOpenCount(stats.getSessionOpenCount());
        data.setSessionCloseCount(stats.getSessionCloseCount());
        data.setSuccessfulTransactionCount(stats.getSuccessfulTransactionCount());
        data.setTransactionCount(stats.getTransactionCount());
        data.setOptimisticFailureCount(stats.getOptimisticFailureCount());
        data.setFlushCount(stats.getFlushCount());
        data.setConnectCount(stats.getConnectCount());
        data.setPrepareStatementCount(stats.getPrepareStatementCount());
        data.setCloseStatementCount(stats.getCloseStatementCount());
        data.setSecondLevelCachePutCount(stats.getSecondLevelCachePutCount());
        data.setSecondLevelCacheHitCount(stats.getSecondLevelCacheHitCount());
        data.setSecondLevelCacheMissCount(stats.getSecondLevelCacheMissCount());
        data.setEntityLoadCount(stats.getEntityLoadCount());
        data.setEntityUpdateCount(stats.getEntityUpdateCount());
        data.setEntityInsertCount(stats.getEntityInsertCount());
        data.setEntityDeleteCount(stats.getEntityDeleteCount());
        data.setEntityFetchCount(stats.getEntityFetchCount());
        data.setQueryExecutionCount(stats.getQueryExecutionCount());
        data.setQueryCachePutCount(stats.getQueryCachePutCount());
        data.setQueryCacheHitCount(stats.getQueryCacheHitCount());
        data.setQueryCacheMissCount(stats.getQueryCacheMissCount());
        data.setQueryExecutionMaxTime(stats.getQueryExecutionMaxTime());
        data.setCollectionLoadCount(stats.getCollectionLoadCount());
        data.setCollectionRecreateCount(stats.getCollectionRecreateCount());
        data.setCollectionRemoveCount(stats.getCollectionRemoveCount());
        data.setCollectionUpdateCount(stats.getCollectionUpdateCount());

        DefaultBeanInfoResolver resolver = new DefaultBeanInfoResolver();
        BeanInfo beanInfo = resolver.getBeanInfo(data);

        PropertySheetPanel sheet = new PropertySheetPanel();
        sheet.setMode(PropertySheet.VIEW_AS_CATEGORIES);
        sheet.setProperties(beanInfo.getPropertyDescriptors());
        sheet.readFromObject(data);
        sheet.setDescriptionVisible(true);
        sheet.setSortingCategories(true);
        sheet.setSortingProperties(true);
        add(sheet, "*");

        // everytime a property change, update the button with it
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Property prop = (Property)evt.getSource();
                prop.writeToObject(data);
            }
        };
        sheet.addPropertySheetChangeListener(listener);
    }

    /**
     * Class used to hold the property info.
     */
    public static class Bean {
        private long closeStatementCount;
        private long collectionFetchCount;
        private long collectionLoadCount;
        private long collectionRecreateCount;
        private long collectionRemoveCount;
        private long collectionUpdateCount;
        private long connectCount;
        private long entityDeleteCount;
        private long entityFetchCount;
        private long entityInsertCount;
        private long entityLoadCount;
        private long entityUpdateCount;
        private long flushCount;
        private long optimisticFailureCount;
        private long prepareStatementCount;
        private long queryCacheHitCount;
        private long queryCacheMissCount;
        private long queryCachePutCount;
        private long queryExecutionCount;
        private long queryExecutionMaxTime;
        private long secondLevelCacheHitCount;
        private long secondLevelCacheMissCount;
        private long secondLevelCachePutCount;
        private long sessionCloseCount;

        private long sessionOpenCount;
        private long successfulTransactionCount;
        private long transactionCount;

        /**
         * Gets the closeStatementCount.
         * <p>
         * @return Returns the closeStatementCount.
         */
        public long getCloseStatementCount() {
            return this.closeStatementCount;
        }

        /**
         * Gets the collectionFetchCount.
         * <p>
         * @return Returns the collectionFetchCount.
         */
        public long getCollectionFetchCount() {
            return this.collectionFetchCount;
        }

        /**
         * Gets the collectionLoadCount.
         * <p>
         * @return Returns the collectionLoadCount.
         */
        public long getCollectionLoadCount() {
            return this.collectionLoadCount;
        }

        /**
         * Gets the collectionRecreateCount.
         * <p>
         * @return Returns the collectionRecreateCount.
         */
        public long getCollectionRecreateCount() {
            return this.collectionRecreateCount;
        }

        /**
         * Gets the collectionRemoveCount.
         * <p>
         * @return Returns the collectionRemoveCount.
         */
        public long getCollectionRemoveCount() {
            return this.collectionRemoveCount;
        }

        /**
         * Gets the collectionUpdateCount.
         * <p>
         * @return Returns the collectionUpdateCount.
         */
        public long getCollectionUpdateCount() {
            return this.collectionUpdateCount;
        }

        /**
         * Gets the connectCount.
         * <p>
         * @return Returns the connectCount.
         */
        public long getConnectCount() {
            return this.connectCount;
        }

        /**
         * Gets the entityDeleteCount.
         * <p>
         * @return Returns the entityDeleteCount.
         */
        public long getEntityDeleteCount() {
            return this.entityDeleteCount;
        }

        /**
         * Gets the entityFetchCount.
         * <p>
         * @return Returns the entityFetchCount.
         */
        public long getEntityFetchCount() {
            return this.entityFetchCount;
        }

        /**
         * Gets the entityInsertCount.
         * <p>
         * @return Returns the entityInsertCount.
         */
        public long getEntityInsertCount() {
            return this.entityInsertCount;
        }

        /**
         * Gets the entityLoadCount.
         * <p>
         * @return Returns the entityLoadCount.
         */
        public long getEntityLoadCount() {
            return this.entityLoadCount;
        }

        /**
         * Gets the entityUpdateCount.
         * <p>
         * @return Returns the entityUpdateCount.
         */
        public long getEntityUpdateCount() {
            return this.entityUpdateCount;
        }

        /**
         * Gets the flushCount.
         * <p>
         * @return Returns the flushCount.
         */
        public long getFlushCount() {
            return this.flushCount;
        }

        /**
         * Gets the optimisticFailureCount.
         * <p>
         * @return Returns the optimisticFailureCount.
         */
        public long getOptimisticFailureCount() {
            return this.optimisticFailureCount;
        }

        /**
         * Gets the prepareStatementCount.
         * <p>
         * @return Returns the prepareStatementCount.
         */
        public long getPrepareStatementCount() {
            return this.prepareStatementCount;
        }

        /**
         * Gets the queryCacheHitCount.
         * <p>
         * @return Returns the queryCacheHitCount.
         */
        public long getQueryCacheHitCount() {
            return this.queryCacheHitCount;
        }

        /**
         * Gets the queryCacheMissCount.
         * <p>
         * @return Returns the queryCacheMissCount.
         */
        public long getQueryCacheMissCount() {
            return this.queryCacheMissCount;
        }

        /**
         * Gets the queryCachePutCount.
         * <p>
         * @return Returns the queryCachePutCount.
         */
        public long getQueryCachePutCount() {
            return this.queryCachePutCount;
        }

        /**
         * Gets the queryExecutionCount.
         * <p>
         * @return Returns the queryExecutionCount.
         */
        public long getQueryExecutionCount() {
            return this.queryExecutionCount;
        }

        /**
         * Gets the queryExecutionMaxTime.
         * <p>
         * @return Returns the queryExecutionMaxTime.
         */
        public long getQueryExecutionMaxTime() {
            return this.queryExecutionMaxTime;
        }

        /**
         * Gets the secondLevelCacheHitCount.
         * <p>
         * @return Returns the secondLevelCacheHitCount.
         */
        public long getSecondLevelCacheHitCount() {
            return this.secondLevelCacheHitCount;
        }

        /**
         * Gets the secondLevelCacheMissCount.
         * <p>
         * @return Returns the secondLevelCacheMissCount.
         */
        public long getSecondLevelCacheMissCount() {
            return this.secondLevelCacheMissCount;
        }

        /**
         * Gets the secondLevelCachePutCount.
         * <p>
         * @return Returns the secondLevelCachePutCount.
         */
        public long getSecondLevelCachePutCount() {
            return this.secondLevelCachePutCount;
        }

        /**
         * Gets the sessionCloseCount.
         * <p>
         * @return Returns the sessionCloseCount.
         */
        public long getSessionCloseCount() {
            return this.sessionCloseCount;
        }

        /**
         * Gets the sessionOpenCount.
         * <p>
         * @return Returns the sessionOpenCount.
         */
        public long getSessionOpenCount() {
            return this.sessionOpenCount;
        }

        /**
         * Gets the successfulTransactionCount.
         * <p>
         * @return Returns the successfulTransactionCount.
         */
        public long getSuccessfulTransactionCount() {
            return this.successfulTransactionCount;
        }

        /**
         * Gets the transactionCount.
         * <p>
         * @return Returns the transactionCount.
         */
        public long getTransactionCount() {
            return this.transactionCount;
        }

        /**
         * Sets the closeStatementCount.
         * <p>
         * @param aCloseStatementCount The closeStatementCount to set.
         */
        public void setCloseStatementCount(long aCloseStatementCount) {
            this.closeStatementCount = aCloseStatementCount;
        }

        /**
         * Sets the collectionFetchCount.
         * <p>
         * @param aCollectionFetchCount The collectionFetchCount to set.
         */
        public void setCollectionFetchCount(long aCollectionFetchCount) {
            this.collectionFetchCount = aCollectionFetchCount;
        }

        /**
         * Sets the collectionLoadCount.
         * <p>
         * @param aCollectionLoadCount The collectionLoadCount to set.
         */
        public void setCollectionLoadCount(long aCollectionLoadCount) {
            this.collectionLoadCount = aCollectionLoadCount;
        }

        /**
         * Sets the collectionRecreateCount.
         * <p>
         * @param aCollectionRecreateCount The collectionRecreateCount to set.
         */
        public void setCollectionRecreateCount(long aCollectionRecreateCount) {
            this.collectionRecreateCount = aCollectionRecreateCount;
        }

        /**
         * Sets the collectionRemoveCount.
         * <p>
         * @param aCollectionRemoveCount The collectionRemoveCount to set.
         */
        public void setCollectionRemoveCount(long aCollectionRemoveCount) {
            this.collectionRemoveCount = aCollectionRemoveCount;
        }

        /**
         * Sets the collectionUpdateCount.
         * <p>
         * @param aCollectionUpdateCount The collectionUpdateCount to set.
         */
        public void setCollectionUpdateCount(long aCollectionUpdateCount) {
            this.collectionUpdateCount = aCollectionUpdateCount;
        }

        /**
         * Sets the connectCount.
         * <p>
         * @param aConnectCount The connectCount to set.
         */
        public void setConnectCount(long aConnectCount) {
            this.connectCount = aConnectCount;
        }

        /**
         * Sets the entityDeleteCount.
         * <p>
         * @param aEntityDeleteCount The entityDeleteCount to set.
         */
        public void setEntityDeleteCount(long aEntityDeleteCount) {
            this.entityDeleteCount = aEntityDeleteCount;
        }

        /**
         * Sets the entityFetchCount.
         * <p>
         * @param aEntityFetchCount The entityFetchCount to set.
         */
        public void setEntityFetchCount(long aEntityFetchCount) {
            this.entityFetchCount = aEntityFetchCount;
        }

        /**
         * Sets the entityInsertCount.
         * <p>
         * @param aEntityInsertCount The entityInsertCount to set.
         */
        public void setEntityInsertCount(long aEntityInsertCount) {
            this.entityInsertCount = aEntityInsertCount;
        }

        /**
         * Sets the entityLoadCount.
         * <p>
         * @param aEntityLoadCount The entityLoadCount to set.
         */
        public void setEntityLoadCount(long aEntityLoadCount) {
            this.entityLoadCount = aEntityLoadCount;
        }

        /**
         * Sets the entityUpdateCount.
         * <p>
         * @param aEntityUpdateCount The entityUpdateCount to set.
         */
        public void setEntityUpdateCount(long aEntityUpdateCount) {
            this.entityUpdateCount = aEntityUpdateCount;
        }

        /**
         * Sets the flushCount.
         * <p>
         * @param aFlushCount The flushCount to set.
         */
        public void setFlushCount(long aFlushCount) {
            this.flushCount = aFlushCount;
        }

        /**
         * Sets the optimisticFailureCount.
         * <p>
         * @param aOptimisticFailureCount The optimisticFailureCount to set.
         */
        public void setOptimisticFailureCount(long aOptimisticFailureCount) {
            this.optimisticFailureCount = aOptimisticFailureCount;
        }

        /**
         * Sets the prepareStatementCount.
         * <p>
         * @param aPrepareStatementCount The prepareStatementCount to set.
         */
        public void setPrepareStatementCount(long aPrepareStatementCount) {
            this.prepareStatementCount = aPrepareStatementCount;
        }

        /**
         * Sets the queryCacheHitCount.
         * <p>
         * @param aQueryCacheHitCount The queryCacheHitCount to set.
         */
        public void setQueryCacheHitCount(long aQueryCacheHitCount) {
            this.queryCacheHitCount = aQueryCacheHitCount;
        }

        /**
         * Sets the queryCacheMissCount.
         * <p>
         * @param aQueryCacheMissCount The queryCacheMissCount to set.
         */
        public void setQueryCacheMissCount(long aQueryCacheMissCount) {
            this.queryCacheMissCount = aQueryCacheMissCount;
        }

        /**
         * Sets the queryCachePutCount.
         * <p>
         * @param aQueryCachePutCount The queryCachePutCount to set.
         */
        public void setQueryCachePutCount(long aQueryCachePutCount) {
            this.queryCachePutCount = aQueryCachePutCount;
        }

        /**
         * Sets the queryExecutionCount.
         * <p>
         * @param aQueryExecutionCount The queryExecutionCount to set.
         */
        public void setQueryExecutionCount(long aQueryExecutionCount) {
            this.queryExecutionCount = aQueryExecutionCount;
        }

        /**
         * Sets the queryExecutionMaxTime.
         * <p>
         * @param aQueryExecutionMaxTime The queryExecutionMaxTime to set.
         */
        public void setQueryExecutionMaxTime(long aQueryExecutionMaxTime) {
            this.queryExecutionMaxTime = aQueryExecutionMaxTime;
        }

        /**
         * Sets the secondLevelCacheHitCount.
         * <p>
         * @param aSecondLevelCacheHitCount The secondLevelCacheHitCount to set.
         */
        public void setSecondLevelCacheHitCount(long aSecondLevelCacheHitCount) {
            this.secondLevelCacheHitCount = aSecondLevelCacheHitCount;
        }

        /**
         * Sets the secondLevelCacheMissCount.
         * <p>
         * @param aSecondLevelCacheMissCount The secondLevelCacheMissCount to set.
         */
        public void setSecondLevelCacheMissCount(long aSecondLevelCacheMissCount) {
            this.secondLevelCacheMissCount = aSecondLevelCacheMissCount;
        }

        /**
         * Sets the secondLevelCachePutCount.
         * <p>
         * @param aSecondLevelCachePutCount The secondLevelCachePutCount to set.
         */
        public void setSecondLevelCachePutCount(long aSecondLevelCachePutCount) {
            this.secondLevelCachePutCount = aSecondLevelCachePutCount;
        }

        /**
         * Sets the sessionCloseCount.
         * <p>
         * @param aSessionCloseCount The sessionCloseCount to set.
         */
        public void setSessionCloseCount(long aSessionCloseCount) {
            this.sessionCloseCount = aSessionCloseCount;
        }

        /**
         * Sets the sessionOpenCount.
         * <p>
         * @param aSessionOpenCount The sessionOpenCount to set.
         */
        public void setSessionOpenCount(long aSessionOpenCount) {
            this.sessionOpenCount = aSessionOpenCount;
        }

        /**
         * Sets the successfulTransactionCount.
         * <p>
         * @param aSuccessfulTransactionCount The successfulTransactionCount to set.
         */
        public void setSuccessfulTransactionCount(long aSuccessfulTransactionCount) {
            this.successfulTransactionCount = aSuccessfulTransactionCount;
        }

        /**
         * Sets the transactionCount.
         * <p>
         * @param aTransactionCount The transactionCount to set.
         */
        public void setTransactionCount(long aTransactionCount) {
            this.transactionCount = aTransactionCount;
        }

    }

    /**
     * Class used to hold the property sheet descriptor info.
     */
    public static class BeanBeanInfo
        extends BaseBeanInfo {

        public BeanBeanInfo() {
            super(Bean.class);
            ExtendedPropertyDescriptor descriptor = null;

            descriptor = addProperty("queryExecutionCount");
            descriptor.setCategory(QUERY);
            descriptor.setDisplayName("Query Execution Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofqueriesexecuted"));

            descriptor = addProperty("queryExecutionMaxTime");
            descriptor.setCategory(QUERY);
            descriptor.setDisplayName("Query Execution Max Time");
            descriptor.setShortDescription(Resources.getString("label.Maximumamountoftime"));

            descriptor = addProperty("entityLoadCount");
            descriptor.setCategory(ENTITY);
            descriptor.setDisplayName("Entity Load Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofentitiesloaded"));

            descriptor = addProperty("entityInsertCount");
            descriptor.setCategory(ENTITY);
            descriptor.setDisplayName("Entity Insert Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofentitiesinserted"));

            descriptor = addProperty("entityUpdateCount");
            descriptor.setCategory(ENTITY);
            descriptor.setDisplayName("Entity Update Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofentitiesupdated"));

            descriptor = addProperty("entityDeleteCount");
            descriptor.setCategory(ENTITY);
            descriptor.setDisplayName("Entity Delete Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofentitiesdeleted"));

            descriptor = addProperty("entityFetchCount");
            descriptor.setCategory(ENTITY);
            descriptor.setDisplayName("Entity Fetch Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofentitiesfetched"));

            descriptor = addProperty("collectionFetchCount");
            descriptor.setCategory(COLLECTIONS);
            descriptor.setDisplayName("Collection Fetch Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofcollectionsfetched"));

            descriptor = addProperty("collectionLoadCount");
            descriptor.setCategory(COLLECTIONS);
            descriptor.setDisplayName("Collection Load Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofcollectionsloaded"));

            descriptor = addProperty("collectionRecreateCount");
            descriptor.setCategory(COLLECTIONS);
            descriptor.setDisplayName("Collection Recreate Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofcollectionsrecreated"));

            descriptor = addProperty("collectionRemoveCount");
            descriptor.setCategory(COLLECTIONS);
            descriptor.setDisplayName("Collection Removed Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofcollectionsremoved"));

            descriptor = addProperty("collectionUpdateCount");
            descriptor.setCategory(COLLECTIONS);
            descriptor.setDisplayName("Collection Update Count");
            descriptor.setShortDescription(Resources.getString("label.Numberofcollectionsupdated"));

            descriptor = addProperty("secondLevelCachePutCount");
            descriptor.setCategory(CACHE);
            descriptor.setDisplayName("Second Level Cache Puts");
            descriptor.setShortDescription(Resources.getString("label.Numberofcacheputs"));

            descriptor = addProperty("secondLevelCacheHitCount");
            descriptor.setCategory(CACHE);
            descriptor.setDisplayName("Second Level Cache Hits");
            descriptor.setShortDescription(Resources.getString("label.Numberofcachehits"));

            descriptor = addProperty("secondLevelCacheMissCount");
            descriptor.setCategory(CACHE);
            descriptor.setDisplayName("Second Level Cache Misses");
            descriptor.setShortDescription(Resources.getString("label.Numberofcachemisses"));

            descriptor = addProperty("queryCachePutCount");
            descriptor.setCategory(CACHE);
            descriptor.setDisplayName("Query Cache Puts");
            descriptor.setShortDescription(Resources.getString("label.Numberofquerycacheputs"));

            descriptor = addProperty("queryCacheHitCount");
            descriptor.setCategory(CACHE);
            descriptor.setDisplayName("Query Cache Hits");
            descriptor.setShortDescription(Resources.getString("label.Numberofquerycachehits"));

            descriptor = addProperty("queryCacheMissCount");
            descriptor.setCategory(CACHE);
            descriptor.setDisplayName("Query Cache Misses");
            descriptor.setShortDescription(Resources.getString("label.Numberofquerycachemisses"));

            descriptor = addProperty("sessionOpenCount");
            descriptor.setCategory(SESSION);
            descriptor.setDisplayName("Sessions Opened");
            descriptor.setShortDescription(Resources.getString("label.Numberofsessionsopened"));

            descriptor = addProperty("sessionCloseCount");
            descriptor.setCategory(SESSION);
            descriptor.setDisplayName("Sessions Closed");
            descriptor.setShortDescription(Resources.getString("label.Numberofsessionsclosed"));

            descriptor = addProperty("flushCount");
            descriptor.setCategory(SESSION);
            descriptor.setDisplayName("Flushes");
            descriptor.setShortDescription(Resources.getString("label.Numberofsessionsflushed"));

            descriptor = addProperty("connectCount");
            descriptor.setCategory(SESSION);
            descriptor.setDisplayName("Connections");
            descriptor.setShortDescription(Resources.getString("label.Numberofsessionsconnections"));

            descriptor = addProperty("prepareStatementCount");
            descriptor.setCategory(SESSION);
            descriptor.setDisplayName("Prepared Statements");
            descriptor.setShortDescription(Resources.getString("label.Numberofsessionpreparedstatements"));

            descriptor = addProperty("closeStatementCount");
            descriptor.setCategory(SESSION);
            descriptor.setDisplayName("Closed Statements");
            descriptor.setShortDescription(Resources.getString("label.Numberofsessionclosedstatements"));

            descriptor = addProperty("successfulTransactionCount");
            descriptor.setCategory(TRANSACTION);
            descriptor.setDisplayName("Successful Transactions");
            descriptor.setShortDescription(Resources.getString("label.Numberofsuccessfultransactions"));

            descriptor = addProperty("transactionCount");
            descriptor.setCategory(TRANSACTION);
            descriptor.setDisplayName("Transactions");
            descriptor.setShortDescription(Resources.getString("label.Numberoftransactions"));

            descriptor = addProperty("optimisticFailureCount");
            descriptor.setCategory(TRANSACTION);
            descriptor.setDisplayName("Optimistic Lock Failures");
            descriptor.setShortDescription(Resources.getString("label.Numberoffailedoptimisticlocks"));
        }
    }

}