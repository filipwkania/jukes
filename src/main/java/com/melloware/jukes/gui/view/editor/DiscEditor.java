package com.melloware.jukes.gui.view.editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.binding.adapter.ComboBoxAdapter;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uif.component.ToolBarButton;
import com.jgoodies.uif.panel.SimpleInternalFrame;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uifextras.util.UIFactory;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.file.filter.ImageFilter;
import com.melloware.jukes.file.image.ChooserImagePreview;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.file.image.ImageFileView;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.file.tag.TagFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.MainMenuBuilder;
import com.melloware.jukes.gui.view.component.AlbumImage;
import com.melloware.jukes.gui.view.component.ComponentFactory;
import com.melloware.jukes.gui.view.component.TrackListCellRenderer;
import com.melloware.jukes.gui.view.tasks.TimerListener;
import com.melloware.jukes.gui.view.tasks.UpdateTagsTask;
import com.melloware.jukes.gui.view.validation.DiscValidationModel;
import com.melloware.jukes.gui.view.validation.ArtistValidationModel; //AZ
import com.melloware.jukes.gui.view.validation.IconFeedbackPanel;
import com.melloware.jukes.util.JukesValidationMessage;
import com.melloware.jukes.util.MessageUtil;
import com.melloware.jukes.gui.view.dialogs.FreeDBDialog;//AZ

/**
 * An implementation of {@link Editor} that displays a {@link Disc}.
 * <p>
 * 
 * This container uses a <code>FormLayout</code> and the panel building is done
 * with the <code>PanelBuilder</code> class. Columns and rows are specified
 * before the panel is filled with components.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * 
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ Development 2009, 2010
 */
@SuppressWarnings("unchecked")
public final class DiscEditor extends AbstractEditor {

	private static final Log LOG = LogFactory.getLog(DiscEditor.class);
	private static final List GENRE_LIST = MusicTag.getGenreTypes();
	private AlbumImage albumImage;
	private DefaultListModel listModel;
	private JComboBox genre;
	private JComponent discPanel;
	private JComponent genreEditor;
	private JLabel genreLabel;
	private JLabel location;
	private JList trackList;
	private JTextArea notesField;
	private JTextComponent nameField;
	private JTextComponent year;
	private JTextComponent artistField; // AZ artistField
	private JToolBar headerToolbar;
	private List tracks;
	private SimpleInternalFrame listScrollPane;

	/**
	 * Constructs a <code>DiscEditor</code>.
	 */
	public DiscEditor() {
		super(Resources.DISC_TREE_ICON);
	}

