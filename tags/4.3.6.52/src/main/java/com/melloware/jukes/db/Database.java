package com.melloware.jukes.db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Server;
import org.hsqldb.server.ServerConstants;

import com.melloware.jukes.exception.InfrastructureException;

/**
 * Static class used to start and stop the HSQLDB Server which will be run in
 * process in the same JVM but can be connected to externally by other
 * applications.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * @see org.hsqldb.Server AZ 2010
 */
public final class Database {

   private static final Log LOG = LogFactory.getLog(Database.class);
   private static Server server = null;
   private static String jdbcUrl = null;

   /**
    * Private constructor to prevent instantiation.
    */
   private Database() {
      super();
   }

   /**
    * Gets the connection URL for this database.
    * <p>
    * @return the string connection URL for JDBC drivers
    */
   public static String getJdbcURL() {
      if (LOG.isInfoEnabled()) {
         LOG.info("JDBC URL: " + jdbcUrl);
      }
      return jdbcUrl;
   }

   /**
    * But HSQLDB has a hidden catch; the write delay. By default, HSQLDB has a
    * write delay for all activity of 60 seconds. I'd forgotten this myself when
    * developing the example for this article and was running short lived tests
    * and was surprised to find empty databases on disk. You can of course turn
    * off the write delay feature completely, or tune it to something more
    * appropriate to your application's life cycle.
    * <p>
    * @param connection the JDBC connection to use
    * @param writeDelay the write delay either TRUE, FALSE, or a number in
    *           seconds
    */
   public static void setWriteDelay(final Connection connection, final String writeDelay) {
      Statement stmt = null;
      try {
         stmt = connection.createStatement();
         final String sql = "SET WRITE_DELAY " + writeDelay + ";";
         LOG.debug(sql);
         stmt.execute(sql);
      } catch (SQLException ex) {
         LOG.error(ex.getMessage(), ex);
      } finally {
         if (stmt != null) {
            try {
               stmt.close();
            } catch (SQLException ex) {
               LOG.error(ex.getMessage(), ex);
               throw new InfrastructureException(ex);
            }
         }
      }

   }

   /**
    * Back up this database to a zipped file at location aLocation.
    * <p>
    * @param aConnection the connection to use
    * @param aZipLocation the location to back up to
    */
   public static void backupDatabase(final Connection aConnection, final String aZipLocation) {
      if (LOG.isInfoEnabled()) {
         LOG.info("Backing up database to " + aZipLocation);
      }

      // first call CHECKPOINT to get the database in a proper state for
      // archiving
      executeSQL(aConnection, "CHECKPOINT DEFRAG");
      // AZ: create archive file name
      final String aTarLocation = aZipLocation + ".tar.gz";
      final File tarFile = new File(aTarLocation);

      try {
         // create the directory structure if it does not exist
         final String path = FilenameUtils.getFullPathNoEndSeparator(aZipLocation);
         FileUtils.forceMkdir(new File(path));
         // AZ: delete tar-file if exists
         if (tarFile.exists()) {
            FileUtils.forceDelete(tarFile);
         }
         // AZ: create TAR.GZ database archive including jukes.data,
         // jukes.properties, jukes.script
         executeSQL(aConnection, "BACKUP DATABASE TO '" + aTarLocation + "' COMPRESSED BLOCKING");
      } catch (Exception ex) {
         throw new InfrastructureException(ex);
      }

      /**
       * AZ: old archiving procedure. not used for HSQLDB 2.0 // now zip the
       * file up to the location specified final String location =
       * databaseLocation; final String[] filesToZip = new String[4];
       * filesToZip[0] = location + ".backup"; filesToZip[1] = location +
       * ".properties"; filesToZip[2] = location + ".script"; // dummy zero
       * length file just as placeholder for restoring filesToZip[3] =
       * databaseAlias + ".data"; final byte[] buffer = new byte[18024]; try {
       * final File zipFile = new File(aZipLocation); // create the directory
       * structure if it does not exist if (!zipFile.exists()) { final String
       * path = FilenameUtils.getFullPathNoEndSeparator(aZipLocation);
       * FileUtils.forceMkdir(new File(path)); } final ZipOutputStream out = new
       * ZipOutputStream(new FileOutputStream(zipFile)); // set the compression
       * ratio out.setLevel(Deflater.BEST_COMPRESSION); // iterate through the
       * array adding each file to the zip for (int i = 0; i <
       * filesToZip.length; i++) { final String filename = filesToZip[i];
       * LOG.info("Zipping " + filename); // add to the zip out.putNextEntry(new
       * ZipEntry(FilenameUtils.getName(filename))); File file = new
       * File(filename); FileInputStream in = null; if (file.exists()) { in =
       * new FileInputStream(file); // transfer bytes from the current file to
       * the zip int len; while ((len = in.read(buffer)) > 0) {
       * out.write(buffer, 0, len); } // close the current file input stream
       * in.close(); } // close the current entry out.closeEntry(); } // close
       * the ZipOutputStream out.close(); } catch (Exception ex) { throw new
       * InfrastructureException(ex); }
       **/

   }

