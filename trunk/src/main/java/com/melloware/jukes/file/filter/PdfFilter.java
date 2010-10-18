package com.melloware.jukes.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * Filters for pdf files in JFileChooser.  Such as .pdf files.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class PdfFilter
    extends FileFilter {

    public static final String PDF = "pdf";
    public static final String[] EXTENSIONS = new String[] { PDF };

    /**
     * Default Constuctor
     */
    public PdfFilter() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return "Adobe Acrobat Files (*.pdf)";
    }

    /**
     * Accept all PDF files such as .pdf.
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