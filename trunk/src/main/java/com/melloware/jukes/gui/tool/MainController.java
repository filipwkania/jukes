package com.melloware.jukes.gui.tool;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;

import javazoom.jlgui.basicplayer.BasicPlayer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;

import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uifextras.convenience.DefaultAboutDialog;
import com.jgoodies.uifextras.convenience.SetupManager;
import com.jgoodies.uifextras.convenience.TipOfTheDayDialog;
import com.l2fprod.common.swing.JDirectoryChooser;
import com.melloware.jukes.db.Database;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.file.Disclist;
import com.melloware.jukes.file.FileUtil;
import com.melloware.jukes.file.MusicDirectory;
import com.melloware.jukes.file.Playlist;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.gui.view.DisclistPanel;
import com.melloware.jukes.gui.view.FilterPanel;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.gui.view.PlaylistPanel;
import com.melloware.jukes.gui.view.component.AlbumImage;
import com.melloware.jukes.gui.view.dialogs.DifferenceToolDialog;
import com.melloware.jukes.gui.view.dialogs.DiscAddDialog;
import com.melloware.jukes.gui.view.dialogs.DiscFindDialog;
import com.melloware.jukes.gui.view.dialogs.DiscRemoveDialog;
import com.melloware.jukes.gui.view.dialogs.DiscTableModel;
import com.melloware.jukes.gui.view.dialogs.GenresToolDialog;
import com.melloware.jukes.gui.view.dialogs.LocationChangeDialog;
import com.melloware.jukes.gui.view.dialogs.MemoryDialog;
import com.melloware.jukes.gui.view.dialogs.SearchDialog;
import com.melloware.jukes.gui.view.dialogs.SearchTableModel;
import com.melloware.jukes.gui.view.dialogs.StatisticsDialog;
import com.melloware.jukes.gui.view.dialogs.TrackAddDialog;
import com.melloware.jukes.gui.view.dialogs.XMLExportDialog;
import com.melloware.jukes.gui.view.dialogs.XMLImportDialog;
import com.melloware.jukes.gui.view.editor.AbstractEditor;
import com.melloware.jukes.gui.view.node.AbstractTreeNode;
import com.melloware.jukes.gui.view.node.ArtistNode;
import com.melloware.jukes.gui.view.preferences.PreferencesDialog;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.JukesValidationMessage;
import com.melloware.jukes.util.MessageUtil;

/**
 * Provides all application-level behavior. Most of the methods in this class
 * will be invoked by <code>AbstractActions</code> as defined in the
 * <code>Actions</code> class.
 * <p>
 * Copyright (c) 2006 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ 2009, 2010
 */
public final class MainController {

   private static final Log LOG = LogFactory.getLog(MainController.class);
   private static final String LINE_BREAK = "\n\n";
   private static final String ERROR_WRITING_FILE = Resources.getString("label.Errorwritingfile");
   private static final String ERROR_URL = Resources.getString("label.Enterurl");

   /**
    * Refers to the module that provides all high-level models. Used to modify
    * the project and access the domain object tree.
    * @see #getMainModule()
    */
   private final MainModule mainModule;

   // Instance Creation ******************************************************

   /**
    * Constructs the <code>MainController</code> for the given main module. Many
    * methods require that the default parent frame is set once it is available.
    * @param mainModule provides bound properties and high-level models
    * @see #setDefaultParentFrame(Frame)
    */
   public MainController(final MainModule mainModule) {
      this.mainModule = mainModule;

   }

