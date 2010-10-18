package com.melloware.jukes.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

/**
 * Provides FilterFactory for file and directory choosers based on Jakarta
 * Commons-IO helper package.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class FilterFactory {
	
	public static final IOFileFilter MUSIC_FILTER = musicIOFilter();
	public static final IOFileFilter IMAGE_FILTER = imageIOFilter();
	public static final IOFileFilter TRUE_FILTER = TrueFileFilter.INSTANCE;

    /**
     * Default constructor.
     */
    private FilterFactory() {
        super();
    }

    /**
     * Return a CsvFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter csvFileFilter() {
        return new CsvFilter();
    }

    /**
     * Return a csvIOFilter to filter out csv files of a directory.
     * <p>
     * @return the IOFilter
     */
    public static IOFileFilter csvIOFilter() {
        return new SuffixFileFilter(CsvFilter.EXTENSIONS);
    }

    /**
     * Static method to force a certain extenion on a file.
     * <p>
     * @param aFile the file to check extension for
     * @return the new file or the original if no changes made
     */
    public static File forceCsvExtension(final File aFile) {
        File file = aFile;
        if (!FilenameUtils.isExtension(file.getAbsolutePath(), CsvFilter.EXTENSIONS)) {
            file = new File(file.getAbsolutePath() + "." + CsvFilter.CSV);
        }
        return file;
    }

    /**
     * Static method to force a certain extenion on a file.
     * <p>
     * @param aFile the file to check extension for
     * @return the new file or the original if no changes made
     */
    public static File forceM3uExtension(final File aFile) {
        File file = aFile;
        if (!FilenameUtils.isExtension(file.getAbsolutePath(), M3uFilter.EXTENSIONS)) {
            file = new File(file.getAbsolutePath() + "." + M3uFilter.M3U);
        }
        return file;
    }

    /**
     * Static method to force a certain extenion on a file.
     * <p>
     * @param aFile the file to check extension for
     * @return the new file or the original if no changes made
     */
    public static File forcePdfExtension(final File aFile) {
        File file = aFile;
        if (!FilenameUtils.isExtension(file.getAbsolutePath(), PdfFilter.EXTENSIONS)) {
            file = new File(file.getAbsolutePath() + "." + PdfFilter.PDF);
        }
        return file;
    }

    /**
     * Static method to force a certain extenion on a file.
     * <p>
     * @param aFile the file to check extension for
     * @return the new file or the original if no changes made
     */
    public static File forcePlaylistExtension(final File aFile) {
        File file = aFile;
        if (!FilenameUtils.isExtension(file.getAbsolutePath(), PlaylistFilter.EXTENSIONS)) {
            file = new File(file.getAbsolutePath() + "." + PlaylistFilter.M3U);
        }
        return file;
    }

    /**
     * Static method to force a certain extenion on a file.
     * <p>
     * @param aFile the file to check extension for
     * @return the new file or the original if no changes made
     */
    public static File forceTextExtension(final File aFile) {
        File file = aFile;
        if (!FilenameUtils.isExtension(file.getAbsolutePath(), TextFilter.EXTENSIONS)) {
            file = new File(file.getAbsolutePath() + "." + TextFilter.TXT);
        }
        return file;
    }

    /**
     * Static method to force a certain extenion on a file.
     * <p>
     * @param aFile the file to check extension for
     * @return the new file or the original if no changes made
     */
    public static File forceXmlExtension(final File aFile) {
        File file = aFile;
        if (!FilenameUtils.isExtension(file.getAbsolutePath(), XmlFilter.EXTENSIONS)) {
            file = new File(file.getAbsolutePath() + "." + XmlFilter.XML);
        }
        return file;
    }

    /**
     * Static method to force a certain extenion on a file.
     * <p>
     * @param aFile the file to check extension for
     * @return the new file or the original if no changes made
     */
    public static File forceXspfExtension(final File aFile) {
        File file = aFile;
        if (!FilenameUtils.isExtension(file.getAbsolutePath(), XspfFilter.EXTENSIONS)) {
            file = new File(file.getAbsolutePath() + "." + XspfFilter.XSPF);
        }
        return file;
    }

    /**
     * Return a ImageFileFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter imageFileFilter() {
        return new ImageFilter();
    }

    /**
     * Return a imageIOFilter to filter out images of a directory.
     * <p>
     * @return the IOFilter
     */
    public static IOFileFilter imageIOFilter() {
        return new SuffixFileFilter(ImageFilter.EXTENSIONS);
    }

    /**
     * Return a m3uFileFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter m3uFileFilter() {
        return new M3uFilter();
    }

    /**
     * Return a m3uIOFilter to filter out m3u files of a directory.
     * <p>
     * @return the IOFilter
     */
    public static IOFileFilter m3uIOFilter() {
        return new SuffixFileFilter(M3uFilter.EXTENSIONS);
    }

    /**
     * Return a MusicFileFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter musicFileFilter() {
        return new MusicFilter();
    }

    /**
     * Return a MusicIOFilter to filter out MP3's of a directory.
     * <p>
     * @return the IOFilter
     */
    public static IOFileFilter musicIOFilter() {
        return new SuffixFileFilter(MusicFilter.EXTENSIONS);
    }

    /**
     * Return a PdfFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter pdfFileFilter() {
        return new PdfFilter();
    }

    /**
     * Return a pdfIOFilter to filter out pdf files of a directory.
     * <p>
     * @return the IOFilter
     */
    public static IOFileFilter pdfIOFilter() {
        return new SuffixFileFilter(PdfFilter.EXTENSIONS);
    }

    /**
     * Return a PlaylistFileFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter playlistFileFilter() {
        return new PlaylistFilter();
    }

    /**
     * Return a playlistIOFilter to filter out playlists of a directory.
     * <p>
     * @return the IOFilter
     */
    public static IOFileFilter playlistIOFilter() {
        return new SuffixFileFilter(PlaylistFilter.EXTENSIONS);
    }

    /**
     * Return a TextFileFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter textFileFilter() {
        return new TextFilter();
    }

    /**
     * Return a textIOFilter to filter out text files of a directory.
     * <p>
     * @return the IOFilter
     */
    public static IOFileFilter textIOFilter() {
        return new SuffixFileFilter(TextFilter.EXTENSIONS);
    }

    /**
     * Return a XmlFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter xmlFileFilter() {
        return new XmlFilter();
    }

    /**
     * Return a textIOFilter to filter out xml files of a directory.
     * <p>
     * @return the IOFilter
     */
    public static IOFileFilter xmltIOFilter() {
        return new SuffixFileFilter(XmlFilter.EXTENSIONS);
    }

    /**
     * Return a xspfFileFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter xspfFileFilter() {
        return new XspfFilter();
    }

    /**
     * Return a xspfIOFilter to filter out xspf files of a directory.
     * <p>
     * @return the IOFilter
     */
    public static IOFileFilter xspfIOFilter() {
        return new SuffixFileFilter(XspfFilter.EXTENSIONS);
    }
    
    //AZ Development
    /**
     * Static method to force a certain extenion on a file.
     * <p>
     * @param aFile the file to check extension for
     * @return the new file or the original if no changes made
     */
    public static File forceLstExtension(final File aFile) {
        File file = aFile;
        if (!FilenameUtils.isExtension(file.getAbsolutePath(), lstFilter.EXTENSIONS)) {
            file = new File(file.getAbsolutePath() + "." + lstFilter.lst);
        }
        return file;
    }

    /**
     * Return a DisclistFileFilter for a JFileChooser.
     * <p>
     * @return the FileFilter to use.
     */
    public static FileFilter disclistFileFilter() {
        return new DisclistFilter();
    }
 
    /**AZ:
     * Return a dirFilter to filter out sub-directories of a directory.
     * <p>
     * @return the FileFilter
     */
    public static FileFilter dirFilter() {
        return new dirFilter();
    }
    
    /**AZ:
     * Return a dirFilter to filter out sub-directories of a directory.
     * <p>
     * @return the IOFileFilter
     */
    public static IOFileFilter dirIOFilter() {
    	return FileFilterUtils.directoryFileFilter();
    }
    
}