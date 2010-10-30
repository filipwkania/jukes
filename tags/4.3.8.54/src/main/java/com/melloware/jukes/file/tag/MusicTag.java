package com.melloware.jukes.file.tag;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaudiotagger.tag.id3.valuepair.V2GenreTypes;
import org.jaudiotagger.tag.reference.GenreTypes;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import com.melloware.jukes.exception.MusicTagException;
import com.melloware.jukes.file.FileUtil;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.util.TimeSpan;

/**
 * Abstract superclass for all tag types.
 * <p>
 * Copyright (c) 2006 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ - some modifications 2009
 */
@SuppressWarnings("PMD")
abstract public class MusicTag implements ITag, Comparable {

   private static final Log LOG = LogFactory.getLog(MusicTag.class);
   public static final String NO_TAG = "[notag]";
   /**
    * AZ - if no year is specified in tags then use year 9999 instead of current
    * year public static final String CURRENT_YEAR = currentYear();
    */
   public static final String CURRENT_YEAR = "9999";
   public static final String DEFAULT_FILE_FORMAT = "%n - %t";

   protected File file;
   protected Long bitRate = null;
   protected long trackLength = 1;
   protected Map header;
   protected String artist = null;
   protected String comment = null;
   protected String disc = null;
   protected String encodedBy = null;
   protected String genre = null;
   protected String title = null;
   protected String track = null;
   protected String year = null;

   // since this is static it will be used by all instances of MP3Tag created
   /** EAL **/
   public static Settings settings = null;

   /** EAL **/
   // call this method only once to store the static settings variable from the
   // MainModule code where its set
   public static void setSettings(Settings aSettings) {
      settings = aSettings;
   }

   /**
    * Constructor that takes the music file.
    * <p>
    * @param aFile the music file
    * @throws MusicTagException if any error occurs opening tag
    */
   public MusicTag(File aFile) throws MusicTagException {
      super();
      if (aFile == null) {
         throw new MusicTagException("File is not valid.");
      }
      this.file = aFile;

      try {
         // set the file to writable if it is readonly
         if (!aFile.canWrite()) {
            aFile.setReadable(true, false);
         }

         // get the header info
         final AudioFileFormat format = AudioSystem.getAudioFileFormat(aFile);
         if (format instanceof TAudioFileFormat) {
            // Tritonus SPI compliant audio file format.
            final Map props = ((TAudioFileFormat) format).properties();
            // Clone the Map because it is not mutable.
            this.header = FileUtil.deepCopy(props);
         } else {
            this.header = null;
         }
      } catch (UnsupportedAudioFileException ex) {
         LOG.error(ex.getMessage(), ex);
         throw new MusicTagException(Resources.getString("messages.UnsupportedAudioFileException")
                  + aFile.getAbsolutePath() + "\n\n" + ex.getMessage());
      } catch (IOException ex) {
         LOG.error(ex.getMessage(), ex);
         throw new MusicTagException(Resources.getString("messages.IOExceptionMusicFileTag") + aFile.getAbsolutePath()
                  + "\n\n" + ex.getMessage());
      } catch (Exception ex) {
         LOG.error(ex.getMessage(), ex);
         throw new MusicTagException(Resources.getString("messages.ErrorMusicFileTag") + aFile.getAbsolutePath()
                  + "\n\n" + ex.getMessage());
      }
   }

   /**
    * Gets the artist.
    * <p>
    * @return Returns the artist.
    */
   abstract public String getArtist();

   /**
    * Gets the bitRate.
    * <p>
    * @return Returns the bitRate.
    */
   abstract public Long getBitRate();

   /**
    * Gets the comment.
    * <p>
    * @return Returns the comment.
    */
   abstract public String getComment();

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.file.tag.ITag#getCopyrighted()
    */
   abstract public String getCopyrighted();

   /**
    * Gets the disc.
    * <p>
    * @return Returns the disc.
    */
   abstract public String getDisc();

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.file.tag.ITag#getEmphasis()
    */
   abstract public String getEmphasis();

