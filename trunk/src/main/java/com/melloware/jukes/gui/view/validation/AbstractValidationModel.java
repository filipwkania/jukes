package com.melloware.jukes.gui.view.validation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;


/**
 * Abstract validation model for all validation models in the Jukes.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
@SuppressWarnings("PMD")
abstract public class AbstractValidationModel
    extends PresentationModel {

    protected boolean dirty = false;
    protected final ValidationResultModel validationResultModel;

    /**
     * Default constructor.
     */
    public AbstractValidationModel(Object aObject) {
        super(aObject);
        validationResultModel = new DefaultValidationResultModel();
        initEventHandling();
        updateValidationResult();
    }

    abstract public void updateButtonState(boolean enabled);

    public ValidationResultModel getValidationResultModel() {
        return validationResultModel;
    }

    abstract protected String[] getPropertiesToCheck();

    abstract protected void updateValidationResult();

    /**
     * Has this form been modified?
     * <p>
     * @return if the form is dirty
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
    * Listens to changes in all properties of the current Orm
    * and to Orm changes.
    */
    private void initEventHandling() {
        final PropertyChangeListener handler = new ValidationUpdateHandler();
        addBeanPropertyChangeListener(handler);
        getBeanChannel().addValueChangeListener(handler);
    }

    /**
     * Validates the order using an OrmValidator and
     * updates the validation result.
     */
    private class ValidationUpdateHandler
        implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) { //NOPMD
            updateValidationResult();
            if (StringUtils.indexOfAny(evt.getPropertyName(), getPropertiesToCheck()) >= 0) { //NOPMD
                if (!ObjectUtils.equals(evt.getOldValue(), evt.getNewValue())) { //NOPMD
                    updateButtonState(true);
                }
            }
        }

    }

	/**
	 * Sets the dirty.
	 * <p>
	 * @param aDirty The dirty to set.
	 */
	public void setDirty(boolean aDirty) {
		this.dirty = aDirty;
	}

}