	/**
	 * Gets the domain class associated with this editor.
	 */
	public Class getDomainClass() {
		return Disc.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.melloware.jukes.gui.view.editor.AbstractEditor#getHeaderToolBar()
	 */
	public JToolBar getHeaderToolBar() {
		return headerToolbar;
	}

	/**
	 * Builds the content pane.
	 */
	public void build() {
		initComponents();
		initComponentAnnotations();
		initEventHandling();

		discPanel = buildDiscPanel();

		FormLayout layout = new FormLayout("fill:pref:grow",
				"p, 12px, p, p, 12px, p, 12px, p, 12px, p");

		setLayout(layout);
		PanelBuilder builder = new PanelBuilder(layout, this);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		builder.add(buildMusicPanel(), cc.xy(1, 1));
		builder.add(buildHintAreaPane(), cc.xy(1, 3));
		builder.addSeparator(Resources.getString("label.disc"), cc.xy(1, 4));
		builder.add(discPanel, cc.xy(1, 6));
		JComponent audit = buildAuditInfoPanel();
		if (this.getSettings().isAuditInfo()) {
			builder.addSeparator(Resources.getString("label.auditinfo"),
					cc.xy(1, 8));
			builder.add(audit, cc.xy(1, 10));
		}
	}

	/**
	 * Commits with the update tags flag ON meaning it will write the ID3 tags
	 * to all tracks. AZ - commit with update flag as specified in Settings
	 */
	public void commit() {
		/** AZ **/
		commit(this.getSettings().isUpdateTags());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.melloware.jukes.gui.view.editor.AbstractEditor#delete()
	 */
	public void delete() {
		super.delete();
		try {
			if (!MessageUtil.confirmDelete(this)) {
				return;
			}
			// try to delete from database
			setBusyCursor(true);
			HibernateUtil.beginTransaction();

			Disc disc = getDisc();
			final Artist artist = disc.getArtist();
			// AZ: no refreshing to speed-up processing
			// HibernateDao.refresh(artist);
			artist.getDiscs().remove(disc);
			// HibernateDao.refresh(disc);
			artist.getDiscs().remove(disc);
			;
			HibernateDao.delete(disc);
			HibernateUtil.commitTransaction();

			// AZ : If transaction is committed and copies of images are used
			// then delete the image copy
			if (this.getSettings().isCopyImagesToDirectory()) {
				final String oldImageName = ImageFactory.standardImageFileName(
						artist.getName(), disc.getName(), disc.getYear());
				File oldImageFile = new File(oldImageName);
				if (oldImageFile.exists()) {
					if (!oldImageFile.delete()) {
						LOG.debug("Error deleting file: "
								+ oldImageFile.getAbsolutePath());
					}
				}
			}

			// reset dirty flag since we are deleting
			getValidationModel().setDirty(false);

			if (artist.getDiscs().size() == 0) {
				// refresh the tree
				ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
			} else {
				// tell the tree to select the parent node
				this.getMainModule().refreshSelection(disc,
						Resources.NODE_DELETED);
			}
		} catch (Exception ex) {
			final String errorMessage = ResourceUtils
					.getString("messages.ErrorDeletingDisc");
			MessageUtil.showError(this, errorMessage);
			LOG.error(errorMessage, ex);
			HibernateUtil.rollbackTransaction();
		} finally {
			setBusyCursor(false);
		}
	}

	/**
	 * Let the user select another album cover.
	 */
	public void findCover() {
		Disc disc = getDisc();
		if (disc.isNotValid()) {
			final String errorMessage = ResourceUtils
					.getString("messages.discnotexists");
			LOG.error(errorMessage);
			MessageUtil.showError(this, errorMessage); // AZ
			return;
		}

		updateModel();

		// check for validation errors, if any then do no changes
		boolean hasErrors = hasErrors();
		if (hasErrors) {
			LOG.error(Resources.MESSAGE_EDITOR_ERRORS);
			MessageUtil.showError(this, Resources.MESSAGE_EDITOR_ERRORS); // AZ
			return;
		}

		final File currentDir = new File(getDisc().getLocation());
		JFileChooser chooser = new JFileChooser();
		chooser.setApproveButtonText("Select");
		chooser.setDialogTitle("Find Cover Image");
		chooser.setCurrentDirectory(currentDir);
		chooser.addChoosableFileFilter(FilterFactory.imageFileFilter());
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileView(new ImageFileView());
		chooser.setAccessory(new ChooserImagePreview(chooser));
		chooser.setMultiSelectionEnabled(false);
		int returnVal = chooser.showOpenDialog(Application
				.getDefaultParentFrame());
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = chooser.getSelectedFile();
		/** AZ **/
		// now scale and copy cover image depending on settings
		String imageLocation;
		try {
			imageLocation = ImageFactory.saveImageToUserDefinedDirectory(file,
					disc.getArtist().getName(), disc.getName().toString(), disc
							.getYear().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			imageLocation = file.getAbsolutePath();
		}
		// try to persist
		try {
			setBusyCursor(true);
			HibernateUtil.beginTransaction();
			disc.setCoverUrl(imageLocation);
			HibernateDao.persist(disc);
			HibernateUtil.commitTransaction();

			// now update this editor
			updateView();
		} catch (InfrastructureException ex) {
			final String errorMessage = ResourceUtils
					.getString("messages.DiscUnique");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage, ex);
			HibernateUtil.rollbackTransaction();
			HibernateDao.refresh(disc);
		} catch (Exception ex) {
			final String errorMessage = ResourceUtils
					.getString("messages.ErrorUpdatingCoverImage");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage, ex);
			HibernateUtil.rollbackTransaction();
			HibernateDao.refresh(disc);
		} finally {
			setBusyCursor(false);
		}
	}

	/**
	 * Rename the file to a good format.
	 */
	public void renameFiles() {
		if (getDisc().isNotValid()) {
			final String errorMessage = ResourceUtils
					.getString("messages.discnotexists");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage);
			return;
		}

		updateModel();

		// check for validation errors, if any then do no changes
		boolean hasErrors = hasErrors();
		if (hasErrors) {
			LOG.error(Resources.MESSAGE_EDITOR_ERRORS);
			MessageUtil.showError(this, Resources.MESSAGE_EDITOR_ERRORS); // AZ
			return;
		}

		MusicTag musicTag = null;
		try {
			setBusyCursor(true);
			for (Iterator iter = getDisc().getTracks().iterator(); iter
					.hasNext();) {
				Track track = (Track) iter.next();
				final File file = new File(track.getTrackUrl());

				if (file.exists()) {
					musicTag = TagFactory.getTag(file);
					if (musicTag.renameFile(this.getSettings()
							.getFileFormatMusic())) {
						track.setTrackUrl(musicTag.getAbsolutePath());
					}
				}
			}
			commit(false);
		} catch (InfrastructureException ex) {
			LOG.error(ex.getMessage());
			MessageUtil.showError(this, ex.getMessage()); // AZ
		} catch (Exception ex) {
			final String errorMessage = ResourceUtils
					.getString("messages.ErrorRenamingFile");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage, ex);
		} finally {
			setBusyCursor(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.melloware.jukes.gui.view.editor.AbstractEditor#rollback()
	 */
	public void rollback() {
		super.rollback();
		try {
			setBusyCursor(true);
			// try to reload from database
			Disc disc = getDisc();
			HibernateDao.refresh(disc);
			updateView();
			super.rollback();
		} catch (Exception ex) {
			final String errorMessage = ResourceUtils
					.getString("messages.ErrorRefreshingDisc")
					+ ": "
					+ getDisc().getName();
			MessageUtil.showError(this, errorMessage);
			LOG.error(errorMessage, ex);
		} finally {
			setBusyCursor(false);
		}
	}

	/**
	 * AZ Performs the FreeDB service search.
	 */
	public void freeDBSearch() {
		super.freeDBSearch();
		final Disc disc = getDisc();
		if (disc.isNotValid()) {
			final String errorMessage = ResourceUtils
					.getString("messages.discnotexists");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage);
			return;
		}
		// check for validation errors, if any then do no changes
		if (hasErrors()) {
			LOG.error(Resources.MESSAGE_EDITOR_ERRORS);
			MessageUtil.showError(this, Resources.MESSAGE_EDITOR_ERRORS);
			return;
		}
		Iterator it;
		final FreeDBDialog dialog = new FreeDBDialog(getMainFrame(),
				getSettings());
		dialog.setSelectedArtist(disc.getArtist().getName());
		dialog.setSelectedDisc(disc.getName());
		// Fill the Set of track lengths
		float[] trackLength = new float[disc.getTracks().size()];
		it = disc.getTracks().iterator();
		int i = 0;
		while (it.hasNext()) {
			trackLength[i] = ((Track) it.next()).getDuration();
			i++;
		}
		dialog.setTrackLength(trackLength);
		dialog.open();

		// if the user did not select anything
		if (dialog.hasBeenCanceled()) {
			return;
		}

		// flag for if this year or name was modified
		boolean modified = false;

		// if disc is not blank and does not contain a subtitle like - Disc 1
		final String[] checkList = { "- Disc", "-Disc", "- disc" };
		if (((StringUtils.isNotBlank(dialog.getSelectedDisc())) && (StringUtils
				.indexOfAny(nameField.getText(), checkList) <= 0))) {
			if (!(dialog.getSelectedDisc().equals(nameField.getText()))) {
				disc.setName(dialog.getSelectedDisc());
				nameField.setText(dialog.getSelectedDisc());
				modified = true;
			}
		}
		if ((StringUtils.isNotBlank(dialog.getSelectedYear()))
				&& (Integer.valueOf(dialog.getSelectedYear()).intValue() != Integer
						.valueOf(year.getText()).intValue())) {
			disc.setYear(dialog.getSelectedYear());
			year.setText(dialog.getSelectedYear());
			modified = true;
		}

		if (StringUtils.isNotBlank(dialog.getSelectedArtist())) {
			if (!(dialog.getSelectedArtist().equals(artistField.getText()))) {
				artistField.setText(dialog.getSelectedArtist());
				modified = true;
			}
		}

		if (StringUtils.isNotBlank(dialog.getSelectedGenre())) {
			if (!(dialog.getSelectedGenre().equals(genre.getSelectedItem()))) {
				genre.setSelectedItem(dialog.getSelectedGenre());
				modified = true;
			}
		}

		if (dialog.getSelectedTracks() != null) {
			if (dialog.getSelectedTracks().size() == disc.getTracks().size()) {
				int ii = 0;
				List trackList = new ArrayList(dialog.getSelectedTracks());
				for (Iterator iter = getDisc().getTracks().iterator(); iter
						.hasNext();) {
					Track track = (Track) iter.next();
					track.setName(trackList.get(ii).toString());
					ii = ii + 1;
				}
				modified = true;
			} else {
				final String errorMessage = ResourceUtils
						.getString("messages.WrongNumberOfTracks");
				MessageUtil.showwarn(this, errorMessage);
				LOG.error(errorMessage);
			}
		}

		// update dataModel and view for changes //AZ
		if (modified) {
			updateModel();
			updateView();
		}
	}

	/**
	 * Gets the title for the title bar.
	 * <p>
	 * 
	 * @return the title to put on the title bar
	 */
	protected String getTitleSuffix() {
		return getDisc().getDisplayText(getSettings().getDisplayFormatDisc());
	}

	/**
	 * Writes view contents to the underlying model.
	 */
	protected void updateModel() {
		Disc disc = getDisc();
		Artist foundArtist = null;
		// compare any fields that may have changed.
		if (!StringUtils.equals(disc.getName(), nameField.getText())) {
			disc.setName(nameField.getText());
		}

		if (!StringUtils.equalsIgnoreCase(disc.getYear(), year.getText())) {
			disc.setYear(year.getText());
		}
		if (!StringUtils.equalsIgnoreCase(disc.getGenre(),
				(String) genre.getSelectedItem())) {
			disc.setGenre((String) genre.getSelectedItem());
		}

		if (!StringUtils
				.equalsIgnoreCase(disc.getNotes(), notesField.getText())) {
			disc.setNotes(notesField.getText());
		}
		/** AZ set Artist **/
		// AZ - Check for empty Artist field. In addition to standard Validation
		// method.
		if ((artistField.getText() == null)
				|| (ValidationUtils.isBlank(artistField.getText()))) {
			final String errorMessage = ResourceUtils
					.getString("messages.UpdateModel")
					+ Resources.MESSAGE_EDITOR_ERRORS
					+ "\n "
					+ ResourceUtils.getString("messages.ArtistNameMandatory");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage);
		} else if (!StringUtils.equalsIgnoreCase(disc.getArtist().getName(),
				artistField.getText())) {
			// try to persist
			try {
				setBusyCursor(true);
				// see if this artist exists
				final String resource = ResourceUtils
						.getString("hql.artist.findCaseSensitive");
				final String hql = MessageFormat.format(resource,
						new Object[] { StringEscapeUtils.escapeSql(artistField
								.getText()) });
				foundArtist = (Artist) HibernateDao.findUniqueByQuery(hql);
				// If Artists does not exist in database
				if (foundArtist == null) {
					final String errorMessage = ResourceUtils
							.getString("messages.ErrorUpdatingArtistNotFound")
							+ artistField.getText();
					LOG.error(errorMessage);
					MessageUtil.showError(this, errorMessage); // AZ
				}
				if (foundArtist != null) {
					// if artist was found
					disc.setArtist(foundArtist);
				}
			} catch (InfrastructureException ex) {
				final String errorMessage = ResourceUtils
						.getString("messages.ArtistNameUnique");
				LOG.error(errorMessage);
				MessageUtil.showError(this, errorMessage); // AZ
			} catch (Exception ex) {
				final String errorMessage = ResourceUtils
						.getString("messages.ErrorUpdatingArtist");
				LOG.error(errorMessage, ex);
				MessageUtil.showError(this, errorMessage); // AZ
			} finally {
				setBusyCursor(false);
			}

		}
	}

