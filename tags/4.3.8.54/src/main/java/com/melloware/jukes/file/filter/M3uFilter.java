package com.melloware.jukes.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * Filters for M3U "winamp" files in JFileChooser.  Such as .m3u files.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class M3uFilter extends FileFilter {
    
    public final static String M3U = "m3u";
    public final static String[] EXTENSIONS = new String[] {M3U};
    
    /**
     * Default Constuctor
     */
    public M3uFilter() {
        super();
    }

    /**
     * Accept all music files such as .m3u.
     * <p>
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(final File aFile) {
        if (aFile.isDirectory()) {
            return true;
        }
        
        return FilenameUtils.isExtension(aFile.getName().toLowerCase(),EXTENSIONS);
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return "M3U 'Winamp' Files (*.m3u)";
    }
    
    

}
