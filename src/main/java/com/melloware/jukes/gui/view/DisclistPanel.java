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
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
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
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.file.Disclist;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.component.AlbumImage;
import com.melloware.jukes.gui.view.component.ComponentFactory;
import com.melloware.jukes.gui.view.component.DisclistCellRenderer;
import com.melloware.jukes.gui.view.component.DisclistListModel;
import com.melloware.jukes.gui.view.tasks.LoadDisclistTask;
import com.melloware.jukes.gui.view.tasks.TimerListener;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.MessageUtil;

/**
 * Display the disclist and all the disclist options.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 2009 AZ Development
 */
public final class DisclistPanel extends SimpleInternalFrame implements PropertyChangeListener {

   private static final Log LOG = LogFactory.getLog(DisclistPanel.class);
   private AlbumImage albumImage;
   private JLabel artist;
   private JLabel bitrate;
   private JLabel disc;
   private JLabel year;
   private final JList discList;
   private JScrollPane disclistScrollPane;
   private final Disclist disclist;
   private final DisclistListModel disclistModel;

   /**
    * Constructs a <code>DisclistPanel</code> for the given module.
    * @param aDisclist provides the disclist class needed for display
    */
   DisclistPanel(Disclist aDisclist) {
      super(Resources.DISCLIST_ICON, ResourceUtils.getString("label.disclist"));
      LOG.debug("Disclist panel created.");

      this.disclist = aDisclist;
      this.disclist.addPropertyChangeListener(this);

      // build disclist and assign listeners
      disclistModel = new DisclistListModel(this.disclist, this);
      discList = new JList(disclistModel);
      discList.putClientProperty(Resources.EDITOR_COMPONENT, this);
      discList.addKeyListener(disclistModel.getKeyTypedListener());

      // add right click menu to the disclist
      final JPopupMenu popup = MainMenuBuilder.buildDisclistPopupMenu(this, discList);
      MouseListener popupListener = new ListPopupListener(popup);
      discList.addMouseListener(popupListener);
      discList.add(popup);

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
      discList.clearSelection();
      if (disclist.size() > 0) {
         discList.setSelectedIndex(0);
      }
   }

   /**
    * Load a disclist from disk.
    */
   public void load() {
      LOG.debug("Load disclist");
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(Resources.getString("label.LoadDisclist"));
      chooser.setAcceptAllFileFilterUsed(false);
      chooser.setFileFilter(FilterFactory.disclistFileFilter());
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

      // now try and import the disclist
      try {
         GuiUtil.setBusyCursor(mainFrame, true);
         final LoadDisclistTask task = new LoadDisclistTask(file, this.disclist);
         // AZ: Put the Title of Progress Monitor Dialog Box and Cancel button
         UIManager.put("ProgressMonitor.progressText", Resources.getString("label.ProgressTitle"));
         UIManager.put("OptionPane.cancelButtonText", Resources.getString("label.Cancel"));

         final ProgressMonitor progressMonitor = new ProgressMonitor(Application.getDefaultParentFrame(), Resources
                  .getString("messages.loaddiscs"), "", 0, task.getLengthOfTask());
         progressMonitor.setProgress(0);
         progressMonitor.setMillisToDecideToPopup(10);
         task.go();
         final Timer timer = new Timer(50, null);
         timer.addActionListener(new TimerListener(progressMonitor, task, timer));
         timer.start();

      } catch (IOException ex) {
         final String errorMessage = ResourceUtils.getString("messages.ErrorLoadingFile") + ": \n\n" + ex.getMessage();
         LOG.error(errorMessage, ex);
         MessageUtil.showError(this, errorMessage); // AZ
      } catch (SAXParseException spe) {
         final String err = spe.toString() + "\nLine number: " + spe.getLineNumber() + "\nColumn number: "
                  + spe.getColumnNumber() + "\nPublic ID: " + spe.getPublicId() + "\nSystem ID: " + spe.getSystemId();
         LOG.error(err, spe);
         MessageUtil.showError(this, err); // AZ
      } catch (Exception ex) {
         final String errorMessage = ResourceUtils.getString("messages.ErrorLoadingFile");
         LOG.error(errorMessage, ex);
         MessageUtil.showError(this, errorMessage); // AZ
      } finally {
         GuiUtil.setBusyCursor(mainFrame, false);
      }
   }