   /**
    * Gets the encodedBy.
    * <p>
    * @return Returns the encodedBy.
    */
   abstract public String getEncodedBy();

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.file.tag.ITag#getFrequency()
    */
   abstract public String getFrequency();

   /**
    * Gets the genre.
    * <p>
    * @return Returns the genre.
    */
   abstract public String getGenre();

   /**
    * Gets the header.
    * <p>
    * @return Returns the header.
    */
   abstract public Map getHeader();

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.file.tag.ITag#getLayer()
    */
   abstract public String getLayer();

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.file.tag.ITag#getMode()
    */
   abstract public String getMode();

   /**
    * Gets the title.
    * <p>
    * @return Returns the title.
    */
   abstract public String getTitle();

   /**
    * Gets the track.
    * <p>
    * @return Returns the track.
    */
   abstract public String getTrack();

   /**
    * Gets the trackLength.
    * <p>
    * @return Returns the trackLength.
    */
   abstract public long getTrackLength();

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.file.tag.ITag#getVersion()
    */
   abstract public String getVersion();

   /**
    * Gets the year.
    * <p>
    * @return Returns the year.
    */
   abstract public String getYear();

   /**
    * Sets the artist.
    * <p>
    * @param aArtist The artist to set.
    */
   abstract public void setArtist(String aArtist);

   /**
    * Sets the comment.
    * <p>
    * @param aComment The comment to set.
    */
   abstract public void setComment(String aComment);

   /**
    * Sets the disc.
    * <p>
    * @param aDisc The disc to set.
    */
   abstract public void setDisc(String aDisc);

   /**
    * Sets the encodedBy.
    * <p>
    * @param aEncodedBy The encodedBy to set.
    */
   abstract public void setEncodedBy(String aEncodedBy);

   /**
    * Sets the genre.
    * <p>
    * @param aGenre The genre to set.
    */
   abstract public void setGenre(String aGenre);

   /**
    * Sets the title.
    * <p>
    * @param aTitle The title to set.
    */
   abstract public void setTitle(String aTitle);

   /**
    * Sets the track.
    * <p>
    * @param aTrack The track to set.
    */
   abstract public void setTrack(String aTrack);

   /**
    * Sets the track.
    * <p>
    * @param aTrack The track to set.
    * @param padding the number of 0's to pad
    */
   abstract public void setTrack(String track, int padding);

   /**
    * Sets the trackLength.
    * <p>
    * @param aTrackLength The trackLength to set.
    */
   abstract public void setTrackLength(long aTrackLength);

   /**
    * Sets the year.
    * <p>
    * @param aYear The year to set.
    */
   abstract public void setYear(String aYear);

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.file.tag.ITag#isVBR()
    */
   abstract public boolean isVBR();

   /**
    * Removes tags from the audio file.
    * <p>
    * @throws MusicTagException if any error occurs removing the tag
    */
   abstract public void removeTags() throws MusicTagException;

   /**
    * Renames this Music file based on a format from prefs. The format is in
    * aFormat and can have values %n for track number, %t for title, %a for
    * artist, and %d for disc. Replaces any invalid characters (\\, /, :, , *,
    * ?, ", <, >, or |) with underscores _ to prevent any errors on file
    * systems. Examples: %n -%t = 01 - Track.mp3 %a - %d - %n - %t = Artist -
    * Album - 01 - Track.mp3
    * <p>
    * @param aFormat the string format like %n -%t to rename 01 - Track.mp3
    * @return true if renamed, false if failure
    */
   abstract public boolean renameFile(String aFormat);

   /**
    * Saves the tag back to the file.
    * <p>
    * @throws MusicTagException if any error occurs saving the file
    */
   abstract public void save() throws MusicTagException;

   /**
    * Gets the genre list.
    * <p>
    * @return Returns the genre list
    */
   public static List getGenreTypes() {
      return V2GenreTypes.getInstanceOf().getAlphabeticalValueList();
   }