	/**
	 * Reads view contents from the underlying model.
	 */
	protected void updateView() {
		Disc disc = getDisc();
		final String currentCoverUrl;
		// reset any fields
		genre.setSelectedItem(null);
		// load new values
		nameField.setText(disc.getName());
		year.setText(disc.getYear());
		genre.setSelectedItem(disc.getGenre());
		genreLabel.setText(disc.getGenre());
		location.setText(disc.getLocation());
		location.setToolTipText(disc.getLocation());
		notesField.setText(disc.getNotes());
		artistField.setText(disc.getArtist().getName()); // AZ
		createdDateLabel.setText(DATE_FORMAT.format(disc.getCreatedDate()));
		createdByLabel.setText(disc.getCreatedUser());
		modifiedDateLabel.setText(DATE_FORMAT.format(disc.getModifiedDate()));
		modifiedByLabel.setText(disc.getModifiedUser());
		listModel.removeAllElements();
		tracks = new ArrayList();
		for (Iterator iter = disc.getTracks().iterator(); iter.hasNext();) {
			final Track track = (Track) iter.next();
			final String name = track.getDisplayText(getSettings()
					.getDisplayFormatTrack());
			final JukesValidationMessage message = new JukesValidationMessage(
					name, null, track);
			tracks.add(message);
		}
		Collections.sort(tracks, TrackListCellRenderer.TRACK_COMPARATOR);
		for (Iterator iter = tracks.iterator(); iter.hasNext();) {
			JukesValidationMessage message = (JukesValidationMessage) iter
					.next();
			listModel.addElement(message);
		}

		// load the album cover
		int dimension = this.getSettings().getCoverSizeLarge();
		albumImage.setDisc(disc);
		if (this.getSettings().isCopyImagesToDirectory()) {
			currentCoverUrl = ImageFactory.standardImageFileName(disc
					.getArtist().getName(), disc.getName(), disc.getYear());
		} else {
			currentCoverUrl = disc.getCoverUrl();
		}
		albumImage.setImage(ImageFactory.getScaledImage(currentCoverUrl,
				dimension, dimension).getImage());
	}

