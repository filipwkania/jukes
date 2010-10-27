package com.melloware.jukes.gui.tool;

import java.awt.Color;
import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;

import com.jgoodies.binding.beans.Model;
import com.melloware.jukes.file.filter.XspfFilter;
import com.melloware.jukes.gui.view.component.SpectrumTimeAnalyzer;


/**
 * Provides bound properties for application related settings.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ - modifications 2009
 *
 * @see java.util.prefs.Preferences
 */
public final class Settings
    extends Model {   
    public static final String PROPERTYNAME_FADE_IN_ON_PLAY = "fadeInOnPlay";
    public static final String PROPERTYNAME_FADE_OUT_ON_PAUSE = "fadeOutOnPause";
    public static final String PROPERTYNAME_FADE_OUT_ON_STOP = "fadeOutOnStop";
    public static final String PROPERTYNAME_FADE_OUT_ON_CHANGE = "fadeOutOnChange";
    public static final String PROPERTYNAME_UPDATE_TAGS = "updateTags";
    public static final String PROPERTYNAME_DISPLAY_FORMAT_DISC = "displayFormatDisc";
    public static final String PROPERTYNAME_DISPLAY_FORMAT_TRACK = "displayFormatTrack";
    public static final String PROPERTYNAME_DIRECTORY_START_IN = "startInDirectory";
    public static final String PROPERTYNAME_DIRECTORY_DB_LOCATION = "databaseLocation";
    public static final String PROPERTYNAME_FILE_FORMAT_MUSIC = "fileFormatMusic";
    public static final String PROPERTYNAME_FILE_FORMAT_IMAGE = "fileFormatImage";
    public static final String PROPERTYNAME_NEW_FILE_IN_DAYS = "newFileInDays";
    public static final String PROPERTYNAME_LOCALE = "locale";
    public static final String PROPERTYNAME_ROW_COLOR_EVEN = "rowColorEven";
    public static final String PROPERTYNAME_ROW_COLOR_ODD = "rowColorOdd";
    public static final String PROPERTYNAME_PLAYLIST_TYPE = "playlistType";
    public static final String PROPERTYNAME_LOG_LEVEL = "logLevel";
    public static final String PROPERTYNAME_PLAYER_BUFFER_SIZE = "playerBufferSize";
    public static final String PROPERTYNAME_FILE_BACKUP = "fileBackup";
    public static final String PROPERTYNAME_COVER_SIZE_SMALL = "coverSizeSmall";
    public static final String PROPERTYNAME_COVER_SIZE_LARGE = "coverSizeLarge";
    public static final String PROPERTYNAME_AUDIT_INFO = "auditInfo";
    public static final String PROPERTYNAME_REMOTE_DATABASE_URL = "remoteDatabaseURL";
    public static final String PROPERTYNAME_CATALOG_SCROLL_UNITS = "catalogScrollUnits";
    public static final String PROPERTYNAME_ANALYZER_MODE = "analyzerMode";
    /** AZ **/
    public static final String PROPERTYNAME_USE_CD_NUMBER = "useCDNumber";
    public static final String PROPERTYNAME_ALBUM_ARTIST_TAG = "albumArtistTag";
    public static final String PROPERTYNAME_COPY_IMAGES = "copyImagesToDirectory";
    public static final String PROPERTYNAME_IMAGES_LOCATION = "imagesLocation"; 
    public static final String PROPERTYNAME_SHOW_DEFAULT_TREE = "showDefaultTree";
    public static final String PROPERTYNAME_SHOW_EMPTY_NODE = "showEmptyNode";
    /**
     * URL of remote database location.
     */
    public static final String DEFAULT_REMOTE_DATABASE_URL = "jdbc:hsqldb:hsql://127.0.0.1/jukes";

    /**
     * The default value (true) for fading properties.
     */
    private static final boolean DEFAULT_FADING = true;

    /**
     * The default value (false) for property 'updateTags'.
     */
    private static final boolean DEFAULT_UPDATE_TAGS = false;

    /**
     * The default value (true) for property 'auditInfo'.
     */
    private static final boolean DEFAULT_AUDIT_INFO = true;
    
    /**    AZ
     * The default value (true) for property 'useCDNumber'.
     */
    private static final boolean DEFAULT_USE_CD_NUMBER = true;
    /**    AZ
     * The default value for property 'albumArtistTag'.
     */
    private static final String DEFAULT_ALBUM_ARTIST_TAG = "ALBUM ARTIST";
         
    /**    AZ
     * The default value (false) for property 'copyImagesToDirectory'.
     */
    private static final boolean DEFAULT_COPY_IMAGES = false;
    
    /**    AZ
     * The default value (true) for property 'showDefaultTree'.
     */
    private static final boolean DEFAULT_SHOW_DEFAULT_TREE = true;
    
    /**    AZ
     * The default value (false) for property 'showEmptyNode'.
     */
    private static final boolean DEFAULT_SHOW_EMPTY_NODE = false;
    
    /** AZ redirected to current directory
     * The default value for property 'imagesLocation'.
     */
    private static final File DEFAULT_IMAGES_LOCATION = new File(FilenameUtils.normalizeNoEndSeparator(SystemUtils.USER_DIR
    														+ SystemUtils.FILE_SEPARATOR 
    														+ "images"));       

    /**AZ redirected to current directory
     * The default value for property 'startInDirectory'.
     */
    private static final File DEFAULT_DIRECTORY_START_IN = new File(FilenameUtils.normalizeNoEndSeparator(SystemUtils.USER_DIR));

    /**AZ redirected to current directory
     * The default value for property 'databaseLocation'.
     */
    private static final File DEFAULT_DIRECTORY_DB_LOCATION = new File(FilenameUtils.normalizeNoEndSeparator(SystemUtils.USER_DIR
                                                                                                             + SystemUtils.FILE_SEPARATOR
                                                                                                             + "db"));

    /**AZ redirected to current directory
     * The default value for property 'fileBackup'.
     */
    private static final File DEFAULT_FILE_BACKUP = new File(FilenameUtils.normalizeNoEndSeparator(SystemUtils.USER_DIR
                                                                                                   + SystemUtils.FILE_SEPARATOR
                                                                                                   + "backup"
                                                                                                   + SystemUtils.FILE_SEPARATOR
                                                                                                   + Resources.APPLICATION_LOCATION
                                                                                                   + "-backup.zip"));

    /**
     * The default value for property 'fileFormatMusic'.
     */
    private static final String DEFAULT_FILE_FORMAT_MUSIC = "%n - %t";

    /**
     * The default value for property 'fileFormatImage'.
     */
    private static final String DEFAULT_FILE_FORMAT_IMAGE = "%a - %d";

    /**
     * The default value for property 'fileFormatMusic'.
     */
    private static final String DEFAULT_DISPLAY_FORMAT_DISC = "[%y] %d (%r)";

    /**
     * The default value for property 'fileFormatMusic'.
     */
    private static final String DEFAULT_DISPLAY_FORMAT_TRACK = "%n. %t  (%r)";

    /**
     * The default value for property 'locale'.
     */
    private static final String DEFAULT_LOCALE = Locale.getDefault().getLanguage();

    /**
     * The default value for property 'rowColorEven'.
     */
    private static final Color DEFAULT_ROW_COLOR_EVEN = Color.WHITE;

    /**
     * The default value for property 'rowColorOdd'.
     */
    private static final Color DEFAULT_ROW_COLOR_ODD = new Color(0xee, 0xee, 0xff);

    /**
     * The default value for property 'newFileInDays'.
     */
    private static final int DEFAULT_NEW_FILE_IN_DAYS = 30;
    
    /**
     * The default value for property 'analyzerMode'.
     */
    private static final String DEFAULT_ANALYZER_MODE = SpectrumTimeAnalyzer.ANALYZER;

    /**
     * The default value for property 'catalogScrollUnits'.
     */
    private static final int DEFAULT_CATALOG_SCROLL_UNITS = 5;

    /**
     * The default value for property 'coverSizeSmall'.
     */
    private static final int DEFAULT_COVER_SIZE_SMALL = 91;

    /**
     * The default value for property 'coverSizeLarge'.
     */
    private static final int DEFAULT_COVER_SIZE_LARGE = 300;

    /**
     * The default value for property 'playerBufferSize'.
     */
    private static final int DEFAULT_PLAYER_BUFFER_SIZE = 512000;

    /**
     * The default value for property 'playlistType'.
     */
    private static final String DEFAULT_PLAYLIST_TYPE = XspfFilter.XSPF;
    
    /**
     * The default value for playerVolume.
     */
    private static final float DEFAULT_KEY_PLAYER_VOLUME = 0.7f;

    /**
     * The default value for property 'logLevel'.
     */
    private static final String DEFAULT_LOG_LEVEL = Level.INFO.toString();

    private static final String KEY_FADE_IN_ON_PLAY = "state.fadeInOnPlay";
    private static final String KEY_FADE_OUT_ON_STOP = "state.fadeOutOnStop";
    private static final String KEY_FADE_OUT_ON_PAUSE = "state.fadeOutOnPause";
    private static final String KEY_FADE_OUT_ON_CHANGE = "state.fadeOutOnChange";
    private static final String KEY_DIRECTORY_START_IN = "state.startInDirectory";
    private static final String KEY_DIRECTORY_DB_LOCATION = "state.databaseLocation";
    private static final String KEY_FILE_FORMAT_MUSIC = "state.fileFormatMusic";
    private static final String KEY_FILE_FORMAT_IMAGE = "state.fileFormatImage";
    private static final String KEY_NEW_FILE_IN_DAYS = "state.newFileInDays";
    private static final String KEY_DISPLAY_FORMAT_TRACK = "state.displayFormatTrack";
    private static final String KEY_DISPLAY_FORMAT_DISC = "state.displayFormatDisc";
    private static final String KEY_LOCALE = "state.locale";
    private static final String KEY_UPDATE_TAGS = "state.updateTags";
    private static final String KEY_ROW_COLOR_EVEN = "state.rowColorEven";
    private static final String KEY_ROW_COLOR_ODD = "state.rowColorOdd";
    private static final String KEY_PLAYLIST_TYPE = "state.playlistType";
    private static final String KEY_LOG_LEVEL = "state.logLevel";
    private static final String KEY_PLAYER_BUFFER_SIZE = "state.playerBufferSize";
    private static final String KEY_FILE_BACKUP = "state.fileBackup";
    private static final String KEY_COVER_SIZE_SMALL = "state.coverSizeSmall";
    private static final String KEY_COVER_SIZE_LARGE = "state.coverSizeLarge";
    private static final String KEY_AUDIT_INFO = "state.auditInfo";
    private static final String KEY_REMOTE_DATABASE_URL = "state.remoteDatabaseURL";
    private static final String KEY_CATALOG_SCROLL_UNITS = "state.catalogScrollUnits";
    private static final String KEY_ANALYZER_MODE = "state.analyzerMode";
    /** AZ  */
    private static final String KEY_USE_CD_NUMBER = "state.useCDNumber";
    private static final String KEY_ALBUM_ARTIST_TAG = "state.albumArtistTag";
    private static final String KEY_COPY_IMAGES = "state.copyImagesToDirectory";
    private static final String KEY_IMAGES_LOCATION = "state.imagesLocation";
    private static final String KEY_SHOW_DEFAULT_TREE = "state.showDefaultTree";
    private static final String KEY_SHOW_EMPTY_NODE = "state.showEmptyNode";
    private static final String KEY_PLAYER_VOLUME = "playerVolume";

    
    /** AZ  
     * Default player volume
     */
    private float playerVolume = DEFAULT_KEY_PLAYER_VOLUME;
    
    /**
     * Whether to show Audit Info or not
     */
    private boolean auditInfo = DEFAULT_AUDIT_INFO;
    
    /** AZ  
     * Default description of TXXX tag for Album Artist
     */
    private String albumArtistTag = DEFAULT_ALBUM_ARTIST_TAG;
    /** AZ   
     * Whether to use CD Number or not
     */
    private boolean useCDNumber = DEFAULT_USE_CD_NUMBER;
    /** AZ   
     * Whether to copy images to user defined directory or not
     */
    private boolean copyImagesToDirectory = DEFAULT_COPY_IMAGES;
    /** AZ
     * Describes where the images location is.
     */
    private File imagesLocation = DEFAULT_IMAGES_LOCATION;
    
    /** AZ   
     * Whether to show main tree by default
     */
    private boolean showDefaultTree = DEFAULT_SHOW_DEFAULT_TREE;
    
    /** AZ   
     * Whether to show empty node by default
     */
    private boolean showEmptyNode = DEFAULT_SHOW_EMPTY_NODE;
    
    /**
     * Describes whether a song shall be faded out or not
     */
    private boolean fadeInOnPlay = DEFAULT_FADING;
    private boolean fadeOutOnChange = DEFAULT_FADING;
    private boolean fadeOutOnPause = DEFAULT_FADING;
    private boolean fadeOutOnStop = DEFAULT_FADING;

    /**
     * Whether to update the tags on a Disc Find (slow).
     */
    private boolean updateTags = DEFAULT_UPDATE_TAGS;

    /**
     * Describes the alternating row color.
     */
    private Color rowColorEven = DEFAULT_ROW_COLOR_EVEN;

    /**
     * Describes the alternating row color.
     */
    private Color rowColorOdd = DEFAULT_ROW_COLOR_ODD;

    /**
     * Describes where the HSQLDB database location is.
     */
    private File databaseLocation = DEFAULT_DIRECTORY_DB_LOCATION;

    /**
     * Describes the file to backup to.
     */
    private File fileBackup = DEFAULT_FILE_BACKUP;

    /**
     * Describes what the start in directory is for all Find Dialogs.
     */
    private File startInDirectory = DEFAULT_DIRECTORY_START_IN;

    /**
     * Describes scroll speed of the catalog tree view
     */
    private int catalogScrollUnits = DEFAULT_CATALOG_SCROLL_UNITS;

    /**
     * Describes cover size of the large covers
     */
    private int coverSizeLarge = DEFAULT_COVER_SIZE_LARGE;

    /**
     * Describes cover size of the small covers
     */
    private int coverSizeSmall = DEFAULT_COVER_SIZE_SMALL;

    /**
     * Describes the number of days a file is considered 'new'
     */
    private int newFileInDays = DEFAULT_NEW_FILE_IN_DAYS;
    
    /**
     * Describes the mode to start the spectrum analyzer in
     */
    private String analyzerMode = DEFAULT_ANALYZER_MODE;

    /**
     * Describes the buffer size of the audio player
     */
    private int playerBufferSize = DEFAULT_PLAYER_BUFFER_SIZE;

    /**
     * Describes the format to display discs.
     */
    private String displayFormatDisc = DEFAULT_DISPLAY_FORMAT_DISC;

    /**
     * Describes the format to display tracks
     */
    private String displayFormatTrack = DEFAULT_DISPLAY_FORMAT_TRACK;

    /**
     * Describes what the image file renaming convention is.
     */
    private String fileFormatImage = DEFAULT_FILE_FORMAT_IMAGE;

    /**
     * Describes what the music file renaming convention is.
     */
    private String fileFormatMusic = DEFAULT_FILE_FORMAT_MUSIC;

    /**
     * Describes what the current filter is.
     */
    private String filter = "";

    /**
     * Describes the country locale
     */
    private String locale = DEFAULT_LOCALE;

    /**
     * Describes the logger level
     */
    private String logLevel = DEFAULT_LOG_LEVEL;

    /**
     * Describes the country locale
     */
    private String playlistType = DEFAULT_PLAYLIST_TYPE;

    /**
     * Describes the location of the remote database if connecting to one
     */
    private String remoteDatabaseURL = DEFAULT_REMOTE_DATABASE_URL;

    /**
     * Gets the catalogScrollUnits.
     * <p>
     * @return Returns the catalogScrollUnits.
     */
    public int getCatalogScrollUnits() {
        return this.catalogScrollUnits;
    }

    /**
     * Gets the coverSizeLarge.
     * <p>
     * @return Returns the coverSizeLarge.
     */
    public int getCoverSizeLarge() {
        return this.coverSizeLarge;
    }

    /**
     * Gets the coverSizeSmall.
     * <p>
     * @return Returns the coverSizeSmall.
     */
    public int getCoverSizeSmall() {
        return this.coverSizeSmall;
    }

    /**
     * Gets the databaseLocation.
     * <p>
     * @return Returns the databaseLocation.
     */
    public File getDatabaseLocation() {
        return this.databaseLocation;
    }

    /**
     * Gets the displayFormatDisc.
     * <p>
     * @return Returns the displayFormatDisc.
     */
    public String getDisplayFormatDisc() {
        return this.displayFormatDisc;
    }

    /**
     * Gets the displayFormatTrack.
     * <p>
     * @return Returns the displayFormatTrack.
     */
    public String getDisplayFormatTrack() {
        return this.displayFormatTrack;
    }

    /**
     * Gets the fileBackup.
     * <p>
     * @return Returns the fileBackup.
     */
    public File getFileBackup() {
        return this.fileBackup;
    }

    /**
     * Gets the fileFormatImage.
     * <p>
     * @return Returns the fileFormatImage.
     */
    public String getFileFormatImage() {
        return this.fileFormatImage;
    }

    /**
     * Gets the fileFormatMusic.
     * <p>
     * @return Returns the fileFormatMusic.
     */
    public String getFileFormatMusic() {
        return this.fileFormatMusic;
    }

    /**
     * Gets the filter.
     * <p>
     * @return Returns the filter.
     */
    public String getFilter() {
        return this.filter;
    }

    /**
     * Gets the locale.
     * <p>
     * @return Returns the locale.
     */
    public String getLocale() {
        return this.locale;
    }

    /**
     * Gets the logLevel.
     * <p>
     * @return Returns the logLevel.
     */
    public String getLogLevel() {
        return this.logLevel;
    }

    /**
     * Gets the newFileInDays.
     * <p>
     * @return Returns the newFileInDays.
     */
    public int getNewFileInDays() {
        return this.newFileInDays;
    }

    /**
     * Gets the playerBufferSize.
     * <p>
     * @return Returns the playerBufferSize.
     */
    public int getPlayerBufferSize() {
        return this.playerBufferSize;
    }

    /**
     * Gets the playlistType.
     * <p>
     * @return Returns the playlistType.
     */
    public String getPlaylistType() {
        return this.playlistType;
    }

    /**
     * Gets the remoteDatabaseURL.
     * <p>
     * @return Returns the remoteDatabaseURL.
     */
    public String getRemoteDatabaseURL() {
        return this.remoteDatabaseURL;
    }

    /**
     * Gets the rowColorEven.
     * <p>
     * @return Returns the rowColorEven.
     */
    public Color getRowColorEven() {
        return this.rowColorEven;
    }

    /**
     * Gets the rowColorOdd.
     * <p>
     * @return Returns the rowColorOdd.
     */
    public Color getRowColorOdd() {
        return this.rowColorOdd;
    }

    /**
     * Gets the startInDirectory.
     * <p>
     * @return Returns the startInDirectory.
     */
    public File getStartInDirectory() {
        return this.startInDirectory;
    }
    
    /**
     * Gets the analyzerMode.
     * <p>
     * @return Returns the analyzerMode.
     */
    public String getAnalyzerMode() {
       return this.analyzerMode;
    }

    /** AZ
     * Gets the albumArtistTag.
     * <p>
     * @return Returns the albumArtistTag.
     */
    public String getAlbumArtistTag() {
       return this.albumArtistTag;
    }
    /** AZ
     * Gets the imagesLocation.
     * <p>
     * @return Returns the imagesLocation.
     */
    public File getImagesLocation() {
        return this.imagesLocation;
    }
    /**
     * Sets the auditInfo.
     * <p>
     * @param aAuditInfo The auditInfo to set.
     */
    public void setAuditInfo(boolean aAuditInfo) {
        boolean oldValue = isAuditInfo();
        this.auditInfo = aAuditInfo;
        firePropertyChange(PROPERTYNAME_AUDIT_INFO, oldValue, aAuditInfo);
    }

    /**
     * Sets the catalogScrollUnits.
     * <p>
     * @param aCatalogScrollUnits The catalogScrollUnits to set.
     */
    public void setCatalogScrollUnits(int aCatalogScrollUnits) {
        int oldValue = getCatalogScrollUnits();
        this.catalogScrollUnits = aCatalogScrollUnits;
        firePropertyChange(PROPERTYNAME_CATALOG_SCROLL_UNITS, oldValue, aCatalogScrollUnits);
    }

    /**
     * Sets the coverSizeLarge.
     * <p>
     * @param aCoverSizeLarge The coverSizeLarge to set.
     */
    public void setCoverSizeLarge(int aCoverSizeLarge) {
        int oldValue = getCoverSizeLarge();
        this.coverSizeLarge = aCoverSizeLarge;
        firePropertyChange(PROPERTYNAME_COVER_SIZE_LARGE, oldValue, aCoverSizeLarge);
    }

    /**
     * Sets the coverSizeSmall.
     * <p>
     * @param aCoverSizeSmall The coverSizeSmall to set.
     */
    public void setCoverSizeSmall(int aCoverSizeSmall) {
        int oldValue = getCoverSizeSmall();
        this.coverSizeSmall = aCoverSizeSmall;
        firePropertyChange(PROPERTYNAME_COVER_SIZE_SMALL, oldValue, aCoverSizeSmall);

    }

    /**
     * Sets the databaseLocation.
     * <p>
     * @param aDatabaseLocation The databaseLocation to set.
     */
    public void setDatabaseLocation(File aDatabaseLocation) {
        File oldValue = getDatabaseLocation();
        this.databaseLocation = aDatabaseLocation;
        firePropertyChange(PROPERTYNAME_DIRECTORY_DB_LOCATION, oldValue, aDatabaseLocation);
    }

    /**
     * Sets the displayFormatDisc.
     * <p>
     * @param aDisplayFormatDisc The displayFormatDisc to set.
     */
    public void setDisplayFormatDisc(String aDisplayFormatDisc) {
        String oldValue = getDisplayFormatDisc();
        this.displayFormatDisc = aDisplayFormatDisc;
        firePropertyChange(PROPERTYNAME_DISPLAY_FORMAT_DISC, oldValue, aDisplayFormatDisc);
    }

    /**
     * Sets the displayFormatTrack.
     * <p>
     * @param aDisplayFormatTrack The displayFormatTrack to set.
     */
    public void setDisplayFormatTrack(String aDisplayFormatTrack) {
        String oldValue = getDisplayFormatTrack();
        this.displayFormatTrack = aDisplayFormatTrack;
        firePropertyChange(PROPERTYNAME_DISPLAY_FORMAT_TRACK, oldValue, aDisplayFormatTrack);
    }

    /**
     * Sets the fadeInOnPlay.
     * <p>
     * @param aFadeInOnPlay The fadeInOnPlay to set.
     */
    public void setFadeInOnPlay(boolean aFadeInOnPlay) {
        boolean oldValue = isFadeInOnPlay();
        this.fadeInOnPlay = aFadeInOnPlay;
        firePropertyChange(PROPERTYNAME_FADE_IN_ON_PLAY, oldValue, aFadeInOnPlay);
    }

    /**
     * Sets the fadeOutOnChange.
     * <p>
     * @param aFadeOutOnChange The fadeOutOnChange to set.
     */
    public void setFadeOutOnChange(boolean aFadeOutOnChange) {
        boolean oldValue = isFadeOutOnChange();
        this.fadeOutOnChange = aFadeOutOnChange;
        firePropertyChange(PROPERTYNAME_FADE_OUT_ON_CHANGE, oldValue, aFadeOutOnChange);
    }

    /**
     * Sets the fadeOutOnPause.
     * <p>
     * @param aFadeOutOnPause The fadeOutOnPause to set.
     */
    public void setFadeOutOnPause(boolean aFadeOutOnPause) {
        boolean oldValue = isFadeOutOnPause();
        this.fadeOutOnPause = aFadeOutOnPause;
        firePropertyChange(PROPERTYNAME_FADE_OUT_ON_PAUSE, oldValue, aFadeOutOnPause);
    }

    /**
     * Sets the fadeOutOnStop.
     * <p>
     * @param aFadeOutOnStop The fadeOutOnStop to set.
     */
    public void setFadeOutOnStop(boolean aFadeOutOnStop) {
        boolean oldValue = isFadeOutOnStop();
        this.fadeOutOnStop = aFadeOutOnStop;
        firePropertyChange(PROPERTYNAME_FADE_OUT_ON_STOP, oldValue, aFadeOutOnStop);
    }

    /**
     * Sets the fileBackup.
     * <p>
     * @param aFileBackup The fileBackup to set.
     */
    public void setFileBackup(File aFileBackup) {
        File oldValue = getFileBackup();
        this.fileBackup = aFileBackup;
        firePropertyChange(PROPERTYNAME_FILE_BACKUP, oldValue, aFileBackup);
    }

    /**
     * Sets the fileFormatImage.
     * <p>
     * @param aFileFormatImage The fileFormatImage to set.
     */
    public void setFileFormatImage(String aFileFormatImage) {
        String oldValue = getFileFormatImage();
        this.fileFormatImage = aFileFormatImage;
        firePropertyChange(PROPERTYNAME_FILE_FORMAT_IMAGE, oldValue, aFileFormatImage);
    }

    /**
     * Sets the fileFormatMusic.
     * <p>
     * @param aFileFormat The fileFormatMusic to set.
     */
    public void setFileFormatMusic(String aFileFormat) {
        String oldValue = getFileFormatMusic();
        this.fileFormatMusic = aFileFormat;
        firePropertyChange(PROPERTYNAME_FILE_FORMAT_MUSIC, oldValue, aFileFormat);
    }

    /**
     * Sets the filter.
     * <p>
     * @param aFilter The filter to set.
     */
    public void setFilter(String aFilter) {
        this.filter = aFilter;
    }

    /**
     * Sets the locale.
     * <p>
     * @param aLocale The locale to set.
     */
    public void setLocale(String aLocale) {
        String oldValue = getLocale();
        this.locale = aLocale;
        firePropertyChange(PROPERTYNAME_LOCALE, oldValue, aLocale);

    }

    /**
     * Sets the logLevel.
     * <p>
     * @param aLogLevel The logLevel to set.
     */
    public void setLogLevel(String aLogLevel) {
        String oldValue = getLogLevel();
        this.logLevel = aLogLevel;
        firePropertyChange(PROPERTYNAME_LOG_LEVEL, oldValue, aLogLevel);
    }

    /**
     * Sets the newFileInDays.
     * <p>
     * @param aNewFileInDays The newFileInDays to set.
     */
    public void setNewFileInDays(int aNewFileInDays) {
        int oldValue = getNewFileInDays();
        this.newFileInDays = aNewFileInDays;
        firePropertyChange(PROPERTYNAME_NEW_FILE_IN_DAYS, oldValue, aNewFileInDays);

    }

    /**
     * Sets the playerBufferSize.
     * <p>
     * @param aPlayerBufferSize The playerBufferSize to set.
     */
    public void setPlayerBufferSize(int aPlayerBufferSize) {
        int oldValue = getPlayerBufferSize();
        this.playerBufferSize = aPlayerBufferSize;
        firePropertyChange(PROPERTYNAME_PLAYER_BUFFER_SIZE, oldValue, aPlayerBufferSize);
    }
    
    /**
     * Sets the analyzerMode.
     * <p>
     * @param aAnalyzerMode The analyzerMode to set.
     */
    public void setAnalyzerMode(String aAnalyzerMode) {
       String oldValue = getAnalyzerMode();
       this.analyzerMode = aAnalyzerMode;
       firePropertyChange(PROPERTYNAME_ANALYZER_MODE, oldValue, aAnalyzerMode);
    }
    /**AZ
   * Sets the useCDNumber.
     * <p>
     * @param aUseCDNumber The useCDNumber to set.
     */
    public void setUseCDNumber(boolean aUseCDNumber) {
        boolean oldValue = isUseCDNumber();
        this.useCDNumber = aUseCDNumber;
        firePropertyChange(PROPERTYNAME_USE_CD_NUMBER, oldValue, aUseCDNumber);
    }
    
    /**AZ
     * Sets the showDefaultTree.
       * <p>
       * @param aShowDefaultTree The showDefaultTree to set.
       */
      public void setShowDefaultTree(boolean aShowDefaultTree) {
          boolean oldValue = isShowDefaultTree();
          this.showDefaultTree = aShowDefaultTree;
          firePropertyChange(PROPERTYNAME_SHOW_DEFAULT_TREE, oldValue, aShowDefaultTree);
      }
      
      /**AZ
       * Sets the showEmptyNode.
         * <p>
         * @param aShowEmptyNode The showEmptyNode to set.
         */
        public void setShowEmptyNode(boolean aShowEmptyNode) {
            boolean oldValue = isShowEmptyNode();
            this.showEmptyNode = aShowEmptyNode;
            firePropertyChange(PROPERTYNAME_SHOW_EMPTY_NODE, oldValue, aShowEmptyNode);
        }
    
    /** AZ
     * Sets the imagesLocation.
     * <p>
     * @param aImagesLocation The imagesLocation to set.
     */
    public void setImagesLocation(File aImagesLocation) {
        File oldValue = getImagesLocation();
        this.imagesLocation = aImagesLocation;
        firePropertyChange(PROPERTYNAME_IMAGES_LOCATION, oldValue, aImagesLocation);
    }

    /**AZ
     * Sets the copyImages.
       * <p>
       * @param aCopyImages The copyImages to set.
       */
    public void setCopyImagesToDirectory(boolean aCopyImages) {
        boolean oldValue = isCopyImagesToDirectory();
        this.copyImagesToDirectory = aCopyImages;
        firePropertyChange(PROPERTYNAME_COPY_IMAGES, oldValue, aCopyImages);
    }
    /** AZ
     * Sets the albumArtistTag
     * <p>
     * @param aAlbumArtistTag The albumArtistTag to set.
     */
    public void setAlbumArtistTag(String aAlbumArtistTag) {
        String oldValue = getAlbumArtistTag();
        this.albumArtistTag = aAlbumArtistTag;
        firePropertyChange(PROPERTYNAME_ALBUM_ARTIST_TAG, oldValue, aAlbumArtistTag);
    }
       /**
     * Sets the playlistType.
     * <p>
     * @param aPlaylistType The playlistType to set.
     */
    public void setPlaylistType(String aPlaylistType) {
        String oldValue = getPlaylistType();
        this.playlistType = aPlaylistType;
        firePropertyChange(PROPERTYNAME_PLAYLIST_TYPE, oldValue, aPlaylistType);
    }

    /**
     * Sets the remoteDatabaseURL.
     * <p>
     * @param aRemoteDatabaseURL The remoteDatabaseURL to set.
     */
    public void setRemoteDatabaseURL(String aRemoteDatabaseURL) {
        String oldValue = getRemoteDatabaseURL();
        this.remoteDatabaseURL = aRemoteDatabaseURL;
        firePropertyChange(PROPERTYNAME_REMOTE_DATABASE_URL, oldValue, aRemoteDatabaseURL);

    }

    /**
     * Sets the rowColorEven.
     * <p>
     * @param aRowColorEven The rowColorEven to set.
     */
    public void setRowColorEven(Color aRowColorEven) {
        Color oldValue = getRowColorEven();
        this.rowColorEven = aRowColorEven;
        firePropertyChange(PROPERTYNAME_ROW_COLOR_EVEN, oldValue, aRowColorEven);
    }

    /**
     * Sets the rowColorOdd.
     * <p>
     * @param aRowColorOdd The rowColorOdd to set.
     */
    public void setRowColorOdd(Color aRowColorOdd) {
        Color oldValue = getRowColorOdd();
        this.rowColorOdd = aRowColorOdd;
        firePropertyChange(PROPERTYNAME_ROW_COLOR_ODD, oldValue, aRowColorOdd);
    }

    /**
     * Sets the startInDirectory.
     * <p>
     * @param aStartInDirectory The startInDirectory to set.
     */
    public void setStartInDirectory(File aStartInDirectory) {
        File oldValue = getStartInDirectory();
        startInDirectory = aStartInDirectory;
        firePropertyChange(PROPERTYNAME_DIRECTORY_START_IN, oldValue, aStartInDirectory);
    }

    /**
     * Sets the updateTags.
     * <p>
     * @param aUpdateTags The updateTags to set.
     */
    public void setUpdateTags(boolean aUpdateTags) {
        boolean oldValue = isUpdateTags();
        this.updateTags = aUpdateTags;
        firePropertyChange(PROPERTYNAME_UPDATE_TAGS, oldValue, aUpdateTags);
    }
    
    /**AZ
     * Gets the useCDNumber.
     * <p>
     * @return Returns the useCDNumber.
     */
    public boolean isUseCDNumber() {
        return this.useCDNumber;
    } 
    
    /** AZ
     * Gets the showDefaultTree.
     * <p>
     * @return Returns the showDefaultTree.
     */
    public boolean isShowDefaultTree() {
       return this.showDefaultTree;
    }
    
    /** AZ
     * Gets the showEmptyNode.
     * <p>
     * @return Returns the showEmptyNode.
     */
    public boolean isShowEmptyNode() {
       return this.showEmptyNode;
    }
    
    /**AZ
     * Gets the copyImagesToDirectory.
     * <p>
     * @return Returns the copyImages.
     */
    public boolean isCopyImagesToDirectory() {
        return this.copyImagesToDirectory;
    }  

    /**
     * Gets the auditInfo.
     * <p>
     * @return Returns the auditInfo.
     */
    public boolean isAuditInfo() {
        return this.auditInfo;
    }

    /**
     * Gets the fadeInOnPlay.
     * <p>
     * @return Returns the fadeInOnPlay.
     */
    public boolean isFadeInOnPlay() {
        return this.fadeInOnPlay;
    }

    /**
     * Gets the fadeOutOnChange.
     * <p>
     * @return Returns the fadeOutOnChange.
     */
    public boolean isFadeOutOnChange() {
        return this.fadeOutOnChange;
    }

    /**
     * Gets the fadeOutOnPause.
     * <p>
     * @return Returns the fadeOutOnPause.
     */
    public boolean isFadeOutOnPause() {
        return this.fadeOutOnPause;
    }

    /**
     * Gets the fadeOutOnStop.
     * <p>
     * @return Returns the fadeOutOnStop.
     */
    public boolean isFadeOutOnStop() {
        return this.fadeOutOnStop;
    }

    /**
     * Gets the updateTags.
     * <p>
     * @return Returns the updateTags.
     */
    public boolean isUpdateTags() {
        return this.updateTags;
    }

    /**
     * Sets the player volume
     * <p>
     * @param volume in the range 0 <--> 1
     */
    public void setPlayerVolume(float volume) {
    	if (volume > 1) {
    		volume = 1;
    	} else if (volume < 0) {
    		volume = 0;
    	}
    	this.playerVolume = volume;
    }
    
    /**
     * Gets the player volume
     * <p>
     */
    public float getPlayerVolume() {
    	return this.playerVolume;
    }
    
    /**
     * Restores the persistent properties from the specified Preferences.
     *
     * @param prefs   the Preferences object that holds the property values
     */
    public void restoreFrom(Preferences prefs) {
        setFadeInOnPlay(prefs.getBoolean(KEY_FADE_IN_ON_PLAY, DEFAULT_FADING));
        setFadeOutOnChange(prefs.getBoolean(KEY_FADE_OUT_ON_CHANGE, DEFAULT_FADING));
        setFadeOutOnPause(prefs.getBoolean(KEY_FADE_OUT_ON_PAUSE, DEFAULT_FADING));
        setFadeOutOnStop(prefs.getBoolean(KEY_FADE_OUT_ON_STOP, DEFAULT_FADING));
        setUpdateTags(prefs.getBoolean(KEY_UPDATE_TAGS, DEFAULT_UPDATE_TAGS));
        setStartInDirectory(new File(prefs.get(KEY_DIRECTORY_START_IN, DEFAULT_DIRECTORY_START_IN.getAbsolutePath())));
        setDatabaseLocation(new File(prefs.get(KEY_DIRECTORY_DB_LOCATION,
                                               DEFAULT_DIRECTORY_DB_LOCATION.getAbsolutePath())));
        setFileBackup(new File(prefs.get(KEY_FILE_BACKUP, DEFAULT_FILE_BACKUP.getAbsolutePath())));
        setFileFormatMusic(prefs.get(KEY_FILE_FORMAT_MUSIC, DEFAULT_FILE_FORMAT_MUSIC));
        setFileFormatImage(prefs.get(KEY_FILE_FORMAT_IMAGE, DEFAULT_FILE_FORMAT_IMAGE));
        setNewFileInDays(prefs.getInt(KEY_NEW_FILE_IN_DAYS, DEFAULT_NEW_FILE_IN_DAYS));
        setAnalyzerMode(prefs.get(KEY_ANALYZER_MODE, DEFAULT_ANALYZER_MODE));
        setDisplayFormatDisc(prefs.get(KEY_DISPLAY_FORMAT_DISC, DEFAULT_DISPLAY_FORMAT_DISC));
        setDisplayFormatTrack(prefs.get(KEY_DISPLAY_FORMAT_TRACK, DEFAULT_DISPLAY_FORMAT_TRACK));
        setLocale(prefs.get(KEY_LOCALE, DEFAULT_LOCALE));
        setRowColorEven(new Color(prefs.getInt(KEY_ROW_COLOR_EVEN, DEFAULT_ROW_COLOR_EVEN.getRGB())));
        setRowColorOdd(new Color(prefs.getInt(KEY_ROW_COLOR_ODD, DEFAULT_ROW_COLOR_ODD.getRGB())));
        setPlaylistType(prefs.get(KEY_PLAYLIST_TYPE, DEFAULT_PLAYLIST_TYPE));
        setLogLevel(prefs.get(KEY_LOG_LEVEL, DEFAULT_LOG_LEVEL));
        setPlayerBufferSize(prefs.getInt(KEY_PLAYER_BUFFER_SIZE, DEFAULT_PLAYER_BUFFER_SIZE));
        setAuditInfo(prefs.getBoolean(KEY_AUDIT_INFO, DEFAULT_AUDIT_INFO));
        setCoverSizeSmall(prefs.getInt(KEY_COVER_SIZE_SMALL, DEFAULT_COVER_SIZE_SMALL));
        setCoverSizeLarge(prefs.getInt(KEY_COVER_SIZE_LARGE, DEFAULT_COVER_SIZE_LARGE));
        setRemoteDatabaseURL(prefs.get(KEY_REMOTE_DATABASE_URL, DEFAULT_REMOTE_DATABASE_URL));
        setCatalogScrollUnits(prefs.getInt(KEY_CATALOG_SCROLL_UNITS, DEFAULT_CATALOG_SCROLL_UNITS));
        /** AZ **/
        setUseCDNumber(prefs.getBoolean(KEY_USE_CD_NUMBER, DEFAULT_USE_CD_NUMBER));
        setAlbumArtistTag(prefs.get(KEY_ALBUM_ARTIST_TAG, DEFAULT_ALBUM_ARTIST_TAG));
        setCopyImagesToDirectory(prefs.getBoolean(KEY_COPY_IMAGES, DEFAULT_COPY_IMAGES));
        setImagesLocation(new File(prefs.get(KEY_IMAGES_LOCATION, DEFAULT_IMAGES_LOCATION.getAbsolutePath())));
        setShowDefaultTree(prefs.getBoolean(KEY_SHOW_DEFAULT_TREE, DEFAULT_SHOW_DEFAULT_TREE));      
        setShowEmptyNode(prefs.getBoolean(KEY_SHOW_EMPTY_NODE, DEFAULT_SHOW_EMPTY_NODE));
        setPlayerVolume(prefs.getFloat(KEY_PLAYER_VOLUME, DEFAULT_KEY_PLAYER_VOLUME));
      }

    /**
     * Stores the persistent properties in the specified Preferences.
     *
     * @param prefs   the Preferences object that holds the property values
     */
    void storeIn(Preferences prefs) {
        prefs.putBoolean(KEY_FADE_IN_ON_PLAY, isFadeInOnPlay());
        prefs.putBoolean(KEY_FADE_OUT_ON_STOP, isFadeOutOnStop());
        prefs.putBoolean(KEY_FADE_OUT_ON_PAUSE, isFadeOutOnPause());
        prefs.putBoolean(KEY_FADE_OUT_ON_CHANGE, isFadeOutOnChange());
        prefs.putBoolean(KEY_UPDATE_TAGS, isUpdateTags());
        prefs.put(KEY_DIRECTORY_START_IN, getStartInDirectory().getAbsolutePath());
        prefs.put(KEY_DIRECTORY_DB_LOCATION, getDatabaseLocation().getAbsolutePath());
        prefs.put(KEY_FILE_BACKUP, getFileBackup().getAbsolutePath());
        prefs.put(KEY_FILE_FORMAT_MUSIC, getFileFormatMusic());
        prefs.put(KEY_FILE_FORMAT_IMAGE, getFileFormatImage());
        prefs.putInt(KEY_NEW_FILE_IN_DAYS, getNewFileInDays());
        prefs.put(KEY_ANALYZER_MODE, getAnalyzerMode());
        prefs.put(KEY_DISPLAY_FORMAT_DISC, getDisplayFormatDisc());
        prefs.put(KEY_DISPLAY_FORMAT_TRACK, getDisplayFormatTrack());
        prefs.put(KEY_LOCALE, getLocale());
        prefs.putInt(KEY_ROW_COLOR_EVEN, getRowColorEven().getRGB());
        prefs.putInt(KEY_ROW_COLOR_ODD, getRowColorOdd().getRGB());
        prefs.put(KEY_PLAYLIST_TYPE, getPlaylistType());
        prefs.put(KEY_LOG_LEVEL, getLogLevel());
        prefs.putInt(KEY_PLAYER_BUFFER_SIZE, getPlayerBufferSize());
        prefs.putBoolean(KEY_AUDIT_INFO, isAuditInfo());
        prefs.putInt(KEY_COVER_SIZE_SMALL, getCoverSizeSmall());
        prefs.putInt(KEY_COVER_SIZE_LARGE, getCoverSizeLarge());
        prefs.put(KEY_REMOTE_DATABASE_URL, getRemoteDatabaseURL());
        prefs.putInt(KEY_CATALOG_SCROLL_UNITS, getCatalogScrollUnits());
        /** AZ  **/
        prefs.putBoolean(KEY_USE_CD_NUMBER, isUseCDNumber());
        prefs.put(KEY_ALBUM_ARTIST_TAG, getAlbumArtistTag());
        prefs.putBoolean(KEY_COPY_IMAGES, isCopyImagesToDirectory());
        prefs.put(KEY_IMAGES_LOCATION, getImagesLocation().getAbsolutePath());
        prefs.putBoolean(KEY_SHOW_DEFAULT_TREE, isShowDefaultTree());
        prefs.putBoolean(KEY_SHOW_EMPTY_NODE, isShowEmptyNode());   
        prefs.putFloat(KEY_PLAYER_VOLUME, playerVolume);
     }
 

}