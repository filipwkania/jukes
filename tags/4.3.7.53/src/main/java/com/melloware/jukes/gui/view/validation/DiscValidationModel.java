package com.melloware.jukes.gui.view.validation;

import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.validation.ValidationResult;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.gui.tool.Actions;

/**
 * Provides all models to bind an artist editor to its domain model, an instance
 * of {@link Disc}.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class DiscValidationModel extends AbstractValidationModel {

   private static final String[] VALIDATION_PROPERTIES = { Disc.PROPERTYNAME_NAME, Disc.PROPERTYNAME_NOTES,
            Disc.PROPERTYNAME_COVER_URL, Disc.PROPERTYNAME_GENRE, Disc.PROPERTYNAME_YEAR };

   /**
    * Constructor that takes an Disc object.
    * <p>
    * @param aDisc the domain object
    */
   public DiscValidationModel(Disc aDisc) {
      super(aDisc);
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

      final Disc disc = (Disc) getBean();
      final boolean isValid = ((disc == null) ? false : disc.isValid());
      ActionManager.get(Actions.FILE_RENAME_ID).setEnabled(isValid && enabled);
      ActionManager.get(Actions.DISC_COVER_ID).setEnabled(isValid && enabled);
      ActionManager.get(Actions.DISC_WEB_ID).setEnabled(isValid && enabled);
      ActionManager.get(Actions.FREE_DB_ID).setEnabled(isValid && enabled);
   }

   /*
    * (non-Javadoc)
    * @seecom.melloware.jukes.gui.view.validation.AbstractValidationModel#
    * getPropertiesToCheck()
    */
   @Override
   protected String[] getPropertiesToCheck() {
      return VALIDATION_PROPERTIES;
   }

   @Override
   protected void updateValidationResult() {
      Disc disc = (Disc) getBean();
      ValidationResult result = new DiscValidator(disc).validate(null);
      validationResultModel.setResult(result);
   }

}