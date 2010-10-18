package com.melloware.jukes.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemTray;
import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.LogManager;

import javax.sound.sampled.spi.FormatConversionProvider;
import javax.swing.UIManager;
import javax.swing.plaf.DimensionUIResource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;

import com.jgoodies.looks.Options;
import com.jgoodies.uif.AbstractFrame;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.application.ApplicationConfiguration;
import com.jgoodies.uif.application.ApplicationDescription;
import com.jgoodies.uif.application.ResourceIDs;
import com.jgoodies.uif.osx.OSXApplicationMenu;
import com.jgoodies.uif.splash.ImageSplash;
import com.jgoodies.uif.splash.Splash;
import com.jgoodies.uif.splash.SplashProvider;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uifextras.convenience.DefaultApplicationStarter;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeException;
import com.melloware.jukes.db.Database;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.MainController;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.tool.logging.AwtLogHandler;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.tray.ITrayIcon;
import com.melloware.jukes.tray.JukesTrayIcon;
import com.melloware.jukes.util.MessageUtil;
import com.melloware.jukes.util.NoFlickerSplashWrapper;
import com.sun.media.sound.JDK13Services;

/**
 * This is the main class of the Jukes application. It utilizes the default
 * application startup process from the JGoodies UI framework.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * 2009, 2010 AZ Development
 * <p>
 * @see Actions
 * @see MainController
 * @see MainModule
 * @see MainFrame
 */
public final class Jukes extends DefaultApplicationStarter {

   private static final Log LOG = LogFactory.getLog(Jukes.class);
   private static ApplicationDescription description;
   private static ApplicationConfiguration configuration;
   private static MainModule mainModule;

   /**
    * Defines a bunch of application wide constants, and launches the boot
    * process for the Jukes application.
    */
   public static void main(String[] arguments) {
      LOG.info("Application Started...");
      try {
         // assigns a custom handler for catching uncaught exceptions
         System.setProperty("sun.awt.exception.handler", AwtLogHandler.class.getName());

         // set the resource bundle path now, so that we can internationalize
         // some parameters
         ResourceUtils.setBundlePath("Resource");
         
         //AZ: seek for JIntellitype.dll file in the Application directory
         final File aFile = new File("JIntellitype.dll");
         if (!aFile.exists()) {
        	 final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
        	 final String errorMessage = ResourceUtils.getString("messages.JIntellitypeNotExists");
             LOG.error(errorMessage);
             MessageUtil.showError(mainFrame, errorMessage);
             System.exit(1);
         }

         // verify that the user has the correct version of the JRE
         verifyJREVersion();

         // load the setup information for the application
         getConfiguration();
         getDescription();

         if (SystemUtils.IS_OS_WINDOWS) {
            fixWindowsTimingBug();

            if (JIntellitype.checkInstanceAlreadyRunning(Resources.APPLICATION_NAME)) {
               System.exit(0);
            }
         }

         final Jukes launcher = new Jukes();
         launcher.boot(description, configuration);
      } catch (Throwable ex) {
    	 final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
    	 final String errorMessage = ResourceUtils.getString("messages.ExceptionStarting" + "\n\n" + ex);
         LOG.error(errorMessage, ex);
         MessageUtil.showError(mainFrame, errorMessage); //AZ 
         System.exit(1);
      }

   }

   /*
    * (non-Javadoc)
    * @see com.jgoodies.swing.convenience.DefaultApplicationStarter#getDefaultLogFilePattern()
    */
   protected String getDefaultLogFilePattern() {
      //return "%h/.jukes/gui.log";
	  return "./log/gui.log";//AZ redirect LOG-file to /log/ sub-directory
   }

   /**
    * Configures the splash to set a brown progress bar.
    */
   protected void configureSplash() {
      super.configureSplash();
      // Create image splash
      final Image image = ResourceUtils.getIcon(ResourceIDs.SPLASH_IMAGE).getImage();
      final ImageSplash splash = new ImageSplash(image, true);

      // Wrap with de-flicker "filter"
      final SplashProvider splashWrapper = new NoFlickerSplashWrapper(splash);
      Splash.setProvider(splashWrapper);
      splash.setNoteEnabled(true);
      splash.setAlwaysOnTop(true);
      splash.setProgressBarBounds(60);
      splash.setTextColor(Color.WHITE);
      splash.setForeground(Color.YELLOW);
      splash.setBackground(Color.WHITE);
   }

