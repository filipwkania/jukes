package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.text.JTextComponent;
import javax.swing.JCheckBox;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uif.util.Worker;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.Severity;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.file.MusicDirectory;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.component.ComponentFactory;
import com.melloware.jukes.gui.view.component.MessageCellRenderer;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.JukesValidationMessage;
import com.melloware.jukes.util.MessageUtil;

/**
 * Searches an entire directory and subdiretories looking for all music folders
 * and adding them to the catalog. If any error occurs the dir is skipped and
 * the next one processed. Icons are used in the JList to display whether a
 * success or failure adding this directory occurred.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ - some modifications 2009
 */
@SuppressWarnings("unchecked")
public final class DiscFindDialog extends AbstractDialog {

   private static final Log LOG = LogFactory.getLog(DiscFindDialog.class);
   private static final String HQL_DISC_LOCATIONS = ResourceUtils.getString("hql.disc.locations");
   private final Map mapDiscs = new HashMap();
   private DefaultListModel listModel;
   private JButton buttonSave;
   private JButton buttonApply;
   private JButton buttonCancel;
   private JButton buttonClose;
   private JComponent buttonBar;
   private JList list;
   private JPanel panel;
   private JLabel currentDirectory;
   private JTextComponent directory;
   private final Settings settings;
   private Worker worker;
   private static boolean closeDialog = false;
   private JCheckBox flagErrorsOnly; //AZ

   /**
    * Constructs a default about dialog using the given owner.
    * @param owner the dialog's owner
    */
   public DiscFindDialog(Frame owner, Settings settings) {
      super(owner);
      LOG.debug("Disc Finder created.");
      this.settings = settings;

      // since we are doing a find we want to compact the database on shutdown
      HibernateUtil.setCompact(true);

      // load the discs into a map for fast access
      loadAllDiscs();
   }

   public DiscFindDialog(Frame owner, Settings settings, boolean startFinder) {
      super(owner);
      LOG.debug("Disc Finder created.");
      DiscFindDialog.closeDialog = startFinder;
      this.settings = settings;

      // since we are doing a find we want to compact the database on shutdown
      HibernateUtil.setCompact(true);

      // load the discs into a map for fast access
      loadAllDiscs();

      if (startFinder) {
         this.build();
         doApply();
      }
   }

   /*
    * (non-Javadoc)
    * @see com.jgoodies.swing.AbstractDialog#doApply()
    */
   public void doApply() {
      LOG.debug("Apply pressed.");
      buttonCancel.setEnabled(true);
      buttonApply.setEnabled(false);
      buttonClose.setEnabled(false);
      buttonSave.setEnabled(false);
      GuiUtil.setBusyCursor(this, true);
      LOG.info("[START] Disc Finder");

      /*
       * Invoking start() on the SwingWorker causes a new Thread to be created
       * that will call construct(), and then finished(). Note that finished()
       * is called even if the worker is interrupted because we catch the
       * InterruptedException in doWork().
       */
      worker = new Worker() {
         public Object construct() {
            return doWork(flagErrorsOnly.isSelected());
         }

         public void finished() {
            if (closeDialog) {
               ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
               LOG.info("[CLOSE] Disc Finder");
               dispose();
            } else {
               threadFinished(get());
            }
         }
      };
      worker.start();

   }

   /*
    * (non-Javadoc)
    * @see com.jgoodies.swing.AbstractDialog#doCancel()
    */
   public void doCancel() {
      LOG.debug("Cancel Pressed.");
      if (worker != null) {
         worker.interrupt();
      }
      buttonCancel.setEnabled(false);
      buttonApply.setEnabled(true);
      buttonClose.setEnabled(true);
      buttonSave.setEnabled(true);
   }

   /**
    * Builds and answers the dialog's content.
    * @return the dialog's content with tabbed pane and button bar
    */
   protected JComponent buildContent() {
      final JPanel content = new JPanel(new BorderLayout());
      content.add(buildMainPanel(), BorderLayout.CENTER);
      content.add(buttonBar, BorderLayout.SOUTH);
      return content;
   }

   /**
    * Builds and returns the dialog's header.
    * @return the dialog's header component
    */
   protected JComponent buildHeader() {
      return new HeaderPanel(Resources.getString("label.discfinder"), Resources.getString("label.discfindermessage"),
               Resources.DISC_FINDER_ICON);
   }