   /**
    * Moves items down on the list.
    */
   public void moveDown() {
      LOG.debug("Moving on down");
      int[] selected = discList.getSelectedIndices();
      if (selected.length <= 0) {
         MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils
                  .getString("messages.SelectSomethingToMove"));
      } else {
         int[] newSelections = discList.getSelectedIndices();
         for (int i = selected.length - 1; i >= 0; i--) {
            int index = selected[i];
            if (index == (this.disclist.size() - 1)) {
               break;
            }
            this.disclist.moveDown(index);
            newSelections[i] = (index + 1);

         }
         discList.setSelectedIndices(newSelections);
      }
   }

   /**
    * Moves items up on the list.
    */
   public void moveUp() {
      LOG.debug("Moving on up");
      int[] selected = discList.getSelectedIndices();
      if (selected.length <= 0) {
         MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils
                  .getString("messages.SelectSomethingToMove"));
      } else {
         int[] newSelections = discList.getSelectedIndices();
         for (int i = 0; i < selected.length; i++) {
            int index = selected[i];
            if (index == 0) {
               break;
            }
            this.disclist.moveUp(index);
            newSelections[i] = (index - 1);
         }
         discList.setSelectedIndices(newSelections);
      }
   }

   /*
    * If the disclist changes, update the frame
    */
   public void propertyChange(PropertyChangeEvent aEvt) {
      // update current disc
      final Disc currentdisc = this.disclist.getCurrentDisc();
      if (currentdisc != null) {
         albumImage.setDisc(currentdisc);
         if (MainModule.SETTINGS.isCopyImagesToDirectory()) {
            final String imageName = ImageFactory.standardImageFileName(currentdisc.getArtist().getName(), currentdisc
                     .getName(), currentdisc.getYear());
            albumImage.setImage(ImageFactory.getScaledImage(imageName, 150, 150).getImage());
         } else {
            if (StringUtils.isNotBlank(currentdisc.getCoverUrl())) {
               albumImage.setImage(ImageFactory.getScaledImage(currentdisc.getCoverUrl(), 150, 150).getImage());
            } else {
               albumImage.setImage(null);
            }
         }
         artist.setText(currentdisc.getArtist().getName());
         disc.setText(currentdisc.getName());
         year.setText(currentdisc.getYear());
         bitrate.setText(currentdisc.getBitrate().toString() + " kbps");
      }
   }

   /**
    * Removes disc from the disclist
    */
   public void removeDisc() {
      LOG.debug("Remove disc");
      int[] selected = discList.getSelectedIndices();
      if (selected.length <= 0) {
         MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils
                  .getString("messages.SelectSomethingToRemove"));
      } else {
         for (int i = selected.length - 1; i >= 0; i--) {
            int index = selected[i];
            this.disclist.remove(index);
         }
         this.clearSelection();
      }
   }

   /**
    * Clears the disclist.
    */
   public void removeAllDiscs() {
      LOG.debug("Remove ALL discs from disclist");
      for (int i = disclist.size() - 1; i >= 0; i--) {
         this.disclist.remove(i);
      }
      this.clearSelection();
   }

   /**
    * Saves the current disclist.
    */
   public void save() {
      LOG.debug("Save disclist");
      final JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(ResourceUtils.getString("label.SaveDisclist"));
      chooser.setAcceptAllFileFilterUsed(false);
      FileFilter lst = FilterFactory.disclistFileFilter();
      chooser.addChoosableFileFilter(lst);
      chooser.setFileFilter(lst);

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
      file = FilterFactory.forceLstExtension(file);

      try {
         this.disclist.save(file);

         MessageUtil.showInformation(Application.getDefaultParentFrame(), ResourceUtils
                  .getString("messages.DisclistSavedSuccessfully"));
      } catch (IOException ex) {
         final String errorMessage = ResourceUtils.getString("messages.ErrorWritingFile") + "\n\n" + ex.getMessage();
         LOG.error(errorMessage, ex);
         MessageUtil.showError(this, errorMessage); // AZ
      } catch (Exception ex) {
         final String errorMessage = ResourceUtils.getString("messages.ErrorWritingFile");
         LOG.error(errorMessage, ex);
         MessageUtil.showError(this, errorMessage); // AZ
      }
   }

   /**
    * Builds and answers the content pane.
    */
   private JComponent buildContent() {

      JScrollPane scrollPane = UIFactory.createStrippedScrollPane(buildDisclist());
      scrollPane.setMinimumSize(new Dimension(300, 100));
      scrollPane.setPreferredSize(new Dimension(300, 100));
      return scrollPane;
   }

   private JComponent buildDisclist() {
      discList.setFocusable(true);
      discList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      discList.setSelectedIndex(0);
      discList.setVisibleRowCount(13);
      discList.setCellRenderer(new DisclistCellRenderer());
      disclistScrollPane = new JScrollPane(discList);

      FormLayout layout = new FormLayout("left:275px, 4dlu, right:pref:grow", "p");

      layout.setRowGroups(new int[][] { { 1 } });
      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();

      builder.add(disclistScrollPane, cc.xy(1, 1, "left,top"));
      builder.add(buildDiscPanel(), cc.xy(3, 1, "left,top"));
      return builder.getPanel();
   }

   /**
    * Builds and answers the toolbar.
    */
   private JToolBar buildToolBar() {
      final ToolBarBuilder bar = new ToolBarBuilder("Disclist");
      AbstractButton button = null;
      button = ComponentFactory.createToolBarButton(Actions.DISCLIST_LOAD_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = ComponentFactory.createToolBarButton(Actions.DISCLIST_SAVE_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = ComponentFactory.createToolBarButton(Actions.DISCLIST_MOVEUP_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = ComponentFactory.createToolBarButton(Actions.DISCLIST_MOVEDOWN_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = ComponentFactory.createToolBarButton(Actions.DISCLIST_REMOVE_DISC_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = ComponentFactory.createToolBarButton(Actions.DISCLIST_CLEAR_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = ComponentFactory.createToolBarButton(Actions.DISCLIST_GOTO_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, discList);
      bar.add(button);
      button = ComponentFactory.createToolBarButton(Actions.DISCLIST_CLOSE_ID);
      bar.add(button);
      return bar.getToolBar();
   }

   /**
    * Builds the Disc editor panel.
    * <p>
    * @return the panel to edit disc info.
    */
   private JComponent buildDiscPanel() {
      FormLayout layout = new FormLayout("pref, left:min(80dlu;pref):grow, 40dlu, pref, right:pref",
               "4px, p, 4px, p, 4px, p, 4px, p, 4px, p, p, 75px");
      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();

      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      artist = UIFactory.createBoldLabel("");
      disc = UIFactory.createBoldLabel("");
      year = UIFactory.createBoldLabel("");
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
      builder.addLabel(ResourceUtils.getString("label.year") + ": ", cc.xy(1, 6));
      builder.add(year, cc.xyw(2, 6, 3));
      builder.addLabel(ResourceUtils.getString("label.bitrate") + ": ", cc.xy(1, 10));
      builder.add(bitrate, cc.xyw(2, 10, 3));
      // builder.add(mainFrame.getAnalyzer(), cc.xywh(1, 11, 4, 2));
      final JComponent panel = builder.getPanel();
      panel.setBorder(new TitledBorder(ResourceUtils.getString("label.currentdisc")));
      return panel;
   }

   // shows popups on right click of List nodes
   private static class ListPopupListener extends MouseAdapter {

      final JPopupMenu popup;

      public ListPopupListener(JPopupMenu popup) {
         this.popup = popup;
      }

      @Override
      public void mousePressed(MouseEvent evt) {
         maybeShowPopup(evt);

         /**
          * // left mouse button pressed twice, change current disc in disclist
          * // middle mouse button pressed, change current disc in disclist if
          * ((((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) &&
          * (evt.getClickCount() == 2)) || ((evt.getModifiers() &
          * MouseEvent.BUTTON2_MASK) != 0)) { //dummy for double-click }
          **/

         JComponent source = (JComponent) evt.getSource();
         final ActionEvent event = new ActionEvent(source, 1, Actions.DISCLIST_SET_CURRENT_ID);
         source.putClientProperty(Resources.EDITOR_COMPONENT, source);
         ActionManager.get(Actions.DISCLIST_SET_CURRENT_ID).actionPerformed(event);
      }

      @Override
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