   /**
    * Configures the user interface.
    */
   protected void configureUI() {
      Options.setUseSystemFonts(true);
      Options.setDefaultIconSize(new Dimension(16, 16));
      Options.setPopupDropShadowEnabled(true);
      UIManager.put("ToolBar.separatorSize", new DimensionUIResource(6, 18));
      UIManager.put("FileChooser.useSystemIcons", Boolean.TRUE);
      LOG.info("Adding Look and Feel [net.beeger.squareness.SquarenessLookAndFeel]");
      UIManager.installLookAndFeel("Squareness", "net.beeger.squareness.SquarenessLookAndFeel");
      LOG.info("Adding Look and Feel [net.sourceforge.napkinlaf.NapkinLookAndFeel]");
      UIManager.installLookAndFeel("Napkin", "net.sourceforge.napkinlaf.NapkinLookAndFeel");
      LOG.info("Adding Look and Feel [com.nilo.plaf.nimrod.NimRODLookAndFeel]");
      UIManager.installLookAndFeel("NimROD", "com.nilo.plaf.nimrod.NimRODLookAndFeel");
      LOG.info("Adding Look and Feel [org.jvnet.substance.SubstanceLookAndFeel]");
      UIManager.installLookAndFeel("Substance", "org.jvnet.substance.SubstanceLookAndFeel");
      LOG.info("Adding Look and Feel [com.lipstikLF.LipstikLookAndFeel]");
      UIManager.installLookAndFeel("Lipstik", "com.lipstikLF.LipstikLookAndFeel");
      super.configureUI();
   }

   /*
    * (non-Javadoc)
    * @see com.jgoodies.uifextras.convenience.DefaultApplicationStarter#createMainFrame()
    */
   @Override
   protected AbstractFrame createMainFrame() {
      final MainFrame mainFrame = new MainFrame(mainModule);
      Application.setDefaultParentFrame(mainFrame);
      return mainFrame;
   }

   /**
    * Initializes the actions used in this application.
    */
   @Override
   protected void initializeActions() {
      LOG.debug("Initialize Actions");
      OSXApplicationMenu.register(ActionManager.get(Actions.HELP_ABOUT_DIALOG_ID), ActionManager
               .get(Actions.PREFERENCES_ID), ActionManager.get(Actions.EXIT_ID));
      OSXApplicationMenu.setAboutName(Resources.APPLICATION_NAME);

   }

   /**
    * Load all the SPI sound codecs and log them.
    */
   protected void initializeCodecs() {
      LOG.info("Loading Codecs");
      final List codecs = JDK13Services.getProviders(FormatConversionProvider.class);
      for (Iterator iter = codecs.iterator(); iter.hasNext();) {
         FormatConversionProvider codec = (FormatConversionProvider) iter.next();
         LOG.info("Sound Codec: " + codec.toString());
      }
   }

   /**
    * Brings up the application, it therefore initializes the main frame, checks
    * the setup process, initializes all actions, then builds the main frame,
    * and finally opens it.
    */
   protected void launchApplication() {
      // Create the module that provides all high-level models.
      Splash.setNote("Creating Models...", 20);
      LOG.info("Creating Models...");
      mainModule = new MainModule();

      // initialize logging
      Splash.setNote("Init Logging...", 30);
      LOG.info("Init Logging...");
      initializeLogging();

      // Now add a more sophisticated handler.
      addMessageHandler();

      // initialize language from prefs
      ResourceUtils.setBundle(ResourceBundle.getBundle("Resource", new Locale(MainModule.SETTINGS.getLocale()),
               Jukes.class.getClassLoader()));

      // initialize database and hibernate
      Splash.setNote("Init DB...", 35);
      LOG.info("Init DB...");
      initializeDatabase();

      // Create the controller that provides the major operations.
      Splash.setNote("Controller...", 40);
      LOG.info("Creating Controller...");
      final MainController mainController = new MainController(mainModule);

      // initialize sound codecs
      Splash.setNote("Sound Codecs...", 45);
      initializeCodecs();

      // Initialize all Actions
      Splash.setNote("Init Actions...", 50);
      LOG.info("Init Actions...");
      Actions.initializeFor(mainModule, mainController);
      initializeActions();

      // Create and build the main frame.
      Splash.setNote("Create Mainframe...", 55);
      LOG.info("Create Mainframe...");
      final AbstractFrame mainFrame = createMainFrame();

      checkSetup();

      // initialize tray icon if on Windows
      Splash.setNote("Init Tray...", 60);
      LOG.info("Init Tray...");
      intializeTrayIcon((MainFrame) mainFrame);

      // initialize any browsers on the system
      Splash.setNote("Init Jintellitype...", 70);
      LOG.info("Init Jintellitype...");
      intializeJIntellitype((MainFrame) mainFrame);

      // add the shutdown listener
      shutdownHook();

      Splash.setNote("Building UI...", 80);
      LOG.info("Building UI...");
      mainFrame.build();

      Splash.setNote("Finishing...", 90);
      LOG.info("Finishing...");
      mainFrame.open();
      mainController.checkForOpenTipOfTheDayDialog();
      LOG.info("Application Finished Loading.");
   }

