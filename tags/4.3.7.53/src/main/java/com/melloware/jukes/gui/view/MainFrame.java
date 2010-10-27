package com.melloware.jukes.gui.view;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.prefs.Preferences;

import javax.swing.JComponent;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FetchMode;

import com.jgoodies.uif.AbstractFrame;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.WindowUtils;
import com.jgoodies.uifextras.convenience.SetupManager;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Catalog;
import com.melloware.jukes.file.Disclist;
import com.melloware.jukes.file.Playlist;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Player;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.dialogs.DiscFindDialog;
import com.melloware.jukes.tray.ITrayIcon;
import com.melloware.jukes.util.MessageUtil;

/**
 * The main frame of the Skeleton Pro application. It creates the menus, menu
 * bar, tool bar and all subpanels.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ 2009, 2010
 */
public final class MainFrame extends AbstractFrame implements HotkeyListener, IntellitypeListener {

   private static final Log LOG = LogFactory.getLog(MainFrame.class);
   private static final Dimension MINIMUM_SIZE = new Dimension(800, 600);
   private static final int WINDOWS_J = 1;
   private ITrayIcon trayIcon;
   private JIntellitype jintellitype;
   private final MainMenuBuilder mainMenuBuilder;
   private final MainModule mainModule;
   private MainPageBuilder mainPageBuilder;
   private Player player;

   // private final SpectrumTimeAnalyzer analyzer = new SpectrumTimeAnalyzer();
   // //AZ Suspended

   /**
    * Constructs an instance of the Skeleton Pro app's main frame.
    * @param mainModule provides bound properties and high-level models
    */
   public MainFrame(MainModule mainModule) {
      super(Application.getDescription().getWindowTitle());
      this.mainModule = mainModule;
      this.mainMenuBuilder = new MainMenuBuilder();
      this.player = new Player();

      /**
       * AZ Suspended Spectrum analyzer // create and setup the Spectrum
       * Analyzer final int[] visualLocation = { 24, 44 }; final int[]
       * visualSize = { 76, 15 };
       * analyzer.setDisplayMode(MainModule.SETTINGS.getAnalyzerMode());
       * analyzer.setSpectrumAnalyserBandCount(19);
       * analyzer.setLocation(visualLocation[0], visualLocation[1]);
       * analyzer.setSize(visualSize[0], visualSize[1]);
       * analyzer.setSpectrumAnalyserDecay(0.05f);
       * analyzer.setPeakColor(Color.BLACK);
       * analyzer.setPeakDelay((int)(SpectrumTimeAnalyzer.DEFAULT_FPS
       * SpectrumTimeAnalyzer.DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO));
       * analyzer.setBackground(this.getBackground());
       */

      LOG.info(Application.getUserPreferences().toString());
   }

   /**
    * AZ Suspended Spectrum analyzer Gets the analyzer.
    * <p>
    * @return Returns the analyzer. public SpectrumTimeAnalyzer getAnalyzer() {
    *         return this.analyzer; }
    */

   /**
    * Gets the jintellitype.
    * <p>
    * @return Returns the jintellitype.
    */
   public JIntellitype getJintellitype() {
      return this.jintellitype;
   }

   /**
    * Gets the mainModule.
    * <p>
    * @return Returns the mainModule.
    */
   public MainModule getMainModule() {
      return this.mainModule;
   }

   /**
    * Gets the mainPageBuilder.
    * <p>
    * @return Returns the mainPageBuilder.
    */
   public MainPageBuilder getMainPageBuilder() {
      return this.mainPageBuilder;
   }

   /**
    * Gets the player.
    * <p>
    * @return Returns the player.
    */
   public Player getPlayer() {
      return this.player;
   }

   /**
    * Gets the playlist.
    * <p>
    * @return Returns the playlist.
    */
   public Playlist getPlaylist() {
      return this.getMainPageBuilder().getPlaylist();
   }

   /**
    * Gets the trayIcon.
    * <p>
    * @return Returns the trayIcon.
    */
   public ITrayIcon getTrayIcon() {
      return this.trayIcon;
   }

   /**
    * Change the tray icon if necessary.
    * <p>
    * @param aImage the Image to change the icon to.
    */
   public void updateTrayIcon(final Image aImage) {
      LOG.debug("Tray Icon image update");
      if (getTrayIcon() != null) {
         getTrayIcon().changeImage(aImage);
      }
   }

