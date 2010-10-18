package com.melloware.jukes.gui.view.editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uif.component.ToolBarButton;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uifextras.util.UIFactory;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.exception.MusicTagException;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.file.tag.TagFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.component.AlbumImage;
import com.melloware.jukes.gui.view.component.ComponentFactory;
import com.melloware.jukes.gui.view.tasks.TimerListener;
import com.melloware.jukes.gui.view.tasks.UpdateTagsTask;
import com.melloware.jukes.gui.view.validation.IconFeedbackPanel;
import com.melloware.jukes.gui.view.validation.TrackValidationModel;
import com.melloware.jukes.util.MessageUtil;

/**
 * An implementation of {@link Editor} that displays a {@link Track}.
 * <p>
 * This container uses a <code>FormLayout</code> and the panel building is done
 * with the <code>PanelBuilder</code> class. Columns and rows are specified
 * before the panel is filled with components.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ - some modifications 2009, 2010
 */
public final class TrackEditor extends AbstractEditor {

   private static final Log LOG = LogFactory.getLog(TrackEditor.class);
   private AlbumImage albumImage;
   private JComponent trackPanel;
   private JLabel bitRate;
   private JLabel copyrighted;
   private JLabel duration;
   private JLabel emphasis;
   private JLabel fileSize;
   private JLabel frequency;
   private JLabel layer;
   private JLabel location;
   private JLabel mode;
   private JLabel version;
   // private JTextComponent comment;
   private JTextArea comment;
   /** AZ - use multi-line area for comment **/
   private JTextComponent titleField;
   private JTextComponent trackNumber;
   private JToolBar headerToolbar;
   private transient MusicTag musicTag;

   /**
    * Constructs a <code>TrackEditor</code>.
    */
   public TrackEditor() {
      super(Resources.TRACK_TREE_ICON);
   }

   /**
    * Gets the domain class associated with this editor.
    */
   @Override
   public Class getDomainClass() {
      return Track.class;
   }

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.gui.view.editor.AbstractEditor#getHeaderToolBar()
    */
   @Override
   public JToolBar getHeaderToolBar() {
      return headerToolbar;
   }

