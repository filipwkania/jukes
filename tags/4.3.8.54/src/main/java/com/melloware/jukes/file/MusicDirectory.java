package com.melloware.jukes.file;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.validation.Severity;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.exception.MusicTagException;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.file.tag.TagFactory;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.util.JukesValidationMessage;
import com.melloware.jukes.util.MessageUtil;
import com.melloware.jukes.util.TimeSpan;

/**
 * This class is used for working with music file directories. Functions said as
 * loading a directory into the database, updating ID3 tags, etc.
 * <p>
 * Copyright (c) 2006 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * @see MusicTag
 * AZ - some modifications 2009, 2010
 */
public final class MusicDirectory {

   private static final Log LOG = LogFactory.getLog(MusicDirectory.class);

   /**
    * Private constructor for no instantiation.
    */
   private MusicDirectory() {
      super();
   }

   /**
    * Creates a new disc in the catalog and optionally updates its ID3 tags.
    * <p>
    * @param aTags the array of MusicTag objects
    * @param aCoverImage the cover image
    * @param aDirectory the directory where these files are located
    * @param aUpdateTags true to update tags, false to not modify them
    * @return a validation message containing the result
    */
   @SuppressWarnings("unused")
   public static JukesValidationMessage createNewDisc(final Object[] aTags, final File aCoverImage,
          final File aDirectory, final JukesValidationMessage aMessage, final boolean aUpdateTags) {
      JukesValidationMessage result = null;
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      boolean setDiscComment = true; //AZ: Fill Disc.Notes on the basis of Track.Comment
      String previousComment = "";
      if (aMessage == null) {
         result = new JukesValidationMessage("Disc created successfully", Severity.OK);
      } else {
         result = aMessage;
      }
      String message = null;
      try {
         HibernateUtil.beginTransaction();
         Artist artist = null;
         Disc disc = null;
         String artistName, discName, sGenre, sYear, sTitle, sComment;
         long totalDuration = 0;
         final int padding = ((aTags.length >= 100) ? 3 : 2);
         
         final MusicTag firstMusicFile = (MusicTag) aTags[0];
         /** AZ **/
         // find or create the artist
            artistName = firstMusicFile.getArtist();
            if (artistName.length()>100) {
            	artistName = artistName.substring(0, 100);
                message = "Artist Name was too long: " + artistName;
                result.setMessage(message);
                result.setSeverity(Severity.WARNING);
                result.setToolTip(message);
                LOG.warn(message);
            }

            String resource = ResourceUtils.getString("hql.artist.find");
            String hql = MessageFormat.format(resource,
                     new Object[] { StringEscapeUtils.escapeSql(artistName) });
            artist = (Artist) HibernateDao.findUniqueByQuery(hql);
            if (artist == null) {
               artist = new Artist();
               artist.setName(artistName);
            }
            // find or create the disc
               discName = firstMusicFile.getDisc();
               if (discName.length()>100) {
            	   discName = discName.substring(0, 100);
                   message = "Disc Name was too long: " + discName;
                   result.setMessage(message);
                   result.setSeverity(Severity.WARNING);
                   result.setToolTip(message);
                   LOG.warn(message);
               }
               resource = ResourceUtils.getString("hql.disc.find");
               hql = MessageFormat.format(resource, new Object[] {
                        StringEscapeUtils.escapeSql(artist.getName()), StringEscapeUtils.escapeSql(discName) });
               disc = (Disc) HibernateDao.findUniqueByQuery(hql);
               if (disc == null) {
                  disc = new Disc();
                  disc.setArtist(artist);
                  disc.setName(discName);
                  artist.addDisc(disc);     
                  sGenre = firstMusicFile.getGenre();
                  if (sGenre.length()>100) {
                	  sGenre = sGenre.substring(0, 100);
                  }
                  disc.setGenre(sGenre);
                  sYear = firstMusicFile.getYear();
                  if (sYear.length()>4) {
                	  sYear = sYear.substring(0, 4);
                  }                 
                  disc.setYear(sYear);
                  disc.setBitrate(firstMusicFile.getBitRate());              
                  
                  if (aCoverImage != null) {
                     disc.setCoverUrl(aCoverImage.getAbsolutePath());
                     disc.setCoverSize(aCoverImage.length());
            	     /** AZ 
             	     Scale and copy images to user defined directory **/
                     String imageLocation = ImageFactory.saveImageToUserDefinedDirectory(aCoverImage,
                    		  disc.getArtist().getName(),
              	              disc.getName(),
              	              disc.getYear());
                  } else {
                      message = "No images found in " + aDirectory;
                      result.setMessage(message);
                      result.setSeverity(Severity.WARNING);
                      result.setToolTip(message);
                      LOG.warn(message);
                  }
                  disc.setCreatedDate(new Date(aDirectory.lastModified()));
                  disc.setLocation(aDirectory.getAbsolutePath());
         // Loop by tracks
         MUSIC_LOOP: for (int i = 0; i < aTags.length; i++) {
            final MusicTag musicFile = (MusicTag) aTags[i];
               musicFile.setArtist(artist.getName());
               musicFile.setDisc(discName);
 
               // clear out the old tracks
               /** AZ ** HibernateDao.deleteAll(disc.getTracks()); **/
                      
            // now create the track
            final Track track = new Track();
            disc.addTrack(track);
            track.setBitrate(musicFile.getBitRate());
            track.setDuration(musicFile.getTrackLength());
            track.setDurationTime(musicFile.getTrackLengthAsString());
            sTitle = musicFile.getTitle();
            if (sTitle.length()>100) {
            	sTitle = sTitle.substring(0, 100);
            }   
            track.setName(sTitle);
            track.setComment(musicFile.getComment());
            track.setTrackUrl(musicFile.getAbsolutePath());
            track.setTrackSize(musicFile.getFile().length());
            musicFile.setTrack(Integer.toString(i + 1), padding);
            track.setTrackNumber(musicFile.getTrack());
            track.setCreatedDate(new Date(musicFile.getFile().lastModified()));
            totalDuration = totalDuration + musicFile.getTrackLength();
            
            // save the tag back out if the flag is set
            if (aUpdateTags) {
               musicFile.save();
            }
         } // MUSIC_LOOP_LOOP

           //Look for identical comments, set Disc.Notes and purge Track.Comments
           final MusicTag musicFileFirst = (MusicTag) aTags[0]; 
           previousComment = musicFileFirst.getComment();
           for (int i = 1; i < aTags.length; i++) {
         	   final MusicTag musicFile = (MusicTag) aTags[i];
                  if (!(previousComment.equals(musicFile.getComment()))) {
                	  setDiscComment = false;
                  }
               previousComment = musicFile.getComment();
           }
           
           if (setDiscComment) {
        	 if (previousComment.length()>500) {
        		 previousComment = previousComment.substring(0, 500);
        	 }
        	 disc.setNotes(previousComment);
        	 final Collection tracks = disc.getTracks();
        	 Track track;
        	 for (final Iterator iter = tracks.iterator(); iter.hasNext();) {
        		 track = (Track) iter.next();
        		 track.setComment("");
        	 }
           } else {
          	 final Collection tracks = disc.getTracks();
        	 Track track;
        	 for (final Iterator iter = tracks.iterator(); iter.hasNext();) {
        		 track = (Track) iter.next();
        		 sComment = track.getComment();
        		 if (sComment.length()>254) {
        			 sComment = sComment.substring(0, 254);
        			 track.setComment(sComment);
        		 }
        	 }       	   
           }

         // update the discs total duration and time
         disc.setDuration(totalDuration);
         final TimeSpan timespan = new TimeSpan(totalDuration * 1000);
         disc.setDurationTime(timespan.getMusicDuration());
       
         // commit changes
         HibernateDao.saveOrUpdate(artist);
         HibernateUtil.commitTransaction();
         } //If Disc not found
         /** AZ **/
         else {
      	   message = Resources.getString("messages.DiscAlreadyExist") + disc.getArtist().getName() + " [" + disc.getYear() + "] " + disc.getName() + " at " + disc.getLocation();
             result.setSeverity(Severity.ERROR);
             result.setMessage(message);
             result.setToolTip(message);
             LOG.warn(message);
         } 
      } catch (MusicTagException ex) {
         message = Resources.getString("messages.ErrorWritingMusicTag") + ex.getMessage();
         result.setMessage(message);
         result.setSeverity(Severity.ERROR);
         result.setToolTip(message);
         LOG.error(message, ex);
         HibernateUtil.rollbackTransaction();
      } catch (InfrastructureException ex) {
         message = Resources.getString("messages.ErrorDB") + ex.getMessage();
         result.setMessage(message);
         result.setSeverity(Severity.ERROR);
         result.setToolTip(message);
         LOG.error(message, ex);
         HibernateUtil.rollbackTransaction();
      } catch (Throwable ex) {
         message = Resources.getString("messages.UnexpectedError") + ex.getMessage();
         result.setMessage(message);
         result.setSeverity(Severity.ERROR);
         result.setToolTip(message);
         LOG.error(message, ex);
         HibernateUtil.rollbackTransaction();
      }

      return result;
   }