   /**
    * The UI Framework needs some kind of ID to tell windows apart. Since we
    * only have one window, it doesn't matter what it is.
    * @return ID of the window.
    */
   @Override
   public String getWindowID() {
      return "JukesMainWindow";
   }

   /**
    * Returns the frame's minimum size. It is used by the WindowUtils to resize
    * the window if the user has shrinked the window below this given size.
    * @return the frame's minimum size
    * @see com.jgoodies.swing.AbstractFrame#getWindowMinimumSize()
    */
   @Override
   public Dimension getWindowMinimumSize() {
      return MINIMUM_SIZE;
   }

   /**
    * Sets the jintellitype.
    * <p>
    * @param aJintellitype The jintellitype to set.
    */
   public void setJintellitype(JIntellitype aJintellitype) {
      this.jintellitype = aJintellitype;
      this.jintellitype.addHotKeyListener(this);
      this.jintellitype.addIntellitypeListener(this);

      // assign the WINDOWS+J key to the unique id 1 for identification
      LOG.info("Registering WINDOWS+J hotkey to show and hide Jukes");
      jintellitype.registerHotKey(WINDOWS_J, JIntellitype.MOD_WIN, 'J');
   }

   /**
    * Sets the player.
    * <p>
    * @param aPlayer The player to set.
    */
   public void setPlayer(Player aPlayer) {
      this.player = aPlayer;
   }

   /**
    * Sets the trayIcon.
    * <p>
    * @param aTrayIcon The trayIcon to set.
    */
   public void setTrayIcon(ITrayIcon aTrayIcon) {
      this.trayIcon = aTrayIcon;
   }

   /*
    * (non-Javadoc)
    * @see
    * com.jgoodies.swing.application.AbstractMainFrame#aboutToExitApplication()
    */
   public void aboutToExitApplication() {
      LOG.info("Exiting application.");
      this.storeState();
      this.player.stop();
      writeUserPreferencesToFile();
      // Force clean application exit
      // If we will not do this the Application will continue with useless
      // frames and windows disposal procedure which in some cases gets stuck
      // making exit not available. As we don't have anything else to do with
      // application we simply do clean exit here.
      System.exit(0);
   }

   /**
    * Builds this <code>MainFrame</code>. Firstly, it executes the superclass
    * behavior, then sets the menu bar, registers help sets, and finally makes
    * the help view invisible.
    */
   @Override
   public void build() {
      super.build();
      setJMenuBar(mainMenuBuilder.build());

      // load all artists and discs first so they are in the cache, slow up
      // front hit
      LOG.debug("START Loading all discs");
      HibernateDao.createCriteria(Artist.class).setFetchMode("discs", FetchMode.JOIN).list();
      LOG.debug("END Loading all discs");

      final Catalog catalog = new Catalog("");
      mainModule.setCatalog(catalog);

      // if this is the first use look for ~/Music to load music
      if (SetupManager.usageCount() <= 1) {
         final File musicDir = new File("./Music");
         if (musicDir.exists()) {
            MainModule.SETTINGS.setStartInDirectory(musicDir);
            new DiscFindDialog(this, MainModule.SETTINGS, true);
         }
      }

      // if number of uses = 30 or 100 then ask for donation
      if ((SetupManager.usageCount() == 2) || (SetupManager.usageCount() == 30) || (SetupManager.usageCount() == 100)) {
         String donateMsg = Resources.getString("messages.donate");
         donateMsg = MessageFormat.format(donateMsg, new Object[] { SetupManager.usageCount() });
         MessageUtil.showwarn(this, donateMsg);
         ActionManager.get(Actions.HELP_DONATE_ID).actionPerformed(null);
      }
   }