   /**
    * Builds the content pane.
    */
   @Override
   public void build() {
      initComponents();
      initComponentAnnotations();
      initEventHandling();

      trackPanel = buildTrackPanel();

      FormLayout layout = new FormLayout("fill:pref:grow", "max(14dlu;pref), p, p, p, 12px, p, 7px, p, 12px, p, 7px, p");

      setLayout(layout);
      PanelBuilder builder = new PanelBuilder(layout, this);
      builder.setDefaultDialogBorder();
      CellConstraints cc = new CellConstraints();

      builder.add(buildHintAreaPane(), cc.xy(1, 1));
      builder.addSeparator(Resources.getString("label.track"), cc.xy(1, 3));
      builder.add(trackPanel, cc.xy(1, 4));
      builder.addSeparator(Resources.getString("label.taginfo"), cc.xy(1, 6));
      builder.add(buildMusicPanel(), cc.xy(1, 8));
      JComponent audit = buildAuditInfoPanel();
      if (this.getSettings().isAuditInfo()) {
         builder.addSeparator(Resources.getString("label.auditinfo"), cc.xy(1, 10));
         builder.add(audit, cc.xy(1, 12));
      }

   }

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.gui.view.editor.AbstractEditor#commit()
    */
   @Override
   public void commit() {
      super.commit();
      Track track = getTrack();
      updateModel();

      // check for validation errors, if any then do no changes
      boolean hasErrors = hasErrors();
      if (hasErrors) {
         LOG.error(Resources.getString("messages.editorerrors"));
         MessageUtil.showError(this, Resources.getString("messages.editorerrors")); // AZ
         return;
      }

      // try to persist
      try {
         setBusyCursor(true);
         HibernateUtil.beginTransaction();
         HibernateDao.saveOrUpdate(track);
         HibernateUtil.commitTransaction();

         // now update this editor and the treeview
         updateView();
         this.getMainModule().refreshSelection(track, Resources.NODE_CHANGED);
      } catch (InfrastructureException ex) {
         HibernateUtil.rollbackTransaction();
         final String errorMessage = ResourceUtils.getString("messages.UniqueTrack");
         MessageUtil.showError(this, errorMessage); // AZ
         LOG.error(errorMessage, ex);
         HibernateDao.refresh(track);
         hasErrors = true;
      } catch (Exception ex) {
         HibernateUtil.rollbackTransaction();
         final String errorMessage = ResourceUtils.getString("messages.ErrorUpdatingTrack");
         MessageUtil.showError(this, errorMessage); // AZ
         LOG.error(errorMessage, ex);
         HibernateDao.refresh(track);
         hasErrors = true;
      } finally {
         setBusyCursor(false);
      }
      /** AZ - commit with update flag as specified in Settings **/
      final Boolean aUpdateTags = this.getSettings().isUpdateTags();
      if (!hasErrors) {
         if (aUpdateTags) {
            // now update the ID3 tags
            task = new UpdateTagsTask(track);
            // AZ: Put the Title of Progress Monitor Dialog Box and Cancel
            // button
            UIManager.put("ProgressMonitor.progressText", Resources.getString("label.ProgressTitle"));
            UIManager.put("OptionPane.cancelButtonText", Resources.getString("label.Cancel"));

            progressMonitor = new ProgressMonitor(getMainFrame(), Resources.getString("messages.updatetracks"), "", 0,
                     task.getLengthOfTask());
            progressMonitor.setProgress(0);
            progressMonitor.setMillisToDecideToPopup(1);
            task.go();
            timer = new Timer(50, null);
            timer.addActionListener(new TimerListener(progressMonitor, task, timer));
            timer.start();
         } else {
            MessageUtil.showSuccess(this);
         }
         super.commit();
      }
   }

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.gui.view.editor.AbstractEditor#delete()
    */
   @Override
   public void delete() {
      super.delete();
      final Track track = getTrack();
      final Disc disc = track.getDisc();
      try {
         if (!MessageUtil.confirmDelete(this)) {
            return;
         }
         // try to delete track from database
         setBusyCursor(true);
         HibernateUtil.beginTransaction();
         // AZ: no refreshing to speed-up processing
         // HibernateDao.refresh(disc);
         disc.getTracks().remove(track);
         // HibernateDao.refresh(track);
         HibernateDao.delete(track);
         HibernateUtil.commitTransaction();
         // reset dirty flag since we are deleting
         getValidationModel().setDirty(false);
         // tell the tree to select the parent node
         this.getMainModule().refreshSelection(disc, Resources.NODE_DELETED);
      } catch (Exception ex) {
         final String errorMessage = ResourceUtils.getString("messages.ErrorDeletingTrack");
         MessageUtil.showError(this, errorMessage); // AZ
         LOG.error(errorMessage, ex);
         HibernateUtil.rollbackTransaction();
      } finally {
         setBusyCursor(false);
      }
      // AZ Verify if disc has no more tracks and delete the disc
      if (disc.getTracks().isEmpty()) {
         try {
            // try to delete disc from database
            setBusyCursor(true);
            HibernateUtil.beginTransaction();
            final Artist artist = disc.getArtist();
            // AZ: no refreshing to speed-up processing
            // HibernateDao.refresh(artist);
            artist.getDiscs().remove(disc);
            // HibernateDao.refresh(disc);
            HibernateDao.delete(disc);
            HibernateUtil.commitTransaction();

            // AZ : If transaction is committed and copies of images are used
            // then delete the image copy
            if (this.getSettings().isCopyImagesToDirectory()) {
               final String oldImageName = ImageFactory.standardImageFileName(artist.getName(), disc.getName(), disc
                        .getYear());
               File oldImageFile = new File(oldImageName);
               if (oldImageFile.exists()) {
                  if (!oldImageFile.delete()) {
                     LOG.debug("Error deleting file: " + oldImageFile.getAbsolutePath());
                  }
               }
            }
            // reset dirty flag since we are deleting
            getValidationModel().setDirty(false);
         } catch (Exception ex) {
            final String errorMessage = ResourceUtils.getString("messages.ErrorDeletingDisc");
            MessageUtil.showError(this, errorMessage); // AZ
            LOG.error(errorMessage, ex);
            HibernateUtil.rollbackTransaction();
         } finally {
            setBusyCursor(false);
            // refresh main tree
            ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
         }
      }
   }

