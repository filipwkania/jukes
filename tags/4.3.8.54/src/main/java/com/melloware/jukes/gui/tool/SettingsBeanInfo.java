package com.melloware.jukes.gui.tool;


import org.apache.log4j.Level;

import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import com.l2fprod.common.beans.editor.DirectoryPropertyEditor;
import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.beans.editor.IntegerPropertyEditor;
import com.melloware.jukes.file.filter.PlaylistFilter;
import com.melloware.jukes.gui.view.component.SpectrumTimeAnalyzer;

/**
 * Static class used to represent settings in property sheet.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ - modifications 2009
 */
public class SettingsBeanInfo
    extends BaseBeanInfo {
	
	private static final String[] LOG_LEVELS = new String[] { Level.DEBUG.toString(), Level.INFO.toString(), Level.WARN.toString(), Level.ERROR.toString() };
    private static final String[] ANALYZER_MODES = new String[] { SpectrumTimeAnalyzer.OFF, SpectrumTimeAnalyzer.ANALYZER, SpectrumTimeAnalyzer.SCOPE };
	private static final String DATABASE = "Database";
	private static final String DISPLAY = "Display";
	private static final String FILES = "Files and Directories";
	private static final String PLAYER = "Player";
	private static final String TAGS = "Tags"; //AZ
	

    public SettingsBeanInfo() {
        super(Settings.class);
        ExtendedPropertyDescriptor descriptor = null;
        
        descriptor = addProperty("databaseLocation");
        descriptor.setCategory(DATABASE);
        descriptor.setDisplayName("Local Database Location");
        descriptor.setShortDescription(Resources.getString("label.databaseLocation"));
        descriptor.setPropertyEditorClass(DirectoryPropertyEditor.class);
        
        descriptor = addProperty("remoteDatabaseURL");
        descriptor.setCategory(DATABASE);
        descriptor.setDisplayName("Remote Database URL");
        descriptor.setShortDescription(Resources.getString("label.remoteDatabaseURL"));
        
        descriptor = addProperty("displayFormatDisc");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Disc Display Format");
        descriptor.setShortDescription(Resources.getString("label.displayFormatDisc"));

        descriptor = addProperty("displayFormatTrack");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Track Display Format");
        descriptor.setShortDescription(Resources.getString("label.displayFormatTrack"));

        descriptor = addProperty("newFileInDays");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("New File Marker (in days)");
        descriptor.setShortDescription(Resources.getString("label.newFileInDays"));
        
        descriptor = addProperty("catalogScrollUnits");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Catalog Scroll Units");
        descriptor.setShortDescription(Resources.getString("label.catalogScrollUnits"));
        
        descriptor = addProperty("auditInfo");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Audit Information");
        descriptor.setShortDescription(Resources.getString("label.auditInfo"));
        
        descriptor = addProperty("coverSizeSmall");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Cover Size Small (in pixels)");
        descriptor.setShortDescription(Resources.getString("label.coverSizeSmall"));
        
        descriptor = addProperty("coverSizeLarge");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Cover Size Large (in pixels)");
        descriptor.setShortDescription(Resources.getString("label.coverSizeLarge"));
        
        descriptor = addProperty("rowColorEven");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Row Color Even");
        descriptor.setShortDescription(Resources.getString("label.rowColorEven"));
        
        descriptor = addProperty("rowColorOdd");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Row Color Odd");
        descriptor.setShortDescription(Resources.getString("label.rowColorOdd"));
        
        /**
         * AZ - showDefaultTree
         */
        descriptor = addProperty("showDefaultTree");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Show Main Tree by Default");
        descriptor.setShortDescription(Resources.getString("label.showDefaultTree"));
 
        /**
         * AZ - showEmptyNode
         */
        descriptor = addProperty("showEmptyNode");
        descriptor.setCategory(DISPLAY);
        descriptor.setDisplayName("Show Empty Nodes in the Tree");
        descriptor.setShortDescription(Resources.getString("label.showEmptyNode"));
 
        /**
         * AZ - useCDNumber
         */
        descriptor = addProperty("useCDNumber");
        descriptor.setCategory(TAGS);
        descriptor.setDisplayName("Use CD Number");
        descriptor.setShortDescription(Resources.getString("label.useCDNumber"));
        
        /**
         * AZ - albumArtistTag
         */
        descriptor = addProperty("albumArtistTag");
        descriptor.setCategory(TAGS);
        descriptor.setDisplayName("Album Artist TAG");
        descriptor.setShortDescription(Resources.getString("label.albumArtistTag"));
        /**
         * AZ - update TAGs in music files
         */
        descriptor = addProperty("updateTags");
        descriptor.setCategory(TAGS);
        descriptor.setDisplayName("Update audio tags in files");
        descriptor.setShortDescription(Resources.getString("label.updateTags"));
        
        descriptor = addProperty("startInDirectory");
        descriptor.setCategory(FILES);
        descriptor.setDisplayName("Music Directory");
        descriptor.setShortDescription(Resources.getString("label.startInDirectory"));
        descriptor.setPropertyEditorClass(DirectoryPropertyEditor.class);

        descriptor = addProperty("fileFormatMusic");
        descriptor.setCategory(FILES);
        descriptor.setDisplayName("File Format Music");
        descriptor.setShortDescription(Resources.getString("label.fileFormatMusic"));
        
        descriptor = addProperty("fileFormatImage");
        descriptor.setCategory(FILES);
        descriptor.setDisplayName("File Format Image");
        descriptor.setShortDescription(Resources.getString("label.fileFormatImage"));
        
        descriptor = addProperty("playlistType");
        descriptor.setCategory(FILES);
        descriptor.setDisplayName("Default Playlist Type");
        descriptor.setShortDescription(Resources.getString("label.playlistType"));
        descriptor.setPropertyEditorClass(PlaylistTypeEditor.class);
        
        descriptor = addProperty("fileBackup");
        descriptor.setCategory(FILES);
        descriptor.setDisplayName("Backup File");
        descriptor.setShortDescription(Resources.getString("label.fileBackup"));
        descriptor.setPropertyEditorClass(FilePropertyEditor.class);
        
        descriptor = addProperty("logLevel");
        descriptor.setCategory(FILES);
        descriptor.setDisplayName("Logfile Level");
        descriptor.setShortDescription(Resources.getString("label.logLevel"));
        descriptor.setPropertyEditorClass(LogLevelTypeEditor.class);
    
        /**
         * AZ - copyImages
         */
        descriptor = addProperty("copyImagesToDirectory");
        descriptor.setCategory(FILES);
        descriptor.setDisplayName("Copy Images");
        descriptor.setShortDescription(Resources.getString("label.copyImagesToDirectory"));

        /**
         * AZ - imagesLocation
         */
        descriptor = addProperty("imagesLocation");
        descriptor.setCategory(FILES);
        descriptor.setDisplayName("Images Directory");
        descriptor.setShortDescription(Resources.getString("label.imagesLocation"));
        descriptor.setPropertyEditorClass(DirectoryPropertyEditor.class);

        descriptor = addProperty("analyzerMode");
        descriptor.setCategory(PLAYER);
        descriptor.setDisplayName("Spectrum Analyzer Mode");
        descriptor.setShortDescription(Resources.getString("label.analyzerMode"));
        descriptor.setPropertyEditorClass(SpectrumAnalyzerTypeEditor.class);
        
        descriptor = addProperty("playerBufferSize");
        descriptor.setCategory(PLAYER);
        descriptor.setDisplayName("Buffer Size (in bytes)");
        descriptor.setShortDescription(Resources.getString("label.playerBufferSize"));
        descriptor.setPropertyEditorClass(IntegerPropertyEditor.class);
        
        descriptor = addProperty("fadeInOnPlay");
        descriptor.setCategory(PLAYER);
        descriptor.setDisplayName("Fade In On Play");
        descriptor.setShortDescription(Resources.getString("label.fadeInOnPlay"));
        
        descriptor = addProperty("fadeOutOnChange");
        descriptor.setCategory(PLAYER);
        descriptor.setDisplayName("Fade Out On Change");
        descriptor.setShortDescription(Resources.getString("label.fadeOutOnChange"));
        
        descriptor = addProperty("fadeOutOnPause");
        descriptor.setCategory(PLAYER);
        descriptor.setDisplayName("Fade Out On Pause");
        descriptor.setShortDescription(Resources.getString("label.fadeOutOnPause"));
        
        descriptor = addProperty("fadeOutOnStop");
        descriptor.setCategory(PLAYER);
        descriptor.setDisplayName("Fade Out On Stop");
        descriptor.setShortDescription(Resources.getString("label.fadeOutOnStop"));
    }
    
    /**
     * Class used to pick playlist types from a combo box
     */
    public static class PlaylistTypeEditor extends ComboBoxPropertyEditor {
  	  public PlaylistTypeEditor() {
  	    super();	    
  	    setAvailableValues(PlaylistFilter.EXTENSIONS);
  	  }
  	}
    
    /**
     * Class used to pick log levels for output
     */
    public static class LogLevelTypeEditor extends ComboBoxPropertyEditor {
  	  public LogLevelTypeEditor() {
  	    super();
  	    setAvailableValues(LOG_LEVELS);
  	  }
  	}
    
    /**
     * Class used to pick Spectrum Analyzer Levels.
     */
    public static class SpectrumAnalyzerTypeEditor extends ComboBoxPropertyEditor {
     public SpectrumAnalyzerTypeEditor() {
       super();
       setAvailableValues(ANALYZER_MODES);
     }
   }
}
