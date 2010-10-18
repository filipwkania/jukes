package com.melloware.jukes.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * Filters for XSPF "spiff" files in JFileChooser.  Such as .xspf files.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class XspfFilter
    extends FileFilter {

    public static final String XSPF = "xspf";
    public static final String[] EXTENSIONS = new String[] { XSPF };

    /**
     * Default Constuctor
     */
    public XspfFilter() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return "XSPF 'Spiff' Files (*.xspf)";
    }

    /**
     * Accept all music files such as .xspf.
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