package com.melloware.jukes.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * Filters for disclist files in JFileChooser.  Such as .lst files.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2009
 */
public final class DisclistFilter
    extends FileFilter {

    public static final String lst = "lst";
    public static final String[] EXTENSIONS = new String[] { lst };

    /**
     * Default Constuctor
     */
    public DisclistFilter() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return "Disclist Files (*.lst)";
    }

    /**
     * Accept all files such as .lst.
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