	/**
	 * Gets the domain object associated with this editor.
	 * <p>
	 * 
	 * @return an Disc instance associated with this editor
	 */
	private Disc getDisc() {
		return (Disc) getModel();
	}

	/**
	 * Builds the Disc editor panel.
	 * <p>
	 * 
	 * @return the panel to edit disc info.
	 */
	private JComponent buildDiscPanel() {

		FormLayout layout = new FormLayout(
				"right:max(14dlu;pref), 4dlu, left:min(80dlu;pref):grow, pref, 4dlu, pref, 25px",
				"p, 4px, p, 4px, p, 4px, p, 4px, p, 4px, p, 4px");

		layout.setRowGroups(new int[][] { { 1, 3, 5 } });
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		/** AZ - add artistField **/
		builder.addLabel(Resources.getString("label.artist") + ": ",
				cc.xy(1, 1));
		builder.add(artistField, cc.xyw(3, 1, 3));

		builder.addLabel(Resources.getString("label.discName") + ": ", cc.xy(1, 3));
		builder.add(nameField, cc.xyw(3, 3, 3));
		builder.add(ComponentFactory.createTitleCaseButton(nameField),
				cc.xy(6, 3));
		builder.addLabel(Resources.getString("label.genre") + ": ", cc.xy(1, 5));
		builder.add(genreLabel, cc.xyw(2, 5, 2));
		builder.add(genre, cc.xyw(3, 5, 3));
		builder.addLabel(Resources.getString("label.year") + ": ", cc.xy(1, 7));
		builder.add(year, cc.xy(3, 7));
		builder.addLabel(Resources.getString("label.file") + ": ", cc.xy(1, 9));
		builder.add(location, cc.xyw(3, 9, 5));
		builder.addLabel(Resources.getString("label.notes") + ": ",
				cc.xy(1, 11, "left,top"));
		builder.add(notesField, cc.xyw(3, 11, 3));

		return new IconFeedbackPanel(getValidationModel()
				.getValidationResultModel(), builder.getPanel());
	}

