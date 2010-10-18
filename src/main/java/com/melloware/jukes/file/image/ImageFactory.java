package com.melloware.jukes.file.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.util.ResourceUtils;
import com.melloware.jukes.file.FileUtil;
import com.melloware.jukes.file.filter.ImageFilter;
import com.melloware.jukes.gui.tool.Settings;

/**
 * Class to represent and storage area for all icon references. This will reduce
 * overhead and memory usage throughout the application. Also contains some
 * static helper methods for loading and scaling images.
 * <p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Melloware, Inc.
 * </p>
 * @author Emil A. Lefkof III
 * @version 4.0
 */
public final class ImageFactory {

   private static final Log LOG = LogFactory.getLog(ImageFactory.class); // AZ

   public static final ImageIcon ICO_TRAYICON;
   public static final ImageIcon ICO_TRAYPLAY;
   public static final ImageIcon ICO_TRAYPAUSE;
   public static final ImageIcon ICO_TRAYSTOP;
   public static final ImageIcon IMAGE_NOCOVER;

   /** EAL **/
   public static Settings settings = null;

   /** EAL **/
   // call this method only once to store the static settings variable from the
   // MainModule code where its set
   public static void setSettings(Settings aSettings) {
      settings = aSettings;
   }

   static {
      if (SystemUtils.IS_OS_WINDOWS) {
         ICO_TRAYICON = getIcon("images/trayicon.png");
         ICO_TRAYPLAY = getIcon("images/trayplay.png");
         ICO_TRAYPAUSE = getIcon("images/traypause.png");
         ICO_TRAYSTOP = getIcon("images/traystop.png");
      } else {
         ICO_TRAYICON = getIcon("images/tray_icon_24x24.png");
         ICO_TRAYPLAY = getIcon("images/tray_play_24x24.png");
         ICO_TRAYPAUSE = getIcon("images/tray_pause_24x24.png");
         ICO_TRAYSTOP = getIcon("images/tray_stop_24x24.png");
      }

      IMAGE_NOCOVER = getIcon("images/image_nocover.jpg");
   }

   /**
    * Default constructor. Private so no instantiation.
    */
   private ImageFactory() {
      super();
   }

   /**
    * Gets the disc and returns it the correct size.
    * <p>
    * @param aFileName the file location of the disc
    * @return the ImageIcon
    */
   public static ImageIcon getDiscImage(final String aFileName) {
      // return getScaledImage(aFileName, 300, 300);
      // AZ - use settings instead of fixed size
      return getScaledImage(aFileName, settings.getCoverSizeLarge(), settings.getCoverSizeLarge());
   }

   /**
    * Method to create a new ImageIcon and return the reference
    * <p>
    * @param aFileName filename of icon to load
    * @return ImageIcon reference of fileName
    */
   public static ImageIcon getIconFromFile(final String aFileName) {
      return new ImageIcon(aFileName);
   }

   /**
    * Gets an Image from the URL specified. Safe so we return NULL if anything
    * goes wrong and the image can not be loaded.
    * <p>
    * @param aUrl the URL to look for the image
    * @return the Image if found, NULL if nothing found
    */
   public static Image getImageFromUrl(final String aUrl) {
      Image image = null;
      if (StringUtils.isBlank(aUrl)) {
         throw new IllegalArgumentException("aUrl must be specified");
      }

      try {
         final URL url = new URL(aUrl);
         image = ImageIO.read(url);
      } catch (MalformedURLException ex) {
         image = null;
      } catch (IOException ex) {
         image = null;
      }
      return image;
   }

   /**
    * Gets an ImageIcon and scales it to the size requested.
    * <p>
    * @param aFileName the filename to get
    * @param height the desired height of the image
    * @param width the desired width of the image
    * @return the ImageIcon
    */
   public static ImageIcon getScaledImage(final String aFileName, final int height, final int width) {
      ImageIcon icon = null;
      final String errorString = ResourceUtils.getString("messages.ErrorReadingImageFile") + ": " + aFileName;
      if (StringUtils.isBlank(aFileName)) {
         icon = IMAGE_NOCOVER;
      } else {
         // get the image
         /** AZ **/
         try {
            icon = new ImageIcon(aFileName);
         } catch (Exception ex) {
            LOG.error(errorString, ex);
         }
      }

      // if no image was found return the default
      if (MediaTracker.ERRORED == icon.getImageLoadStatus()) {
         icon = IMAGE_NOCOVER;
      }

      // resizing Image Icon
      // AZ Image.SCALE_SMOOTH
      final Image image = icon.getImage().getScaledInstance(height, width, Image.SCALE_SMOOTH);

      return new ImageIcon(image);
   }