   /**
    * AZ Gets genre description from the standard genre list.
    * <p>
    * @return Returns the genre string
    */
   public static String getStandardGenreType(int genreNumber) {
      return GenreTypes.getInstanceOf().getValueForId(genreNumber).toString();
   }

   /**
    * Gets the absolute file path.
    * <p>
    * @return Returns the absolute file path
    */
   public String getAbsolutePath() {
      return this.getFile().getAbsolutePath();
   }

   /**
    * Returns the bit rate as a string.
    * @return the bitrate value as a string decoded with VBR
    */
   public String getBitRateAsString() {
      String rate = "0 ";
      if (isVBR()) {
         rate = "VBR ~" + getBitRate().toString();
      } else {
         rate = getBitRate().toString();
      }

      return rate + " kbps";
   }

   /**
    * Gets the file.
    * <p>
    * @return Returns the file.
    */
   public File getFile() {
      return this.file;
   }

   /**
    * Returns the info about the audio file.
    * <p>
    * @return string containing the info about the MP3 file
    */
   public String getHeaderInfo() {

      /*
       * ogg.version = 0 ogg.channels = 2 ogg.length.bytes = 3654577
       * ogg.bitrate.nominal.bps = 160003 ogg.serial = 59211796 ogg.frequency.hz
       * = 44100 duration = 181055000
       */
      /*
       * mp3.frequency.hz = 44100 title = Goodbye You Lizard Scum
       * mp3.length.bytes = 3706880 comment = mp3.channels = 2 date = 1997
       * mp3.version.layer = 3 mp3.framesize.bytes = 413 mp3.id3tag.track = 1
       * mp3.version.mpeg = 1 mp3.bitrate.nominal.bps = 128000 mp3.vbr.scale = 0
       * mp3.length.frames = 8889 mp3.crc = false album = Arizona Bay mp3.vbr =
       * false mp3.copyright = false mp3.framerate.fps = 38.28125 mp3.id3tag.v2
       * = java.io.ByteArrayInputStream@294f62 mp3.id3tag.v2.version = 4
       * mp3.version.encoding = MPEG1L3 mp3.id3tag.genre = Comedy mp3.header.pos
       * = 487 mp3.original = true mp3.mode = 1 mp3.padding = false duration =
       * 232202000 author = Bill Hicks
       */
      final StringBuffer buffer = new StringBuffer();
      if (header != null) {
         final Iterator it = header.keySet().iterator();
         while (it.hasNext()) {
            final Object key = it.next();
            final Object value = header.get(key);
            buffer.append(key);
            buffer.append(" = ");
            buffer.append(value);
         }
      }
      return buffer.toString();
   }

   /**
    * Returns the info about the audio file.
    * <p>
    * @return string containing the info about the MP3 file
    */
   public String getHeaderValue(final String aKey) {
      return ((String) header.get(aKey));
   }

   /**
    * Gets the track length in seconds.
    * <p>
    * @return Returns the track length in seconds.
    */
   public String getTrackLengthAsString() {
      return new TimeSpan(getTrackLength() * 1000).getMusicDuration();
   }