	/**
	 * Builds the Music information panel.
	 * <p>
	 * 
	 * @return the panel to display the music info
	 */
	private JComponent buildMusicPanel() {
		FormLayout layout = new FormLayout("left:pref, 4dlu, right:pref", "p");

		layout.setRowGroups(new int[][] { { 1 } });
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(listScrollPane, cc.xy(1, 1, "left,top"));
		builder.add(albumImage, cc.xy(3, 1, "left,top"));
		return builder.getPanel();
	}

	private JToolBar buildToolBar() {
		final ToolBarBuilder bar = new ToolBarBuilder("Disc Toolbar");
		ToolBarButton button = null;
		button = (ToolBarButton) ComponentFactory
				.createToolBarButton(Actions.UNLOCK_ID);
		button.putClientProperty(Resources.EDITOR_COMPONENT, this);
		bar.add(button);
		button = (ToolBarButton) ComponentFactory
				.createToolBarButton(Actions.COMMIT_ID);
		button.putClientProperty(Resources.EDITOR_COMPONENT, this);
		bar.add(button);
		button = (ToolBarButton) ComponentFactory
				.createToolBarButton(Actions.ROLLBACK_ID);
		button.putClientProperty(Resources.EDITOR_COMPONENT, this);
		bar.add(button);
		button = (ToolBarButton) ComponentFactory
				.createToolBarButton(Actions.DELETE_ID);
		button.putClientProperty(Resources.EDITOR_COMPONENT, this);
		bar.add(button);
		button = (ToolBarButton) ComponentFactory
				.createToolBarButton(Actions.FILE_RENAME_ID);
		button.putClientProperty(Resources.EDITOR_COMPONENT, this);
		bar.add(button);
		button = (ToolBarButton) ComponentFactory
				.createToolBarButton(Actions.DISC_COVER_ID);
		button.putClientProperty(Resources.EDITOR_COMPONENT, this);
		bar.add(button);
		button = (ToolBarButton) ComponentFactory
				.createToolBarButton(Actions.FREE_DB_ID);
		button.putClientProperty(Resources.EDITOR_COMPONENT, this);
		bar.add(button);
		return bar.getToolBar();
	}

