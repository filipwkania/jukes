package com.melloware.jukes.gui.view.preferences;

import java.beans.BeanInfo;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2fprod.common.model.DefaultBeanInfoResolver;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.melloware.jukes.gui.tool.Settings;

/**
 * A <code>PropertySheet</code> for editing all the application settings.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class PropertySheetSettings
    extends JPanel {

	private static final Log LOG = LogFactory.getLog(PropertySheetSettings.class);
    private final Settings settings;
    private final PropertySheetPanel sheet;

    /**
     * Constructor that takes a Settings object.
     * <p>
     * @param aSettings the Settings object to use for this panel
     */
    public PropertySheetSettings(Settings aSettings) {
    	LOG.debug("PropertySheetSettings created.");
        this.settings = aSettings;

        setLayout(LookAndFeelTweaks.createVerticalPercentLayout());

        final DefaultBeanInfoResolver resolver = new DefaultBeanInfoResolver();
        final BeanInfo beanInfo = resolver.getBeanInfo(this.settings);

        sheet = new PropertySheetPanel();
        sheet.setMode(PropertySheet.VIEW_AS_CATEGORIES);
        sheet.setProperties(beanInfo.getPropertyDescriptors());
        sheet.readFromObject(this.settings);
        sheet.setDescriptionVisible(true);
        sheet.setSortingCategories(true);
        sheet.setSortingProperties(false);
        add(sheet, "*");
    }

	/**
	 * Gets the sheet.
	 * <p>
	 * @return Returns the sheet.
	 */
	public PropertySheetPanel getSheet() {
		return this.sheet;
	}

    

}