   /**
    * Loops through an array of files and determines the largest one in bytes.
    * <p>
    * @param aDirectory the directory to look for the largest file in.
    * @return the file found to be the largest
    */
   public static File findLargestImageFile(final File aDirectory) {
      return findLargestImageFile(aDirectory, null);
   }

   /**
    * For a directory gets all of its music type files into a collection. If
    * none are found return null, and set the validation message.
    * <p>
    * @param aDirectory the directory to scan
    * @return a collection of files or NULL if not found
    */
   public static Collection findMusicFiles(final File aDirectory) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Finding music files in directory:" + aDirectory);
      }
      return findMusicFiles(aDirectory, null);
   }

   /**
    * Silently loads all tracks and one image file from the aDirectory. It must
    * be a directory and if any exception is thrown all we do is return false to
    * silently report the error.
    * <p>
    * @param aDirectory the directory to add
    * @param aUpdateTags boolean whether to overwrite new tags or not
    * @return true if successful, false if any error
    */
   @SuppressWarnings( { "unused", "unchecked" })
   public static JukesValidationMessage loadDiscFromDirectory(final File aDirectory, final boolean aUpdateTags) {

      if (aDirectory == null) {
         throw new IllegalArgumentException("A directory must be selected");
      }

      JukesValidationMessage result = new JukesValidationMessage(aDirectory.getAbsolutePath(), Severity.OK);
      String message = null;
      final File directory = aDirectory;
      // make sure it is a directory
      if ((!directory.isDirectory()) || (!directory.exists())) {
         throw new IllegalArgumentException("A directory must be selected");
      }

      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Loading music directory: " + directory);
         }

         // first get all the music files in this dir
         final Collection musicFiles = findMusicFiles(directory, result);
         if ((musicFiles == null) || (musicFiles.isEmpty())) {
            return result;
         }

         // now get a list of all the image files for disc cover
         final File imageFile = findLargestImageFile(aDirectory, result);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Files found = " + musicFiles.size());
            LOG.debug("Image selected = " + imageFile);
         }

         final ArrayList tagList = new ArrayList();
         MUSIC_LOOP: for (final Iterator iter = musicFiles.iterator(); iter.hasNext();) {
            final File musicFile = (File) iter.next();
            if (LOG.isDebugEnabled()) {
               LOG.debug("Music File: " + musicFile);
            }
            // load the tag
            final MusicTag tag = TagFactory.getTag(musicFile);
            tagList.add(tag);
         } // MUSIC_LOOP
         
         // sort the tags by track number
         Collections.sort(tagList);

         result = createNewDisc(tagList.toArray(), imageFile, aDirectory, result, aUpdateTags);

      } catch (MusicTagException ex) {
         message = "Error opening music file: " + ex.getMessage();
         result.setSeverity(Severity.ERROR);
         result.setToolTip(message);
         LOG.error(message, ex);
      } catch (Throwable ex) {
         message = "Unexpected Error: " + ex.getMessage();
         result.setSeverity(Severity.ERROR);
         result.setToolTip(message);
         LOG.error(message, ex);
      }

      return result;
   }

   /**
    * Checks for the disc at the location on the hard drive. If that location no
    * longer exists on the hard drive then this disc is removed from the
    * database. This is useful for when using the Jukes with portable storage
    * devices like Archos Jukebox. Thanks to Bill Farkas for suggesting this
    * feature.
    * @param aDisc the disc to check the hard drive for
    * @return true if the disc was OK, false if the disc was removed
    */
   public static boolean removeDiscIfNoLongerExists(final Disc aDisc) {
      boolean result = false;
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();

      if (aDisc == null) {
         throw new IllegalArgumentException("Disc may not be null");
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Checking disc for validity: " + aDisc.getName());
      }

      // get the file location for this disc
      final File discDirectory = new File(aDisc.getLocation());

      // return true if the directory still exists
      result = discDirectory.exists();

      // remove the directory if it no longer exists
      try {
         if (!result) {
            // try to delete from database
            HibernateUtil.beginTransaction();
            final Artist artist = aDisc.getArtist();
            artist.getDiscs().remove(aDisc);
            HibernateDao.delete(aDisc);
            HibernateUtil.commitTransaction();
         }
      } catch (InfrastructureException ex) {
         final String errorMessage = ResourceUtils.getString("messages.ErrorDeletingDisc");
         LOG.error(errorMessage, ex);
         MessageUtil.showError(mainFrame, errorMessage); //AZ
         HibernateUtil.rollbackTransaction();
         result = false;
      }

      return result;
   }

   /**
    * Loops through an array of files and determines the largest one in bytes.
    * <p>
    * @param aDirectory the directory to look for the largest file in.
    * @param aMessage a JukesValidationMessage to contain any errors, may be
    *           null
    * @return the file found to be the largest
    */
   private static File findLargestImageFile(final File aDirectory, final JukesValidationMessage aMessage) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Finding largest image in directory: " + aDirectory);
      }

      File result = null;
      final Collection imageFiles = FileUtils.listFiles(aDirectory, FilterFactory.IMAGE_FILTER, null);

      // get the largest of the images if there are more than one
      if ((imageFiles != null) && (!imageFiles.isEmpty())) {
         LOG.debug("Images found in directory");
         final Object[] files = (Object[]) imageFiles.toArray();

         // if only one image then just return that image
         if (imageFiles.size() == 1) {
            result = (File) files[0];
         } else {
            // need to pick the largest file for the biggest resolution cover
            long filesize = 0;
            /** AZ - find "cover.*" and "folder.*" files
             * 
             */ File coverFile = null;
            for (int i = 0; i < files.length; i++) {
               final File file = (File) files[i];
               final long size = file.length();
               if (size > filesize) {
                  filesize = size;
                  result = file;
               }
               final String shortName;
               if (file.getName().lastIndexOf(".") != -1) {
            	   shortName = file.getName().substring(0,file.getName().lastIndexOf(".")).toUpperCase();
               } else {
            	   shortName = file.getName().toUpperCase();
               }
               if ((shortName.equals("COVER") ||
            	   shortName.equals("FOLDER") ||
            	   shortName.equals("FRONT") ) &
            	   (coverFile == null ) ) {
            	   coverFile = file;
            	   }
            }
     	   if (coverFile != null) {
     		   result = coverFile;
      	   }
     	   else {
     		  if (aMessage != null) {
                  LOG.info("More than one image found.");
                  aMessage.setSeverity(Severity.WARNING);
                  aMessage.setToolTip("Directory contained more than one image so largest was selected. ");
               }
     	   }
         }
      }     
      return result;
   }

   /**
    * For a directory gets all of its music type files into a collection. If
    * none are found return null, and set the validation message.
    * AZ: if sub-directories are found no warning messages to be set  
    * <p>
    * @param aDirectory the directory to scan
    * @param aMessage the validation message to fill
    * @return a collection of files or NULL if not found
    */
   private static Collection findMusicFiles(final File aDirectory, final JukesValidationMessage aMessage) {
      // first get all the music files and subDirs in this dir
      final Collection results = FileUtils.listFiles(aDirectory, FilterFactory.MUSIC_FILTER, null);
      final Collection resultsDir = FileUtils.listFiles(aDirectory, FilterFactory.dirIOFilter(), null);//AZ

      // if collection is null just return true
      if (((results == null) || (results.isEmpty())) && ((resultsDir==null) || (resultsDir.isEmpty()) ) && (aMessage != null)) {
         final String message = "Directory contained no music files. " + aDirectory;
         LOG.warn(message);
         aMessage.setSeverity(Severity.WARNING);
         aMessage.setToolTip(message);
      }

      return results;
   }
}