   /**
    * Rename the file to a good format.
    */
   @Override
   public void renameFiles() {
      updateModel();

      // check for validation errors, if any then do no changes
      if (hasErrors()) {
         LOG.error(Resources.getString("messages.editorerrors"));
         MessageUtil.showError(this, Resources.getString("messages.editorerrors")); // AZ
         return;
      }

      if (musicTag == null) {
         LOG.error(Resources.getString("messages.filenotexists"));
         MessageUtil.showError(this, Resources.getString("messages.filenotexists")); // AZ
         return;
      }

      try {
         setBusyCursor(true);
         if (this.musicTag.renameFile(this.getSettings().getFileFormatMusic())) {
            getTrack().setTrackUrl(this.musicTag.getAbsolutePath());
            commit();
         }
      } catch (InfrastructureException ex) {
         LOG.error(ex.getMessage());
         MessageUtil.showError(this, ex.getMessage());
      } catch (Exception ex) {
         LOG.error(Resources.getString("messages.ErrorRenamingFile"), ex);
         MessageUtil.showError(this, Resources.getString("messages.ErrorRenamingFile"));
      } finally {
         setBusyCursor(false);
      }
   }

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.gui.view.editor.AbstractEditor#rollback()
    */
   @Override
   public void rollback() {
      super.rollback();
      try {
         setBusyCursor(true);
         // try to reload from database
         Track track = getTrack();
         HibernateDao.refresh(track);
         updateView();
         super.rollback();
      } catch (Exception ex) {
         final String errorMessage = ResourceUtils.getString("messages.ErrorRefreshingTrack");
         LOG.error(errorMessage, ex);
         MessageUtil.showError(this, errorMessage); // AZ
      } finally {
         setBusyCursor(false);
      }
   }

   /**
    * Gets the title for the title bar.
    * <p>
    * @return the title to put on the title bar
    */
   @Override
   protected String getTitleSuffix() {
      return getTrack().getDisplayText(getSettings().getDisplayFormatTrack());
   }

   /**
    * Writes view contents to the underlying model.
    */
   @Override
   protected void updateModel() {
      Track track = getTrack();

      // compare any fields that may have changed.
      if (!StringUtils.equals(track.getName(), titleField.getText())) {
         track.setName(titleField.getText());
      }
      if (!StringUtils.equalsIgnoreCase(track.getTrackNumber(), trackNumber.getText())) {
         track.setTrackNumber(trackNumber.getText());
      }
      if (!StringUtils.equalsIgnoreCase(track.getComment(), comment.getText())) {
         track.setComment(comment.getText());
      }

      if (musicTag != null) {
         musicTag.setTitle(titleField.getText());
         musicTag.setTrack(trackNumber.getText());
         musicTag.setComment(comment.getText());
      }
   }