   /**
    * Default Equals method.
    */
   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof MusicTag)) {
         return false;
      }
      if (this == obj) {
         return true;
      }
      final MusicTag rhs = (MusicTag) obj;
      final EqualsBuilder builder = new EqualsBuilder();
      builder.append(artist, rhs.artist);
      builder.append(disc, rhs.disc);
      builder.append(title, rhs.title);
      builder.append(year, rhs.year);
      builder.append(genre, rhs.genre);

      return builder.isEquals();
   }

   /**
    * Tries to extract the title of the song from the filename.
    * <p>
    * EXAMPLES: 06 - Opiate.mp3 = Opiate Tool - Aenima - 06 - Opiate.mp3 =
    * Opiate 02 - Positively 4th Street.mp3 = Positively 4th Street 11 - Nothing
    * Compares 2 U.mp3 = Nothing Compares 2 U
    * <p>
    * @return the title extracted from the filename
    */
   public String extractTitleFromFilename() {
      String title = "";

      // first strip off the extension .mp3 or .ogg
      final String filename = FilenameUtils.getBaseName(file.getAbsolutePath());

      // split into tokens by whitespace
      final String[] tokens = StringUtils.split(filename);

      // loop backwards through the tokens to see if they should be used
      for (int i = tokens.length - 1; i >= 0; i--) {
         final String token = tokens[i];

         // throw out any dashes which are used as separators
         if (StringUtils.equalsIgnoreCase(token, "-")) {
            continue;
         }

         // break if we hit a number which matches the TRACK
         if ((StringUtils.isNumericSpace(token)) && (NumberUtils.toInt(token) == NumberUtils.toInt(this.getTrack()))) {
            break;
         }

         // append this token onto the title we are building
         title = token + " " + title;
      }

      // if no title was extracted then just use the filename
      if (StringUtils.isBlank(title)) {
         title = filename;
      }

      return title;
   }

   /**
    * If the file exists or not.
    * <p>
    * @return Returns the file exists flag
    */
   public boolean fileExists() {
      return this.file.exists();
   }

   /**
    * Default hashcode method.
    */
   @Override
   public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
   }

   /**
    * Renames this Music file based on the default format of %n - %t.mp3 so it
    * looks like 01 - Track.mp3.
    * <p>
    * @return true if renamed, false if failure
    */
   public boolean renameFile() {
      return renameFile(DEFAULT_FILE_FORMAT);
   }

   /**
    * Synchronizes the tag to have the same data in the V1 and V2 tag.
    */
   public void synchronize() {
      this.setDisc(this.getDisc());
      this.setArtist(this.getArtist());
      this.setComment(this.getComment());
      this.setGenre(this.getGenre());
      this.setTitle(this.getTitle());
      this.setTrack(this.getTrack());
      this.setYear(this.getYear());
      this.setTrackLength(this.getTrackLength());
      this.setEncodedBy(this.getEncodedBy());
   }

   /**
    * CompareTo method for sorting tracks.
    */
   @Override
   public int compareTo(Object o) {
      MusicTag myClass = (MusicTag) o;
      return new CompareToBuilder().append(this.track, myClass.track).append(this.title, myClass.title).toComparison();
   }

   /**
    * Default toString() method.
    */
   @Override
   public String toString() {
      final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
      builder.append("artist", artist);
      builder.append("disc", disc);
      builder.append("title", title);
      builder.append("track", track);
      builder.append("genre", genre);
      builder.append("year", year);
      builder.append("comment", comment);
      builder.append("length", trackLength);

      return builder.toString();
   }

   /**
    * Creates a new filename based on the format passed in.
    * <p>
    * @param aFormat the file format like %n -%t = 01 - Track.mp3
    * @return the new file name
    */
   protected String createFilenameFromFormat(String aFormat) {
      final String oldFileName = this.file.getAbsolutePath();
      final String directory = FilenameUtils.getFullPath(oldFileName);
      final String extension = FilenameUtils.getExtension(oldFileName.toLowerCase(Locale.US));
      String newFileName = aFormat;
      newFileName = StringUtils.replace(newFileName, "%n", getTrack());
      newFileName = StringUtils.replace(newFileName, "%t", getTitle());
      newFileName = StringUtils.replace(newFileName, "%a", getArtist());
      newFileName = StringUtils.replace(newFileName, "%d", getDisc());
      newFileName = StringUtils.replace(newFileName, "%N", getTrack().toUpperCase());
      newFileName = StringUtils.replace(newFileName, "%T", getTitle().toUpperCase());
      newFileName = StringUtils.replace(newFileName, "%A", getArtist().toUpperCase());
      newFileName = StringUtils.replace(newFileName, "%D", getDisc().toUpperCase());
      newFileName = FileUtil.correctFileName(newFileName);
      newFileName = directory + newFileName + "." + extension;
      if (LOG.isDebugEnabled()) {
         LOG.debug("Renaming: '" + oldFileName + "' to '" + newFileName + "'");
      }
      return newFileName;
   }

}