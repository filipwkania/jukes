package com.melloware.jukes.gui.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXParseException;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uif.panel.SimpleInternalFrame;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uifextras.util.UIFactory;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.file.Playlist;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.file.filter.M3uFilter;
import com.melloware.jukes.file.filter.XspfFilter;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.component.AlbumImage;
import com.melloware.jukes.gui.view.component.ComponentFactory;
import com.melloware.jukes.gui.view.component.PlaylistCellRenderer;
import com.melloware.jukes.gui.view.component.PlaylistListModel;
import com.melloware.jukes.gui.view.tasks.LoadPlaylistTask;
import com.melloware.jukes.gui.view.tasks.TimerListener;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.MessageUtil;

/**
 * Display the playlist and all the playlist optins.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class PlaylistPanel extends SimpleInternalFrame implements PropertyChangeListener {

   private static final Log LOG = LogFactory.getLog(PlaylistPanel.class);
   private AlbumImage albumImage;
   private JLabel artist;
   private JLabel bitrate;
   private JLabel disc;
   private JLabel duration;
   private JLabel track;
   private final JList trackList;
   private JScrollPane listScrollPane;
   private JToggleButton shuffleCatalog;
   private JToggleButton shufflePlaylist;
   private final Playlist playlist;
   private final PlaylistListModel listModel;

   /**
    * Constructs a <code>PlaylistPanel</code> for the given module.
    * 
    * @param aPlaylist provides the playlist class needed for display
    */
   PlaylistPanel(Playlist aPlaylist) {
      super(Resources.PLAYLIST_ICON, ResourceUtils.getString("label.playlist"));
      LOG.debug("Playlist planel created.");
      this.playlist = aPlaylist;
      this.playlist.addPropertyChangeListener(this);

      // build playlist and assign listeners
      listModel = new PlaylistListModel(this.playlist, this);
      trackList = new JList(listModel);
      trackList.putClientProperty(Resources.EDITOR_COMPONENT, this);
      trackList.addKeyListener(listModel.getKeyTypedListener());

      // add right click menu to the playlist
      final JPopupMenu popup = MainMenuBuilder.buildPlaylistPopupMenu(this, trackList);
      MouseListener popupListener = new ListPopupListener(popup);
      trackList.addMouseListener(popupListener);
      trackList.add(popup);

      // build the panel
      setToolBar(buildToolBar());
      setContent(buildContent());
      setSelected(true);
   }

   /**
    * Clears out selected items.
    */
   public void clearSelection() {
      // clear the selection
      trackList.clearSelection();
      if (playlist.size() > 0) {
         trackList.setSelectedIndex(0);
      }
   }

   /**
    * Load a playlist from disk.
    */
   public void load() {
      LOG.debug("Load playlist");
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Load Playlist");
      chooser.setAcceptAllFileFilterUsed(false);
      chooser.setFileFilter(FilterFactory.playlistFileFilter());
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileHidingEnabled(true);
      final int returnVal = chooser.showOpenDialog(Application.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }

      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();

      final File file = chooser.getSelectedFile();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Absolute: " + file.getAbsolutePath());
      }

      // now try and import the playlist
      try {
         GuiUtil.setBusyCursor(mainFrame, true);
         final LoadPlaylistTask task = new LoadPlaylistTask(file, this.playlist);
         //AZ: Put the Title of Progress Monitor Dialog Box and Cancel button
         UIManager.put("ProgressMonitor.progressText", Resources.getString("label.ProgressTitle"));
         UIManager.put("OptionPane.cancelButtonText", Resources.getString("label.Cancel"));
         
         final ProgressMonitor progressMonitor = new ProgressMonitor(Application.getDefaultParentFrame(), Resources
                  .getString("messages.loadtracks"), "", 0, (int) task.getLengthOfTask());
         progressMonitor.setProgress(0);
         progressMonitor.setMillisToDecideToPopup(10);
         task.go();
         final Timer timer = new Timer(50, null);
         timer.addActionListener(new TimerListener(progressMonitor, task, timer));
         timer.start();

      } catch (IOException ex) {
    	  final String errorMessage = ResourceUtils.getString("messages.ErrorLoadingFile"); 
          MessageUtil.showError(this, errorMessage); //AZ 
          LOG.error(errorMessage + "\n\n" + ex.getMessage(), ex);
      } catch (SAXParseException spe) {
         final String err = spe.toString() + "\nLine number: " + spe.getLineNumber() + "\nColumn number: "
                  + spe.getColumnNumber() + "\nPublic ID: " + spe.getPublicId() + "\nSystem ID: " + spe.getSystemId();
         LOG.error(err, spe);
      } catch (Exception ex) {
    	  final String errorMessage = ResourceUtils.getString("messages.ErrorLoadingFile"); 
          MessageUtil.showError(this, errorMessage); //AZ 
          LOG.error(errorMessage, ex);
      } finally {
         GuiUtil.setBusyCursor(mainFrame, false);
      }
   }

   /**
    * Moves items down on the list.
    */
   public void moveDown() {
      LOG.debug("Moving on down");
      int[] selected = trackList.getSelectedIndices();
      if (selected.length <= 0) {
         MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils.getString("messages.SelectSomethingToMove"));
      } else {
         int[] newSelections = trackList.getSelectedIndices();
         for (int i = selected.length - 1; i >= 0; i--) {
            int index = selected[i];
            if (index == (this.playlist.size() - 1)) {
               break;
            }
            this.playlist.moveDown(index);
            newSelections[i] = (index + 1);

         }
         trackList.setSelectedIndices(newSelections);
      }
      this.playlist.updateState();
   }

   /**
    * Moves the selected tracks to the other list either history or current.
    */
   public void moveOver() {
      LOG.debug("Moving Over");
      int[] selected = trackList.getSelectedIndices();
      if (selected.length <= 0) {
         MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils.getString("messages.SelectSomethingToMoveOver"));
      } else {
         for (int i = 0; i < selected.length; i++) {
            int index = selected[i];
            this.playlist.moveOver(index);
         }
         // now remove them from the list
         for (int i = selected.length - 1; i >= 0; i--) {
            int index = selected[i];
            this.playlist.remove(index);
         }
         this.clearSelection();
      }
      this.playlist.updateState();

   }

   /**
    * Moves items up on the list.
    */
   public void moveUp() {
      LOG.debug("Moving on up");
      int[] selected = trackList.getSelectedIndices();
      if (selected.length <= 0) {
         MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils.getString("messages.SelectSomethingToMove"));
      } else {
         int[] newSelections = trackList.getSelectedIndices();
         for (int i = 0; i < selected.length; i++) {
            int index = selected[i];
            if (index == 0) {
               break;
            }
            this.playlist.moveUp(index);
            newSelections[i] = (index - 1);
         }
         trackList.setSelectedIndices(newSelections);
      }
      this.playlist.updateState();
   }

   /*
    * If the playlist changes, update the frame title and icon
    */
   public void propertyChange(PropertyChangeEvent aEvt) {
      StringBuffer sb = new StringBuffer();
      Icon icon = null;
      if (this.playlist.isCurrent()) {
         sb.append(ResourceUtils.getString("label.playlist"));
         sb.append("  (");
         sb.append(this.playlist.getCurrentDuration());
         sb.append(')');
         icon = Resources.PLAYLIST_ICON;
      } else {
         sb.append(ResourceUtils.getString("label.history"));
         sb.append("  (");
         sb.append(this.playlist.getHistoryDuration());
         sb.append(')');
         icon = Resources.HISTORY_ICON;
      }

      setTitle(sb.toString());
      setFrameIcon(icon);

      // update the now playing
      final Track currenttrack = this.playlist.getCurrentTrack();
      if (currenttrack != null) {
        albumImage.setDisc(currenttrack.getDisc()); 
      	if (MainModule.SETTINGS.isCopyImagesToDirectory()) {  //AZ
      		final String imageName = ImageFactory.standardImageFileName(currenttrack.getDisc().getArtist().getName(), currenttrack.getDisc().getName(), currenttrack.getDisc().getYear());
      		albumImage.setImage(ImageFactory.getScaledImage(imageName, 150, 150).getImage());
      	} else {
         albumImage.setDisc(currenttrack.getDisc());
         if (StringUtils.isNotBlank(currenttrack.getDisc().getCoverUrl())) {
            albumImage.setImage(ImageFactory.getScaledImage(currenttrack.getDisc().getCoverUrl(), 150, 150).getImage());
         } else {
            albumImage.setImage(null);
         }
      	}
         artist.setText(currenttrack.getDisc().getArtist().getName());
         disc.setText(currenttrack.getDisc().getName());
         track.setText(currenttrack.getDisplayText(MainModule.SETTINGS.getDisplayFormatTrack()));
         duration.setText(currenttrack.getDurationTime());
         bitrate.setText(currenttrack.getBitrate().toString() + " kbps");
      }
     
   }

   /**
    * Removes tracks from the playlist
    */
   public void removeTracks() {
      LOG.debug("Remove tracks");
      int[] selected = trackList.getSelectedIndices();
      if (selected.length <= 0) {
         MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils.getString("messages.SelectSomethingToRemove"));
      } else {
         for (int i = selected.length - 1; i >= 0; i--) {
            int index = selected[i];
            this.playlist.remove(index);
         }
         this.clearSelection();
      }
      this.playlist.updateState();
   }

   /**
    * Clears the playlist.
    */
   public void removeAllTracks() {
      LOG.debug("Remove ALL tracks");
      for (int i = playlist.size() - 1; i >= 0; i--) {
         this.playlist.remove(i);
      }
      this.clearSelection();

      //AZ clear the currently playing track from the playlist
      if (this.playlist.getCurrentTrack() != null) {
    	  this.playlist.removeCurrentTrack();
      }
   }

   /**
    * Saves the current playlist.
    */
   public void save() {
      LOG.debug("Save playlist");
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Save Playlist");
      chooser.setAcceptAllFileFilterUsed(false);
      FileFilter m3u = FilterFactory.m3uFileFilter();
      FileFilter xspf = FilterFactory.xspfFileFilter();
      chooser.addChoosableFileFilter(m3u);
      chooser.addChoosableFileFilter(xspf);
      if (MainModule.SETTINGS.getPlaylistType().equals(XspfFilter.XSPF)) {
         chooser.setFileFilter(xspf);
      } else {
         chooser.setFileFilter(m3u);
      }
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileHidingEnabled(true);
      final int returnVal = chooser.showSaveDialog(Application.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      File file = chooser.getSelectedFile();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Absolute: " + file.getAbsolutePath());
      }

      // add the extension if missing
      if (chooser.getFileFilter() instanceof M3uFilter) {
         file = FilterFactory.forceM3uExtension(file);
      } else if (chooser.getFileFilter() instanceof XspfFilter) {
         file = FilterFactory.forceXspfExtension(file);
      }

      try {
         this.playlist.save(file);

         MessageUtil.showInformation(Application.getDefaultParentFrame(), Resources.getString("messages.PlaylistSavedSuccessfully"));
      } catch (IOException ex) {
     	  final String errorMessage = ResourceUtils.getString("label.Errorwritingfile")+"\n\n" + ex.getMessage(); 
          MessageUtil.showError(this, errorMessage); //AZ 
          LOG.error(errorMessage, ex);
      } catch (Exception ex) {
     	  final String errorMessage = ResourceUtils.getString("label.Errorwritingfile")+"\n\n" + ex.getMessage(); 
          MessageUtil.showError(this, errorMessage); //AZ 
          LOG.error(errorMessage, ex);
      }
   }

   /**
    * Toggles shuffling catalog or not.
    * <p>
    * @param enabled whether to shuffle or not
    */
   public void shuffleCatalog(boolean enabled) {
      LOG.debug("Shuffling catalog");
      this.playlist.setShuffleCatalog(enabled);
      shufflePlaylist.setSelected(false);
      this.playlist.setShufflePlaylist(false);
      this.playlist.updateState();
   }

   /**
    * Toggles shuffling playlist or not.
    * <p>
    * @param enabled whether to shuffle or not
    */
   public void shufflePlaylist(boolean enabled) {
      LOG.debug("Shuffling playlist");
      this.playlist.setShufflePlaylist(enabled);
      shuffleCatalog.setSelected(false);
      this.playlist.setShuffleCatalog(false);
      this.playlist.updateState();
   }

   /**
    * Toggle between history and current playlist.
    */
   public void toggle(boolean current) {
      this.playlist.setCurrent(current);
   }

   /**
    * Builds and answers the content pane.
    */
   private JComponent buildContent() {

      JScrollPane scrollPane = UIFactory.createStrippedScrollPane(buildPlaylist());
      scrollPane.setMinimumSize(new Dimension(300, 100));
      scrollPane.setPreferredSize(new Dimension(300, 100));
      return scrollPane;
   }

   private JComponent buildPlaylist() {
      trackList.setFocusable(true);
      trackList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      trackList.setSelectedIndex(0);
      trackList.setVisibleRowCount(13);
      trackList.setCellRenderer(new PlaylistCellRenderer());
      listScrollPane = new JScrollPane(trackList);

      FormLayout layout = new FormLayout("left:275px, 4dlu, right:pref:grow", "p");

      layout.setRowGroups(new int[][] { { 1 } });
      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();
      builder.add(listScrollPane, cc.xy(1, 1, "left,top"));
      builder.add(buildTrackPanel(), cc.xy(3, 1, "left,top"));
      return builder.getPanel();
   }

   /**
    * Builds and answers the toolbar.
    */
   private JToolBar buildToolBar() {
      final ToolBarBuilder bar = new ToolBarBuilder("Playlist");
      AbstractButton button = null;
      button = (JToggleButton) ComponentFactory.createToolBarToggleButton(Actions.PLAYLIST_TOGGLE_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.PLAYLIST_LOAD_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.PLAYLIST_SAVE_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.TRACK_PLAY_IMMEDIATE_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, trackList);
      bar.add(button);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.PLAYLIST_MOVEUP_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.PLAYLIST_MOVEDOWN_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.PLAYLIST_MOVEOVER_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.PLAYLIST_REMOVE_TRACK_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.PLAYLIST_CLEAR_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.PLAYLIST_GOTO_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, trackList);
      bar.add(button);
      shufflePlaylist = (JToggleButton) ComponentFactory.createToolBarToggleButton(Actions.PLAYLIST_SHUFFLE_LIST_ID);
      shufflePlaylist.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(shufflePlaylist);
      shuffleCatalog = (JToggleButton) ComponentFactory.createToolBarToggleButton(Actions.PLAYLIST_SHUFFLE_CATALOG_ID);
      shuffleCatalog.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(shuffleCatalog);
      button = (AbstractButton) ComponentFactory.createToolBarButton(Actions.PLAYLIST_CLOSE_ID);
      bar.add(button);
      return bar.getToolBar();
   }

   /**
    * Builds the Track editor panel.
    * <p>
    * @return the panel to edit track info.
    */
   private JComponent buildTrackPanel() {
      FormLayout layout = new FormLayout("pref, left:min(80dlu;pref):grow, 40dlu, pref, right:pref",
               "4px, p, 4px, p, 4px, p, 4px, p, 4px, p, p, 75px");
      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();

      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      artist = UIFactory.createBoldLabel("");
      disc = UIFactory.createBoldLabel("");
      track = UIFactory.createBoldLabel("");
      duration = UIFactory.createBoldLabel("");
      bitrate = UIFactory.createBoldLabel("");
      albumImage = new AlbumImage(new Dimension(170, 170));
      ActionListener actionListener = new ActionListener() {
         public void actionPerformed(ActionEvent event) {
            AlbumImage preview = (AlbumImage) event.getSource();
            if (preview.getDisc() != null) {
               GuiUtil.setBusyCursor(mainFrame, true);
               mainFrame.getMainModule().selectNodeInTree(preview.getDisc());
               GuiUtil.setBusyCursor(mainFrame, false);
            }
         }
      };
      albumImage.addActionListener(actionListener);

      builder.addLabel(ResourceUtils.getString("label.artist") + ": ", cc.xy(1, 2));
      builder.add(artist, cc.xyw(2, 2, 3));
      builder.add(albumImage, cc.xywh(5, 2, 1, 11));
      builder.addLabel(ResourceUtils.getString("label.disc") + ": ", cc.xy(1, 4));
      builder.add(disc, cc.xyw(2, 4, 3));
      builder.addLabel(ResourceUtils.getString("label.track") + ": ", cc.xy(1, 6));
      builder.add(track, cc.xyw(2, 6, 3));
      builder.addLabel(ResourceUtils.getString("label.duration") + ": ", cc.xy(1, 8));
      builder.add(duration, cc.xyw(2, 8, 3));
      builder.addLabel(ResourceUtils.getString("label.bitrate") + ": ", cc.xy(1, 10));
      builder.add(bitrate, cc.xyw(2, 10, 3));
      //builder.add(mainFrame.getAnalyzer(), cc.xywh(1, 11, 4, 2)); //AZ Suspended Spectrum analyzer
      final JComponent panel = builder.getPanel();
      panel.setBorder(new TitledBorder(ResourceUtils.getString("label.current")));
      return panel;
   }

   // shows popups on right click of List nodes
   private static class ListPopupListener extends MouseAdapter {

      final JPopupMenu popup;

      public ListPopupListener(JPopupMenu popup) {
         this.popup = popup;
      }

      public void mousePressed(MouseEvent evt) {
         maybeShowPopup(evt);
      }

      public void mouseReleased(MouseEvent e) {
         maybeShowPopup(e);
      }

      private void maybeShowPopup(MouseEvent e) {
         if (e.isPopupTrigger()) {
            ActionManager.get(Actions.TRACK_PLAY_IMMEDIATE_ID).setEnabled(true);

            // popup the menu
            popup.show(e.getComponent(), e.getX(), e.getY());
         }
      }

   }

}