   /**
    * Reads view contents from the underlying model.
    */
   @Override
   protected void updateView() {
      final String currentCoverUrl;
      Track track = getTrack();
      titleField.setText(track.getName());
      trackNumber.setText(track.getTrackNumber());
      comment.setText(track.getComment());
      location.setText(track.getTrackUrl());
      location.setToolTipText(track.getTrackUrl());
      createdDateLabel.setText(DATE_FORMAT.format(track.getCreatedDate()));
      createdByLabel.setText(track.getCreatedUser());
      modifiedDateLabel.setText(DATE_FORMAT.format(track.getModifiedDate()));
      modifiedByLabel.setText(track.getModifiedUser());
      albumImage.setDisc(track.getDisc());
      if (this.getSettings().isCopyImagesToDirectory()) {
         currentCoverUrl = ImageFactory.standardImageFileName(track.getDisc().getArtist().getName(), track.getDisc()
                  .getName(), track.getDisc().getYear());
      } else {
         currentCoverUrl = track.getDisc().getCoverUrl();
      }
      if (StringUtils.isNotBlank(currentCoverUrl)) {
         int dimension = this.getSettings().getCoverSizeSmall();
         albumImage.setImage(ImageFactory.getScaledImage(currentCoverUrl, dimension, dimension).getImage());
      } else {
         albumImage.setImage(null);
      }

      // try and open the tag, but fail safely
      try {
         // AZ: verify if file is accessible
         final File file = new File(track.getTrackUrl());
         if (file.exists()) {
            musicTag = TagFactory.getTag(track.getTrackUrl());
            duration.setText(musicTag.getTrackLengthAsString());
            layer.setText(musicTag.getLayer());
            version.setText(musicTag.getVersion());
            bitRate.setText(musicTag.getBitRateAsString());
            frequency.setText(musicTag.getFrequency() + " Hz");
            mode.setText(musicTag.getMode());
            fileSize.setText(FileUtils.byteCountToDisplaySize(musicTag.getFile().length()));
            emphasis.setText(musicTag.getEmphasis());
            copyrighted.setText(musicTag.getCopyrighted());
         } else {
            LOG.debug("File: " + track.getTrackUrl() + " is not accessible");
            duration.setText("");
            layer.setText("");
            version.setText("");
            bitRate.setText("");
            frequency.setText("");
            mode.setText("");
            fileSize.setText("");
            emphasis.setText("");
            copyrighted.setText("");
            musicTag = null;
         }
      } catch (MusicTagException ex) {
         LOG.info(ex.getMessage(), ex);
         duration.setText("");
         layer.setText("");
         version.setText("");
         bitRate.setText("");
         frequency.setText("");
         mode.setText("");
         fileSize.setText("");
         emphasis.setText("");
         copyrighted.setText("");
         musicTag = null;
      }
   }

   /**
    * Gets the domain object associated with this editor.
    * <p>
    * @return an Track instance associated with this editor
    */
   private Track getTrack() {
      return (Track) getModel();
   }

   /**
    * Builds the Music information panel.
    * <p>
    * @return the panel to display the music info
    */
   private JComponent buildMusicPanel() {
      FormLayout layout = new FormLayout(
               "right:max(14dlu;pref), 4dlu, left:min(80dlu;pref), 100px, right:max(14dlu;pref),pref:grow, 4px",
               "p, 4px, p, 4px, p, 4px, p, 4px, p, 4px");

      layout.setRowGroups(new int[][] { { 1, 3 } });
      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();
      builder.addLabel(Resources.getString("label.file") + ": ", cc.xy(1, 1));
      builder.add(location, cc.xyw(3, 1, 5));
      builder.addLabel(Resources.getString("label.duration") + ": ", cc.xy(1, 3));
      builder.add(duration, cc.xy(3, 3));
      builder.addLabel(Resources.getString("label.layer") + ": ", cc.xy(5, 3));
      builder.add(layer, cc.xy(6, 3));
      builder.addLabel(Resources.getString("label.bitrate") + ": ", cc.xy(1, 5));
      builder.add(bitRate, cc.xyw(3, 5, 4));
      builder.addLabel(Resources.getString("label.version") + ": ", cc.xy(5, 5));
      builder.add(version, cc.xy(6, 5));
      builder.addLabel(Resources.getString("label.frequency") + ": ", cc.xy(1, 7));
      builder.add(frequency, cc.xy(3, 7));
      builder.addLabel(Resources.getString("label.mode") + ": ", cc.xy(5, 7));
      builder.add(mode, cc.xy(6, 7));
      builder.addLabel(Resources.getString("label.filesize") + ": ", cc.xy(1, 9));
      builder.add(fileSize, cc.xy(3, 9));
      builder.addLabel(Resources.getString("label.copyright") + ": ", cc.xy(5, 9));
      builder.add(copyrighted, cc.xy(6, 9));

      return builder.getPanel();
   }