   /**
    * Return JGoodies UIF ApplicationConfiguration for specified prefix. The
    * prefix is used as a subdirectory under the .bb directory to have separate
    * persistent areas, and is also used as a prefix node in the Preferences
    * (register) to keep those separate as well.
    * @return application configuration object.
    */
   private static synchronized ApplicationConfiguration getConfiguration() {
      if (configuration == null) {
         configuration = new ApplicationConfiguration(Resources.APPLICATION_LOCATION, 
                  "", // resource properties URL
                  Resources.getString(Resources.HELP_GLOBAL_HELPSET), //"docs/help/global/Help.hs",  helpset URL
                  Resources.getString(Resources.HELP_TIPS_INDEX)); //"docs/help/tips/index.txt" Tips index path
      }

      return configuration;
   }

   /**
    * Creates application description object.
    * @return application description.
    */
   private static synchronized ApplicationDescription getDescription() {
      // get the version from the manifest file
      final String buildFullLabel = getProjectVersion();

      // trim off the build number
      final String buildLabel = StringUtils.substringBeforeLast(buildFullLabel, ".");
      if (description == null) {
         LOG.info("Jukes Version: " + buildFullLabel);
         description = new ApplicationDescription(Resources.APPLICATION_NAME, 
                  Resources.APPLICATION_NAME, // Application long name
                  buildLabel, // Version
                  buildFullLabel, // Full version
                  Resources.APPLICATION_DESCRIPTION, // Description
                  Resources.APPLICATION_COPYRIGHT, // Copyright
                  Resources.APPLICATION_VENDOR, // Vendor
                  Resources.APPLICATION_URL, // Vendor URL
                  Resources.APPLICATION_EMAIL); // Vendor email
      }

      return description;
   }

   /**
    * Attempts to read the version number out of the pom.properties. If not
    * found then RUNNING.IN.IDE.FULL is returned as the version.
    * <p>
    * @return the full version number of this application
    */
   private static String getProjectVersion() {
      String version;

      try {
         final Properties pomProperties = new Properties();
         pomProperties.load(Jukes.class.getResourceAsStream("/META-INF/maven/com.melloware/jukes/pom.properties"));
         version = pomProperties.getProperty("version");
      } catch (Exception e) {
         version = "RUNNING.IN.IDE.FULL";
      }
      return version;
   }

   /**
    * Listens for application shutdown and cleans up any resources and properly
    * compacts the HSQLDB to prevent restart problems with a .lck file being
    * leftover. Also is a good citizen by properly cleaning up Windows DLL's
    * used.
    */
   private static void shutdownHook() {
      // Set shutdown hook for HSQLDB and DLLS
      Runtime.getRuntime().addShutdownHook(new Thread() {
         public void run() {
            LOG.info("Shutdown hook.");
            if (JIntellitype.isJIntellitypeSupported()) {
               LOG.info("Cleaning up JIntellitype");
               JIntellitype.getInstance().cleanUp();
            }

            // shut down database and hibernate
            try {
               HibernateUtil.shutdown();
            } catch (RuntimeException ex) {
          	   final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
        	   final String errorMessage = ResourceUtils.getString("messages.ErrorShuttingDown");
               LOG.error(errorMessage, ex);
               MessageUtil.showError(mainFrame, errorMessage); //AZ 
            }
         }
      });
   }

   /**
    * If the Java version installed is not AT LEAST the correct version then
    * throw error and terminate. Using common-lang SystemUtils.
    */
   private static void verifyJREVersion() {
      final float requiredVersion = 1.60f;

      LOG.info("Checking Java version...");
      LOG.info("Current Java version: " + SystemUtils.JAVA_VERSION);

      if (!SystemUtils.isJavaVersionAtLeast(requiredVersion)) {
         LOG.info("Java version is not sufficient to run this application!");
         LOG.info("Current Java version: " + SystemUtils.JAVA_VERSION);
         LOG.info("REQUIRED Java version: " + requiredVersion);

         final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
         final String errorMsg = ResourceUtils.getString("application.java.version.error.text");
         final String displayMsg = MessageFormat.format(errorMsg, new Object[] { SystemUtils.JAVA_VERSION,
                  Float.toString(requiredVersion) });
         LOG.error(displayMsg);
         MessageUtil.showError(mainFrame, displayMsg); //AZ 
         System.exit(1);
      }
   }

