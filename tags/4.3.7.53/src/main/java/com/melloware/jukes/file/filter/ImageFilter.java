package com.melloware.jukes.file.filter;

import java.io.File;
import java.util.Locale;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.WordUtils;

/**
 * Filters for image files in JFileChooser.  Such as .jpg and .gif files.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class ImageFilter
    extends FileFilter {

    public static final String JPEG = "jpeg";
    public static final String JPG = "jpg";
    public static final String GIF = "gif";
    public static final String TIFF = "tiff";
    public static final String TIF = "tif";
    public static final String PNG = "png";
    public static final String[] EXTENSIONS = new String[] {
                                                  JPEG, JPG, GIF, TIFF, TIF, PNG, JPEG.toUpperCase(Locale.US),
                                                  JPG.toUpperCase(Locale.US), GIF.toUpperCase(Locale.US),
                                                  TIFF.toUpperCase(Locale.US), TIF.toUpperCase(Locale.US),
                                                  PNG.toUpperCase(Locale.US), WordUtils.capitalize(JPEG),
                                                  WordUtils.capitalize(JPG), WordUtils.capitalize(GIF),
                                                  WordUtils.capitalize(TIFF), WordUtils.capitalize(TIF),
                                                  WordUtils.capitalize(PNG)
                                              };

    /**
     * Default Constuctor
     */
    public ImageFilter() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return "Image Files";
    }

    /**
     * Accept all music files such as .png.
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