	/**
	 * Commits to the database and if updateTags flag is set to true it updates
	 * the ID3 tags as well.
	 * <p>
	 * 
	 * @param aUpdateTags
	 *            true to update tags, false to not update tags
	 */
	private void commit(boolean aUpdateTags) {
		super.commit();
		final Disc disc = getDisc();
		final String temporaryDiscName = disc.getName(); // AZ Store disc to
															// rollback
		final String temporaryDiscYear = disc.getYear();
		final String temporaryDiscGenre = disc.getGenre();
		final String temporaryDiscNotes = disc.getNotes();
		final String oldImageName = ImageFactory.standardImageFileName(disc
				.getArtist().getName(), disc.getName(), disc.getYear());
		Artist foundArtist = null;
		Artist artist = null;
		// AZ - Check for empty Artist field. In addition to standard Validation
		// method.
		if ((artistField.getText() == null)
				|| (ValidationUtils.isBlank(artistField.getText()))) {
			final String errorMessage = Resources.MESSAGE_EDITOR_ERRORS + "\n"
					+ ResourceUtils.getString("messages.ArtistNameMandatory");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage);
			return;
		}
		// AZ - check Artist field length
		if (artistField.getText().length() > 100) {
			final String errorMessage = Resources.MESSAGE_EDITOR_ERRORS + "\n"
					+ ResourceUtils.getString("messages.ArtistNameLength")
					+ " <= 100";
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage);
			return;
		}
		// AZ - check Notes field length
		if (notesField.getText().length() > 500) {
			final String errorMessage = Resources.MESSAGE_EDITOR_ERRORS + "\n"
					+ ResourceUtils.getString("messages.NotesLength");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage);
			return;
		}

		boolean artistChanged = (!StringUtils.equalsIgnoreCase(disc.getArtist()
				.getName(), artistField.getText()));

		// check for validation errors, if any then do no changes
		boolean hasErrors = hasErrors();
		if (hasErrors) {
			LOG.error(Resources.MESSAGE_EDITOR_ERRORS);
			MessageUtil.showError(this, Resources.MESSAGE_EDITOR_ERRORS); // AZ
			return;
		}
		// AZ : trying to find Artist
		// try to persist
		if (artistChanged) {
			try {
				setBusyCursor(true);
				// see if this artist exists
				final String resource = ResourceUtils
						.getString("hql.artist.findCaseSensitive");
				final String hql = MessageFormat.format(resource,
						new Object[] { StringEscapeUtils.escapeSql(artistField
								.getText()) });
				foundArtist = (Artist) HibernateDao.findUniqueByQuery(hql);
				if (foundArtist == null) {
					// if artist was not found then add new Artist
					artist = new Artist();
					artist.setName(artistField.getText());
					artist.setNotes("");
					HibernateUtil.beginTransaction();
					HibernateDao.saveOrUpdate(artist);
					HibernateUtil.commitTransaction();
				} else {
					// if artist was found
					LOG.debug("DiscEditor - Change Artist, Artist was found in Database: "
							+ artistField.getText());
				}
			} catch (InfrastructureException ex) {
				final String errorMessage = ResourceUtils
						.getString("messages.ArtistNameUnique");
				MessageUtil.showError(this, errorMessage); // AZ
				LOG.error(errorMessage);
			} catch (Exception ex) {
				final String errorMessage = ResourceUtils
						.getString("messages.ErrorUpdatingArtist");
				MessageUtil.showError(this, errorMessage); // AZ
				LOG.error(errorMessage, ex);
			} finally {
				setBusyCursor(false);
			}
		} // if artistChanged

		updateModel();

		// try to persist
		try {
			setBusyCursor(true);
			HibernateUtil.beginTransaction();
			HibernateDao.saveOrUpdate(disc);
			HibernateUtil.commitTransaction();
			// AZ : If transaction is committed and copies of images are used
			// then change the name of the image copy
			if (this.getSettings().isCopyImagesToDirectory()) {
				File oldImageFile = new File(oldImageName);
				if (oldImageFile.exists()) {
					final String newImageName = ImageFactory
							.standardImageFileName(artistField.getText(),
									disc.getName(), disc.getYear());
					final File newImageFile = new File(newImageName);
					oldImageFile.renameTo(newImageFile);
				}
			}
			// now update this editor and the treeview
			updateView();
			this.getMainModule().refreshSelection(disc, Resources.NODE_CHANGED);
			if (artistChanged) {
				this.getMainModule().refreshTree();
			}
		} catch (InfrastructureException ex) {
			final String errorMessage = ResourceUtils
					.getString("messages.DiscUnique");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage);
			HibernateUtil.rollbackTransaction();
			// AZ Rollback changes
			disc.setName(temporaryDiscName);// AZ rollback disc
			disc.setYear(temporaryDiscYear);
			disc.setGenre(temporaryDiscGenre);
			disc.setNotes(temporaryDiscNotes);
			nameField.setText(temporaryDiscName);
			year.setText(temporaryDiscYear);
			genre.setSelectedItem(temporaryDiscGenre);
			notesField.setText(temporaryDiscNotes);
			// AZ: no refreshing to speed-up processing
			// HibernateDao.refresh(disc);
			updateModel();

			// Refresh View
			final Object selectedNode = this.getMainModule().getSelection();
			this.getMainModule().refreshTree(selectedNode);
			rollback();
			hasErrors = true;
			getValidationModel().setDirty(false);// Set Dirty false to lock
													// editor withour changes
		} catch (Exception ex) {
			final String errorMessage = ResourceUtils
					.getString("messages.ErrorUpdatingDisc");
			MessageUtil.showError(this, errorMessage); // AZ
			LOG.error(errorMessage, ex);
			HibernateUtil.rollbackTransaction();
			HibernateDao.refresh(disc);
			hasErrors = true;
		} finally {
			setBusyCursor(false);
		}

		if (!hasErrors) {
			if (aUpdateTags) {
				// now update the ID3 tags
				task = new UpdateTagsTask(disc);
				// AZ: Put the Title of Progress Monitor Dialog Box and Cancel
				// button
				UIManager.put("ProgressMonitor.progressText",
						Resources.getString("label.ProgressTitle"));
				UIManager.put("OptionPane.cancelButtonText",
						Resources.getString("label.Cancel"));
				progressMonitor = new ProgressMonitor(getMainFrame(),
						Resources.getString("messages.updatetracks"), "", 0,
						(int) task.getLengthOfTask());
				progressMonitor.setProgress(0);
				progressMonitor.setMillisToDecideToPopup(10);
				task.go();
				timer = new Timer(50, null);
				timer.addActionListener(new TimerListener(progressMonitor,
						task, timer));
				timer.start();
			} else {
				MessageUtil.showSuccess(this);
			}
			super.commit();
		}
	}

	/**
	 * Initializes validation annotations.
	 */
	private void initComponentAnnotations() {
		ValidationComponentUtils.setInputHint(nameField,
				Resources.getString("messages.DiscNameIsMandatory"));
		ValidationComponentUtils.setMandatory(nameField, true);
		ValidationComponentUtils.setMessageKey(nameField, "Disc.Name");
		ValidationComponentUtils.setInputHint(year,
				Resources.getString("messages.YearIsMandatory"));
		ValidationComponentUtils.setMandatory(year, true);
		ValidationComponentUtils.setMessageKey(year, "Disc.Year");
		ValidationComponentUtils.setInputHint(genre,
				Resources.getString("messages.GenreIsMandatory"));
		ValidationComponentUtils.setMandatory(genre, true);
		ValidationComponentUtils.setMessageKey(genre, "Disc.Genre");
		ValidationComponentUtils.setInputHint(notesField,
				Resources.getString("messages.NotesLength"));
		ValidationComponentUtils.setMessageKey(notesField, "Disc.Notes");
		/** AZ **/
		ValidationComponentUtils.setInputHint(artistField,
				Resources.getString("messages.ArtistIsMandatory"));
		ValidationComponentUtils.setMessageKey(artistField, "Artist.Name");
		ValidationComponentUtils.setMandatory(artistField, true);

		ValidationComponentUtils.setInputHint(genreEditor,
				Resources.getString("messages.GenreIsMandatory"));
	}

	/**
	 * Creates and configures the UI components;
	 */
	private void initComponents() {
		validationModel = new DiscValidationModel(new Disc());
		ArtistValidationModel artistValidationModel = new ArtistValidationModel(
				new Artist());

		headerToolbar = buildToolBar();
		nameField = ComponentFactory.createTextField(
				validationModel.getModel(Disc.PROPERTYNAME_NAME), false);
		/** AZ **/
		artistField = ComponentFactory
				.createTextField(artistValidationModel
						.getModel(Artist.PROPERTYNAME_NAME), false);

		year = ComponentFactory.createTextField(
				validationModel.getModel(Disc.PROPERTYNAME_YEAR), false);
		((JTextField) year).setColumns(5);
		final ValueModel genreChoiceModel = validationModel
				.getModel(Disc.PROPERTYNAME_GENRE);
		genreLabel = new JLabel();
		genreLabel.setName("GENRE");
		// genre = ComponentFactory.createComboBox(new
		// SelectionInList(GENRE_LIST, genreChoiceModel));
		// AZ - Constructs an editable JComboBox for the specified List of
		// Genres
		ComboBoxAdapter genreAdapter = new ComboBoxAdapter(
				GENRE_LIST.toArray(), genreChoiceModel); // AZ
		genre = new JComboBox(genreAdapter); // AZ
		genre.setEditable(true); // AZ
		// AZ set sub-component of ComboBox - ComboBoxEditorTextField - as
		// JComponent
		genreEditor = (JComponent) genre.getComponent(2);// AZ

		notesField = ComponentFactory.createTextArea(getValidationModel()
				.getModel(Disc.PROPERTYNAME_NOTES), false);
		notesField.setLineWrap(true);
		notesField.setWrapStyleWord(true);
		notesField.setRows(3);
		location = ComponentFactory.createLabel(getValidationModel().getModel(
				Disc.PROPERTYNAME_LOCATION));
		albumImage = new AlbumImage(new Dimension(this.getSettings()
				.getCoverSizeLarge(), this.getSettings().getCoverSizeLarge()));
		listModel = new DefaultListModel();
		// Create the list and put it in a scroll pane.
		trackList = new JList(listModel);
		trackList.setFocusable(true);
		trackList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		trackList.setSelectedIndex(0);
		trackList.setVisibleRowCount(20);
		trackList
				.setCellRenderer(new TrackListCellRenderer(this.getSettings()));
		final JPopupMenu popup = MainMenuBuilder
				.buildPlayerPopupMenu(trackList);
		MouseListener popupListener = new ListPopupListener(popup);
		trackList.addMouseListener(popupListener);
		trackList.add(popup);
		listScrollPane = new SimpleInternalFrame(
				Resources.getString("label.tracks"));
		listScrollPane.setFrameIcon(Resources.TRACK_TREE_ICON);
		listScrollPane
				.setContent(UIFactory.createStrippedScrollPane(trackList));
		listScrollPane.setSelected(true);
	}

	// shows popups on right click of List nodes
	private static class ListPopupListener extends MouseAdapter {

		final JPopupMenu popup;

		public ListPopupListener(JPopupMenu popup) {
			this.popup = popup;
		}

		public void mousePressed(MouseEvent evt) {
			maybeShowPopup(evt);

			// left mouse button pressed twice, queue in playlist
			// middle mouse button pressed, queue in playlist
			if ((((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) && (evt
					.getClickCount() == 2))
					|| ((evt.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)) {
				LOG.debug("[List left double clicked or middle clicked].");
				JComponent source = (JComponent) evt.getSource();
				final ActionEvent event = new ActionEvent(source, 1,
						Actions.PLAYER_QUEUE_ID);
				source.putClientProperty(Resources.EDITOR_COMPONENT, source);
				ActionManager.get(Actions.PLAYER_QUEUE_ID).actionPerformed(
						event);
			}
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				ActionManager.get(Actions.TRACK_PLAY_IMMEDIATE_ID).setEnabled(
						true);

				// popup the menu
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

	}

}