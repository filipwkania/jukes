package com.melloware.jukes.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.jgoodies.looks.LookUtils;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.panel.SimpleInternalFrame;
import com.jgoodies.uif.util.ComponentTreeUtils;
import com.jgoodies.uifextras.util.ActionLabel;
import com.jgoodies.uifextras.util.UIFactory;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.file.Disclist;
import com.melloware.jukes.file.Playlist;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.editor.ArtistEditor;
import com.melloware.jukes.gui.view.editor.DiscEditor;
import com.melloware.jukes.gui.view.editor.EditorPanel;
import com.melloware.jukes.gui.view.editor.EmptyPanel;
import com.melloware.jukes.gui.view.editor.TrackEditor;
import com.melloware.jukes.gui.view.editor.WelcomePanel;
import com.melloware.jukes.util.MessageUtil;
import com.melloware.jukes.util.TimeSpan;

/**
 * Builds the main page of the Jukes application: the tool bar, navigator, help
 * navigation, all editors, the dynamic help viewer, and the status bar.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ Development 2009, 2010
 */
public final class MainPageBuilder implements BasicPlayerListener {

   private static final Log LOG = LogFactory.getLog(MainPageBuilder.class);
   private static final Dimension PREFERRED_SIZE = LookUtils.IS_LOW_RESOLUTION ? new Dimension(800, 600)
            : new Dimension(1024, 768);
   private static final String MAIN_DIVIDER_LOCATION_KEY = "mainDividerLocation";
   private static final String VIEWER_DIVIDER_LOCATION_KEY = "viewerDividerLocation";
   private static final String NAVIGATOR_DIVIDER_LOCATION_KEY = "navigatorDividerLocation";
   private static final String NAVIGATOR_VISIBLE_KEY = "navigatorVisible";
   private static final String PLAYLIST_VISIBLE_KEY = "playlistVisible";
   private static final String DISCLIST_VISIBLE_KEY = "disclistVisible"; // AZ
   private static final TimeSpan TIMESPAN = new TimeSpan(0);
   private ActionLabel trackField;
   private EditorPanel editorPanel;
   private FilterPanel filterPanel;
   private JLabel bitrateField;
   private JLabel durationField;
   private JPanel mainPage;
   private JProgressBar elapsedBar;
   private JSplitPane editorPlaylistSplitPane;
   private JSplitPane mainSplitPane;
   private JSplitPane navigatorSplitPane;
   private long elapsedTime;
   private final MainFrame mainframe;
   private final MainModule mainModule;
   private Map audioInfo;
   private NavigationPanelBuilder navigationPanel;
   private final Playlist playlist;
   private PlaylistPanel playlistPanel;
   private final Disclist disclist; // AZ
   private DisclistPanel disclistPanel; // AZ
   private SimpleInternalFrame navigator;
   private Timer timer;
   private Boolean playlistVisible; // AZ
   private Boolean disclistVisible; // AZ
   private JSlider volumeSlider; // AZ

   /**
    * Constructs a MainPageBuilder for the given main module.
    * @param mainModule provides high-level models
    */
   MainPageBuilder(MainModule mainModule) {
      this.mainModule = mainModule;
      this.playlist = new Playlist();
      this.disclist = new Disclist();
      this.mainModule.addPropertyChangeListener(this.playlist);
      this.mainModule.addPropertyChangeListener(this.disclist);
      this.mainframe = (MainFrame) Application.getDefaultParentFrame();
      MainModule.SETTINGS.addPropertyChangeListener(new PresentationSettingsChangeHandler());
   }

   /**
    * Gets the playlist.
    * <p>
    * @return Returns the playlist.
    */
   public Playlist getPlaylist() {
      return this.playlist;
   }

   /**
    * Gets the disclist.
    * <p>
    * @return Returns the disclist.
    */
   public Disclist getDisclist() {
      return this.disclist;
   }