   /**
    * Builds and returns the dialog's pane.
    * @return the dialog's pane component
    */
   protected JComponent buildMainPanel() {
      final JButton[] buttons = new JButton[4];
      final JButton button = createApplyButton();
      button.setText(Resources.getString("label.Find"));
      final Action export = new AbstractAction(Resources.getString("label.saveerrorreport"), Resources.FILE_TEXT_ICON) {
         // This method is called when the button is pressed
         public void actionPerformed(ActionEvent evt) {
            saveErrorReport(evt);
         }
      };
      buttonSave = new JButton(export);
      buttonCancel = createCancelButton();
      buttonApply = button;
      buttonClose = createCloseButton(true);
      buttonClose.setText(Resources.getString("label.Close"));
      buttonCancel.setEnabled(false);
      buttonCancel.setText(Resources.getString("label.Cancel"));
      buttons[0] = buttonApply;
      buttons[1] = buttonCancel;
      buttons[2] = buttonSave;
      buttons[3] = buttonClose;
      buttonBar = ButtonBarFactory.buildRightAlignedBar(buttons);
      final FormLayout layout = new FormLayout("3px, pref, fill:pref:grow", "p, p, p, 4px, p, 4px, p");
      final PanelBuilder builder = new PanelBuilder(layout);
      builder.setDefaultDialogBorder();
      final CellConstraints cc = new CellConstraints();
      int row = 1;
      builder.addSeparator(Resources.getString("label.Find"), cc.xyw(1, row++, 3));
      builder.add(buildDirectoryPanel(), cc.xyw(1, row++, 3));
      builder.add(buildListPanel(), cc.xyw(1, row, 3));
      //AZ flagErrorsOnly
      flagErrorsOnly = new JCheckBox(Resources.getString("label.errorsonly"), false);
      builder.add(flagErrorsOnly, cc.xyw(2, 5, 2));
      panel = builder.getPanel();
      panel.setBorder(Borders.DIALOG_BORDER);
      return panel;
   }

