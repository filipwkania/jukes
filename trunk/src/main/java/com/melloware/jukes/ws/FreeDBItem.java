package com.melloware.jukes.ws;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import entagged.freedb.FreedbReadResult;

/**
 * Wrapper class for FreeDB search results
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2010
 */
@SuppressWarnings("unchecked")
public final class FreeDBItem {

   private final Collection tracks = new ArrayList();
   private String disc = null;
   private String artist = null;
   private String itemId = null;
   private String releaseDate = null;
   private String releaseYear = null;
   private String genre = null;

   
   /**
    * Default constructor based on FreeDB search result. 
    */
   public FreeDBItem(FreedbReadResult freedbResult) {
      super();
      FreeDBinitialize(freedbResult);
   }

   /**
    * Initialize all the fields of this object from the FreeDB search result.
    *
    * @param FreedbReadResult aFreedbResult
    */
   private void FreeDBinitialize(FreedbReadResult aFreedbResult) {
      final FreedbReadResult freedbResult = aFreedbResult;
      if (freedbResult == null) {
         throw new IllegalArgumentException("FreeDB search result is null.");
      }
      setItemId(freedbResult.getDiscId());
      setArtist(freedbResult.getArtist());
      setTitle(freedbResult.getAlbum());
      setReleaseYear(freedbResult.getYear());
      setGenre(freedbResult.getGenre());

      final int tracksNumber = freedbResult.getTracksNumber(); 
	  for (int ij=0; ij<tracksNumber; ij=ij+1){ //loop for tracks
	  setTrack(freedbResult.getTrackTitle(ij));
      }
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
    * Gets the genre.
    * <p>
    * @return Returns the genre.
    */
   public String getGenre() {
      return this.genre;
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
   public boolean equals(Object obj) {
      if (!(obj instanceof FreeDBItem)) {
         return false;
      }
      if (this == obj) {
         return true;
      }
      FreeDBItem rhs = (FreeDBItem) obj;
      EqualsBuilder builder = new EqualsBuilder();
      builder.append(artist, rhs.artist);
      builder.append(disc, rhs.disc);
      builder.append(itemId, rhs.itemId);

      return builder.isEquals();
   }

   /**
    * Default hashcode method.
    */
   public int hashCode() {
      return new HashCodeBuilder(17, 37).append(artist).append(disc).append(releaseDate).toHashCode();
   }

   /**
    * Default toString() method.
    */
   public String toString() {
      ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
      builder.append("artist", artist);
      builder.append("disc", disc);
      builder.append("itemId", itemId);
      builder.append("releaseDate", releaseDate);
      builder.append("genre", genre);
      return builder.toString();
   }
 
   /**
    * Set Item Id
    */
   private void setItemId(String aItemId){
	  if (aItemId != null) {
		  this.itemId = aItemId;
	  }
   }
  
  
   /**
    * Set Artist Name
    */
   private void setArtist(String aArtistName){
		  if (aArtistName != null) {
			  this.artist = aArtistName;
		  }
   }
   
   /**
    * Set Disc Title
    */
   private void setTitle(String aDiscTitle){
		  if (aDiscTitle != null) {
			  this.disc = aDiscTitle;
		  }
   }
     
   /**
    * Set Disc Release Date
    */
   private void setReleaseYear(String aYear){
		  if (aYear != null) {
			  this.releaseYear = aYear;
		  }
   }
     
   /**
    * Set Disc Genre
    */
   private void setGenre(String aGenre){
		  if (aGenre != null) {
			  this.genre = aGenre;
		  }
   }
   
   /**
    * Set Track Titles
    */
   private void setTrack(String aTrackTitle){
		  if (aTrackTitle != null) {
			  this.tracks.add(aTrackTitle);
		  }
   }
}