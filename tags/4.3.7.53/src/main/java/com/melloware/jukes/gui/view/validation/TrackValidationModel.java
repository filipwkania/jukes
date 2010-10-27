package com.melloware.jukes.gui.view.validation;

import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.validation.ValidationResult;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.gui.tool.Actions;

/**
 * Provides all models to bind an artist editor to its domain model, an instance
 * of {@link Track}.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class TrackValidationModel extends AbstractValidationModel {

   private static final String[] VALIDATION_PROPERTIES = { Track.PROPERTYNAME_NAME, Track.PROPERTYNAME_TRACK_NUMBER,
            Track.PROPERTYNAME_COMMENT };

   /**
    * Constuctor that takes an Track object.
    * <p>
    * @param aTrack the domain object
    */
   public TrackValidationModel(Track aTrack) {
      super(aTrack);
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

      final Track track = (Track) getBean();
      final boolean isValid = ((track == null) ? false : track.isValid());
      ActionManager.get(Actions.FILE_RENAME_ID).setEnabled(isValid && enabled);
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
      Track track = (Track) getBean();
      ValidationResult result = new TrackValidator(track).validate(null);
      validationResultModel.setResult(result);
   }

}