   /**
    * Saves the error report to a TXT file.
    * <p>
    * @param aEvt the event fired
    */
   protected void saveErrorReport(final ActionEvent aEvt) {
      LOG.debug("Save Error Report pressed.");
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(Resources.getString("label.saveerrorreport"));

      chooser.setFileFilter(FilterFactory.textFileFilter());
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileHidingEnabled(true);
      final int returnVal = chooser.showSaveDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
         return;
      }
      File file = chooser.getSelectedFile();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Absolute: " + file.getAbsolutePath());
      }

      // add the extension if missing
      file = FilterFactory.forceTextExtension(file);

      try {
         // now print errors and warns to the file
         final ArrayList results = new ArrayList();
         final Enumeration enumeration = listModel.elements();
         while (enumeration.hasMoreElements()) {
            final JukesValidationMessage message = (JukesValidationMessage) enumeration.nextElement();
            if ((message.severity() == Severity.ERROR) || (message.severity() == Severity.WARNING)) {
               // formatted Text contains the directory
               results.add("DIR: " + message.formattedText());
               // tooltip contains the error
               results.add(message.severity().toString().toUpperCase() + ": " + message.getToolTip());
               // add spacer between directories
               results.add("  ");
            }
         }

         FileUtils.writeLines(file, null, results);

         MessageUtil.showInformation(this, Resources.getString("label.reportsaved"));
      } catch (IOException ex) {
       	final String errorMessage = ResourceUtils.getString("label.Errorwritingfile") + "\n\n" + ex.getMessage(); 
         MessageUtil.showError(this, errorMessage); //AZ
         LOG.error(errorMessage, ex);
      } catch (InfrastructureException ex) {
         final String errorMessage = ResourceUtils.getString("label.Errorwritingfile") + "\n\n" + ex.getMessage(); 
         MessageUtil.showError(this, errorMessage); //AZ
         LOG.error(errorMessage, ex);
      } catch (Exception ex) {
          final String errorMessage = ResourceUtils.getString("label.Errorwritingfile"); 
          MessageUtil.showError(this, errorMessage); //AZ
          LOG.error(errorMessage, ex);
      }
   }

   /*
    * (non-Javadoc)
    * @see com.jgoodies.swing.AbstractDialog#doCloseWindow()
    */
   protected void doCloseWindow() {
      super.doClose();
   }

   /**
    * Builds the directory selection panel.
    * <p>
    * @return the panel used to select the directory.
    */
   private JComponent buildDirectoryPanel() {
      directory = new JTextField();
      currentDirectory = new JLabel();
      ((JTextField) directory).setColumns(50);
      directory.setText(this.settings.getStartInDirectory().getAbsolutePath());
      final FormLayout layout = new FormLayout(
               "right:max(14dlu;pref), 4dlu, left:min(60dlu;pref):grow, pref, 40dlu, ,pref, pref", "p, 4px, p, 4px"); // extra
                                                                                                                        // bottom
                                                                                                                        // space
                                                                                                                        // for
                                                                                                                        // icons

      final PanelBuilder builder = new PanelBuilder(layout);
      final CellConstraints cc = new CellConstraints();

      builder.addLabel(Resources.getString("label.searchdirectory"), cc.xy(1, 1));
      builder.add(directory, cc.xyw(3, 1, 3));
      builder.add(ComponentFactory.createDirectoryChooserButton(directory), cc.xy(6, 1));
      builder.addLabel(Resources.getString("label.Processing"), cc.xy(1, 3));
      builder.add(currentDirectory, cc.xyw(3, 3, 3));

      return builder.getPanel();
   }

   /**
    * Builds the message list panel.
    * <p>
    * @return the panel used to display messages
    */
   private JComponent buildListPanel() {
      listModel = new DefaultListModel();
      // Create the list and put it in a scroll pane.
      list = new JList(listModel);
      list.setFocusable(false);
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.setSelectedIndex(0);
      list.setCellRenderer(new MessageCellRenderer());
      list.setVisibleRowCount(19);
      return new JScrollPane(list);
   }

   /**
    * This method represents the application code that we'd like to run on a
    * separate thread.
    */
   private Object doWork(boolean flagErrorsOnly) {
      Object result = null;
      final JukesValidationMessage message = new JukesValidationMessage("", Severity.OK);;
      try {
         // clear all old elements out
         listModel.removeAllElements();

         // get the directory from the text box
         String directory = this.directory.getText();
         if (!directory.endsWith(SystemUtils.FILE_SEPARATOR)) { //AZ - ensure path ends with FILE_SEPARATOR
        	 directory = directory + SystemUtils.FILE_SEPARATOR;
         }
         final File dir = new File(directory); 
         // make sure it is a directory
         if ((!dir.isDirectory()) || (!dir.exists())) {
        	final String errorMessage = ResourceUtils.getString("messages.SelectValidDirectory"); 
            LOG.error(errorMessage);
            MessageUtil.showError(this, errorMessage); //AZ
            throw new InterruptedException();
         }
         //AZ get the images directory from Settings
         if (this.settings.isCopyImagesToDirectory()) {
         final String imagesDirectory = this.settings.getImagesLocation().getAbsolutePath();
         final File imagesDir = new File(imagesDirectory);
         // make sure it is a directory
         if ((!imagesDir.isDirectory()) || (!imagesDir.exists())) {
         	final String errorMessage = ResourceUtils.getString("messages.SelectImageDirectory") + imagesDirectory + " " + 
         								ResourceUtils.getString("messages.DoesntExist"); 
            LOG.error(errorMessage);
            MessageUtil.showError(this, errorMessage); //AZ
            throw new InterruptedException();
         }
         }
         // now recursively look for all MP3 directories
         recurseDirectories(dir, flagErrorsOnly);

         if (Thread.interrupted()) {
            LOG.debug("Thread interrupted.");
            throw new InterruptedException();
         }

      } catch (InterruptedException e) {
         return result; // SwingWorker.get() returns this
      }
      message.setMessage(Resources.getString("label.allsubdirectoriesprocessed")); //AZ set final message
      updateList(message);
      return result; // or this

   }

   /**
    * Asks the data store if we already have added this disc by checking if a
    * music file from this directory already exists.
    * <p>
    * @param aDirectory the path of the directory to check.
    * @return true if we already have this, false if not
    */
   private boolean hasDiscAlready(final String aDirectory) {
      return mapDiscs.containsKey(aDirectory);
   }

   /**
    * Puts all disc locations into a Map for fast access.
    */
   private void loadAllDiscs() {
      final Collection discs = HibernateDao.findByQuery(HQL_DISC_LOCATIONS);

      for (final Iterator iter = discs.iterator(); iter.hasNext();) {
         final Object queryResult = (Object) iter.next();
         mapDiscs.put(queryResult, queryResult);
      }
   }

   /**
    * A recursive subroutine that lists the contents of the directory dir,
    * including the contents of its subdirectories to any level of nesting. It
    * is assumed that dir is in fact a directory.
    * <p>
    * @param aDirectory the directory to recurse
    * @throws InterruptedException if the thread is Interrupted stop processing
    */
   private void recurseDirectories(final File aDirectory, boolean flagErrorsOnly) throws InterruptedException {
      final String[] files = aDirectory.list(DirectoryFileFilter.INSTANCE);
      boolean hasSubDirectory = false;//AZ
      if (Thread.interrupted()) {
         LOG.debug("Thread interrupted.");
         throw new InterruptedException();
      }

      // if there are more directories found then recurse them
      if (files.length > 0) {
         for (int i = 0; i < files.length; i++) {
            final File f = new File(aDirectory, files[i]);
            /** AZ - test for hidden directory**/
            if (f.isDirectory() & (!f.isHidden())) {
               hasSubDirectory = true;	//AZ
               recurseDirectories(f, flagErrorsOnly);
            }
         }
      } 
      //AZ  Check current directory for music file (not only the bottom level node)
         updateProcessing(aDirectory.getAbsolutePath());
         if (hasDiscAlready(aDirectory.getAbsolutePath())) {
            return;
         }
         /** AZ - Do not update tags in files **/
         final JukesValidationMessage message = MusicDirectory.loadDiscFromDirectory(aDirectory, false);
         //AZ: OK and Warning are added only if flagErrorsOnly is not set
         if (message.getSeverity() == Severity.OK) {
        	if (!flagErrorsOnly) {
            message.setMessage(aDirectory.getAbsolutePath());
            updateList(message);
        	}
         } else if (message.getSeverity() == Severity.WARNING) {
        	if ((!flagErrorsOnly) & (!hasSubDirectory)) { //AZ: Warning are added only for bottom level node
            message.setMessage(aDirectory.getAbsolutePath());
            updateList(message);
        	}
         } else {
            message.setMessage("ERROR " + aDirectory.getAbsolutePath());
            updateList(message);
         }
         hasSubDirectory = false;
   }

   /**
    * When the thread is finished this method is called.
    * <p>
    * @param result the Object return from the doWork thread.
    */
   private void threadFinished(Object result) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Thread Finished");
         LOG.debug(result);
      }
      GuiUtil.setBusyCursor(this, false);
      buttonCancel.setEnabled(false);
      buttonApply.setEnabled(true);
      buttonClose.setEnabled(true);
      buttonSave.setEnabled(true);

      // refresh the tree
      ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
      LOG.info("[STOP] Disc Finder");
   }

   /**
    * When the worker needs to update the GUI we do so by queuing a Runnable for
    * the event dispatching thread with SwingUtilities.invokeLater(). In this
    * case we're just changing the progress bars value.
    */
   private void updateList(final JukesValidationMessage aMessage) {
      final Runnable updateList = new Runnable() {
         public void run() {
            listModel.addElement(aMessage);
            final int index = listModel.indexOf(aMessage);
            list.setSelectedIndex(index);
            list.ensureIndexIsVisible(index);
         }
      };
      EventQueue.invokeLater(updateList);
   }

   /**
    * When the worker needs to update the GUI we do so by queuing a Runnable for
    * the event dispatching thread with SwingUtilities.invokeLater(). In this
    * case we're just changing the progress bars value.
    */
   private void updateProcessing(final String aDirectory) {
      final Runnable updateList = new Runnable() {
         public void run() {
            currentDirectory.setText(aDirectory);
         }
      };
      EventQueue.invokeLater(updateList);
   }

}