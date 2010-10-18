package com.melloware.jukes.ws;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.melloware.jukes.file.image.ImageFactory;

/**
 * Wrapper class for an Amazon Product to mask the complexity from the user and
 * provide easy accessor methods to the data contained within.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ Development 2010
 */
@SuppressWarnings("unchecked")
public final class AmazonItem {

   private final Collection tracks = new ArrayList();
   private Image largeImage = null;
   private Image mediumImage = null;
   private Image smallImage = null;
   private String disc = null;
   private String artist = null;
   private String itemId = null;
   private String largeImageUrl = null;
   private String mediumImageUrl = null;
   private String releaseDate = null;
   private String releaseYear = null;
   private String smallImageUrl = null;
   private int bestImageWidth = 0;
   private int bestImageHeight = 0;

   /**
    * Default constructor based on Amazon search result.
    */
   public AmazonItem(Node aAmazonItem) {
      super();
      initialize(aAmazonItem);
   }

   /**
    * Gets the disc.
    * <p>
    * @return Returns the disc.
    */
   public String getDisc() {
      return this.disc;
   }

   /**
    * Gets the artist.
    * <p>
    * @return Returns the artist.
    */
   public String getArtist() {
      return this.artist;
   }

   /**
    * Gets the itemId.
    * <p>
    * @return Returns the itemId.
    */
   public String getItemId() {
      return this.itemId;
   }

   /**
    * Tries first large, then medium, then small
    * <p>
    * @return Returns the best fit url
    */
   public String getBestImageUrl() {
      if (StringUtils.isNotBlank(this.largeImageUrl)) {
         return this.largeImageUrl;
      } else if (StringUtils.isNotBlank(this.mediumImageUrl)) {
         return this.mediumImageUrl;
      } else if (StringUtils.isNotBlank(this.smallImageUrl)) {
         return this.smallImageUrl;
      } else {
         return "";
      }
   }

   /**
    * Tries first large, then medium, then small
    * <p>
    * @return Returns the best fit image
    */
   public Image getBestImage() {
      if (StringUtils.isNotBlank(this.largeImageUrl)) {
         return getLargeImage();
      } else if (StringUtils.isNotBlank(this.mediumImageUrl)) {
         return getMediumImage();
      } else if (StringUtils.isNotBlank(this.smallImageUrl)) {
         return getSmallImage();
      } else {
         return null;
      }
   }

   /**
    * Tries first small, then medium, then large
    * <p>
    * @return Returns the best fit image
    */
   public Image getSmallestImage() {
      if (StringUtils.isNotBlank(this.smallImageUrl)) {
         return getSmallImage();
      } else if (StringUtils.isNotBlank(this.mediumImageUrl)) {
         return getMediumImage();
      } else if (StringUtils.isNotBlank(this.largeImageUrl)) {
         return getLargeImage();
      } else {
         return null;
      }
   }

   /**
    * Gets the largeImage.
    * <p>
    * @return Returns the largeImage.
    */
   public Image getLargeImage() {
      if ((this.largeImage == null) && (this.largeImageUrl != null)) {
         this.largeImage = ImageFactory.getImageFromUrl(this.largeImageUrl);
      }
      return this.largeImage;
   }

   /**
    * Gets the largeImageUrl.
    * <p>
    * @return Returns the largeImageUrl.
    */
   public String getLargeImageUrl() {
      return this.largeImageUrl;
   }

   /**
    * Gets the mediumImage.
    * <p>
    * @return Returns the mediumImage.
    */
   public Image getMediumImage() {
      if ((this.mediumImage == null) && (this.mediumImageUrl != null)) {
         this.mediumImage = ImageFactory.getImageFromUrl(this.mediumImageUrl);
      }
      return this.mediumImage;
   }

   /**
    * Gets the mediumImageUrl.
    * <p>
    * @return Returns the mediumImageUrl.
    */
   public String getMediumImageUrl() {
      return this.mediumImageUrl;
   }

   /**
    * Gets the releaseDate.
    * <p>
    * @return Returns the releaseDate.
    */
   public String getReleaseDate() {
      return this.releaseDate;
   }

   /**
    * Gets the releaseYear.
    * <p>
    * @return Returns the releaseYear.
    */
   public String getReleaseYear() {
      if ((this.releaseYear == null) && (this.releaseDate != null) && (this.releaseDate.length() > 4)) {
         this.releaseYear = this.releaseDate.substring(0, 4);
      }
      return this.releaseYear;
   }