   /**
    * Executes a SQL string against the connection.
    * <p>
    * @param connection the connection to use
    * @param sql the sql string to execute
    */
   public static void executeSQL(final Connection connection, final String sql) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Executing SQL: " + sql);
      }

      Statement stmt = null;
      try {
         stmt = connection.createStatement();
         LOG.debug(sql);
         stmt.execute(sql);
      } catch (SQLException ex) {
         LOG.error(ex.getMessage(), ex);
         throw new InfrastructureException(ex);
      } finally {
         if (stmt != null) {
            try {
               stmt.close();
            } catch (SQLException ex) {
               LOG.error(ex.getMessage(), ex);
               throw new InfrastructureException(ex);
            }
         }
      }
   }

   /**
    * Shuts the database down.
    */
   public static void shutdown() {
      shutdownHSQLDB();
   }

   /**
    * Starts the database up.
    */
   public static void startup(final String location, final String alias) {
      startupHSQLDB(location, alias);
   }

   /**
    * Shuts down the HSQLDB that is running in process.
    */
   private static void shutdownHSQLDB() {
      try {
         if (server != null) {
            LOG.info("Shutting down HSQLDB Server.");
            server.stop();
            while (server.getState() != ServerConstants.SERVER_STATE_SHUTDOWN) {
               try {
                  LOG.debug("Sleeping waiting for server to stop");
                  Thread.sleep(100);
               } catch (InterruptedException e) {
                  LOG.debug("Interrupted");
               }
            }
            server = null;
         }

      } catch (RuntimeException ex) {
         LOG.error(ex.getMessage(), ex);
         throw new InfrastructureException(ex);
      }
   }

   /**
    * Starts the HSQLDB server in process at the location specified.
    * <p>
    * @param location the location to create the new database on disk
    * @param alias the alias to give the DB
    */
   private static void startupHSQLDB(final String location, final String alias) {
      try {
         LOG.info("Starting up HSQLDB Server.");
         jdbcUrl = "jdbc:hsqldb:hsql://localhost/" + alias;
         server = new Server();

         if (LOG.isInfoEnabled()) {
            LOG.info("Location: " + location);
            LOG.info(server.getProductName() + " " + server.getProductVersion());
         }

         server.setDatabaseName(0, alias);
         server.setDatabasePath(0, "file:" + location + ";runtime.gc_interval=10000;hsqldb.default_table_type=cached;");
         server.setLogWriter(null);
         server.setErrWriter(null);
         server.setSilent(true);
         server.setTrace(false);
         server.start();

         System.getProperties().put("db.product", server.getProductName());
         System.getProperties().put("db.version", server.getProductVersion());
         System.getProperties().put("db.address", server.getAddress());

      } catch (RuntimeException ex) {
         LOG.error(ex.getMessage(), ex);
         throw new InfrastructureException(ex);
      }
   }

}