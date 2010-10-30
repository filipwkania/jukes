package com.melloware.jukes.db;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.Version;//AZ
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.stat.Statistics;

import com.melloware.jukes.exception.InfrastructureException;

/**
 * Basic Hibernate helper class for Hibernate configuration and startup.
 * <p>
 * Uses a static initializer to read startup options and initialize
 * <tt>Configuration</tt> and <tt>SessionFactory</tt>.
 * <p>
 * This class also tries to figure out if JNDI binding of the
 * <tt>SessionFactory</tt> is used, otherwise it falls back to a global static
 * variable (Singleton). If you use this helper class to obtain a
 * <tt>SessionFactory</tt> in your code, you are shielded from these
 * deployment differences.
 * <p>
 * Another advantage of this class is access to the <tt>Configuration</tt>
 * object that was used to build the current <tt>SessionFactory</tt>. You can
 * access mapping metadata programmatically with this API, and even change it
 * and rebuild the <tt>SessionFactory</tt>.
 * <p>
 * If you want to assign a global interceptor, set its fully qualified class
 * name with the system (or hibernate.properties/hibernate.cfg.xml) property
 * <tt>hibernate.util.interceptor_class</tt>. It will be loaded and
 * instantiated on static initialization of HibernateUtil; it has to have a
 * no-argument constructor. You can call <tt>HibernateUtil.getInterceptor()</tt>
 * if you need to provide settings before using the interceptor.
 * <p>
 * Note: This class supports annotations by default, hence needs JDK 5.0 and the
 * Hibernate Annotations library on the classpath. Change the single commented
 * line in the source to make it compile and run on older JDKs with XML mapping
 * files only.
 * <p>
 * Note: This class supports only one data store. Support for several
 * <tt>SessionFactory</tt> instances can be easily added (through a static
 * <tt>Map</tt>, for example). You could then lookup a
 * <tt>SessionFactory</tt> by its name. Copyright (c) 1999-2007 Melloware,
 * Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @author christian@hibernate.org
 * AZ 2010
 */
@SuppressWarnings("unchecked")
public final class HibernateUtil {

   private static final Log LOG = LogFactory.getLog(HibernateUtil.class);

   private static final String HIBERNATE = "hibernate";
   private static final String INTERCEPTOR_CLASS = "hibernate.util.interceptor_class";
   private static final Map threadSession = new HashMap();
   private static final Map threadTransaction = new HashMap();
   private static final Map threadInterceptor = new HashMap();
   private static Configuration configuration;
   private static SessionFactory sessionFactory;
   private static String remoteUrl = null;
   private static boolean isHSQLDialect = false;

   /**
    * This flag is set to true to compact the database. This is slow and only
    * should be done after a bulk load or update like Disc Finder
    */
   private static boolean compact = false;

   /**
    * Default constructor. Private so no instantiation.
    */
   private HibernateUtil() {
      super();
   }

   /**
    * Returns the original Hibernate configuration.
    * @return Configuration
    */
   public static Configuration getConfiguration() {
      return configuration;
   }

   /**
    * Gets the remoteUrl.
    * <p>
    * @return Returns the remoteUrl.
    */
   public static String getRemoteUrl() {
      return remoteUrl;
   }

   /**
    * Retrieves the current Session local to the thread. <p/> If no Session is
    * open, opens a new Session for the running thread.
    * @return Session
    */
   @SuppressWarnings("unchecked")
   public static Session getSession() throws InfrastructureException {
      Session s = (Session) threadSession.get(HIBERNATE);
      try {
         if (s == null) {
            LOG.debug("Opening new Session for this thread." + Thread.currentThread().getName());
            if (getInterceptor() == null) {
               s = getSessionFactory().openSession();
            } else {
               LOG.debug("Using interceptor: " + getInterceptor().getClass());
               s = getSessionFactory().openSession(getInterceptor());

            }
            threadSession.put(HIBERNATE, s);
         }
      } catch (HibernateException ex) {
         throw new InfrastructureException(ex);
      }
      return s;
   }

   /**
    * Returns the SessionFactory used for this static class.
    * @return SessionFactory
    */
   public static SessionFactory getSessionFactory() {

      /*
       * Instead of a static variable, use JNDI: SessionFactory sessions = null;
       * try { Context ctx = new InitialContext(); String jndiName =
       * "java:hibernate/HibernateFactory"; sessions =
       * (SessionFactory)ctx.lookup(jndiName); } catch (NamingException ex) {
       * throw new InfrastructureException(ex); } return sessions;
       */
      return sessionFactory;
   }