   /**
    * Saves an image file to disk in the location specified.
    * <p>
    * @param image the image to save
    * @param filename the file to save it as
    * @throws IOException if any error occurs writing the file.
    */
   public static File saveImage(final Image image, final String filename) throws IOException {
      final int w = image.getHeight(null);
      final int h = image.getHeight(null);
      final BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      final Graphics2D g2 = bi.createGraphics();
      g2.drawImage(image, 0, 0, null);
      g2.dispose();
      final File file = new File(filename);
      ImageIO.write(bi, "jpg", file);
      return file;
   }

   /**
    * Saves this image file based on a format from prefs. The format is in
    * aFormat and can have values %y for year, %a for artist, and %d for disc.
    * Replaces any invalid characters (\\, /, :, , *, ?, ", <, >, or |) with
    * underscores _ to prevent any errors on file systems. Examples: %a - %d =
    * Black Crowes - Amorica.jpg
    * <p>
    * @param image the image to save
    * @param aFormat the string format like %a - %d = Black Crowes - Amorica.jpg
    * @param aDirectory the directory to save the file in.
    * @param aArtist the name of the artist
    * @param aDisc the name of the disc
    * @return true if renamed, false if failure
    * @throws IOException if any error occurs writing the file out
    */
   public static String saveImageWithFileFormat(final Image image, final String aFormat, final String aDirectory,
            final String aArtist, final String aDisc, final String aYear) throws IOException {
      String result = null;
      String newFileName = aFormat;
      newFileName = StringUtils.replace(newFileName, "%y", aYear);
      newFileName = StringUtils.replace(newFileName, "%a", aArtist);
      newFileName = StringUtils.replace(newFileName, "%d", aDisc);
      newFileName = StringUtils.replace(newFileName, "%A", aArtist.toUpperCase(Locale.US));
      newFileName = StringUtils.replace(newFileName, "%D", aDisc.toUpperCase(Locale.US));
      newFileName = FileUtil.correctFileName(newFileName);
      newFileName = aDirectory + SystemUtils.FILE_SEPARATOR + newFileName + "." + ImageFilter.JPG;

      result = newFileName;

      saveImage(image, newFileName);

      return result;
   }

   /** AZ **/
   public static String saveImageToUserDefinedDirectory(final File imageFile, final String aArtist, final String aDisc,
            final String aYear) throws IOException {
      String result = null;
      String newFileName = settings.getFileFormatImage();
      int size = settings.getCoverSizeLarge();

      if (settings.isCopyImagesToDirectory()) {
         newFileName = standardImageFileName(aArtist, aDisc, aYear);
         ImageIcon imageIcon = getScaledImage(imageFile.toString(), size, size);
         Image newImage = imageIcon.getImage();
         saveImage(newImage, newFileName);
      }
      result = imageFile.getAbsolutePath();
      return result;
   }

   /**
    * Method to create a new ImageIcon and return the reference
    * <p>
    * @param aFileName filename of icon to load
    * @return ImageIcon reference of fileName
    */
   private static ImageIcon getIcon(final String aFileName) {
      return new ImageIcon(ClassLoader.getSystemResource(aFileName));
   }

   /** AZ **/
   public static String standardImageFileName(final String aArtist, final String aDisc, final String aYear) {
      String newFileName = settings.getFileFormatImage();
      String fileName = "";
      String aDirectory = settings.getImagesLocation().toString();

      newFileName = StringUtils.replace(newFileName, "%y", aYear);
      newFileName = StringUtils.replace(newFileName, "%a", aArtist);
      newFileName = StringUtils.replace(newFileName, "%d", aDisc);
      newFileName = StringUtils.replace(newFileName, "%A", aArtist.toUpperCase(Locale.US));
      newFileName = StringUtils.replace(newFileName, "%D", aDisc.toUpperCase(Locale.US));
      newFileName = FileUtil.correctFileName(newFileName);
      fileName = aDirectory + SystemUtils.FILE_SEPARATOR + newFileName + "." + ImageFilter.JPG;
      if (fileName.length() > 256) {
         newFileName = newFileName.substring(0, newFileName.length() - 4 - aDirectory.length());
         fileName = aDirectory + SystemUtils.FILE_SEPARATOR + newFileName + "." + ImageFilter.JPG;
         LOG.warn("Image name was too long: " + fileName);
      }
      return fileName;
   }
} // end class ImageFactory