   /**
    * According to Sun Bug 6435126 found here:
    * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6435126 On some windows
    * machines the clocks speeds up like crazy while using the Jukes and
    * multiple timers.
    */
   private static void fixWindowsTimingBug() {
      LOG.info("Fixing Windows Timing Bug...");
      new Thread() {
         {
            this.setDaemon(true);
            this.start();
         }

         public void run() {
            while (true) {
               try {
                  Thread.sleep(Integer.MAX_VALUE);
               } catch (InterruptedException ex) {
               }
            }
         }
      };
   }

   /**
    * Initalize HSQLDB and Hibernate. If any problems exit the application.
    */
   @SuppressWarnings("deprecation")
   private void initializeDatabase() {
      LOG.info("Initializing Database.");
      final String remoteURL = MainModule.SETTINGS.getRemoteDatabaseURL();

      try {
         String dbLocation = MainModule.SETTINGS.getDatabaseLocation().getAbsolutePath();
         dbLocation = dbLocation + SystemUtils.FILE_SEPARATOR + Resources.APPLICATION_LOCATION;
         // start database
         Database.startup(dbLocation, Resources.APPLICATION_LOCATION);

         // if there is a remote URL then we need to set it in the Hibernate
         // config
         if ((StringUtils.isNotBlank(remoteURL)) && (!Settings.DEFAULT_REMOTE_DATABASE_URL.equals(remoteURL))) {
            HibernateUtil.setRemoteUrl(remoteURL);
         }
         // initialize Hibernate
         HibernateUtil.initialize();

         // set the write delay on the HSQL database so writes are immediate
         if (HibernateUtil.isHSQLDialect()) {
            // initializes Hibernate
            HibernateUtil.getSession();
            Database.setWriteDelay(HibernateUtil.getSession().connection(), "FALSE");
         }
      } catch (JDBCConnectionException ex) {
    	  final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
    	  final String errorMessage = remoteURL + " " + ResourceUtils.getString("messages.ErrorConnection"); 
          MessageUtil.showError(mainFrame, errorMessage); //AZ 
          LOG.error(errorMessage);
         System.exit(1);
      } catch (InfrastructureException ex) {
    	 final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
         LOG.error("InfrastructureException", ex);
         MessageUtil.showError(mainFrame, "InfrastructureException"); //AZ 
         System.exit(1);
      } catch (HibernateException ex) {
    	 final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
         LOG.error("HibernateException", ex);
         MessageUtil.showError(mainFrame, "HibernateException"); //AZ 
         System.exit(1);
      } catch (Exception ex) {
    	 final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
         LOG.error("Exception", ex);
         MessageUtil.showError(mainFrame, "Exception"); //AZ 
         System.exit(1);
      }
   }

   /**
    * Initalize java.util.logging for any libraries used in the application that
    * do not use commons-logging.
    */
   private void initializeLogging() {
      try {
         super.configureLogging();

         // configure java.util.logging
         LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("java.logging.properties"));

         // set log4j level from prefs
         Logger.getRootLogger().setLevel(Level.toLevel(MainModule.SETTINGS.getLogLevel()));
         Logger.getLogger("com.melloware").setLevel(Level.toLevel(MainModule.SETTINGS.getLogLevel()));
         Logger.getLogger("com.melloware.jukes.gui").setLevel(Level.toLevel(MainModule.SETTINGS.getLogLevel()));
      } catch (Exception ex) {
         LOG.warn("Error configuring logging.", ex);
      }
   }

   /**
    * Initializes the JIntellitype library if on Windows. Else do nothing.
    * <p>
    * @param mainFrame the MainFrame of the application to store the
    *           JIntellitype ref
    */
   private void intializeJIntellitype(MainFrame mainFrame) {
      if (JIntellitype.isJIntellitypeSupported()) {
         try {
            mainFrame.setJintellitype(JIntellitype.getInstance());
         } catch (JIntellitypeException ex) {
            LOG.error("JIntellitypeException", ex);
            MessageUtil.showError(mainFrame, "JIntellitypeException"); //AZ 
         }
      }
   }

   /**
    * Initializes the tray icon if on Windows. Else do nothing.
    * <p>
    * @param mainFrame the MainFrame of the application to store the TrayIcon
    *           ref
    */
   private void intializeTrayIcon(MainFrame mainFrame) {
      if (SystemTray.isSupported()) {
         try {
            final ITrayIcon trayIcon = new JukesTrayIcon(mainFrame);
            mainFrame.setTrayIcon(trayIcon);
         } catch (AWTException ex) {
            LOG.error("AWTException", ex);
            MessageUtil.showError(mainFrame, "AWTException"); //AZ 
         }
      }
   }

}