   private JToolBar buildToolBar() {
      final ToolBarBuilder bar = new ToolBarBuilder("Track Toolbar");
      ToolBarButton button = null;
      button = (ToolBarButton) ComponentFactory.createToolBarButton(Actions.UNLOCK_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (ToolBarButton) ComponentFactory.createToolBarButton(Actions.COMMIT_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (ToolBarButton) ComponentFactory.createToolBarButton(Actions.ROLLBACK_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (ToolBarButton) ComponentFactory.createToolBarButton(Actions.DELETE_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      button = (ToolBarButton) ComponentFactory.createToolBarButton(Actions.FILE_RENAME_ID);
      button.putClientProperty(Resources.EDITOR_COMPONENT, this);
      bar.add(button);
      return bar.getToolBar();
   }

   /**
    * Builds the Track editor panel.
    * <p>
    * @return the panel to edit track info. AZ - FormLayout corrections
    */
   private JComponent buildTrackPanel() {
      FormLayout layout = new FormLayout(
               "right:max(14dlu;pref), 4dlu, left:20dlu, left:140dlu, 4dlu, left:25px, right:pref:grow",
               "4px, p, 4px, p, 4px, p, " + this.getSettings().getCoverSizeSmall() + "px");
      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();

      builder.addLabel(Resources.getString("label.tracknumber") + ": ", cc.xy(1, 2));
      builder.add(trackNumber, cc.xy(3, 2));
      builder.add(albumImage, cc.xywh(7, 2, 1, 6, "right, top"));
      builder.addLabel(Resources.getString("label.title") + ": ", cc.xy(1, 4));
      builder.add(titleField, cc.xyw(3, 4, 2));
      builder.add(ComponentFactory.createTitleCaseButton(titleField), cc.xy(6, 4));
      builder.addLabel(Resources.getString("label.comment") + ": ", cc.xy(1, 6, "right, top"));
      builder.add(comment, cc.xyw(3, 6, 2));
      return new IconFeedbackPanel(getValidationModel().getValidationResultModel(), builder.getPanel());
   }

   /**
    * Initializes validation annotations.
    */
   private void initComponentAnnotations() {
      ValidationComponentUtils.setInputHint(titleField, Resources.getString("messages.TitleIsMandatory"));
      ValidationComponentUtils.setMandatory(titleField, true);
      ValidationComponentUtils.setMessageKey(titleField, "Track.Title");
      ValidationComponentUtils.setInputHint(trackNumber, Resources.getString("messages.NumberIsMandatory"));
      ValidationComponentUtils.setMandatory(trackNumber, true);
      ValidationComponentUtils.setMessageKey(trackNumber, "Track.Track Number");
      ValidationComponentUtils.setInputHint(comment, Resources.getString("messages.NotesLength254"));
      ValidationComponentUtils.setMessageKey(comment, "Track.Comment");
   }

   /**
    * Creates and configures the UI components;
    */
   private void initComponents() {
      validationModel = new TrackValidationModel(new Track());

      headerToolbar = buildToolBar();
      titleField = ComponentFactory.createTextField(validationModel.getModel(Track.PROPERTYNAME_NAME), false);
      trackNumber = ComponentFactory.createTextField(validationModel.getModel(Track.PROPERTYNAME_TRACK_NUMBER), false);
      ((JTextField) trackNumber).setColumns(4);
      // comment =
      // ComponentFactory.createTextField(validationModel.getModel(Track.PROPERTYNAME_COMMENT),
      // false);
      /** AZ - use multi-line area for comment **/
      comment = ComponentFactory.createTextArea(validationModel.getModel(Track.PROPERTYNAME_COMMENT), false);
      comment.setLineWrap(true);
      comment.setWrapStyleWord(true);
      comment.setRows(3);

      location = ComponentFactory.createLabel(getValidationModel().getModel(Track.PROPERTYNAME_TRACK_URL));
      duration = UIFactory.createBoldLabel("");
      layer = UIFactory.createBoldLabel("");
      version = UIFactory.createBoldLabel("");
      bitRate = UIFactory.createBoldLabel("");
      frequency = UIFactory.createBoldLabel("");
      mode = UIFactory.createBoldLabel("");
      fileSize = UIFactory.createBoldLabel("");
      emphasis = UIFactory.createBoldLabel("");
      copyrighted = UIFactory.createBoldLabel("");

      albumImage = new AlbumImage(new Dimension(this.getSettings().getCoverSizeSmall(), this.getSettings()
               .getCoverSizeSmall()));
      ActionListener actionListener = new ActionListener() {
         public void actionPerformed(ActionEvent event) {
            AlbumImage preview = (AlbumImage) event.getSource();
            if (preview.getDisc() != null) {
               setBusyCursor(true);
               getMainModule().selectNodeInTree(preview.getDisc());
               setBusyCursor(false);
            }
         }
      };
      albumImage.addActionListener(actionListener);
   }

}