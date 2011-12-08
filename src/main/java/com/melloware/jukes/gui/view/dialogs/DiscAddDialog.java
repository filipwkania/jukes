package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uifextras.util.UIFactory;
import com.jgoodies.validation.Severity;
import com.melloware.jukes.exception.MusicTagException;
import com.melloware.jukes.file.FileUtil;
import com.melloware.jukes.file.MusicDirectory;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.file.image.ChooserImagePreview;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.file.image.ImageFileView;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.file.tag.TagFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.component.AlbumImage;
import com.melloware.jukes.gui.view.component.ComponentFactory;
import com.melloware.jukes.gui.view.component.EnhancedTableHeader;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.JukesValidationMessage;
import com.melloware.jukes.util.MessageUtil;

/**
 * Adds a single disc to the catalog.  This disc can be manipulated before it
 * is added to the catalog for correctness.  
 * Amazon may be used to get its cover and track titles.
 * FreeDB may be used to get album information. 
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2009, 2010
 */
@SuppressWarnings("unchecked")
public final class DiscAddDialog
    extends AbstractDialog {

    private static final Log LOG = LogFactory.getLog(DiscAddDialog.class);
    private static final String TRACK_00 = "00";
    private static final String TRACK_32 = "32";
    private AlbumImage webImagePreview;
    private EnhancedTableHeader header;
    private File coverImage;
    private JButton buttonCancel;
    private JButton buttonSave;
    private JComboBox genreField;
    private JComponent buttonBar;
    private JComponent splitPane;
    private JTable tagTable;
    private JTextField artistField;
    private JTextField discField;
    private JTextField yearField;
    private MusicTagTableModel tableModel;
    private Object[] tags;
    private Settings settings;
    private String directory;

    /**
     * Constructs a default about dialog using the given owner.
     *
     * @param owner   the dialog's owner
     */
    public DiscAddDialog(Frame owner, Settings settings, File aFile) {
        super(owner);
        LOG.debug("Disc Add Dialog created.");
        //build empty fields
        artistField = new JTextField("");
        artistField.setColumns(45);
        discField = new JTextField("");
        discField.setColumns(45);
        genreField = new JComboBox(MusicTag.getGenreTypes().toArray());
        genreField.setSelectedItem("Other");
        yearField = new JTextField("");
        ((JTextField)yearField).setColumns(5);
        tagTable = new JTable();
        header = new EnhancedTableHeader(tagTable.getColumnModel(), tagTable);
        tagTable.setTableHeader(header);
        webImagePreview = new AlbumImage();
        //try to read tags
        try {
            this.settings = settings;
            final MusicTag musicTag = TagFactory.getTag(aFile);
            if (LOG.isDebugEnabled()) {
                LOG.debug(musicTag.getHeaderInfo());
            }

            artistField = new JTextField(musicTag.getArtist());
            artistField.setColumns(45);
            discField = new JTextField(musicTag.getDisc());
            discField.setColumns(45);
            genreField = new JComboBox(MusicTag.getGenreTypes().toArray());
            genreField.setSelectedItem(musicTag.getGenre());
            if (genreField.getSelectedItem() == null) {
                genreField.setSelectedItem("Other");
            }
            yearField = new JTextField(musicTag.getYear());
            ((JTextField)yearField).setColumns(5);

            // try and get the best image from the directory
            directory = FilenameUtils.getFullPath(aFile.getAbsolutePath());
            webImagePreview = new AlbumImage();
            final File dir = new File(directory);
            coverImage = MusicDirectory.findLargestImageFile(dir);
            updateCoverImage();

            // try and get music files and load table
            final Collection files = MusicDirectory.findMusicFiles(dir);
            final ArrayList musicTags = new ArrayList();
            
            if (files != null) {
                final int padding = ((files.size() >= 100) ? 3 : 2);
                int counter = 0;
                for (Iterator iter = files.iterator(); iter.hasNext();) {
                    counter++;
                    final File element = (File)iter.next();
                    final MusicTag tag = TagFactory.getTag(element);
                    if ((StringUtils.equals(TRACK_00, tag.getTrack())) || (StringUtils.equals(TRACK_32, tag.getTrack()))) {
                        tag.setTrack(Integer.toString(counter), padding);
                    } else {
                       tag.setTrack(tag.getTrack(), padding);
                    }
                    musicTags.add(tag);
                }
                this.tags = musicTags.toArray();
                loadTable();
            }

            // this.setPreferredSize(new Dimension(700, 576));
        } catch (MusicTagException ex) {
            LOG.error("MusicTagException", ex);
            MessageUtil.showError(this, "MusicTagException: " + ex.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doApply()
     */
    public void doApply() {
        LOG.debug("Save pressed.");

        GuiUtil.setBusyCursor(this, true);

        // update tags from dialog values
        fillTags();

        /** AZ **/
        // now scale and copy cover image depending on settings
        String imageLocation;
        if (this.coverImage != null) {
		try {
			imageLocation = ImageFactory.saveImageToUserDefinedDirectory(this.coverImage, 
				artistField.getText(),
			    discField.getText(),
			    yearField.getText());
		} catch (IOException e) {
			LOG.error("Error saving image file: " + e.getMessage());
			imageLocation = this.coverImage.getAbsolutePath();
		} 
          this.coverImage = new File(imageLocation);
        }

        // now try and save the database record and update tags
        // AZ: Update audio-files if isUpdateTags() is set to TRUE 
        JukesValidationMessage result = MusicDirectory.createNewDisc(this.tags, this.coverImage,
                                                                     new File(this.directory), null, this.settings.isUpdateTags());

        GuiUtil.setBusyCursor(this, false);

        if (result.getSeverity() == Severity.OK) {
            // refresh the tree
            ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
            super.doClose();
        } else {
            MessageUtil.showError(this, result.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doCancel()
     */
    public void doCancel() {
        LOG.debug("Cancel Pressed.");
        super.doCancel();
    }

    /**
     * Finds a new disc cover.
     */
    public void findCover() {
        final File currentDir = new File(this.directory);
        JFileChooser chooser = new JFileChooser();
        chooser.setApproveButtonText(Resources.getString("label.Select"));
        chooser.setDialogTitle(Resources.getString("label.FindCoverImage"));
        chooser.setCurrentDirectory(currentDir);
        chooser.addChoosableFileFilter(FilterFactory.imageFileFilter());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileView(new ImageFileView());
        chooser.setAccessory(new ChooserImagePreview(chooser));
        chooser.setMultiSelectionEnabled(false);
        int returnVal = chooser.showOpenDialog(Application.getDefaultParentFrame());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        this.coverImage = file;
        updateCoverImage();
    }

    /**
     * Renames all the music files
     */
    public void renameFiles() {
        LOG.debug("Renaming Files");
        updateTable();
        MusicTag musicTag = null;
        try {
            GuiUtil.setBusyCursor(this, true);
            // update tags from dialog values
            fillTags();

            for (int i = 0; i < tags.length; i++) {
                musicTag = (MusicTag)tags[i];
                if (musicTag.renameFile(this.settings.getFileFormatMusic())) {
                    LOG.debug("Renamed " + musicTag.getAbsolutePath());
                }
            }
        } catch (Exception ex) {
        	final String errorMessage = ResourceUtils.getString("messages.ErrorRenamingFile") + ": " + ex.getMessage();
            LOG.error(errorMessage, ex);
            MessageUtil.showError(this, errorMessage);
        } finally {
            GuiUtil.setBusyCursor(this, false);
        }
        updateTable();
    }

    /**
     * If track titles are all messed up and no amazon search found, this will
     * attempt to use the filename to construct a valid title.
     */
    public void resetFromFilenames() {
        LOG.debug("Constructing titles from filenames");
        for (int i = 0; i < tags.length; i++) {
            MusicTag tag = (MusicTag)tags[i];
            tag.setTitle(tag.extractTitleFromFilename());
        }
        updateTable();
    }

    /**
     * If track numbers are all screwed up, then loop and make them 1 to N.
     */
    public void resetTrackNumbers() {
        LOG.debug("Resetting track numbers.");
        final int padding = ((tags.length >= 100) ? 3 : 2);
        for (int i = 0; i < tags.length; i++) {
            MusicTag tag = (MusicTag)tags[i];
            tag.setTrack(String.valueOf(i + 1), padding);
        }
        updateTable();
    }

    /**
     * Apply title case to all tracks in the disc.
     */
    public void titleCase() {
        LOG.debug("Title casing all tracks");
        for (int i = 0; i < tags.length; i++) {
            MusicTag tag = (MusicTag)tags[i];
            tag.setTitle(FileUtil.capitalize(tag.getTitle()));
        }
        updateTable();
    }

    /**
     * Updates all of the comments at once
     */
    public void updateComments() {
        LOG.debug("Updating comments");
        updateTable();
        final String inputValue = StringUtils.defaultIfEmpty(JOptionPane.showInputDialog(Resources.getString("label.Enteracomment")+": "), "");
        for (int i = 0; i < tags.length; i++) {
            final MusicTag tag = (MusicTag)tags[i];
            tag.setComment(inputValue);
        }
        updateTable();
    }

    /**AZ
     * Perform the FreeDB search.
     */
    public void freeDBSearch() {
        LOG.debug("FreeBD Search");
        FreeDBDialog dialog = new FreeDBDialog((Frame)this.getParent(), this.settings);
        dialog.setSelectedArtist(artistField.getText());
        dialog.setSelectedDisc(discField.getText());
        
        //Fill the Set with track lengths
 		float[] trackLength = new float[tags.length];
        for (int i = 0; i < tags.length; i++) {
         MusicTag tag = (MusicTag)tags[i];
         trackLength[i] = tag.getTrackLength();
        }
        dialog.setTrackLength(trackLength);
        dialog.open();

        // if the user did not select anything
        if (dialog.hasBeenCanceled()) {
            return;
        }

        if (StringUtils.isNotBlank(dialog.getSelectedArtist())) {
            artistField.setText(dialog.getSelectedArtist());
        }
        if (StringUtils.isNotBlank(dialog.getSelectedDisc())) {
            String end = StringUtils.substringAfterLast(discField.getText(), " -");
            if (StringUtils.isNotBlank(end)) {
                discField.setText(dialog.getSelectedDisc() + " -" + end);
            } else {
                discField.setText(dialog.getSelectedDisc());
            }
        }
        if (StringUtils.isNotBlank(dialog.getSelectedYear())) {    // NOPMD
            if ((NumberUtils.isNumber(yearField.getText())) && (NumberUtils.isNumber(dialog.getSelectedYear()))) {    // NOPMD
                if (Integer.valueOf(dialog.getSelectedYear()).intValue() < Integer.valueOf(yearField.getText()).intValue()) {    // NOPMD
                    yearField.setText(dialog.getSelectedYear());
                }
            }
        }
        if (StringUtils.isNotBlank(dialog.getSelectedGenre())) {
        	genreField.setSelectedItem(dialog.getSelectedGenre());
        }

        // if the track counts match exactly then rename tracks too
        Collection amazonTracks = dialog.getSelectedTracks();
        if (amazonTracks != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("FreeDB Count = " + amazonTracks.size());
                LOG.debug("Tag Count = " + tags.length);
            }

            if (amazonTracks.size() == tags.length) {
                Object[] tracks = amazonTracks.toArray();
                for (int i = 0; i < tags.length; i++) {
                    MusicTag musicTag = (MusicTag)tags[i];
                    musicTag.setTitle((String)tracks[i]);
                }
            }
        }

        updateTable();
    }

        
    /**
     * Builds and answers the dialog's content.
     *
     * @return the dialog's content with tabbed pane and button bar
     */
    protected JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        JButton[] buttons = new JButton[2];
        JButton button = createApplyButton();
        button.setText(Resources.getString("label.Save"));
        button.setEnabled(true);
        buttonSave = button;
        buttonCancel = createCancelButton();
        buttonCancel.setText(Resources.getString("label.Cancel"));
        buttons[0] = buttonSave;
        buttons[1] = buttonCancel;
        buttonBar = ButtonBarFactory.buildRightAlignedBar(buttons);
        splitPane = buildSplitPane();
        content.add(splitPane, BorderLayout.CENTER);
        content.add(buttonBar, BorderLayout.SOUTH);
        return content;
    }

    /**
     * Builds and returns the dialog's header.
     *
     * @return the dialog's header component
     */
    protected JComponent buildHeader() {
        final DiscAddHeaderPanel header = new DiscAddHeaderPanel(this, Resources.getString("label.AddNewDisc"),
        												   Resources.getString("label.AddNewDiscMessage"), 
                                                           Resources.DISC_ADD_ICON);

        return header;
    }

    /**
     * Builds and returns the dialog's pane.
     *
     * @return the dialog's  pane component
     */
    protected JComponent buildMainPanel() {
        FormLayout layout = new FormLayout("fill:pref:grow", "p, p, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.add(buildDiscPanel(), cc.xy(1, 1));
        builder.add(buildTagTablePanel(), cc.xy(1, 3));
        return builder.getPanel();
    }

    /**
     * Resizes the given component to give it a quadratic aspect ratio.
     *
     * @param component   the component to be resized
     */
    protected void resizeHook(JComponent component) {
        // Resizer.ONE2ONE.resizeDialogContent(component);
    }

    /**
     * Builds the search criteria panel.
     * <p>
     * @return the panel used to specify criteria
     */
    private JComponent buildDiscPanel() {
        FormLayout layout = new FormLayout("right:max(14dlu;pref), 400px, pref, pref, pref, pref ,pref, fill:pref:grow",
                                           "p, 4px, p, 4px, p, 4px, 4px");

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.addLabel(Resources.getString("label.artist")+": ", cc.xy(1, 1));
        builder.add(artistField, cc.xyw(2, 1, 4));
        builder.add(webImagePreview, cc.xywh(7, 1, 1, 7));
        builder.add(ComponentFactory.createTitleCaseButton(artistField), cc.xy(6, 1));
        builder.addLabel(Resources.getString("label.disc")+": ", cc.xy(1, 3));
        builder.add(discField, cc.xyw(2, 3, 4));
        builder.add(ComponentFactory.createTitleCaseButton(discField), cc.xy(6, 3));
        builder.addLabel(Resources.getString("label.genre")+": ", cc.xy(1, 5));
        builder.add(genreField, cc.xy(2, 5));
        builder.addLabel(Resources.getString("label.year")+": ", cc.xy(4, 5));
        builder.add(yearField, cc.xy(5, 5));
        return builder.getPanel();
    }

    /**
     * Builds the <code>Search Criteria</code>, the <code>Results</code>
     * and answers them wrapped by a stripped <code>JSplitPane</code>.
     */
    private JComponent buildSplitPane() {
        splitPane = UIFactory.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, buildDiscPanel(), buildTagTablePanel(),
                                                      0.25);
        splitPane.setBorder(Borders.DIALOG_BORDER);
        return splitPane;
    }

    /**
     * Builds the panel with the JTable tags in it.
     * <p>
     * @return the panel used to display messages
     */
    private JComponent buildTagTablePanel() {

        // build the table and model
        tagTable.setShowGrid(false);
        tagTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ask to be notified of selection changes.
        ListSelectionModel rowSM = tagTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    // Ignore extra messages.
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                }
            });

        JComponent resultsPane = UIFactory.createTablePanel(tagTable);
        resultsPane.setPreferredSize(new Dimension(300, 275));

        // build the form
        FormLayout layout = new FormLayout("fill:pref:grow", "p");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        builder.add(resultsPane, cc.xy(1, 1));
        return builder.getPanel();
    }

    /**
     * Fill each tag from the screen.
     */
    private void fillTags() {
        // loop through and set the disc settings into each tag
        for (int i = 0; i < tags.length; i++) {
            MusicTag tag = (MusicTag)tags[i];
            tag.setArtist(artistField.getText());
            tag.setDisc(discField.getText());
            tag.setYear(yearField.getText());
            tag.setGenre((String)genreField.getSelectedItem());
        }
    }

    /**
     * Loads the JTable with data.
     */
    private void loadTable() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading table.");
        }
        tableModel = new MusicTagTableModel(tags);
        RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
        tagTable.setModel(tableModel);
        tagTable.setRowSorter(sorter);
        header.autoSizeColumns();

        // one click to edit the text cell
        ((DefaultCellEditor)tagTable.getDefaultEditor(String.class)).setClickCountToStart(1);
        tagTable.updateUI();
    }

    /**
     * Updates the cover thumbnail.
     */
    private void updateCoverImage() {
        if ((this.coverImage != null) && (this.coverImage.exists())) {
            webImagePreview.setImage(ImageFactory.getScaledImage(coverImage.getAbsolutePath(), 90, 90).getImage());
        }
    }

    /**
     * Closes any cell editors and fires datachanged event.
     */
    private void updateTable() {
        GuiUtil.stopTableEditing(tagTable);
        tableModel.fireTableDataChanged();
    }

}