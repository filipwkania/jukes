package com.melloware.jukes.file.image;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

import org.apache.commons.io.FilenameUtils;

import com.melloware.jukes.file.filter.ImageFilter;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Helper for Image File chooser.
 * <p>
 * Copyright (c) 2006
 * Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class ImageFileView
    extends FileView {
    
    static ImageIcon gifIcon = (ImageIcon)Resources.FILE_GIF_ICON;
    static ImageIcon jpgIcon = (ImageIcon)Resources.FILE_JPG_ICON;
    static ImageIcon pngIcon = (ImageIcon)Resources.FILE_PNG_ICON;
    static ImageIcon tiffIcon = (ImageIcon)Resources.FILE_TIF_ICON;

    public String getDescription(File f) {
        return null;    // let the L&F FileView figure this out
    }

    public Icon getIcon(File f) {
        String extension = FilenameUtils.getExtension(f.getName());
        Icon icon = null;

        if (extension != null) {
            if (extension.equals(ImageFilter.JPEG) || extension.equals(ImageFilter.JPG)) {
                icon = jpgIcon;
            } else if (extension.equals(ImageFilter.GIF)) {
                icon = gifIcon;
            } else if (extension.equals(ImageFilter.TIF) || extension.equals(ImageFilter.TIFF)) {
                icon = tiffIcon;
            } else if (extension.equals(ImageFilter.PNG)) {
                icon = pngIcon;
            }
        }
        return icon;
    }

    public String getName(File f) {
        return null;    // let the L&F FileView figure this out
    }

    public String getTypeDescription(File f) {
        String extension = FilenameUtils.getExtension(f.getName());
        String type = null;

        if (extension != null) {
            if (extension.equals(ImageFilter.JPEG) || extension.equals(ImageFilter.JPG)) {
                type = "JPEG Image";
            } else if (extension.equals(ImageFilter.GIF)) {
                type = "GIF Image";
            } else if (extension.equals(ImageFilter.TIF) || extension.equals(ImageFilter.TIFF)) {
                type = "TIFF Image";
            } else if (extension.equals(ImageFilter.PNG)) {
                type = "PNG Image";
            }
        }
        return type;
    }

    public Boolean isTraversable(File f) {
        return null;    // let the L&F FileView figure this out
    }
}