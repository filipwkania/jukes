package com.melloware.jukes.file.filter;

import java.io.File;
import java.util.Locale;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.WordUtils;

/**
 * Filters for music file in JFileChooser.  Such as .mp3, .ogg, .speex, .ape 
 * and .flac files.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class MusicFilter
    extends FileFilter {

    public static final String MP3 = "mp3";
    public static final String OGG = "ogg";
    public static final String FLAC = "flac";
    public static final String SPEEX = "spx";
    public static final String APE = "ape";
    public static final String M4A = "m4a";
    public static final String[] MP3_EXTENSIONS = new String[] {
                                                      MP3, MP3.toUpperCase(Locale.US), WordUtils.capitalize(MP3)
                                                  };
    public static final String[] ENTAGGED_EXTENSIONS = new String[] {
                                                         OGG, SPEEX, FLAC, OGG.toUpperCase(Locale.US),
                                                         FLAC.toUpperCase(Locale.US), SPEEX.toUpperCase(Locale.US),
                                                         WordUtils.capitalize(OGG),
                                                         WordUtils.capitalize(SPEEX),
                                                         WordUtils.capitalize(FLAC),
                                                    };
    public static final String[] APE_EXTENSIONS = new String[] {APE, APE.toUpperCase(Locale.US),
    												WordUtils.capitalize(APE)
    												};
    public static final String[] EXTENSIONS = new String[] {
                                                  MP3, OGG, FLAC, SPEEX,  MP3.toUpperCase(Locale.US),
                                                  OGG.toUpperCase(Locale.US), FLAC.toUpperCase(Locale.US),
                                                  SPEEX.toUpperCase(Locale.US),
                                                  WordUtils.capitalize(MP3), WordUtils.capitalize(OGG),
                                                  WordUtils.capitalize(FLAC), WordUtils.capitalize(SPEEX),
                                                  APE, APE.toUpperCase(Locale.US), WordUtils.capitalize(APE),
    											  };

    /**
     * Default Constuctor
     */
    public MusicFilter() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return "Music Files (*.mp3, *.ogg, *.flac, *.ape, *.spx)";
    }

    /**
     * Accept all music files such as .mp3, .ogg, and .flac
     * <p>
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(final File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }

        return FilenameUtils.isExtension(aFile.getName().toLowerCase(), EXTENSIONS);
    }

}