   /**
    * Gets the smallImage.
    * <p>
    * @return Returns the smallImage.
    */
   public Image getSmallImage() {
      if ((this.smallImage == null) && (this.smallImageUrl != null)) {
         this.smallImage = ImageFactory.getImageFromUrl(this.smallImageUrl);
      }
      return this.smallImage;
   }

   /**
    * Gets the smallImageUrl.
    * <p>
    * @return Returns the smallImageUrl.
    */
   public String getSmallImageUrl() {
      return this.smallImageUrl;
   }

   /**
    * Gets the tracks.
    * <p>
    * @return Returns the tracks.
    */
   public Collection getTracks() {
      return this.tracks;
   }

   /**
    * Default Equals method.
    */
   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof AmazonItem)) {
         return false;
      }
      if (this == obj) {
         return true;
      }
      AmazonItem rhs = (AmazonItem) obj;
      EqualsBuilder builder = new EqualsBuilder();
      builder.append(artist, rhs.artist);
      builder.append(disc, rhs.disc);
      builder.append(itemId, rhs.itemId);

      return builder.isEquals();
   }

   /**
    * Default hashcode method.
    */
   @Override
   public int hashCode() {
      return new HashCodeBuilder(17, 37).append(artist).append(disc).append(releaseDate).toHashCode();
   }

   /**
    * Default toString() method.
    */
   @Override
   public String toString() {
      ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
      builder.append("artist", artist);
      builder.append("disc", disc);
      builder.append("itemId", itemId);
      builder.append("smallImageUrl", smallImageUrl);
      builder.append("mediumImageUrl", mediumImageUrl);
      builder.append("largeImageUrl", largeImageUrl);
      builder.append("releaseDate", releaseDate);

      return builder.toString();
   }

   /**
    * Initialize all the fields of this object from the Amazon Item.
    */
   private void initialize(Node aAmazonItem) {
      final Node amazonItem = aAmazonItem;
      if (amazonItem == null) {
         throw new IllegalArgumentException("Amazon Item is null.");
      }

      NodeList nodeList = amazonItem.getChildNodes();
      if (nodeList.getLength() != 0) {
         for (int i = 0; i < nodeList.getLength(); i = i + 1) {
            Node n = nodeList.item(i);
            if (n.getNodeName().equalsIgnoreCase("ASIN")) {
               setItemId(n);
            }
            if (n.getNodeName().equalsIgnoreCase("SmallImage")) {
               setSmallImage(n);
            }
            if (n.getNodeName().equalsIgnoreCase("MediumImage")) {
               setMediumImage(n);
            }
            if (n.getNodeName().equalsIgnoreCase("LargeImage")) {
               setLargeImage(n);
            }
            if (n.getNodeName().equalsIgnoreCase("ItemAttributes")) {
               NodeList attributesList = n.getChildNodes();
               if (attributesList.getLength() != 0) {
                  for (int ii = 0; ii < attributesList.getLength(); ii = ii + 1) {
                     Node attributeNode = attributesList.item(ii);
                     if (attributeNode.getNodeName().equalsIgnoreCase("ARTIST")) {
                        setArtist(attributeNode);
                     }
                     if (attributeNode.getNodeName().equalsIgnoreCase("TITLE")) {
                        setTitle(attributeNode);
                     }
                     if (attributeNode.getNodeName().equalsIgnoreCase("RELEASEDATE")) {
                        setReleaseDate(attributeNode);
                     }
                     if (attributeNode.getNodeName().equalsIgnoreCase("ORIGINALRELEASEDATE")) {
                        setOriginalReleaseDate(attributeNode);
                     }
                  }
               }
            }

            if (n.getNodeName().equalsIgnoreCase("Tracks")) {
               NodeList discsList = n.getChildNodes();
               if (discsList.getLength() != 0) {
                  for (int ij = 0; ij < discsList.getLength(); ij = ij + 1) { // loop
                     // for
                     // discs
                     Node discNode = discsList.item(ij);
                     if (discNode.getNodeName().equalsIgnoreCase("Disc")) {
                        NodeList tracksList = discNode.getChildNodes();
                        if (tracksList.getLength() != 0) {
                           for (int iTr = 0; iTr < tracksList.getLength(); iTr = iTr + 1) { // loop
                              // for
                              // tracks
                              Node trackNode = tracksList.item(iTr);
                              if (trackNode.getNodeName().equalsIgnoreCase("Track")) {
                                 setTrack(trackNode);
                              }
                           }
                        }
                     }
                  }
               }
            }

         }
      } else {
         throw new IllegalArgumentException("Amazon Item is empty.");
      }
   }

   /**
    * Gets the bestImageHeight.
    * <p>
    * @return Returns the bestImageHeight.
    */
   public int getBestImageHeight() {
      return this.bestImageHeight;
   }

   /**
    * Gets the bestImageWidth.
    * <p>
    * @return Returns the bestImageWidth.
    */
   public int getBestImageWidth() {
      return this.bestImageWidth;
   }

   /**
    * Set Item Id
    */
   private void setItemId(Node n) {
      if (n.getNodeName().equalsIgnoreCase("ASIN")) {
         this.itemId = n.getTextContent();
      }
   }

   /**
    * Set Small Image
    */
   private void setSmallImage(Node n) {
      if (n.getNodeName().equalsIgnoreCase("SmallImage")) {
         final NodeList nodeList = n.getChildNodes();
         if (nodeList.getLength() != 0) {
            for (int i = 0; i < nodeList.getLength(); i = i + 1) {
               Node node = nodeList.item(i);
               if (node.getNodeName().equalsIgnoreCase("URL")) {
                  this.smallImageUrl = node.getTextContent();
               }
               if (node.getNodeName().equalsIgnoreCase("Height")) {
                  this.bestImageHeight = Integer.parseInt(node.getTextContent().trim());
               }
               if (node.getNodeName().equalsIgnoreCase("Width")) {
                  this.bestImageWidth = Integer.parseInt(node.getTextContent().trim());
               }
            }
         }
      }
   }

   /**
    * Set Medium Image
    */
   private void setMediumImage(Node n) {
      if (n.getNodeName().equalsIgnoreCase("MediumImage")) {
         final NodeList nodeList = n.getChildNodes();
         if (nodeList.getLength() != 0) {
            for (int i = 0; i < nodeList.getLength(); i = i + 1) {
               Node node = nodeList.item(i);
               if (node.getNodeName().equalsIgnoreCase("URL")) {
                  this.mediumImageUrl = node.getTextContent();
               }
               if (node.getNodeName().equalsIgnoreCase("Height")) {
                  this.bestImageHeight = Integer.parseInt(node.getTextContent().trim());
               }
               if (node.getNodeName().equalsIgnoreCase("Width")) {
                  this.bestImageWidth = Integer.parseInt(node.getTextContent().trim());
               }
            }
         }
      }
   }

   /**
    * Set Large Image
    */
   private void setLargeImage(Node n) {
      if (n.getNodeName().equalsIgnoreCase("LargeImage")) {
         final NodeList nodeList = n.getChildNodes();
         if (nodeList.getLength() != 0) {
            for (int i = 0; i < nodeList.getLength(); i = i + 1) {
               Node node = nodeList.item(i);
               if (node.getNodeName().equalsIgnoreCase("URL")) {
                  this.largeImageUrl = node.getTextContent();
               }
               if (node.getNodeName().equalsIgnoreCase("Height")) {
                  this.bestImageHeight = Integer.parseInt(node.getTextContent().trim());
               }
               if (node.getNodeName().equalsIgnoreCase("Width")) {
                  this.bestImageWidth = Integer.parseInt(node.getTextContent().trim());
               }
            }
         }
      }
   }

   /**
    * Set Artist Name
    */
   private void setArtist(Node n) {
      if (n.getNodeName().equalsIgnoreCase("ARTIST")) {
         this.artist = n.getTextContent();
      }
   }

   /**
    * Set Disc Title
    */
   private void setTitle(Node n) {
      if (n.getNodeName().equalsIgnoreCase("TITLE")) {
         this.disc = n.getTextContent();
      }
   }

   /**
    * Set Disc Release Date from ReleaseDate node
    */
   private void setReleaseDate(Node n) {
      if (n.getNodeName().equalsIgnoreCase("RELEASEDATE")) {
         if (this.releaseDate == null) {
            this.releaseDate = n.getTextContent();
         }
      }
   }

   /**
    * Set Disc Release Date from OriginalReleaseDate node
    */
   private void setOriginalReleaseDate(Node n) {
      if (n.getNodeName().equalsIgnoreCase("ORIGINALRELEASEDATE")) {
         if (n.getTextContent().length() > 0) {
            this.releaseDate = n.getTextContent();
         }
      }
   }

   /**
    * Set Track Titles
    */
   private void setTrack(Node n) {
      if (n.getNodeName().equalsIgnoreCase("TRACK")) {
         final String trackName = n.getTextContent();
         this.tracks.add(trackName);
      }
   }

}