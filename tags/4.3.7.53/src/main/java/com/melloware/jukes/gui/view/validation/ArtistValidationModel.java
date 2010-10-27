package com.melloware.jukes.gui.view.validation;

import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.validation.ValidationResult;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.gui.tool.Actions;

/**
 * Provides all models to bind an artist editor to its domain model, an instance
 * of {@link Artist}.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class ArtistValidationModel extends AbstractValidationModel {

   private static final String[] VALIDATION_PROPERTIES = { Artist.PROPERTYNAME_NAME, Artist.PROPERTYNAME_NOTES };

   /**
    * Constructor that takes an Artist object.
    * <p>
    * @param aArtist the domain object
    */
   public ArtistValidationModel(Artist aArtist) {
      super(aArtist);
   }

   @Override
   protected void updateValidationResult() {
      Artist artist = (Artist) getBean();
      ValidationResult result = new ArtistValidator(artist).validate(null);
      validationResultModel.setResult(result);
   }

   /**
    * Turns the buttons on this editor on or off based on state of the
    * underlying ORM object. If it is modified turn these on.
    * <p>
    * @param enabled true to enable false to disable
    */
   @Override
   public void updateButtonState(boolean enabled) {
      this.dirty = enabled;
      ActionManager.get(Actions.COMMIT_ID).setEnabled(enabled);
      ActionManager.get(Actions.ROLLBACK_ID).setEnabled(enabled);
      ActionManager.get(Actions.DELETE_ID).setEnabled(enabled);
   }

   /*
    * (non-Javadoc)
    * @seecom.melloware.jukes.gui.view.validation.AbstractValidationModel#
    * gtePropertiesToCheck()
    */
   @Override
   protected String[] getPropertiesToCheck() {
      return VALIDATION_PROPERTIES;
   }

}