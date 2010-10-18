package com.melloware.jukes.gui.view.validation;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.Validator;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.jgoodies.validation.util.ValidationUtils;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Validates Artists.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class ArtistValidator implements Validator {

   /**
    * Holds the artist to be validated.
    */
   private final Artist artist;

   /**
    * Constructs a ArtistValidator on the given Artist.
    * @param artist the artist to be validated
    */
   public ArtistValidator(Artist artist) {
      this.artist = artist;
   }

   /**
    * Validates this Validator's Artist and returns the result as an instance of
    * {@link ValidationResult}.
    * @return the ValidationResult of the artist validation
    */
   @Override
   public ValidationResult validate(Object validationTarget) {
      final PropertyValidationSupport support = new PropertyValidationSupport(artist, "Artist");
      if (ValidationUtils.isBlank(artist.getName())) {
         support.addError("Name", Resources.getString("messages.isMandatory"));
      }

      if (!ValidationUtils.hasMaximumLength(artist.getName(), 100)) {
         support.addError("Name", Resources.getString("messages.Length100"));
      }

      if (!ValidationUtils.hasMaximumLength(artist.getNotes(), 500)) {
         support.addError("Notes", Resources.getString("messages.Length500"));
      }

      return support.getResult();
   }

}