   /**
    * Sets the compact.
    * <p>
    * @param aCompact The compact to set.
    */
   public static void setCompact(final boolean aCompact) {
      compact = aCompact;
   }

   /**
    * Sets the remoteUrl.
    * <p>
    * @param aRemoteUrl The remoteUrl to set.
    */
   public static void setRemoteUrl(final String aRemoteUrl) {
      remoteUrl = aRemoteUrl;
   }

   /**
    * Gets the compact.
    * <p>
    * @return Returns the compact.
    */
   public static boolean isCompact() {
      return compact;
   }

   /**
    * Gets the isHSQLDialect.
    * <p>
    * @return Returns the isHSQLDialect.
    */
   public static boolean isHSQLDialect() {
      return isHSQLDialect;
   }

   /**
    * Start a new database transaction.
    */
   @SuppressWarnings("unchecked")
   public static void beginTransaction() throws InfrastructureException {
      Transaction tx = (Transaction) threadTransaction.get(HIBERNATE);
      try {
         if (tx == null) {
            LOG.debug("Starting new database transaction in this thread.");
            tx = getSession().beginTransaction();
            threadTransaction.put(HIBERNATE, tx);
         }
      } catch (HibernateException ex) {
         throw new InfrastructureException(ex);
      }
   }

   /**
    * Closes the Session local to the thread.
    */
   @SuppressWarnings("unchecked")
   public static void closeSession() throws InfrastructureException {
      try {
         final Session s = (Session) threadSession.get(HIBERNATE);
         threadSession.put(HIBERNATE, null);
         if ((s != null) && s.isOpen()) {
            LOG.debug("Closing Session of this thread.");
            s.close();
         }
      } catch (HibernateException ex) {
         throw new InfrastructureException(ex);
      }
   }

   /**
    * Commit the database transaction.
    */
   @SuppressWarnings("unchecked")
   public static void commitTransaction() throws InfrastructureException {
      final Transaction tx = (Transaction) threadTransaction.get(HIBERNATE);
      try {
         if ((tx != null) && !tx.wasCommitted() && !tx.wasRolledBack()) {
            LOG.debug("Committing database transaction of this thread.");
            tx.commit();
         }
         threadTransaction.put(HIBERNATE, null);
      } catch (HibernateException ex) {
         rollbackTransaction();
         throw new InfrastructureException(ex);
      }
   }

   /**
    * Disconnect and return Session from current Thread.
    * @return Session the disconnected Session
    */
   public static Session disconnectSession() throws InfrastructureException {

      final Session session = getSession();
      try {
         threadSession.put(HIBERNATE, null);
         if (session.isConnected() && session.isOpen()) {
            session.disconnect();
         }
      } catch (HibernateException ex) {
         throw new InfrastructureException(ex);
      }
      return session;
   }

