package com.melloware.jukes.file.tag;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.melloware.jukes.exception.MusicTagException;
import com.melloware.jukes.file.filter.MusicFilter;

/**
 * Implements the Factory pattern for creating instances of music tag objects.
 * Determines, at runtime, based on the file ending which type of music tag to
 * created. MP3Tag's for .mp3 files, AudioFileTag's for .ogg, .flac, .spx, .ape.
 * <p>
 * Copyright (c) 2006 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class TagFactory {

   private static final Log LOG = LogFactory.getLog(TagFactory.class);

   /**
    * Default constructor. Private so no instantiation.
    */
   private TagFactory() {
      super();
   }

   /**
    * Gets the proper music tag type based on aFile.
    * <p>
    * @param aFile the file to load
    * @return MusicTag the tag loaded
    * @throws MusicTagExeption if any error occurs
    */
   public static MusicTag getTag(final File aFile) throws MusicTagException {

      MusicTag tag = null;
      if (LOG.isDebugEnabled()) {
         LOG.debug("Loading tag: " + aFile.getAbsolutePath());
      }

      if (FilenameUtils.isExtension(aFile.getName(), MusicFilter.MP3_EXTENSIONS)) {
         tag = new Mp3Tag(aFile);
      } else if (FilenameUtils.isExtension(aFile.getName(), MusicFilter.ENTAGGED_EXTENSIONS)) {
         tag = new AudioFileTag(aFile);
      } else if (FilenameUtils.isExtension(aFile.getName(), MusicFilter.APE_EXTENSIONS)) {
         tag = new ApeFileTag(aFile);
      } else {
         throw new MusicTagException(aFile + " is not a known music file type.");
      }

      return tag;
   }

   /**
    * Gets the proper music tag type based on aFile.
    * <p>
    * @param aFileLocation the file path
    * @return MusicTag the tag loaded
    * @throws MusicTagExcption if any error occurs
    */
   public static MusicTag getTag(final String aFileLocation) throws MusicTagException {
      return getTag(new File(aFileLocation));
   }
}