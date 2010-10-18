package com.melloware.jukes.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * Filters for *.lst files in JFileChooser.  
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2009
 */
public final class lstFilter extends FileFilter {
    
    public final static String lst = "lst";
    public final static String[] EXTENSIONS = new String[] {lst};
    
    /**
     * Default Constuctor
     */
    public lstFilter() {
        super();
    }

    /**
     * Accept all files such as .lst.
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
        return "disclist Files (*.lst)";
    }
    
    

}
