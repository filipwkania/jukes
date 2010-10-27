package com.melloware.jukes.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * Filters for playlist files in JFileChooser.  Such as .m3u files.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class PlaylistFilter
    extends FileFilter {

    public static final String M3U = "m3u";
    public static final String XSPF = "xspf";
    public static final String[] EXTENSIONS = new String[] { M3U, XSPF };

    /**
     * Default Constuctor
     */
    public PlaylistFilter() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return "Playlist Files (*.m3u, *.xspf)";
    }

    /**
     * Accept all music files such as .m3u.
     * <p>
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }

        return FilenameUtils.isExtension(aFile.getName().toLowerCase(), EXTENSIONS);
    }

}