   /**
    * Static initializer to startup Hibernate.
    */
   public static void initialize() {
      if (configuration != null) {
         return;
      }

      // Create the initial SessionFactory from the default configuration files
      try {
         LOG.info("Hibernate Initialization");
         // Replace with Configuration() if you don't use annotations or JDK 5.0
         // configuration = new
         // AnnotationConfiguration();

         // This custom entity resolver supports entity placeholders in XML
         // mapping files
         // and tries to resolve them on the classpath as a resource
         // configuration.setEntityResolver(new
         // ImportFromClasspathEntityResolver());

         // Read not only hibernate.properties, but also hibernate.cfg.xml

         configuration = new Configuration();

         configuration.configure();
         final Properties cfg = configuration.getProperties();

         // use the remoteURL if it is set
         if (remoteUrl == null) {
            configuration.getProperties().put("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
         } else {
            LOG.info("Remote URL Connection: " + remoteUrl);
            configuration.setProperty("hibernate.connection.url", remoteUrl);
            configuration.getProperties().remove("hbm2ddl.auto");
            configuration.getProperties().remove("hibernate.hbm2ddl.auto");
         }

         final Enumeration names = cfg.propertyNames();
         // add all hibernate props to system props
         while (names.hasMoreElements()) {
            final String name = (String) names.nextElement();
            final String value = cfg.getProperty(name);
            System.getProperties().put(name, value);
         } 
         System.getProperties().put("hibernate.version", Version.getVersionString());//AZ: for Hibernate 3.5.3 
         //System.getProperties().put("hibernate.version", Environment.VERSION);

         // Assign a global, user-defined interceptor with no-arg constructor
         final String interceptorName = configuration.getProperty(INTERCEPTOR_CLASS);
         if (interceptorName != null) {
            LOG.info("Initializing Interceptor: " + interceptorName);
            final Class interceptorClass = HibernateUtil.class.getClassLoader().loadClass(interceptorName);
            final Interceptor interceptor = (Interceptor) interceptorClass.newInstance();
            configuration.setInterceptor(interceptor);
         }

         if (configuration.getProperty(Environment.SESSION_FACTORY_NAME) == null) {

            // or use static variable handling
            sessionFactory = configuration.buildSessionFactory();
         } else {
            // Let Hibernate bind the factory to JNDI
            configuration.buildSessionFactory();
         }

         // enable statistics
         final Statistics stats = sessionFactory.getStatistics();
         stats.setStatisticsEnabled(true);
         LOG.info("Hibernate " + Version.getVersionString());//AZ

         // Special handling for HSQLDB, which requires an explicit shutdown
         // since version 1.7.x or so
         final String dialectName = configuration.buildSettings().getDialect().toString();
         isHSQLDialect = "org.hibernate.dialect.HSQLDialect".equals(dialectName);
      } catch (Throwable ex) {
         // We have to catch Throwable, otherwise we will miss
         // NoClassDefFoundError and other subclasses of Error
         LOG.error("Building SessionFactory failed.", ex);
         throw new ExceptionInInitializerError(ex);
      }
   }

   /**
    * Rebuild the SessionFactory with the static Configuration.
    */
   public static void rebuildSessionFactory() throws InfrastructureException {
      synchronized (sessionFactory) {
         LOG.info("Rebuilding session factory.");
         try {
            sessionFactory = getConfiguration().buildSessionFactory();
         } catch (Exception ex) {
            throw new InfrastructureException(ex);
         }
      }
   }

   /**
    * Rebuild the SessionFactory with the given Hibernate Configuration.
    * @param cfg
    */
   public static void rebuildSessionFactory(final Configuration cfg) throws InfrastructureException {
      synchronized (sessionFactory) {
         try {
            sessionFactory = cfg.buildSessionFactory();
            configuration = cfg;
         } catch (Exception ex) {
            throw new InfrastructureException(ex);
         }
      }
   }

   /**
    * Register a Hibernate interceptor with the current thread.
    * <p>
    * Every Session opened is opened with this interceptor after registration.
    * Has no effect if the current Session of the thread is already open,
    * effective on next close()/getSession().
    */
   public static void registerInterceptor(final Interceptor interceptor) {
      threadInterceptor.put(HIBERNATE, interceptor);
   }

   /**
    * Commit the database transaction.
    */
   public static void rollbackTransaction() throws InfrastructureException {
      final Transaction tx = (Transaction) threadTransaction.get(HIBERNATE);
      try {
         threadTransaction.put(HIBERNATE, null);
         if ((tx != null) && !tx.wasCommitted() && !tx.wasRolledBack()) {
            LOG.debug("Trying to rollback database transaction of this thread.");
            tx.rollback();
         }
      } catch (HibernateException ex) {
         throw new InfrastructureException(ex);
      } finally {
         closeSession();
      }
   }

   /**
    * Closes the current SessionFactory and releases all resources.
    * <p>
    * The only other method that can be called on HibernateUtil after this one
    * is rebuildSessionFactory(Configuration).
    */
   @SuppressWarnings("deprecation")
   public static void shutdown() {
      LOG.info("Shutting down Hibernate.");

      // if sessionFactory is null someone shut us down already
      if (sessionFactory == null) {
         return;
      }

      // special HSQLDB handling of proper shutdown, do not shut down remote
      if ((isHSQLDialect) && (StringUtils.isBlank(getRemoteUrl()))) {
         Statement stmt = null;
         try {
            String command = "SHUTDOWN";
            if (isCompact()) {
               command = "SHUTDOWN COMPACT";
            }
            final Session session = getSession();
            LOG.info("HSQLDB: " + command);
            stmt = session.connection().createStatement();
            stmt.execute(command);
         } catch (Throwable ex) {
            LOG.error("Could not compact database", ex);
         } finally {
            if (stmt != null) {
               try {
                  stmt.close();
               } catch (SQLException ex) {
                  LOG.error("SQLException", ex);
                  throw new InfrastructureException(ex);
               }
            }
         }
      }

      // clear any thread safe variables
      threadInterceptor.put(HIBERNATE, null);
      rollbackTransaction();

      // close the open sessions
      closeSession();

      // Close caches and connection pools
      getSessionFactory().close();

      // Clear static variables
      configuration = null;
      sessionFactory = null;
   }

   /**
    * Gets the inteceptor on the current thread.
    * <p>
    * @return the Interceptor if there is one in the current thread
    */
   private static Interceptor getInterceptor() {
      return (Interceptor) threadInterceptor.get(HIBERNATE);
   }

}