package com.melloware.jukes.db;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;

import com.melloware.jukes.db.orm.AbstractJukesObject;
import com.melloware.jukes.exception.InfrastructureException;

/**
 * Static methods for access data using Hibernate.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ 2009
 */
public final class HibernateDao {
   private static final Log LOG = LogFactory.getLog(HibernateDao.class);

   /**
    * Private No Instantiation.
    */
   private HibernateDao() {
      super();
   }

   /**
    * Attaches a clean instance of an object to a session.
    * <p>
    * @param instance the instance to attach
    */
   public static void attachClean(final AbstractJukesObject instance) {
      LOG.debug("attaching clean instance");
      try {
         HibernateUtil.getSession().buildLockRequest(LockOptions.NONE).lock(instance);
         LOG.debug("attach successful");
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Attaches a dirty instance of an object to a session.
    * <p>
    * @param instance the instance to attach
    */
   public static void attachDirty(final AbstractJukesObject instance) {
      LOG.debug("attaching dirty instance");
      try {
         HibernateUtil.getSession().saveOrUpdate(instance);
         LOG.debug("attach successful");
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Gets a count using a HQL Query.
    * <p>
    * @param aHQLQuery the query to run
    * @return the count
    */
   public static long count(final String aHQLQuery) {
      LOG.debug("Count : " + aHQLQuery);
      try {
         // final int count = ((Integer)
         // HibernateUtil.getSession().createQuery("select count (*) from " +
         // aHQLQuery)
         // .uniqueResult()).intValue();
         // AZ change type from int to long
         final long count = ((Long) HibernateUtil.getSession().createQuery("select count (*) from " + aHQLQuery)
                  .uniqueResult()).longValue();
         return count;
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Gets a sum of a long type column
    * <p>
    * @param aClass the class to sum up
    * @param aLongField the field to sum up
    * @return the sum
    */
   public static long sum(final Class aClass, final String aLongField) {
      LOG.debug("Sum : " + aClass.getName());
      try {
         final long sum = ((Long) HibernateUtil.getSession().createQuery(
                  "select sum(" + aLongField + ") from " + aClass.getName()).uniqueResult()).longValue();
         return sum;
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Counts ALL of a certain domain class.
    * <p>
    * @param aClass the class to count
    * @return the count
    */
   public static long countAll(final Class aClass) {
      LOG.debug("Count All " + aClass.getName());
      try {
         final long count = ((Long) HibernateUtil.getSession().createQuery("select count(id) from " + aClass.getName())
                  .uniqueResult()).longValue();
         return count;
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Creates a criteria for the Class.
    * <p>
    * @param aClass the class to create a criteria for.
    * @return the Criteria object
    */
   public static Criteria createCriteria(final Class aClass) {
      return HibernateUtil.getSession().createCriteria(aClass).setCacheable(false);
   }

   /**
    * Delete a persistent object from the store.
    * <p>
    * @param persistentInstance the object to delete
    */
   public static void delete(final AbstractJukesObject persistentInstance) {
      LOG.debug("deleting instance");
      try {
         HibernateUtil.getSession().delete(persistentInstance);
         LOG.debug("delete successful");
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Deletes a whole collection from the store.
    * <p>
    * @param aCollection the collection to delete
    */
   public static void deleteAll(final Collection aCollection) {
      LOG.debug("deleting collection");
      if (aCollection == null) {
         return;
      }
      try {
         final Iterator iterator = aCollection.iterator();
         while (iterator.hasNext()) {
            final AbstractJukesObject element = (AbstractJukesObject) iterator.next();
            iterator.remove();
            HibernateDao.delete(element);
         }

         HibernateUtil.getSession().flush();
         LOG.debug("deleting collection successful");
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Find all of an object with a specified sort order.
    * <p>
    * @param aClass the class to find
    * @param aSortColumn the sort column to use
    * @return a List of all objects found
    */
   public static List findAll(final Class aClass, final String aSortColumn) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("geting ALL " + aClass.getName());
      }

      List items;
      try {
         final Criteria criteria = createCriteria(aClass);
         criteria.addOrder(Order.asc(aSortColumn));
         items = criteria.list();
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
      return items;
   }

   /**
    * Finds persistent objects from the store by Criteria provided.
    * <p>
    * @param criteria the criteria object to use
    * @return a List of all objects found
    */
   public static List findByCriteria(final Criteria criteria) {
      LOG.debug("finding instance by criteria");
      try {
         final List results = criteria.list();
         LOG.debug("find by criteria successful, result size: " + results.size());
         return results;
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Finds persistent objects from the store by Example provided.
    * <p>
    * @param instance the persistent object to use an as example
    * @return a List of all objects found
    */
   public static List findByExample(final AbstractJukesObject instance) {
      LOG.debug("finding instance by example");
      try {
         final Example example = Example.create(instance);
         final List results = HibernateUtil.getSession().createCriteria(instance.getClass()).add(example).list();
         LOG.debug("find by example successful, result size: " + results.size());
         return results;
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Find by primary key.
    * <p>
    * @param aClass the Class to look for
    * @param id the primary key
    * @return a persisten object if found by primary key
    */
   public static AbstractJukesObject findById(final Class aClass, final long id) {
      return findById(aClass, Long.valueOf(id));
   }

   /**
    * Find by primary key.
    * <p>
    * @param aClass the Class to look for
    * @param id the primary key
    * @return a persisten object if found by primary key
    */
   public static AbstractJukesObject findById(final Class aClass, final Long id) {
      LOG.debug("getting instance with id: " + id);
      try {
         final AbstractJukesObject instance = (AbstractJukesObject) HibernateUtil.getSession().get(aClass, id);
         if (instance == null) {
            LOG.debug("get successful, no instance found");
         } else {
            LOG.debug("get successful, instance found");
         }
         return instance;
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Find all of an object with a specified HQL Query.
    * <p>
    * @param aHQLQuery the query to execute
    * @return a List of all objects found
    */
   public static List findByQuery(final String aHQLQuery) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("findByQuery " + aHQLQuery);
      }

      List items;
      try {
         items = HibernateUtil.getSession().createQuery(aHQLQuery).list();
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
      return items;
   }

   /**
    * Find all of an object with a specified HQL Query.
    * <p>
    * @param aHQLQuery the query to execute
    * @param aMaxResults the maximum results to return
    * @return a List of all objects found
    */
   public static List findByQuery(final String aHQLQuery, final int aMaxResults) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("findByQuery " + aHQLQuery);
      }

      List items;
      try {
         final Query query = HibernateUtil.getSession().createQuery(aHQLQuery);
         query.setMaxResults(aMaxResults);
         items = query.list();
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
      return items;
   }

   /**
    * Finds a single object using the aHQLQuery.
    * <p>
    * @param aHQLQuery the query to execute
    * @return a List of all objects found
    */
   public static AbstractJukesObject findUniqueByQuery(final String aHQLQuery) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("findUniqueByQuery " + aHQLQuery);
      }

      AbstractJukesObject result = null;
      try {
         result = (AbstractJukesObject) HibernateUtil.getSession().createQuery(aHQLQuery).uniqueResult();
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
      return result;
   }

   /**
    * Finds a single persistent object from the store by Example provided.
    * <p>
    * @param instance the persistent object to use an as example
    * @return the object found if one
    */
   public static AbstractJukesObject findUniqueByExample(final AbstractJukesObject instance) {
      LOG.debug("finding unique instance by example");
      AbstractJukesObject result = null;
      try {
         final Example example = Example.create(instance);
         result = (AbstractJukesObject) HibernateUtil.getSession().createCriteria(instance.getClass()).add(example)
                  .uniqueResult();
         LOG.debug("find by unique example successful ");
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
      return result;
   }

   /**
    * Merges an instance of a persistable object into a session and returns the
    * merged object.
    * <p>
    * @param detachedInstance the instance to attach
    */
   public static AbstractJukesObject merge(final AbstractJukesObject detachedInstance) {
      LOG.debug("merging Album instance");
      try {
         final AbstractJukesObject result = (AbstractJukesObject) HibernateUtil.getSession().merge(detachedInstance);
         LOG.debug("merge successful");
         return result;
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Persist an object to the store.
    * <p>
    * @param transientInstance the object to persist
    */
   public static void persist(final AbstractJukesObject transientInstance) {
      LOG.debug("persisting instance");
      try {
         HibernateUtil.getSession().persist(transientInstance);
         LOG.debug("persist successful");
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Persist an object to the store.
    * <p>
    * @param transientInstance the object to persist
    */
   public static void saveOrUpdate(final AbstractJukesObject transientInstance) {
      LOG.debug("saveOrUpdate instance");
      try {
         HibernateUtil.getSession().saveOrUpdate(transientInstance);
         LOG.debug("saveOrUpdate successful");
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   /**
    * Refreshes an object from the database. If you think data may be stale and
    * need updating.
    * <p>
    * @param transientInstance the object to refresh
    */
   public static void refresh(final AbstractJukesObject transientInstance) {
      LOG.debug("refreshing instance");
      try {
         HibernateUtil.getSession().refresh(transientInstance);
         LOG.debug("refreshing successful");
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

   public static void refreshAll(final Collection aCollection) {
      LOG.debug("refreshing collection");
      if (aCollection == null) {
         return;
      }
      try {
         final Iterator iterator = aCollection.iterator();
         while (iterator.hasNext()) {
            final AbstractJukesObject element = (AbstractJukesObject) iterator.next();
            HibernateDao.refresh(element);
         }
         LOG.debug("refreshing collection successful");
      } catch (RuntimeException re) {
         LOG.error(re.getMessage(), re);
         throw new InfrastructureException(re);
      }
   }

}