   /*
    * (non-Javadoc)
    * @see
    * javazoom.jlgui.basicplayer.BasicPlayerListener#setController(javazoom.
    * jlgui.basicplayer.BasicController)
    */
   public void setController(BasicController controller) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Controller " + controller);
      }
   }

   /**
    * Sets the visibility of the filter panel.
    */
   public void setFilterVisible(boolean b) {
      if (isFilterVisible() == b) {
         return;
      }
      if (b) {
         navigatorSplitPane.setTopComponent(navigator);
         navigatorSplitPane.setBottomComponent(filterPanel);
         int navigatorDividerLocation = Application.getUserPreferences().getInt(NAVIGATOR_DIVIDER_LOCATION_KEY, -1);
         if ((navigatorDividerLocation > 50) && (navigatorDividerLocation < (mainPage.getHeight() - 50))) {
            navigatorSplitPane.setDividerLocation(navigatorDividerLocation);
         }
      } else {
         navigatorSplitPane.setTopComponent(navigator);
         navigatorSplitPane.setBottomComponent(null);
      }
   }

   /**
    * Sets the visibility of the playlist panel.
    * <p>
    * @param visible whether or not the window is visible
    */
   public void setPlaylistVisible(boolean visible) {
      if (visible) {
         editorPlaylistSplitPane.setTopComponent(editorPanel);
         editorPlaylistSplitPane.setBottomComponent(playlistPanel);
         int verticalDividerLocation = Application.getUserPreferences().getInt(VIEWER_DIVIDER_LOCATION_KEY, -1);
         if ((verticalDividerLocation > 100) && (verticalDividerLocation < (mainPage.getHeight() - 50))) {
            editorPlaylistSplitPane.setDividerLocation(verticalDividerLocation);
         }
         playlistVisible = true;
      } else {
         if (disclistVisible != null) {
            if (disclistVisible) {
               editorPlaylistSplitPane.setBottomComponent(disclistPanel);
            } else {
               editorPlaylistSplitPane.setBottomComponent(null);
            }
            editorPlaylistSplitPane.setTopComponent(editorPanel);
            playlistVisible = false;
         } else {
            editorPlaylistSplitPane.setTopComponent(editorPanel);
            editorPlaylistSplitPane.setBottomComponent(null);
            playlistVisible = false;
         }
      }
   }

   /**
    * Answers if the filter panel is visible.
    */
   public boolean isFilterVisible() {
      return navigatorSplitPane.getBottomComponent() != null;
   }

   /**
    * Answers if the playlist panel is visible.
    */
   public boolean isPlaylistVisible() {
      return playlistVisible;
      // return editorPlaylistSplitPane.getBottomComponent() != null;
   }

   /*
    * (non-Javadoc)
    * @see
    * javazoom.jlgui.basicplayer.BasicPlayerListener#opened(java.lang.Object,
    * java.util.Map)
    */
   public void opened(Object stream, Map properties) {
      audioInfo = properties;

      /*
       * mp3.frequency.hz='44100' title='We' mp3.length.bytes='6078464'
       * comment='Test Comments !' mp3.channels='2' date='2003'
       * mp3.version.layer='3' mp3.framesize.bytes='413' mp3.id3tag.track='3'
       * mp3.version.mpeg='1' mp3.bitrate.nominal.bps='229000'
       * mp3.vbr.scale='78' copyright='copy' mp3.length.frames='8122'
       * mp3.crc='false' album='The Sta no Expe' mp3.vbr='true'
       * mp3.copyright='false' mp3.framerate.fps='38.28125'
       * mp3.id3tag.v2='java.io.ByteArrayInputStream@13caecd'
       * mp3.id3tag.v2.version='3' mp3.version.encoding='MPEG1L3'
       * mp3.header.pos='2150' mp3.id3tag.genre='(18)Techno'
       * mp3.original='false' mp3.mode='1' mp3.padding='false' author='Scer'
       * duration='212167000' vbr='true' bitrate='229000'
       */
      final Track track = playlist.getCurrentTrack();
      final String title = track.getName();
      final String disc = track.getDisc().getName();
      final String artist = track.getDisc().getArtist().getName();
      final Long duration = Long.valueOf(track.getDuration() * 1000);
      final String bitrate = (track.getBitrate().intValue()) + " kbps";
      final Runnable updateList = new Runnable() {
         public void run() {
            trackField.setText(artist + " - " + disc + " - " + title);
            bitrateField.setText(bitrate);
            final int ms = (int) (duration.longValue());
            TIMESPAN.setTime(duration.longValue());
            durationField.setText(TIMESPAN.getMusicDuration());
            elapsedBar.setValue(0);
            elapsedBar.setMaximum(ms);

            // set the tray icon tooltip
            if (mainframe.getTrayIcon() != null) {
               mainframe.getTrayIcon().setToolTip(artist + " - " + title);
            }

            // refresh the window
            refreshUI();
         }
      };
      EventQueue.invokeLater(updateList);

   }

   /*
    * (non-Javadoc)
    * @see javazoom.jlgui.basicplayer.BasicPlayerListener#progress(int, long,
    * byte[], java.util.Map)
    */
   public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
      if (audioInfo.containsKey("audio.type")) {
         String audioformat = (String) audioInfo.get("audio.type");
         LOG.debug(audioformat);
         /**
          * AZ Suspended Spectrum analyzer if
          * (audioformat.equalsIgnoreCase("mp3")) {
          * mainframe.getAnalyzer().writeDSP(pcmdata); }
          **/
      }

      this.setElapsedTime(microseconds);
   }

   /**
    * Repaints the screen after a track changes.
    */
   public void refreshUI() {
      navigator.updateUI();
      editorPanel.updateUI();
   }

   /**
    * Restores the frame's state from the user preferences.
    */
   public void restoreFrom(Preferences userPrefs) {
      int mainDividerLocation = userPrefs.getInt(MAIN_DIVIDER_LOCATION_KEY, -1);
      int verticalDividerLocation = userPrefs.getInt(VIEWER_DIVIDER_LOCATION_KEY, -1);
      int navigatorDividerLocation = userPrefs.getInt(NAVIGATOR_DIVIDER_LOCATION_KEY, -1);
      setDisclistVisible(userPrefs.getBoolean(DISCLIST_VISIBLE_KEY, false)); // AZ
      setPlaylistVisible(userPrefs.getBoolean(PLAYLIST_VISIBLE_KEY, true));
      setFilterVisible(userPrefs.getBoolean(NAVIGATOR_VISIBLE_KEY, true));

      if ((mainDividerLocation > 100) && (mainDividerLocation < (mainPage.getWidth() - 50))) {
         mainSplitPane.setDividerLocation(mainDividerLocation);
      }
      if ((verticalDividerLocation > 100) && (verticalDividerLocation < (mainPage.getHeight() - 50))) {
         editorPlaylistSplitPane.setDividerLocation(verticalDividerLocation);
      }
      if ((navigatorDividerLocation > 50) && (navigatorDividerLocation < (mainPage.getHeight() - 50))) {
         navigatorSplitPane.setDividerLocation(navigatorDividerLocation);
      }
   }

   /*
    * (non-Javadoc)
    * @see
    * javazoom.jlgui.basicplayer.BasicPlayerListener#stateUpdated(javazoom.jlgui
    * .basicplayer.BasicPlayerEvent)
    */
   public void stateUpdated(BasicPlayerEvent event) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("stateUpdated : " + event.toString());
      }
      // final SpectrumTimeAnalyzer analyzer = mainframe.getAnalyzer(); //AZ
      // Suspended Spectrum analyzer

      switch (event.getCode()) {
      // case BasicPlayerEvent.PLAYING: {
      // break;
      // }
      case BasicPlayerEvent.PLAYING:
      case BasicPlayerEvent.OPENED:
      case BasicPlayerEvent.RESUMED: {
         mainframe.updateTrayIcon(ImageFactory.ICO_TRAYPLAY.getImage());
         /**
          * AZ Suspended Spectrum analyzer if
          * ((audioInfo.containsKey("basicplayer.sourcedataline")) && (analyzer
          * != null)) { analyzer.setupDSP((SourceDataLine)audioInfo.get(
          * "basicplayer.sourcedataline"));
          * analyzer.startDSP((SourceDataLine)audioInfo
          * .get("basicplayer.sourcedataline")); }
          **/
         timer.start();
         break;
      }
      case BasicPlayerEvent.PAUSED: {
         timer.stop();
         mainframe.updateTrayIcon(ImageFactory.ICO_TRAYPAUSE.getImage());
         break;
      }
      case BasicPlayerEvent.STOPPED: {
         timer.stop();
         mainframe.updateTrayIcon(ImageFactory.ICO_TRAYSTOP.getImage());
         /**
          * AZ Suspended Spectrum analyzer if (analyzer != null) {
          * analyzer.stopDSP(); analyzer.repaint(); }
          **/

         final Runnable updateList = new Runnable() {
            public void run() {
               elapsedBar.setValue(0);
               TIMESPAN.setTime(0);
               elapsedBar.setString(TIMESPAN.getMusicDuration());
            }
         };
         EventQueue.invokeLater(updateList);
         break;
      }
      case BasicPlayerEvent.EOM: {
         timer.stop();
         final Track track = (Track) getPlaylist().getNext();
         if (track != null) {
            mainframe.getPlayer().play(track.getTrackUrl());
         }
         break;
      }
      default: {
         break;
      }
      }
   }

   /**
    * Stores the frame's state in the user preferences.
    */
   public void storeIn(Preferences userPrefs) {
      if (isPlaylistVisible()) {
         int verticalDividerLocation = editorPlaylistSplitPane.getDividerLocation();
         userPrefs.putInt(VIEWER_DIVIDER_LOCATION_KEY, verticalDividerLocation);
      }
      if (isFilterVisible()) {
         int navigatorDividerLocation = navigatorSplitPane.getDividerLocation();
         userPrefs.putInt(NAVIGATOR_DIVIDER_LOCATION_KEY, navigatorDividerLocation);
      }
      // AZ
      if (isDisclistVisible()) {
         int verticalDividerLocation = editorPlaylistSplitPane.getDividerLocation();
         userPrefs.putInt(VIEWER_DIVIDER_LOCATION_KEY, verticalDividerLocation);
      }
      int mainDividerLocation = mainSplitPane.getDividerLocation();
      userPrefs.putInt(MAIN_DIVIDER_LOCATION_KEY, mainDividerLocation);
      userPrefs.putBoolean(NAVIGATOR_VISIBLE_KEY, isFilterVisible());
      userPrefs.putBoolean(PLAYLIST_VISIBLE_KEY, isPlaylistVisible());
      userPrefs.putBoolean(DISCLIST_VISIBLE_KEY, isDisclistVisible());
   }

   /**
    * Builds this panel with the horizontal <code>JSplitPane</code> in the
    * center and a status bar in the south.
    */
   JComponent build() {
      initComponents();

      mainPage = new RefreshedPanel();
      mainPage.setLayout(new BorderLayout());
      mainPage.add(new MainToolBarBuilder().build(), BorderLayout.NORTH);
      mainPage.add(buildMainSplitPane(), BorderLayout.CENTER);
      mainPage.add(buildStatusBar(), BorderLayout.SOUTH);
      mainPage.setPreferredSize(PREFERRED_SIZE);

      return mainPage;
   }

   /**
    * Builds the <code>EditorPanel</code>, the <code>PlaylistPanel</code> and
    * answers them wrapped by a stripped <code>JSplitPane</code>.
    */
   private JComponent buildEditorHelpPanel() {
      editorPlaylistSplitPane = UIFactory.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, buildEditorPanel(),
               playlistPanel, 0.667);
      return editorPlaylistSplitPane;
   }

   /**
    * Builds and answers the <code>EditorPanel</code>.
    */
   private EditorPanel buildEditorPanel() {
      WelcomePanel welcomePanel = new WelcomePanel();

      editorPanel.addEditor(welcomePanel);
      editorPanel.addEditor(new EmptyPanel());
      editorPanel.addEditor(new ArtistEditor());
      editorPanel.addEditor(new DiscEditor());
      editorPanel.addEditor(new TrackEditor());

      editorPanel.setActiveEditor(welcomePanel);
      return editorPanel;
   }

   /**
    * Builds the <code>Navigator</code>, the <code>HelpNavigator</code> and
    * answers them wrapped by a stripped <code>JSplitPane</code>.
    */
   private JComponent buildFilterPanel() {
      return UIFactory.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, navigator, filterPanel, 0.64);
   }

   /**
    * Builds and answers the main <code>JSplitPane</code> that contains the
    * navigation elements on the left, and the view panels on the right.
    */
   private JComponent buildMainSplitPane() {
      navigatorSplitPane = (JSplitPane) buildFilterPanel();
      mainSplitPane = UIFactory.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, navigatorSplitPane,
               buildEditorHelpPanel(), 0.25);
      mainSplitPane.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 4));
      return mainSplitPane;
   }

   /**
    * Builds and answers the status bar.
    */
   private JPanel buildStatusBar() {
      final JPanel statusPanel = new JPanel(new BorderLayout());
      final JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      final JToolBar toolbar = MainToolBarBuilder.buildPlayerToolBar();
      toolbar.setBorder(BorderFactory.createEmptyBorder());
      infoPanel.setBorder(BorderFactory.createEmptyBorder());
      statusPanel.add(toolbar, BorderLayout.WEST);
      statusPanel.add(infoPanel, BorderLayout.EAST);
      statusPanel.setBorder(BorderFactory.createEmptyBorder());
      infoPanel.add(trackField);
      trackField.setBorder(BorderFactory.createEmptyBorder());
      infoPanel.add(bitrateField);
      bitrateField.setBorder(BorderFactory.createEmptyBorder());
      infoPanel.add(durationField);
      durationField.setBorder(BorderFactory.createEmptyBorder());
      infoPanel.add(elapsedBar);
      elapsedBar.setBorder(BorderFactory.createEmptyBorder());
      infoPanel.add(volumeSlider);
      volumeSlider.setBorder(BorderFactory.createEmptyBorder());
      return statusPanel;
   }

   /**
    * Creates, binds and configures the subpanels and components.
    */
   private void initComponents() {
      navigator = new SimpleInternalFrame(Resources.getString("navigator.label"));
      navigator.setFrameIcon(Resources.NAVIGATOR_ICON);
      navigationPanel = new NavigationPanelBuilder(mainModule);
      navigator.setContent(navigationPanel.build());
      navigator.setSelected(true);
      navigator.setMinimumSize(new Dimension(100, 100));
      navigator.setPreferredSize(new Dimension(160, 200));
      navigator.setFrameIcon(Resources.NAVIGATOR_ICON);

      filterPanel = new FilterPanel(MainModule.SETTINGS);
      filterPanel.setSelected(true);
      filterPanel.setMinimumSize(new Dimension(100, 45));
      filterPanel.setPreferredSize(new Dimension(100, 45));

      editorPanel = new EditorPanel(mainModule);
      editorPanel.setMinimumSize(new Dimension(200, 100));
      editorPanel.setPreferredSize(new Dimension(400, 200));

      playlistPanel = new PlaylistPanel(this.playlist);
      playlistPanel.setName("playlistPanel");
      playlistPanel.setMinimumSize(new Dimension(300, 100));
      playlistPanel.setPreferredSize(new Dimension(300, 100));

      // AZ
      disclistPanel = new DisclistPanel(this.disclist);
      disclistPanel.setName("disclistPanel");
      disclistPanel.setMinimumSize(new Dimension(300, 100));
      disclistPanel.setPreferredSize(new Dimension(300, 100));

      trackField = new ActionLabel("");
      trackField.addActionListener(new ActionListener() {

         // select the track in the editor
         public void actionPerformed(ActionEvent event) {
            LOG.debug("Track clicked");
            if (playlist.getCurrentTrack() != null) {
               mainModule.selectNodeInTree(playlist.getCurrentTrack());
            }
         }
      });
      bitrateField = UIFactory.createPlainLabel("");
      durationField = UIFactory.createPlainLabel("");
      elapsedBar = new JProgressBar(0, 1);
      elapsedBar.setIndeterminate(false);
      elapsedBar.setStringPainted(true);
      elapsedBar.setString("");

      // Set volumeSlider
      int iVolume = (int) (100 * mainframe.getPlayer().getVolume());
      // Perform bounds test, -1 or >100 can occur in some undefined cases
      if (iVolume > 100) {
         iVolume = 100;
      } else if (iVolume < 0) {
         iVolume = 0;
      }
      volumeSlider = new JSlider();
      volumeSlider.setValue(iVolume);
      volumeSlider.setToolTipText(Resources.getString("label.Volume") + ": " + iVolume + " %");
      volumeSlider.addChangeListener(new VolumeChangeAction());

      timer = new Timer(950, new ActionListener() {

         public void actionPerformed(ActionEvent aE) {
            final int elapsed = (int) (getElapsedTime() / 1000);
            TIMESPAN.setTime(elapsed);
            elapsedBar.setValue(elapsed);
            elapsedBar.setString(TIMESPAN.getMusicDuration());
         }
      });
   }

   /**
    * Gets the elapsedTime.
    * <p>
    * @return Returns the elapsedTime.
    */
   protected synchronized long getElapsedTime() {
      return this.elapsedTime;
   }

   /**
    * Sets the elapsedTime.
    * <p>
    * @param aElapsedTime The elapsedTime to set.
    */
   protected synchronized void setElapsedTime(long aElapsedTime) {
      this.elapsedTime = aElapsedTime;
   }

   // Updates the application if settings changed
   private class PresentationSettingsChangeHandler implements PropertyChangeListener {

      /**
       * The presentation settings have changed.
       * @param evt describes the property change
       */
      public void propertyChange(PropertyChangeEvent evt) {
         final String[] props = { Settings.PROPERTYNAME_AUDIT_INFO, Settings.PROPERTYNAME_COVER_SIZE_LARGE,
                  Settings.PROPERTYNAME_COVER_SIZE_SMALL };
         final String[] refreshprops = { Settings.PROPERTYNAME_DISPLAY_FORMAT_DISC,
                  Settings.PROPERTYNAME_DISPLAY_FORMAT_TRACK, Settings.PROPERTYNAME_NEW_FILE_IN_DAYS };
         final String[] connectProps = { Settings.PROPERTYNAME_REMOTE_DATABASE_URL,
                  Settings.PROPERTYNAME_DIRECTORY_DB_LOCATION };

         if (Settings.PROPERTYNAME_PLAYER_BUFFER_SIZE.equals(evt.getPropertyName())) {
            LOG.debug("Buffer size changed");
            BasicPlayer.EXTERNAL_BUFFER_SIZE = MainModule.SETTINGS.getPlayerBufferSize();
         }

         if (Settings.PROPERTYNAME_LOG_LEVEL.equals(evt.getPropertyName())) {
            LOG.info("Log Level changed");
            Logger.getRootLogger().setLevel(Level.toLevel(MainModule.SETTINGS.getLogLevel()));
            Logger.getLogger("com.melloware").setLevel(Level.toLevel(MainModule.SETTINGS.getLogLevel()));
            Logger.getLogger("com.melloware.jukes.gui").setLevel(Level.toLevel(MainModule.SETTINGS.getLogLevel()));
         }

         // if any of the editor settings changes reload
         if (StringUtils.indexOfAny(evt.getPropertyName(), props) >= 0) {
            LOG.debug("Editor options changed");
            editorPanel.clearEditors();
            buildEditorPanel();
         }

         // do we need to reconnect on prop change
         if (StringUtils.indexOfAny(evt.getPropertyName(), connectProps) >= 0) {
            LOG.debug("DB Location changed, reconnecting to DB and refreshing tree.");
            HibernateUtil.shutdown();
            final String remoteURL = MainModule.SETTINGS.getRemoteDatabaseURL();
            if ((StringUtils.isNotBlank(remoteURL)) && (!Settings.DEFAULT_REMOTE_DATABASE_URL.equals(remoteURL))) {
               HibernateUtil.setRemoteUrl(remoteURL);
            } else {
               HibernateUtil.setRemoteUrl(null);
            }
            ActionManager.get(Actions.CONNECT_ID).actionPerformed(null);
         }

         // do we need to refresh the tree on prop change
         if (StringUtils.indexOfAny(evt.getPropertyName(), refreshprops) >= 0) {
            LOG.debug("Navigation options changed.");
            ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
         }
         /**
          * AZ Suspended Spectrum analyzer if
          * (Settings.PROPERTYNAME_ANALYZER_MODE.equals(evt.getPropertyName()))
          * { mainframe.getAnalyzer().setDisplayMode(MainModule.SETTINGS.
          * getAnalyzerMode()); }
          **/
      }
   }

   private class RefreshedPanel extends JPanel {

      /**
       * In case some panels are invisible, we explicitly update their UIs.
       */
      @Override
      public void updateUI() {
         super.updateUI();
         if (getComponentCount() == 0) {
            return;
         }
         if ((editorPlaylistSplitPane == null) || isPlaylistVisible()) {
            return;
         }
         ComponentTreeUtils.updateComponentTreeUI(editorPlaylistSplitPane);
      }

   }

   /**
    * Sets the visibility of the disclist panel.
    * <p>
    * @param visible whether or not the window is visible
    */
   public void setDisclistVisible(boolean visible) {
      if (visible) {
         editorPlaylistSplitPane.setTopComponent(editorPanel);
         editorPlaylistSplitPane.setBottomComponent(disclistPanel);
         int verticalDividerLocation = Application.getUserPreferences().getInt(VIEWER_DIVIDER_LOCATION_KEY, -1);
         if ((verticalDividerLocation > 100) && (verticalDividerLocation < (mainPage.getHeight() - 50))) {
            editorPlaylistSplitPane.setDividerLocation(verticalDividerLocation);
         }
         disclistVisible = true;
      } else {
         if (playlistVisible != null) {
            if (playlistVisible) {
               editorPlaylistSplitPane.setBottomComponent(playlistPanel);
            } else {
               editorPlaylistSplitPane.setBottomComponent(null);
            }
            editorPlaylistSplitPane.setTopComponent(editorPanel);
            disclistVisible = false;
         } else {
            editorPlaylistSplitPane.setTopComponent(editorPanel);
            editorPlaylistSplitPane.setBottomComponent(null);
            disclistVisible = false;
         }
      }
   }

   /**
    * Answers if the disclist panel is visible.
    */
   public boolean isDisclistVisible() {
      // AZ
      return disclistVisible;
      // return editorPlaylistSplitPane.getBottomComponent() != null;
   }

   /**
    * Returns the name of list panel currently visible.
    */
   public String panelVisible() {
      String panelName;
      if (editorPlaylistSplitPane.getBottomComponent() != null) {
         panelName = editorPlaylistSplitPane.getBottomComponent().getName();
      } else {
         panelName = "No Panel";
      }
      return panelName;
   }

   public class VolumeChangeAction implements ChangeListener {
      @Override
      public void stateChanged(ChangeEvent e) {
         int iVolume = volumeSlider.getValue();
         float fVolume = iVolume / 100f;
         try {
            mainframe.getPlayer().setVolume(fVolume);
            volumeSlider.setToolTipText(Resources.getString("label.Volume") + ": " + iVolume + " %");
         } catch (Exception ex) {
            LOG.error("Set Volume Exception: ", ex);
            MessageUtil.showError(null, "Set Volume Exception: " + ex.getMessage());
         }
      }
   }

}