   /**
    * Backs up the database to zip file.
    * <p>
    * @param aEvent the Actionevent fired
    */
   @SuppressWarnings("deprecation")
   public void backupTool(final ActionEvent aEvent) {
      LOG.debug("Backup Database");
      if (MessageUtil.confirmBackup(getDefaultParentFrame())) {
         final File zip = MainModule.SETTINGS.getFileBackup();
         getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// AZ:
         // set
         // busy
         // cursor
         Database.backupDatabase(HibernateUtil.getSession().connection(), zip.getAbsolutePath());
         getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));// AZ:
         // switch
         // off
         // busy
         // cursor
         MessageUtil.showTaskCompleted(getDefaultParentFrame());
      }

   }

   /**
    * Checks if we shall show a tip of the day: asks the TipOfTheDayDialog
    * whether it is enabled, and the SetupManager, if we are not running for the
    * first time. We don't want to disturb the user the first time, where we
    * already have opened some extra panels from the setup process.
    * <p>
    * Opens the tip of the day dialog in the event dispatch thread.
    */
   public void checkForOpenTipOfTheDayDialog() {
      if ((SetupManager.usageCount() > 1) && (TipOfTheDayDialog.isShowingTips())) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               openTipOfTheDayDialog();
            }
         });
      }
   }

   /**
    * Opens the Directory chooser dialog.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void chooseDirectory(final ActionEvent aEvent) {
      final JComponent button = (JComponent) aEvent.getSource();
      final JTextComponent text = (JTextComponent) button.getClientProperty(Resources.TEXT_COMPONENT);
      final JDirectoryChooser chooser = new JDirectoryChooser();
      final JTextArea accessory = new JTextArea(Resources.getString("label.SelectDirectory"));
      chooser.setSelectedFile(new File(text.getText()));
      accessory.setLineWrap(true);
      accessory.setWrapStyleWord(true);
      accessory.setEditable(false);
      accessory.setOpaque(false);
      accessory.setFont(UIManager.getFont("Tree.font"));
      chooser.setAccessory(accessory);
      chooser.setMultiSelectionEnabled(false);

      final int choice = chooser.showOpenDialog(button);
      if (choice == JDirectoryChooser.APPROVE_OPTION) {
         final File dir = chooser.getSelectedFile();
         LOG.debug("Directory selected: " + dir.getAbsolutePath());
         text.setText(dir.getAbsolutePath());
      }
   }

   /**
    * Chooses a file and puts it in the text component listed.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void chooseFile(final ActionEvent aEvent) {
      final JComponent button = (JComponent) aEvent.getSource();
      final JTextComponent text = (JTextComponent) button.getClientProperty(Resources.TEXT_COMPONENT);
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle((String) button.getClientProperty(Resources.FILE_CHOOSER_TITLE));
      chooser.setFileFilter((FileFilter) button.getClientProperty(Resources.FILE_CHOOSER_FILTER));
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileHidingEnabled(true);
      final int returnVal = chooser.showOpenDialog(this.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      final File file = chooser.getSelectedFile();
      text.setText(file.getAbsolutePath());
      if (LOG.isDebugEnabled()) {
         LOG.debug(file.getAbsolutePath());

      }
   }

   /**
    * Commits the object to the database and updates tags if necessary.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void commit(final ActionEvent aEvent) {
      LOG.debug("Commit Changes");
      final JComponent button = (JComponent) aEvent.getSource();
      final AbstractEditor editor = (AbstractEditor) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.commit();
   }

   /**
    * Connects to the database and refreshes the whole application.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   @SuppressWarnings("deprecation")
   public void connect(final ActionEvent aEvent) {
      LOG.debug("Connecting to database.");
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      try {
         // first shut the database and Hibernate down.
         HibernateUtil.shutdown();
         Database.shutdown();

         // now try and connect to the database listed in the preferences
         String dbLocation = MainModule.SETTINGS.getDatabaseLocation().getAbsolutePath();
         dbLocation = dbLocation + SystemUtils.FILE_SEPARATOR + Resources.APPLICATION_LOCATION;

         // start database
         Database.startup(dbLocation, Resources.APPLICATION_LOCATION);

         // initializes Hibernate
         HibernateUtil.initialize();
         HibernateUtil.getSession().clear();

         // set the write delay on the HSQL database so writes are immediate
         Database.setWriteDelay(HibernateUtil.getSession().connection(), "FALSE");

         // now refresh the tree
         getMainModule().refreshTree();
      } catch (JDBCConnectionException ex) {
         final String errorMessage = ResourceUtils.getString("messages.NotValidConnection");
         MessageUtil.showError(mainFrame, errorMessage); // AZ
         LOG.error(errorMessage);
      } catch (Exception ex) {
         final String errorMessage = ResourceUtils.getString("messages.ErrorConnect");
         MessageUtil.showError(mainFrame, errorMessage); // AZ
         LOG.error(errorMessage, ex);
         System.exit(1);
      }
   }

   /**
    * Redirects to the www.melloware.com homepage.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void contactUs(final ActionEvent aEvent) {
      LOG.debug("Contact Us");
      try {
         if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(Resources.APPLICATION_URL));
         }
      } catch (UnsupportedOperationException ex) {
         LOG.warn(ex.getMessage() + LINE_BREAK + ERROR_URL + Resources.APPLICATION_URL);
      } catch (Exception ex) {
         LOG.warn(ex.getMessage() + LINE_BREAK + ERROR_URL + Resources.APPLICATION_URL);
      }
   }

   /**
    * Deletes the object and all its descendants but not any files they may
    * point to.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void delete(final ActionEvent aEvent) {
      LOG.debug("Delete Item");
      final JComponent source = (JComponent) aEvent.getSource();
      final Object editor = source.getClientProperty(Resources.EDITOR_COMPONENT);
      if (editor == null) {
         return;
      }

      if (editor instanceof AbstractEditor) {
         ((AbstractEditor) editor).delete();
      } else if (editor instanceof JTree) {
         final JTree tree = (JTree) editor;
         final TreePath path = tree.getSelectionPath();

         // if it's not a AbstractTreeNode, don't do anything
         if (path.getLastPathComponent() instanceof AbstractTreeNode) {
            LOG.debug("Tree Node Delete");
            ((AbstractTreeNode) path.getLastPathComponent()).delete();
         }
      }

   }

   /**
    * Displays the difference tool dialog.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void differenceTool(final ActionEvent aEvent) {
      LOG.debug("Difference Tool Dialog");
      new DifferenceToolDialog(getDefaultParentFrame(), MainModule.SETTINGS).open();

   }

   /**
    * AZ Displays the check genres tool dialog.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void genresTool(final ActionEvent aEvent) {
      LOG.debug("Genres Check Dialog");
      new GenresToolDialog(getDefaultParentFrame(), MainModule.SETTINGS).open();
   }

   /**
    * Adds a single disc to the catalog by making the user select a directory.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void discAdd(final ActionEvent aEvent) {
      LOG.debug("Add New Disc");
      final JFileChooser openDialog = new JFileChooser();
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      openDialog.setDialogTitle(Resources.getString("label.AddNewDisc"));
      final File currentDir = MainModule.SETTINGS.getStartInDirectory();
      openDialog.setCurrentDirectory(currentDir);
      openDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// AZ
      openDialog.setAcceptAllFileFilterUsed(false);// AZ

      openDialog.setMultiSelectionEnabled(false);
      openDialog.setSelectedFile(null);
      final int returnVal = openDialog.showOpenDialog(this.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      final File dir = openDialog.getSelectedFile();

      // Look for music files in selected directory
      // AZ
      final Collection files = MusicDirectory.findMusicFiles(dir);
      if (files.isEmpty()) {
         String message = Resources.getString("messages.NoMusicFilesFound");
         MessageUtil.showError(mainFrame, message);
         return;
      } else {
         File file = (File) files.toArray()[0];
         LOG.debug(file.getAbsolutePath());

         new DiscAddDialog(getDefaultParentFrame(), MainModule.SETTINGS, file).open();
      }
   }

   /**
    * AZ Adds a single track to the catalog
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void trackAdd(final ActionEvent aEvent) {
      LOG.debug("Add Single Track");
      final JFileChooser openDialog = new JFileChooser();
      openDialog.setDialogTitle(Resources.getString("label.AddNewTrack"));
      final File currentDir = MainModule.SETTINGS.getStartInDirectory();
      openDialog.setCurrentDirectory(currentDir);
      openDialog.setFileFilter(FilterFactory.musicFileFilter());
      openDialog.setMultiSelectionEnabled(false);
      openDialog.setSelectedFile(null);
      final int returnVal = openDialog.showOpenDialog(this.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      final File file = openDialog.getSelectedFile();

      LOG.debug(file.getAbsolutePath());

      new TrackAddDialog(getDefaultParentFrame(), MainModule.SETTINGS, file).open();
   }

   /**
    * AZ Search FreeDB for album
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void freeDB(final ActionEvent aEvent) {
      LOG.debug("Search FreeDB");
      final JComponent button = (JComponent) aEvent.getSource();
      final Object editor = button.getClientProperty(Resources.EDITOR_COMPONENT);
      if (editor != null) {
         if (editor instanceof AbstractEditor) {
            ((AbstractEditor) editor).freeDBSearch();
         } else if (editor instanceof DiscAddDialog) {
            ((DiscAddDialog) editor).freeDBSearch();
         }
      }
   }

   /**
    * Updates all of the comments at once.
    * <p>
    * @param aEvent the ActionEvent fired
    */
   public void discAddComments(final ActionEvent aEvent) {
      LOG.debug("Update comments all at once.");
      final JComponent button = (JComponent) aEvent.getSource();
      final Object editor = button.getClientProperty(Resources.EDITOR_COMPONENT);
      if ((editor != null) && (editor instanceof DiscAddDialog)) {
         ((DiscAddDialog) editor).updateComments();
      }
   }

   /**
    * Resets all tracks titles based on filename.
    * <p>
    * @param aEvent the ActionEvent fired
    */
   public void discAddResetFromFilename(final ActionEvent aEvent) {
      LOG.debug("Reset Titles from filenames.");
      final JComponent button = (JComponent) aEvent.getSource();
      final Object editor = button.getClientProperty(Resources.EDITOR_COMPONENT);
      if ((editor != null) && (editor instanceof DiscAddDialog)) {
         ((DiscAddDialog) editor).resetFromFilenames();
      }
   }

   /**
    * Resets all track numbers in a disc if they are really screwed up.
    * <p>
    * @param aEvent the ActionEvent fired
    */
   public void discAddResetTrackNumbers(final ActionEvent aEvent) {
      LOG.debug("Reset Track Numbers.");
      final JComponent button = (JComponent) aEvent.getSource();
      final Object editor = button.getClientProperty(Resources.EDITOR_COMPONENT);
      if ((editor != null) && (editor instanceof DiscAddDialog)) {
         ((DiscAddDialog) editor).resetTrackNumbers();
      }
   }

   /**
    * Title cases all tracks in a disc.
    * <p>
    * @param aEvent the ActionEvent fired
    */
   public void discAddTitleCase(final ActionEvent aEvent) {
      LOG.debug("Title case all tracks.");
      final JComponent button = (JComponent) aEvent.getSource();
      final Object editor = button.getClientProperty(Resources.EDITOR_COMPONENT);
      if ((editor != null) && (editor instanceof DiscAddDialog)) {
         ((DiscAddDialog) editor).titleCase();
      }
   }

   /**
    * Uses a file chooser to let the user select another cover image.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void discCoverImage(final ActionEvent aEvent) {
      LOG.debug("Finding new cover image");
      final JComponent button = (JComponent) aEvent.getSource();

      final Object editor = button.getClientProperty(Resources.EDITOR_COMPONENT);
      LOG.debug(editor);
      if (editor != null) {
         if (editor instanceof AbstractEditor) {
            ((AbstractEditor) editor).findCover();
         } else if (editor instanceof DiscAddDialog) {
            ((DiscAddDialog) editor).findCover();
         }
      }
   }

   /**
    * Opens the Disc Finder dialog.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void discFinder(final ActionEvent aEvent) {
      LOG.debug("Disc Finder Dialog");
      new DiscFindDialog(getDefaultParentFrame(), MainModule.SETTINGS).open();
   }

   /**
    * Opens the Disc Remover dialog.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void discRemover(final ActionEvent aEvent) {
      LOG.debug("Disc Remover Dialog");
      new DiscRemoveDialog(getDefaultParentFrame()).open();
   }

   /**
    * Uses the Amazon.com web service to find album information and covers.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void discWebSearch(final ActionEvent aEvent) {
      LOG.debug("Web Search");
      final JComponent button = (JComponent) aEvent.getSource();
      final Object editor = button.getClientProperty(Resources.EDITOR_COMPONENT);
      if (editor != null) {
         if (editor instanceof AbstractEditor) {
            ((AbstractEditor) editor).webSearch();
         } else if (editor instanceof DiscAddDialog) {
            ((DiscAddDialog) editor).webSearch();
         }
      }
   }

   /**
    * Launches a browser and send to the PayPal website
    */
   public void donate() {
      try {
         if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URL(Resources.APPLICATION_DONATE_URL).toURI());
         }
      } catch (UnsupportedOperationException ex) {
         LOG.warn(ex.getMessage() + LINE_BREAK + ERROR_URL + Resources.APPLICATION_DONATE_URL);
      } catch (Exception ex) {
         LOG.warn(ex.getMessage() + LINE_BREAK + ERROR_URL + Resources.APPLICATION_DONATE_URL);
      }
   }

   /**
    * Exports a catalog to a text file the user selects with a chooser.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   @SuppressWarnings("unchecked")
   public void exportCatalog(final ActionEvent aEvent) {
      LOG.debug("Export Catalog");
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(Resources.getString("label.ExportCatalog"));
      chooser.setFileFilter(FilterFactory.textFileFilter());
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileHidingEnabled(true);
      final int returnVal = chooser.showSaveDialog(this.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      File file = chooser.getSelectedFile();
      if (LOG.isDebugEnabled()) {
         LOG.debug(file.getAbsolutePath());
      }
      // add the extension if missing
      file = FilterFactory.forceTextExtension(file);

      try {
         // now query the catalog and save the results to the file
         final ArrayList results = new ArrayList();
         final Collection discs = HibernateDao.findByQuery(Resources.getString("hql.export.catalog"));

         for (final Iterator iter = discs.iterator(); iter.hasNext();) {
            final Object[] disc = (Object[]) iter.next();
            results.add(disc[0] + Resources.TAB + disc[1] + Resources.TAB + disc[2] + Resources.TAB + disc[3]
                     + Resources.TAB + disc[4] + Resources.TAB + disc[5]);
         }
         FileUtils.writeLines(file, "UTF-8", results);// AZ UTF-8

         MessageUtil.showInformation(getDefaultParentFrame(), Resources
                  .getString("messages.Catalogexportedsuccessfully"));
      } catch (IOException ex) {
         MessageUtil.showError(mainFrame, ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage()); // AZ
         LOG.error(ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage(), ex);
      } catch (InfrastructureException ex) {
         MessageUtil.showError(mainFrame, "Infrastructure Exception: " + LINE_BREAK + ex.getMessage()); // AZ
         LOG.error(ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage(), ex);
      } catch (Exception ex) {
         MessageUtil.showError(mainFrame, ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage()); // AZ
         LOG.error("Unexpected error writing file.", ex);
      }
   }

   /**
    * Renames the file to track number - title.mp3.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void fileRename(final ActionEvent aEvent) {
      LOG.debug("Renaming File");
      final JComponent button = (JComponent) aEvent.getSource();
      final Object editor = button.getClientProperty(Resources.EDITOR_COMPONENT);
      if (editor != null) {
         if (editor instanceof AbstractEditor) {
            ((AbstractEditor) editor).renameFiles();
         } else if (editor instanceof DiscAddDialog) {
            ((DiscAddDialog) editor).renameFiles();
         }
      }
   }

   /**
    * Applies the current filter to the tree.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void filter(final ActionEvent aEvent) {
      LOG.debug("Filter applied.");
      final JToggleButton button = (JToggleButton) aEvent.getSource();
      final FilterPanel editor = (FilterPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      if (button.isSelected()) {
         LOG.debug("Button Selected");
         editor.applyFilter();
      } else {
         LOG.debug("Button Deselected");
         editor.removeFilter();
      }
   }

   /**
    * Clears the current filter.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void filterClear(final ActionEvent aEvent) {
      LOG.debug("Filter cleared.");
      final JComponent button = (JComponent) aEvent.getSource();
      final FilterPanel editor = (FilterPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.clearFilter();

   }

   /**
    * Closes the filter window.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void filterClose(final ActionEvent aEvent) {
      LOG.debug("Close Filter.");
      getMainFrame().getMainPageBuilder().setFilterVisible(false);
   }

   /**
    * Displays or hides the filter window.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void filterDisplay(final ActionEvent aEvent) {
      LOG.debug("Filter displayed/hidden.");
      final boolean visible = !getMainFrame().getMainPageBuilder().isFilterVisible();
      getMainFrame().getMainPageBuilder().setFilterVisible(visible);
   }

   /**
    * Launches the browser to the forums website.
    */
   public void forums() {
      try {
         if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(Resources.APPLICATION_FORUMS_URL));
         }
      } catch (UnsupportedOperationException ex) {
         LOG.warn(ex.getMessage() + LINE_BREAK + ERROR_URL + Resources.APPLICATION_FORUMS_URL);
      } catch (Exception ex) {
         LOG.warn(ex.getMessage() + LINE_BREAK + ERROR_URL + Resources.APPLICATION_FORUMS_URL);
      }
   }

   /**
    * Hides the main window.
    */
   public void hideMainWindow() {
      if (SystemUtils.IS_OS_WINDOWS) {
         final MainFrame mainframe = (MainFrame) getDefaultParentFrame();
         mainframe.getTrayIcon().hideWindow();
      }
   }

   /**
    * Updates the language of the application to aLanguage.
    * <p>
    * @param aEvent the ActionEvent fired
    * @param aLanguage the language to change to
    */
   public void language(final ActionEvent aEvent, final String aLanguage) {
      LOG.debug("Language change to " + aLanguage);
      // MessageUtil.showwarn(getDefaultParentFrame(), "Language support not
      // implemented yet");
      MainModule.SETTINGS.setLocale(aLanguage);
      getMainModule().storeState();
      MessageUtil.showInformation(getDefaultParentFrame(), Resources.getString("messages.update.language"));
   }

   /**
    * Opens the global location change tool.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void locationTool(final ActionEvent aEvent) {
      LOG.debug("Location Tool Dialog");
      new LocationChangeDialog(getDefaultParentFrame(), MainModule.SETTINGS).open();
   }

   /**
    * Displays the memory usage and provide garbage collection.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void memory(final ActionEvent aEvent) {
      LOG.debug("Memory Dialog");
      new MemoryDialog(getDefaultParentFrame()).open();

   }

   /**
    * Plays the next track in the playlist.
    * <p>
    * @param aEvent the actionevent fired
    */
   public void playerNext(final ActionEvent aEvent) {
      LOG.debug("Player Next");
      final Runnable update = new Runnable() {
         public void run() {
            final Playlist playlist = getMainFrame().getPlaylist();
            if (playlist.hasNext()) {
               final Track track = (Track) playlist.getNext();
               if (track.isValid()) {
                  getMainFrame().getPlayer().play(track.getTrackUrl());
               }

            }
         }
      };
      EventQueue.invokeLater(update);
   }

   /**
    * Pauses the media player.
    * <p>
    * @param aEvent the actionevent fired
    */
   public void playerPause(final ActionEvent aEvent) {
      LOG.debug("Player Pause/Resume");
      final Runnable update = new Runnable() {
         public void run() {
            final MainFrame mainFrame = (MainFrame) getDefaultParentFrame();
            mainFrame.getPlayer().pause();
         }
      };
      EventQueue.invokeLater(update);
   }

   /**
    * Plays the media player.
    * <p>
    * @param aEvent the actionevent fired
    */
   public void playerPlay(final ActionEvent aEvent) {
      LOG.debug("Player Play");
      final Runnable update = new Runnable() {
         public void run() {
            final MainFrame mainFrame = (MainFrame) getDefaultParentFrame();
            LOG.debug("Status: " + mainFrame.getPlayer().getStatus());
            if (mainFrame.getPlayer().getStatus() == -1) {
               playerNext(null);
            } else {
               mainFrame.getPlayer().play();
            }
         }
      };
      EventQueue.invokeLater(update);

   }

   /**
    * Plays the previous track in the playlist.
    * <p>
    * @param aEvent the actionevent fired
    */
   public void playerPrevious(final ActionEvent aEvent) {
      LOG.debug("Player Previous");
      final Runnable update = new Runnable() {
         public void run() {
            final Playlist playlist = getMainFrame().getPlaylist();
            if (playlist.hasPrevious()) {
               final Track trackCurrent = (Track) playlist.getPrevious();

               // if elapsed time is greater than 2 then replay this song
               if (getMainFrame().getPlayer().getElapsedTime() > 2000) {
                  LOG.debug("Player Previous: REPLAY current song");
                  playerNext(null);
               } else {
                  final Track trackPrevious = (Track) playlist.getPrevious();
                  if ((trackPrevious != null) && (trackPrevious.isValid())) {
                     LOG.debug("Player Previous: PLAY previous song in playlist");
                     playerNext(null);
                  } else if ((trackCurrent != null) && (trackCurrent.isValid())) {
                     playerNext(null);
                  }
               }
            }
         }
      };
      EventQueue.invokeLater(update);

   }

   /**
    * Stops the media player.
    * <p>
    * @param aEvent the actionevent fired
    */
   public void playerStop(final ActionEvent aEvent) {
      LOG.debug("Player Stop");
      final Runnable update = new Runnable() {
         public void run() {
            MainFrame mainFrame = (MainFrame) getDefaultParentFrame();
            mainFrame.getPlayer().stop();
         }
      };
      EventQueue.invokeLater(update);
   }

   /**
    * Plays the selected track immediately.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void playImmediately(final ActionEvent aEvent) {
      LOG.debug("Play Immediately");
      final JComponent source = (JComponent) aEvent.getSource();
      final Object editor = source.getClientProperty(Resources.EDITOR_COMPONENT);
      final Runnable update = new Runnable() {
         public void run() {

            if (editor != null) {
               final Player player = getMainFrame().getPlayer();
               final Playlist playlist = getMainFrame().getPlaylist();

               if (editor instanceof JTree) {
                  final JTree tree = (JTree) editor;
                  for (int i = 0; i < tree.getSelectionPaths().length; i++) {
                     final TreePath path = tree.getSelectionPaths()[i];
                     if (path.getLastPathComponent() instanceof AbstractTreeNode) {
                        final AbstractTreeNode node = (AbstractTreeNode) path.getLastPathComponent();
                        // AZ look for filtered discs
                        if (node instanceof ArtistNode) {
                           final int nodeCount = node.getChildCount();
                           if (nodeCount > 0) {
                              for (int ii = 0; ii < node.getChildCount(); ii++) {
                                 final AbstractTreeNode childNode = (AbstractTreeNode) node.getChildAt(ii);
                                 playlist.addNext(childNode.getModel());
                              }
                           }
                        } else {
                           playlist.addNext(node.getModel());
                        }
                     }
                  }
               }

               if (editor instanceof JList) {
                  final JList list = (JList) editor;
                  final Object[] selections = list.getSelectedValues();
                  for (int i = selections.length - 1; i >= 0; i--) {
                     Track track = null;
                     if (selections[i] instanceof JukesValidationMessage) {
                        final JukesValidationMessage message = (JukesValidationMessage) selections[i];
                        track = (Track) message.getDomainObject();
                        playlist.addNext(track);
                     } else if (selections[i] instanceof Track) {
                        track = (Track) selections[i];
                        playlist.addNext(track);
                     }
                  }
               }
               if (editor instanceof JTable) {
                  final JTable table = (JTable) editor;
                  final int[] selections = table.getSelectedRows();
                  final SearchTableModel model = (SearchTableModel) table.getModel();
                  for (int i = 0; i < selections.length; i++) {
                     int selectedRow = selections[i];
                     selectedRow = table.getRowSorter().convertRowIndexToModel(selectedRow);
                     Track track = (Track) model.getData()[selectedRow];
                     playlist.addNext(track);
                  }
               }
               if (editor instanceof AlbumImage) {
                  final AlbumImage image = (AlbumImage) editor;
                  if (image.getDisc() != null) {
                     playlist.addNext(image.getDisc());
                  }
               }
               final Track next = (Track) playlist.getNextImmediate();
               if (next != null) {
                  player.play(next.getTrackUrl());
               }

            }
         }
      };
      EventQueue.invokeLater(update);
   }

   /**
    * Closes the playlist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void playlistClose(final ActionEvent aEvent) {
      LOG.debug("Close Playlist.");
      getMainFrame().getMainPageBuilder().setPlaylistVisible(false);

   }

   /**
    * Shows or hides the playlist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void playlistDisplay(final ActionEvent aEvent) {
      LOG.debug("Playlist displayed/hidden.");
      String currentPanel = getMainFrame().getMainPageBuilder().panelVisible();
      boolean visible;
      if (currentPanel == "playlistPanel") {
         visible = false;
      } else {
         visible = true;
      }
      getMainFrame().getMainPageBuilder().setPlaylistVisible(visible);

   }

   /**
    * Go to the track in the navigator.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void playlistGoto(final ActionEvent aEvent) {
      LOG.debug("Playlist goto.");
      final Track selection;
      final JComponent button = (JComponent) aEvent.getSource();
      final JList editor = (JList) button.getClientProperty(Resources.EDITOR_COMPONENT);
      try {
         GuiUtil.setBusyCursor(getDefaultParentFrame(), true);
         if (editor.getSelectedValue() != null) {
            // AZ - ensure the availability of tree node
            selection = (Track) editor.getSelectedValue();
            String query = " and upper(disc.name) = '"
                     + selection.getDisc().getName().toUpperCase().replaceAll("'", "''") + "'";
            String filter = MainModule.SETTINGS.getFilter();
            if (!(MainModule.SETTINGS.isShowDefaultTree() && !StringUtils.isNotBlank(filter))) {
               filter = query;
            }
            MainModule.SETTINGS.setFilter(filter);
            getMainModule().refreshTree();
            getMainModule().selectNodeInTree(editor.getSelectedValue());
         } else {
            MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils
                     .getString("messages.SelectSomethingToGoTo"));
         }
      } finally {
         GuiUtil.setBusyCursor(getDefaultParentFrame(), false);
      }
   }

   /**
    * Loads a playlist from a file.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void playlistLoad(final ActionEvent aEvent) {
      LOG.debug("Load Playlist");
      final JComponent button = (JComponent) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.load();
   }

   /**
    * Moves a track down on the playlist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void playlistMoveDown(final ActionEvent aEvent) {
      LOG.debug("Move down playlist");
      final JComponent button = (JComponent) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.moveDown();

   }

   /**
    * Moves the selected tracks over to the other list.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void playlistMoveOver(final ActionEvent aEvent) {
      LOG.debug("Move over playlist");
      final JComponent button = (JComponent) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.moveOver();

   }

   /**
    * Moves a track up on the playlist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void playlistMoveUp(final ActionEvent aEvent) {
      LOG.debug("Move up playlist");
      final JComponent button = (JComponent) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.moveUp();
   }

   /**
    * Removes tracks from the playlist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void playlistRemoveTracks(final ActionEvent aEvent) {
      LOG.debug("Remove tracks from playlist");
      final JComponent button = (JComponent) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.removeTracks();
      editor.repaint(); // AZ repaint PlaylistPanel
      // getMainFrame().getMainPageBuilder().refreshUI();
   }

   /**
    * Clears all tracks from the playlist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void playlistClear(ActionEvent aEvent) {
      LOG.debug("Clear tracks from playlist");
      final JComponent button = (JComponent) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.removeAllTracks();
      editor.repaint(); // AZ repaint PlaylistPanel
      // getMainFrame().getMainPageBuilder().refreshUI();

   }

   /**
    * Saves a playlist to disk.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void playlistSave(final ActionEvent aEvent) {
      LOG.debug("Save playlist.");
      final JComponent button = (JComponent) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.save();
   }

   /**
    * Toggles between shuffling the catalog or not.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void playlistShuffleCatalog(final ActionEvent aEvent) {
      LOG.debug("Shuffle catalog.");
      final JToggleButton button = (JToggleButton) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.shuffleCatalog(button.isSelected());

      if (button.isSelected()) {
         final Player player = getMainFrame().getPlayer();
         // if the player was stopped then play the next song
         if ((player.getStatus() == BasicPlayer.STOPPED) || (player.getStatus() == BasicPlayer.UNKNOWN)) {
            playerNext(null);
         }
      }
   }

   /**
    * Toggles between shuffling the playlist or not.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void playlistShuffleList(final ActionEvent aEvent) {
      LOG.debug("Shuffle playlist.");
      final JToggleButton button = (JToggleButton) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.shufflePlaylist(button.isSelected());
   }

   /**
    * Toggles between history and current playlist.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void playlistToggle(final ActionEvent aEvent) {
      LOG.debug("Toggle playlist.");
      final JToggleButton button = (JToggleButton) aEvent.getSource();
      final PlaylistPanel editor = (PlaylistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.toggle(!button.isSelected());
   }

   /**
    * Export preferences to an XML file.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void preferencesExport(final ActionEvent aEvent) {
      LOG.debug("Export Preferences");
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(Resources.getString("label.ExportPreferences"));
      chooser.setFileFilter(FilterFactory.xmlFileFilter());
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileHidingEnabled(true);
      final int returnVal = chooser.showSaveDialog(this.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      File file = chooser.getSelectedFile();
      if (LOG.isDebugEnabled()) {
         LOG.debug(file.getAbsolutePath());
      }

      // add the extension if missing
      file = FilterFactory.forceXmlExtension(file);

      // now try and save the prefernces to a file
      try {
         final Preferences prefs = Application.getUserPreferences();
         final FileOutputStream stream = new FileOutputStream(file);
         prefs.exportSubtree(stream);

         MessageUtil.showInformation(getDefaultParentFrame(), Resources
                  .getString("messages.Preferencesexportedsuccessfully"));
      } catch (IOException ex) {
         MessageUtil.showError(mainFrame, ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage()); // AZ
         LOG.error(ERROR_WRITING_FILE + LINE_BREAK + ex, ex);
      } catch (Exception ex) {
         MessageUtil.showError(mainFrame, ERROR_WRITING_FILE); // AZ
         LOG.error("Unexpected error writing file.", ex);
      }
   }

   /**
    * Import preferences from an XML file.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void preferencesImport(final ActionEvent aEvent) {
      LOG.debug("Import Preferences");
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(Resources.getString("label.ImportPreferences"));
      chooser.setFileFilter(FilterFactory.xmlFileFilter());
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileHidingEnabled(true);
      final int returnVal = chooser.showOpenDialog(this.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      final File file = chooser.getSelectedFile();
      if (LOG.isDebugEnabled()) {
         LOG.debug(file.getAbsolutePath());
      }

      // now try and import the preferences from a file
      try {
         final FileInputStream stream = new FileInputStream(file);
         Preferences.importPreferences(stream);

         MainModule.SETTINGS.restoreFrom(Application.getUserPreferences());

         MessageUtil.showInformation(getDefaultParentFrame(), Resources
                  .getString("messages.Preferencesimportedsuccessfully"));
      } catch (IOException ex) {
         MessageUtil.showError(mainFrame, ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage()); // AZ
         LOG.error(ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage(), ex);
      } catch (Exception ex) {
         MessageUtil.showError(mainFrame, ERROR_WRITING_FILE); // AZ
         LOG.error("Unexpected error writing file.", ex);
      }
   }

   /**
    * Adds track(s) to the bottom of the queue.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void queue(final ActionEvent aEvent) {
      LOG.debug("Queue");
      final JComponent source = (JComponent) aEvent.getSource();
      final Component editor = (Component) source.getClientProperty(Resources.EDITOR_COMPONENT);
      if (editor != null) {
         final Playlist playlist = getMainFrame().getPlaylist();
         // check if the playlist is empty. If it is then set a flag to
         // start playing the next song immediately
         // final boolean startPlaying = (playlist.sizeNext() == 0);

         if (editor instanceof JTree) {
            final JTree tree = (JTree) editor;
            for (int i = 0; i < tree.getSelectionPaths().length; i++) {
               final TreePath path = tree.getSelectionPaths()[i];
               if (path.getLastPathComponent() instanceof AbstractTreeNode) {
                  final AbstractTreeNode node = (AbstractTreeNode) path.getLastPathComponent();
                  // AZ look for filtered discs
                  if (node instanceof ArtistNode) {
                     final int nodeCount = node.getChildCount();
                     if (nodeCount > 0) {
                        for (int ii = 0; ii < node.getChildCount(); ii++) {
                           final AbstractTreeNode childNode = (AbstractTreeNode) node.getChildAt(ii);
                           playlist.add(childNode.getModel());
                        }
                     }
                  } else {
                     playlist.add(node.getModel());
                  }
               }
            }
         }

         if (editor instanceof JList) {
            final JList list = (JList) editor;
            final Object[] selections = list.getSelectedValues();
            for (int i = 0; i < selections.length; i++) {
               final JukesValidationMessage message = (JukesValidationMessage) selections[i];
               playlist.add(message.getDomainObject());
            }
         }

         if (editor instanceof JTable) {
            final JTable table = (JTable) editor;
            final int[] selections = table.getSelectedRows();
            if (table.getModel() instanceof DiscTableModel) { // AZ - processing
               // of
               // DiscTableModel
               // added
               final DiscTableModel model = (DiscTableModel) table.getModel();
               for (int i = 0; i < selections.length; i++) {
                  int selectedRow = selections[i];
                  selectedRow = table.getRowSorter().convertRowIndexToModel(selectedRow);
                  playlist.add(model.getData()[selectedRow]);
               }
            } else {
               final SearchTableModel model = (SearchTableModel) table.getModel();
               for (int i = 0; i < selections.length; i++) {
                  int selectedRow = selections[i];
                  selectedRow = table.getRowSorter().convertRowIndexToModel(selectedRow);
                  playlist.add(model.getData()[selectedRow]);
               }
            }
         }
         if (editor instanceof AlbumImage) {
            final AlbumImage image = (AlbumImage) editor;
            if (image.getDisc() != null) {
               playlist.add(image.getDisc());
            }
         }

         getMainFrame().getMainPageBuilder().refreshUI();
      }
   }

   /**
    * Adds track(s) to the top of the queue.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   /**
    * @param aEvent
    */
   /**
    * @param aEvent
    */
   public void queueNext(final ActionEvent aEvent) {
      LOG.debug("Queue Next");
      final JComponent source = (JComponent) aEvent.getSource();
      final Component editor = (Component) source.getClientProperty(Resources.EDITOR_COMPONENT);
      if (editor != null) {
         final Playlist playlist = getMainFrame().getPlaylist();

         if (editor instanceof JTree) {
            final JTree tree = (JTree) editor;
            for (int i = tree.getSelectionPaths().length - 1; i >= 0; i--) {
               final TreePath path = tree.getSelectionPaths()[i];
               if (path.getLastPathComponent() instanceof AbstractTreeNode) {
                  final AbstractTreeNode node = (AbstractTreeNode) path.getLastPathComponent();
                  // AZ look for filtered discs
                  if (node instanceof ArtistNode) {
                     final int nodeCount = node.getChildCount();
                     if (nodeCount > 0) {
                        for (int ii = 0; ii < node.getChildCount(); ii++) {
                           final AbstractTreeNode childNode = (AbstractTreeNode) node.getChildAt(ii);
                           playlist.addNext(childNode.getModel());
                        }
                     }
                  } else {
                     playlist.addNext(node.getModel());
                  }
               }
            }
         }

         if (editor instanceof JList) {
            final JList list = (JList) editor;
            final Object[] selections = list.getSelectedValues();
            for (int i = selections.length - 1; i >= 0; i--) {
               final JukesValidationMessage message = (JukesValidationMessage) selections[i];
               playlist.addNext(message.getDomainObject());
            }
         }

         if (editor instanceof JTable) {
            final JTable table = (JTable) editor;
            final int[] selections = table.getSelectedRows();
            if (table.getModel() instanceof DiscTableModel) { // AZ - processing
               // of
               // DiscTableModel
               // added
               final DiscTableModel model = (DiscTableModel) table.getModel();
               for (int i = 0; i < selections.length; i++) {
                  int selectedRow = selections[i];
                  selectedRow = table.getRowSorter().convertRowIndexToModel(selectedRow);
                  playlist.addNext(model.getData()[selectedRow]);
               }
            } else {
               final SearchTableModel model = (SearchTableModel) table.getModel();
               for (int i = 0; i < selections.length; i++) {
                  int selectedRow = selections[i];
                  selectedRow = table.getRowSorter().convertRowIndexToModel(selectedRow);
                  playlist.addNext(model.getData()[selectedRow]);
               }
            }
         }

         if (editor instanceof AlbumImage) {
            final AlbumImage image = (AlbumImage) editor;
            if (image.getDisc() != null) {
               playlist.addNext(image.getDisc());
            }
         }

         getMainFrame().getMainPageBuilder().refreshUI();
      }
   }

   /**
    * Refreshes the data from the database and reloads the tree
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void refresh(final ActionEvent aEvent) {
      LOG.debug("Refreshing Data.");
      GuiUtil.setBusyCursor(getDefaultParentFrame(), true);
      // completely evict the session and clear all loaded objects.
      // HibernateUtil.getSession().clear();

      // refresh the tree
      getMainModule().refreshTree();
      GuiUtil.setBusyCursor(getDefaultParentFrame(), false);
   }

   /**
    * Rolls changes back to orginal form.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void rollback(final ActionEvent aEvent) {
      LOG.debug("Rollback Changes");
      final JComponent button = (JComponent) aEvent.getSource();
      final AbstractEditor editor = (AbstractEditor) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.rollback();
   }

   /**
    * Searches the database.
    * <p>
    * @param aEvent the actionevent fired
    */
   public void search(final ActionEvent aEvent) {
      LOG.debug("Search");
      final SearchDialog dialog = new SearchDialog(getDefaultParentFrame(), MainModule.SETTINGS);
      dialog.open();

      if (!dialog.hasBeenCanceled()) {
         try {
            GuiUtil.setBusyCursor(getDefaultParentFrame(), true);
            getMainModule().selectNodeInTree(dialog.getSelection());
         } finally {
            GuiUtil.setBusyCursor(getDefaultParentFrame(), false);
         }
      }

   }

   /**
    * Shows the main window.
    */
   public void showMainWindow() {
      if (SystemUtils.IS_OS_WINDOWS) {
         final MainFrame mainframe = (MainFrame) getDefaultParentFrame();
         mainframe.getTrayIcon().showWindow();
      }
   }

   /**
    * Displays the statistics dialog.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void statistics(final ActionEvent aEvent) {
      LOG.debug("Statistics Dialog");
      final MainFrame mainframe = (MainFrame) getDefaultParentFrame();

      GuiUtil.setBusyCursor(mainframe, true);// AZ
      new StatisticsDialog(getDefaultParentFrame()).open();
      GuiUtil.setBusyCursor(mainframe, false);// AZ
   }

   /**
    * Converts the associated TextComponent to title case.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void titleCase(final ActionEvent aEvent) {
      final JComponent button = (JComponent) aEvent.getSource();
      final JTextComponent text = (JTextComponent) button.getClientProperty(Resources.TEXT_COMPONENT);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Capitalizing '" + text.getText() + "'");
      }
      text.setText(FileUtil.capitalize(text.getText()));
   }

   /**
    * Unlocks the current editor for editing.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void unlock(final ActionEvent aEvent) {
      LOG.debug("Unlock item for editing");
      final JComponent button = (JComponent) aEvent.getSource();
      final AbstractEditor editor = (AbstractEditor) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.unlock();
   }

   /**
    * Displays a report with all albums without cover artwork.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void reportNoCoverArt(ActionEvent aEvent) {
      LOG.debug("Report: Albums Without Cover Artwork");
      runReport("/reports/nocoverart.jasper", new HashMap());
   }

   /**
    * Displays a report with the entire catalog in alphabetical order.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void reportCatalog(ActionEvent aEvent) {
      LOG.debug("Report: Catalog");
      runReport("/reports/catalog.jasper", new HashMap());
   }

   /**
    * AZ Displays a report to display all albums for specified artist.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void reportAlbumsForArtist(ActionEvent aEvent) {
      LOG.debug("Report: Albums for Artist");

      final HashMap<String, String> map = new HashMap<String, String>();
      // get list of all artists sorted by artist name
      GuiUtil.setBusyCursor(getDefaultParentFrame(), true);
      final String hql = ResourceUtils.getString("hql.artist.all.sorted");
      final List artistsList = HibernateDao.findByQuery(hql);
      final List artistNames = new ArrayList();
      Artist artist;
      String artistName;
      final Object[] artists;
      for (int i = 0; i < artistsList.size(); i++) {
         artist = (Artist) artistsList.get(i);
         artistName = artist.getName();
         if (artistName.length() > 40) {
            artistName = artistName.substring(0, 40);
         }
         artistNames.add(artistName);
      }
      artists = artistNames.toArray();
      JComboBox artistField = new JComboBox(artists);
      JPanel panel = new JPanel();
      panel.add(new JLabel(Resources.getString("label.SelectArtistName")));
      panel.add(artistField);
      Integer response = JOptionPane.showConfirmDialog(null, panel, Resources.getString("label.AlbumsforArtistReport"),
               JOptionPane.OK_CANCEL_OPTION);
      final String selectedArtistName = artistField.getSelectedItem().toString();
      GuiUtil.setBusyCursor(getDefaultParentFrame(), false);
      if (response == 0) {
         if (StringUtils.isNotEmpty(selectedArtistName)) {
            map.put("selectedartist", selectedArtistName);
            runReport("/reports/albumsforartist.jasper", map);
         }
      }
   }

   /**
    * AZ Displays a report with the entire catalog in alphabetical order of
    * genres.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void reportCatalogByGenres(ActionEvent aEvent) {
      LOG.debug("Report: Catalog By Genres");
      runReport("/reports/catalogbygenres.jasper", new HashMap());
   }

   /**
    * Displays a report with the list of discs in the range of bitrates.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void reportBitrate(ActionEvent aEvent) {
      LOG.debug("Report: Bitrate");
      final HashMap<String, Integer> map = new HashMap<String, Integer>();
      JComboBox bitrateFromField = new JComboBox(Resources.BITRATES);
      JComboBox bitrateToField = new JComboBox(Resources.BITRATES);

      JPanel panel = new JPanel();
      panel.add(new JLabel(Resources.getString("label.bitrateFrom")));
      panel.add(bitrateFromField);
      panel.add(new JLabel(Resources.getString("label.bitrateTo")));
      panel.add(bitrateToField);
      GridLayout grid = new GridLayout(); // Create a layout manager
      grid.setVgap(10);
      grid.setColumns(2);
      grid.setRows(2);
      panel.setLayout(grid);

      Integer response = JOptionPane.showConfirmDialog(null, panel, Resources.getString("label.BitrateReport"),
               JOptionPane.OK_CANCEL_OPTION);
      if (response == 0) {
         int bitrateFrom = Integer.parseInt(bitrateFromField.getSelectedItem().toString());
         int bitrateTo = Integer.parseInt(bitrateToField.getSelectedItem().toString());
         if (bitrateFrom > bitrateTo) {
            MessageUtil.showError(null, Resources.getString("messages.BitrateError"));
         } else {
            map.put("bitratefrom", bitrateFrom);
            map.put("bitrateto", bitrateTo);
            runReport("/reports/bitrate.jasper", map);
         }
      }
   }

   /**
    * AZ Displays a report to display all genres used.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void reportGenres(ActionEvent aEvent) {
      LOG.debug("Report: Genres");
      final HashMap<String, Integer> map = new HashMap<String, Integer>();
      runReport("/reports/genres.jasper", map);
   }

   /**
    * Invokes the application shutdown mechanism. Currently it uses the poor
    * assumption that the default parent frame is an instance of
    * <code>AbstractMainFrame</code>.
    * <p>
    */
   void aboutToExitApplication() {
      ((MainFrame) getDefaultParentFrame()).aboutToExitApplication();
   }

   /**
    * Opens the about dialog.
    */
   void helpAbout() {
      new DefaultAboutDialog(getDefaultParentFrame()).open();
   }

   /**
    * Opens the tip-of-the-day dialog.
    */
   void openTipOfTheDayDialog() {
      final String tipIndexPath = Application.getConfiguration().getTipIndexPath();
      new TipOfTheDayDialog(getDefaultParentFrame(), tipIndexPath).open();
   }

   /**
    * Opens the preferences dialog.
    */
   void preferences() {
      new PreferencesDialog(getDefaultParentFrame(), MainModule.SETTINGS).open();
   }

   /**
    * Gets the default parent frame of the application.
    * <p>
    * @return the default parent frame
    */
   private Frame getDefaultParentFrame() {
      return Application.getDefaultParentFrame();
   }

   /**
    * Gets the main frame of the application.
    * <p>
    * @return the main frame of the application
    */
   private MainFrame getMainFrame() {
      return (MainFrame) getDefaultParentFrame();
   }

   /**
    * Gets the MainModule of the application.
    * <p>
    * @return the MainModule of the application
    */
   private MainModule getMainModule() {
      return mainModule;
   }

   /**
    * Runs a Jasper Report that should be found in the classpath.
    * <p>
    * @param aReport the location on the classpath to find the report
    * @param aParameters the hashmap of parameters
    */
   @SuppressWarnings("deprecation")
   private void runReport(String aReport, HashMap aParameters) {
      try {
         GuiUtil.setBusyCursor(getDefaultParentFrame(), true);
         final JasperPrint print = JasperFillManager.fillReport(MainController.class.getResourceAsStream(aReport),
                  aParameters, HibernateUtil.getSession().connection());
         GuiUtil.setBusyCursor(getDefaultParentFrame(), false);
         JasperViewer.viewReport(print, false);
      } catch (HibernateException ex) {
         LOG.error("HibernateException", ex);
      } catch (InfrastructureException ex) {
         LOG.error("InfrastructureException", ex);
      } catch (JRException ex) {
         LOG.error("JRException", ex);
      }
   }

   /**
    * Closes the disclist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void disclistClose(final ActionEvent aEvent) {
      LOG.debug("Close Disclist.");
      getMainFrame().getMainPageBuilder().setDisclistVisible(false);
   }

   /**
    * Go to the disc in the navigator.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void disclistGoto(final ActionEvent aEvent) {
      LOG.debug("Disclist goto.");
      final Disc selection;
      final JComponent button = (JComponent) aEvent.getSource();
      final JList editor = (JList) button.getClientProperty(Resources.EDITOR_COMPONENT);
      try {
         GuiUtil.setBusyCursor(getDefaultParentFrame(), true);
         if (editor.getSelectedValue() != null) {
            // AZ - ensure the availability of tree node
            selection = (Disc) editor.getSelectedValue();
            String query = " and upper(disc.name) = '" + selection.getName().toUpperCase().replaceAll("'", "''") + "'";
            String filter = MainModule.SETTINGS.getFilter();
            if (!(MainModule.SETTINGS.isShowDefaultTree() && !StringUtils.isNotBlank(filter))) {
               filter = query;
            }
            MainModule.SETTINGS.setFilter(filter);
            getMainModule().refreshTree();
            getMainModule().selectNodeInTree(editor.getSelectedValue());
         } else {
            MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils
                     .getString("messages.SelectSomethingToGoTo"));
         }
      } finally {
         GuiUtil.setBusyCursor(getDefaultParentFrame(), false);
      }
   }

   /**
    * Loads a disclist from a file.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void disclistLoad(final ActionEvent aEvent) {
      LOG.debug("Load Disclist");
      final JComponent button = (JComponent) aEvent.getSource();
      final DisclistPanel editor = (DisclistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.load();
   }

   /**
    * Moves a track down on the disclist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void disclistMoveDown(final ActionEvent aEvent) {
      LOG.debug("Move down disclist");
      final JComponent button = (JComponent) aEvent.getSource();
      final DisclistPanel editor = (DisclistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.moveDown();
   }

   /**
    * Moves a track up on the disclist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void disclistMoveUp(final ActionEvent aEvent) {
      LOG.debug("Move up disclist");
      final JComponent button = (JComponent) aEvent.getSource();
      final DisclistPanel editor = (DisclistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.moveUp();
   }

   /**
    * Removes tracks from the disclist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void disclistRemoveTracks(final ActionEvent aEvent) {
      LOG.debug("Remove discs from disclist");
      final JComponent button = (JComponent) aEvent.getSource();
      final DisclistPanel editor = (DisclistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.removeDisc();
      editor.repaint(); // repaint DisclistPanel
      // getMainFrame().getMainPageBuilder().refreshUI();
   }

   /**
    * Clears all tracks from the disclist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void disclistClear(ActionEvent aEvent) {
      LOG.debug("Clear discs from disclist");
      final JComponent button = (JComponent) aEvent.getSource();
      final DisclistPanel editor = (DisclistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.removeAllDiscs();
      editor.repaint();
   }

   /**
    * Saves a disclist to disk.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void disclistSave(final ActionEvent aEvent) {
      LOG.debug("Save disclist.");
      final JComponent button = (JComponent) aEvent.getSource();
      final DisclistPanel editor = (DisclistPanel) button.getClientProperty(Resources.EDITOR_COMPONENT);
      editor.save();
   }

   /**
    * Shows or hides the disclist.
    * <p>
    * @param aEvent the Actionevent fired
    */
   public void disclistDisplay(final ActionEvent aEvent) {
      LOG.debug("Disclist displayed/hidden.");
      String currentPanel = getMainFrame().getMainPageBuilder().panelVisible();
      boolean visible;
      if (currentPanel == "disclistPanel") {
         visible = false;
      } else {
         visible = true;
      }
      getMainFrame().getMainPageBuilder().setDisclistVisible(visible);

   }

   /**
    * Adds disc(s) to the bottom of the disclist.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void addToDisclist(final ActionEvent aEvent) {
      LOG.debug("Add to disclist");
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      final JComponent source = (JComponent) aEvent.getSource();
      final Component editor = (Component) source.getClientProperty(Resources.EDITOR_COMPONENT);
      if (editor != null) {

         final Disclist disclist = getMainFrame().getDisclist();
         if (editor instanceof JTree) {
            final JTree tree = (JTree) editor;
            for (int i = 0; i < tree.getSelectionPaths().length; i++) {
               final TreePath path = tree.getSelectionPaths()[i];
               if (path.getLastPathComponent() instanceof AbstractTreeNode) {
                  final AbstractTreeNode node = (AbstractTreeNode) path.getLastPathComponent();
                  // AZ look for filtered discs
                  if (node instanceof ArtistNode) {
                     final int nodeCount = node.getChildCount();
                     if (nodeCount > 0) {
                        for (int ii = 0; ii < node.getChildCount(); ii++) {
                           final AbstractTreeNode childNode = (AbstractTreeNode) node.getChildAt(ii);
                           disclist.add(childNode.getModel());
                        }
                     }
                  } else {
                     disclist.add(node.getModel());
                  }
               }
            }
         }

         if (editor instanceof JList) {
            final JList list = (JList) editor;
            final Object[] selections = list.getSelectedValues();
            for (int i = 0; i < selections.length; i++) {
               final JukesValidationMessage message = (JukesValidationMessage) selections[i];
               disclist.add(message.getDomainObject());
            }
         }
         if (editor instanceof JTable) {
            final JTable table = (JTable) editor;
            final int[] selections = table.getSelectedRows();
            if (selections.length == 0) {
               MessageUtil.showMessage(mainFrame, Resources.getString("messages.SelectDisc"));
               ;
            }
            if (table.getModel() instanceof DiscTableModel) { // AZ - processing
               // of
               // DiscTableModel
               // added
               final DiscTableModel model = (DiscTableModel) table.getModel();
               for (int i = 0; i < selections.length; i++) {
                  int selectedRow = selections[i];
                  selectedRow = table.getRowSorter().convertRowIndexToModel(selectedRow);
                  disclist.add(model.getData()[selectedRow]);
               }
            } else {
               final SearchTableModel model = (SearchTableModel) table.getModel();
               for (int i = 0; i < selections.length; i++) {
                  int selectedRow = selections[i];
                  selectedRow = table.getRowSorter().convertRowIndexToModel(selectedRow);
                  disclist.add(model.getData()[selectedRow]);
               }
            }
         }
         if (editor instanceof AlbumImage) {
            final AlbumImage image = (AlbumImage) editor;
            if (image.getDisc() != null) {
               disclist.add(image.getDisc());
            }
         }
         getMainFrame().getMainPageBuilder().refreshUI();
      }
   }

   /**
    * Set selected disc as current disc.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void setCurrent(final ActionEvent aEvent) {
      LOG.debug("Set disc as current");
      final Disclist disclist = getMainFrame().getDisclist();
      Disc currentDisc = disclist.getCurrentDisc();
      final JComponent source = (JComponent) aEvent.getSource();
      final Component editor = (Component) source.getClientProperty(Resources.EDITOR_COMPONENT);
      if (editor instanceof JList) {
         final JList list = (JList) editor;
         final Object[] selections = list.getSelectedValues();
         if (selections.length != 0) {
            currentDisc = (Disc) selections[0];
            disclist.setCurrentDisc(currentDisc);
            disclist.updateState();
         }
      }
   }

   /**
    * AZ Displays the XML Export tool dialog.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void xmlExport(final ActionEvent aEvent) {
      LOG.debug("XML Export Dialog");
      // Select xml-file to write to
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(Resources.getString("label.ExportCatalog"));
      chooser.setFileFilter(FilterFactory.xmlFileFilter());
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileHidingEnabled(true);
      final int returnVal = chooser.showSaveDialog(this.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      File file = chooser.getSelectedFile();
      // add the extension if missing
      file = FilterFactory.forceXmlExtension(file);

      // open dialog with progress bar and run export Thread
      new XMLExportDialog(getDefaultParentFrame(), MainModule.SETTINGS, file).open();
   }

   /**
    * AZ Displays the XML Import tool dialog.
    * <p>
    * @param aEvent the Action Event fired for this button
    */
   public void xmlImport(final ActionEvent aEvent) {
      LOG.debug("XML Import Dialog");
      // Select xml-file to read from
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(Resources.getString("label.ImportCatalog"));
      chooser.setFileFilter(FilterFactory.xmlFileFilter());
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileHidingEnabled(true);
      final int returnVal = chooser.showOpenDialog(this.getDefaultParentFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      File file = chooser.getSelectedFile();
      // add the extension if missing
      file = FilterFactory.forceXmlExtension(file);

      // open dialog with progress bar and run import Thread
      new XMLImportDialog(getDefaultParentFrame(), MainModule.SETTINGS, file).open();
   }

}