   /**
    * When a registered hotkey is received this method is invoked.
    * <p>
    * @param aHotKeyId the unique HotKeyId combination that was pressed
    */
   public void onHotKey(int aHotKeyId) {
      switch (aHotKeyId) {
      case WINDOWS_J: {
         LOG.debug("WINDOWS+J hotkey message received ");
         if (this.isVisible()) {
            trayIcon.hideWindow();
         } else {
            trayIcon.showWindow();
         }
         break;
      }
      default: {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Undefined HOTKEY message caught " + Integer.toString(aHotKeyId));
         }
         break;
      }
      }
   }

   /**
    * When any Intellitype commands are received.
    * <p>
    * @param aCommandId the constant Intellitype command received.
    */
   public void onIntellitype(int aCommandId) {
      switch (aCommandId) {
      case JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK: {
         LOG.debug("MEDIA_NEXTTRACK message received ");
         ActionManager.get(Actions.PLAYER_NEXT_ID).actionPerformed(null);
         break;
      }
      case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE: {
         LOG.debug("MEDIA_PLAY_PAUSE message received ");
         ActionManager.get(Actions.PLAYER_PAUSE_ID).actionPerformed(null);
         break;
      }
      case JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK: {
         LOG.debug("MEDIA_PREVIOUSTRACK message received ");
         ActionManager.get(Actions.PLAYER_PREVIOUS_ID).actionPerformed(null);
         break;
      }
      case JIntellitype.APPCOMMAND_MEDIA_STOP: {
         LOG.debug("MEDIA_STOP message received ");
         ActionManager.get(Actions.PLAYER_STOP_ID).actionPerformed(null);
         break;
      }
      default: {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Undefined INTELLITYPE message caught " + Integer.toString(aCommandId));
         }
         break;
      }
      }

   }

   /**
    * Stores the frame's state in the user preferences.
    * <p>
    */
   @Override
   public void storeState() {
      super.storeState();
      Preferences userPrefs = Application.getUserPreferences();
      WindowUtils.storeBounds(userPrefs, this);
      WindowUtils.storeState(userPrefs, this);
      mainModule.storeState();
      mainPageBuilder().storeIn(userPrefs);
   }

   /**
    * Builds this frame's content pane.
    */
   @Override
   protected JComponent buildContentPane() {
      mainPageBuilder = new MainPageBuilder(mainModule);
      player.getPlayer().addBasicPlayerListener(mainPageBuilder);
      return mainPageBuilder.build();

   }

   /*
    * (non-Javadoc)
    * @see com.jgoodies.uif.AbstractFrame#configureCloseOperation()
    */
   @Override
   protected void configureCloseOperation() {
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      addWindowListener(new java.awt.event.WindowAdapter() {
         @Override
         public void windowClosing(java.awt.event.WindowEvent evt) {
            LOG.debug("Window Close");
            if (trayIcon == null) {
               aboutToExitApplication();
            } else {
               trayIcon.hideWindow();
            }
         }
      });
   }

   /**
    * Restores the frame's state from the user preferences.
    * <p>
    */
   @Override
   protected void restoreState() {
      super.restoreState();
      mainModule.restoreState();
      Preferences userPrefs = Application.getUserPreferences();
      mainPageBuilder().restoreFrom(userPrefs);
   }

   /*
    * Returns the main page.
    */
   private MainPageBuilder mainPageBuilder() {
      return mainPageBuilder;
   }

   /**
    * AZ Gets the disclist.
    * <p>
    * @return Returns the disclist.
    */
   public Disclist getDisclist() {
      return this.getMainPageBuilder().getDisclist();
   }

   /**
    * AZ Write user preferences to Jukes.xml file.
    * <p>
    */
   protected void writeUserPreferencesToFile() {
      final String LINE_BREAK = "\n\n";
      final String ERROR_WRITING_FILE = Resources.getString("label.Errorwritingfile");
      Preferences userPrefs = Application.getUserPreferences();
      final File file = new File(FilenameUtils.normalizeNoEndSeparator(SystemUtils.USER_DIR
               + SystemUtils.FILE_SEPARATOR + "jukes.xml"));
      // now try and save the preferences to a file
      try {
         final FileOutputStream stream = new FileOutputStream(file);
         userPrefs.exportSubtree(stream);
      } catch (IOException ex) {
         final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
         MessageUtil.showError(mainFrame, ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage());
         LOG.error(ERROR_WRITING_FILE + LINE_BREAK + ex, ex);
      } catch (Exception ex) {
         final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
         MessageUtil.showError(mainFrame, ERROR_WRITING_FILE);
         LOG.error("Unexpected error writing file.", ex);
      }
   }

}