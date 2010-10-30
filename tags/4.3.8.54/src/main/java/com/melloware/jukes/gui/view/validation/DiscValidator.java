package com.melloware.jukes.gui.view.validation;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.Validator;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.jgoodies.validation.util.ValidationUtils;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Validates Discs.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class DiscValidator implements Validator {

   private static final String NAME = "Name";
   private static final String GENRE = "Genre";
   private static final String YEAR = "Year";
   private static final String MESSAGE_MANDATORY = Resources.getString("messages.isMandatory");
   private static final String NOTES = "Notes";
   private static final List<String> GENRE_LIST = MusicTag.getGenreTypes();
   private static final String MESSAGE_LENGTH_500 = Resources.getString("messages.Length500");
   private static final String MESSAGE_LENGTH_100 = Resources.getString("messages.Length100");
   private static final String MESSAGE_LENGTH_4 = Resources.getString("messages.Length4");
   private static final String MESSAGE_MUST_BE_A_NUMBER = Resources.getString("messages.mustbeanumber");
   private static final String MESSAGE_BASIC_GENRE = Resources.getString("messages.BasicGenre");

   /**
    * Holds the disc to be validated.
    */
   private final Disc disc;

   /**
    * Constructs a DiscValidator on the given Disc.
    * @param disc the disc to be validated
    */
   public DiscValidator(Disc disc) {
      this.disc = disc;
   }

   public boolean containsIgnoreCase(List<String> l, String s) {
      Iterator<String> it = l.iterator();
      while (it.hasNext()) {
         if (it.next().compareToIgnoreCase(s) == 0) {
            return true;
         }
      }
      return false;
   }

   /**
    * Validates this Validator's Order and returns the result as an instance of
    * {@link ValidationResult}.
    * @return the ValidationResult of the disc validation
    */
   @Override
   public ValidationResult validate(Object validationTarget) {
      final PropertyValidationSupport support = new PropertyValidationSupport(disc, "Disc");
      String[] separatorList = {":", ";", ".", "-", ",", "/"};
      
      if (ValidationUtils.isBlank(disc.getName())) {
         support.addError(NAME, MESSAGE_MANDATORY);
      } else if (!ValidationUtils.hasMaximumLength(disc.getName(), 100)) {
         support.addError(NAME, MESSAGE_LENGTH_100);
      }
      // AZ: Validate Notes length
      if (!ValidationUtils.hasMaximumLength(disc.getNotes(), 500)) {
         support.addError(NOTES, MESSAGE_LENGTH_500);
      }
      if (ValidationUtils.isBlank(disc.getGenre())) {
         support.addError(GENRE, MESSAGE_MANDATORY);
      } else {
         if (!ValidationUtils.hasMaximumLength(disc.getGenre(), 100)) {
            support.addError(GENRE, MESSAGE_LENGTH_100);
         }
         final String TmpString = disc.getGenre().toString();
         int index = TmpString.length();
         
         for(int i=0; i<separatorList.length; i++){
         	final int currentIndex = TmpString.indexOf(separatorList[i]);
         	if ((currentIndex > 0) & (currentIndex < index) ){
             	index = currentIndex;
             }
         }
         final String BasicGenre = TmpString.substring(0, index);
     	
         if (!containsIgnoreCase(GENRE_LIST, BasicGenre)) {
            final String message = MessageFormat.format(MESSAGE_BASIC_GENRE, new Object[] { BasicGenre });
            support.addError(GENRE, message);
         }
      }
      if ((disc.getYear() == null)) {
         support.addError(YEAR, MESSAGE_MANDATORY);
      } else if (ValidationUtils.isBlank(disc.getYear())) {
         support.addError(YEAR, MESSAGE_MANDATORY);
      } else if (!ValidationUtils.hasBoundedLength(disc.getYear(), 4, 4)) { // NOPMD
         support.addError(YEAR, MESSAGE_LENGTH_4);
      } else if (!ValidationUtils.isNumeric(disc.getYear())) { // NOPMD
         support.addError(YEAR, MESSAGE_MUST_BE_A_NUMBER);
      }

      return support.getResult();
   }

}