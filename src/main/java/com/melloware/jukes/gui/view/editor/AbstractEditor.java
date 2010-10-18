package com.melloware.jukes.gui.view.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.component.ToolBarButton;
import com.jgoodies.uifextras.util.UIFactory;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.melloware.jukes.db.orm.AbstractJukesObject;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.gui.view.tasks.UpdateTagsTask;
import com.melloware.jukes.gui.view.validation.AbstractValidationModel;
import com.melloware.jukes.util.MessageUtil;

/**
 * The abstract superclass of all <code>Editor</code> implementations.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * 2010 AZ Development
 */
@SuppressWarnings("PMD")
abstract public class AbstractEditor
    extends JPanel
    implements Editor {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd yyyy h:mm a", Locale.US);
    protected static final Font FONT_ENABLED = new JLabel().getFont();
    protected static final Font FONT_DISABLED = FONT_ENABLED.deriveFont(FONT_ENABLED.getStyle() ^ Font.BOLD);
    protected static final Border BORDER_ENABLED = new JTextField().getBorder();
    protected static final Border BORDER_DISABLED = BorderFactory.createEmptyBorder();
    protected static final String HINT = "HINT";
    protected AbstractValidationModel validationModel;
    protected final Icon icon;
    protected JComponent hintAreaPane;
    protected JLabel createdByLabel;
    protected JLabel createdDateLabel;
    protected JLabel hintLabel;
    protected JLabel modifiedByLabel;
    protected JLabel modifiedDateLabel;
    protected JTextArea hintArea;
    protected ProgressMonitor progressMonitor;
    protected final String titlePrefix;
    protected Timer timer;
    protected UpdateTagsTask task;
    private Object model;

    /**
     * Constructs an <code>AbstractEditor</code> with the specified icon.
     */
    public AbstractEditor(Icon icon) {
        this(icon, "");
    }

    /**
     * Constructs an <code>AbstractEditor</code> with the specified title
     * prefix.
     */
    public AbstractEditor(String titlePrefix) {
        this(null, titlePrefix);
    }

    /**
     * Constructs an <code>AbstractEditor</code> with the specified
     * <code>Icon</code> and title prefix.
     */
    public AbstractEditor(Icon icon, String titlePrefix) {
        this.icon = icon;
        this.titlePrefix = titlePrefix;
        build();
    }

    /**
     * Returns the class used to register this instance in the UpdateManager.
     */
    abstract public Class getDomainClass();

    /**
     * Answers this <code>Editor</code>'s <code>JToolBar</code>.
     * The default implementation specifies that no tool bar is used.
     */
    public JToolBar getHeaderToolBar() {
        return null;
    }

    /**
     * Answers this <code>Editor</code>'s <code>Icon</code>.
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Returns this editor's underlying model.
     */
    public Object getModel() {
        return model;
    }

    /**
     * Returns this editor's underlying model as an AbstractJukesObject.
     */
    public AbstractJukesObject getOrmObject() {
        return (AbstractJukesObject)model;
    }

    /**
     * Answers this <code>Editor</code>'s title.
     */
    public String getTitle() {
        return titlePrefix + ' ' + getTitleSuffix();
    }

    /**
     * Answers this <code>Editor</code>'s <code>JToolBar</code>.
     * The default implementation specifies that no tool bar is used.
     */
    public JToolBar getToolBar() {
        return null;
    }

    /**
     * Sets a new model. Does nothing if the old and new model are the same.
     * If the model changes, invokes <code>#updateView</code>.
     * AZ 2010: compare object Id also.
     * @param newModel   the model to set
     */
    public void setModel(Object newModel) {
        Object oldModel = getModel();
        AbstractJukesObject newObject, oldObject;//AZ
    	oldObject = (AbstractJukesObject) oldModel;
    	newObject = (AbstractJukesObject) newModel;
    	
    	if ((oldModel != null) && (oldModel.equals(newModel)) && (oldObject.getId() == newObject.getId()) ) {
        	this.lock();
            return;
        }
        model = newModel;
        updateView();
        this.lock();
    }

    /**
     * Activates this viewer.
     */
    public void activate() {
        // Do nothing by default; subclasses may override.
    }

    /**
     * Commits any changes made to this editor.
     */
    public void commit() {
    	this.lock();
    }
    
    /**
     * Unlocks this viewer and updates all text fields and buttons.
     */
    public void unlock() {
    	ActionManager.get(Actions.UNLOCK_ID).setEnabled(false);
    	validationModel.updateButtonState(true);
        final Collection components = getAllComponents(this);
    	
    	for (Iterator iter = components.iterator(); iter.hasNext();) {
			final Component component = (Component) iter.next();
			if (component instanceof JTextComponent) {
				final JTextComponent field = (JTextComponent)component;
				//skip if this is the hint area
				if (field.getClientProperty(HINT) != null) {
					continue;
				}
				field.setEnabled(true);
				field.setBorder(BORDER_ENABLED);
				field.setOpaque(true);
				field.setFont(FONT_ENABLED);
			} else if (component instanceof JComboBox) {
				final JComboBox field = (JComboBox)component;
				field.setEnabled(true);
				field.setVisible(true);
			} else if (component instanceof JLabel) {
				final JLabel field = (JLabel)component;
				if ("GENRE".equalsIgnoreCase(field.getName())) {
					field.setVisible(false);
				}
			} else if (component instanceof ToolBarButton) {
				final ToolBarButton button = (ToolBarButton)component;
				button.setVisible(true);
			}
		}
    	
    	this.updateUI();
    }
    
    /**
     * Locks this viewer and updates all text fields and buttons.
     */
    public void lock() {
    	ActionManager.get(Actions.UNLOCK_ID).setEnabled(true);
    	validationModel.updateButtonState(false);
    	final Collection components = getAllComponents(this);
    	
    	for (Iterator iter = components.iterator(); iter.hasNext();) {
			final Component component = (Component) iter.next();
			if (component instanceof JTextComponent) {
				final JTextComponent field = (JTextComponent)component;
				//skip if this is the hint area
				if (field.getClientProperty(HINT) != null) {
					continue;
				}
				
				field.setEnabled(false);
				field.setDisabledTextColor(Color.BLACK);
				field.setBorder(BORDER_DISABLED);
				field.setOpaque(false);
				field.setFont(FONT_DISABLED);
			}  else if (component instanceof JComboBox) {
				final JComboBox field = (JComboBox)component;
				field.setEnabled(false);
				field.setVisible(false);
			} else if (component instanceof JLabel) {
				final JLabel field = (JLabel)component;
				if ("GENRE".equalsIgnoreCase(field.getName())) {
					final String text = field.getText().trim();
					field.setText("  " + text);
					field.setVisible(true);
					field.setFont(FONT_DISABLED);
				}
			} else if (component instanceof ToolBarButton) {
				final ToolBarButton button = (ToolBarButton)component;
				button.setVisible(false);
			}
			
		}
    	
    	this.updateUI();
    }
    
    /**
     * Recurse through a component and build a list of all the components 
     * underneath of it.
     * <p>
     * @param aTop the top container
     * @return the collection of all components under the container
     */
    private Collection getAllComponents(Container aTop) {
    	final Component[] comp = aTop.getComponents();
    	final ArrayList list = new ArrayList();
    	
    	if (comp.length == 0) {
			return list;
		}
    	
    	for (int i = 0; i < comp.length; i++) {
    		list.add(comp[i]);
    		if (comp[i] instanceof Container) {
				list.addAll(getAllComponents((Container)comp[i]));
			}
		}
    	return list;
    }

    /**
     * Deactivates this viewer.
     */
    public void deactivate() {
    	//AZ: Do not refresh the object if no changes were made
    	/**
        // refresh the object if there are errors when leaving
        if (this.hasErrors()) {
            this.rollback();
        } else {**/
    	
        	//if dirty prompt the user to save changes
            if (validationModel.isDirty()) {
                if (MessageUtil.promptYesNo(getMainFrame(), Resources.getString("messages.promptSaveChanges"))) {
                    this.commit();
                } else {
                	//if they don't want to save then refresh this object
                	this.rollback();
                }
            }
        //AZ }
    }

    /**
     * Delete the object and its descendants contained by this editor.
     */
    public void delete() {
        // Do nothing by default; subclasses may override.
    }

    /**
     * Tries to find a new cover for the disc.
     */
    public void findCover() {
        // Do nothing by default; subclasses may override.
    }

    /**
     * Renames any files this editor owns.
     */
    public void renameFiles() {
        // Do nothing by default; subclasses may override.
    }

    /**
     * Rollback any changes made to this editor
     */
    public void rollback() {
        // Do nothing by default; subclasses may override.
    	this.lock();
    }

    /**
     * Perform the web service search to look for disc info.
     */
    public void webSearch() {
        // Do nothing by default; subclasses may override.
    }
    
    /** AZ
     * Perform the web service search to look for disc info.
     */
    public void freeDBSearch() {
        // Do nothing by default; subclasses may override.
    }

    /**
     * Returns a suffix for this editor's title.
     *
     * @return a suffix for this editor's title
     */
    abstract protected String getTitleSuffix();

    /**
     * Builds this panel.
     */
    abstract protected void build();

    /**
     * Writes the view contents to the underlying model.
     */
    abstract protected void updateModel();

    /**
     * Reads the view contents from the underlying model.
     */
    abstract protected void updateView();

    /**
     * Gets the MainFrame for the application.
     * <p>
     * @return the MainFrame object
     */
    protected MainFrame getMainFrame() {
        return (MainFrame)Application.getDefaultParentFrame();
    }

    /**
     * Gets the MainModule for the application.
     * <p>
     * @return the MainModule object
     */
    protected MainModule getMainModule() {
        return getMainFrame().getMainModule();
    }

    /**
     * Gets the settings for the application.
     * <p>
     * @return the Settings object of user defined settings
     */
    protected Settings getSettings() {
        return MainModule.SETTINGS;
    }

    /**
     * Gets the validationModel.
     * <p>
     * @return Returns the validationModel.
     */
    protected AbstractValidationModel getValidationModel() {
        return this.validationModel;
    }

    /**
     * Sets the cursor to hourglass for true and default for false.  Used for
     * long operations such as saves.
     * <p>
     * @param aBusy true for busy cursor, false for default
     */
    protected void setBusyCursor(boolean aBusy) {
        if (aBusy) {
            getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Builds the audit information panel which displays the created by, and
     * modified by info of a ORM record.
     * <p>
     * @return the panel to display the audit info
     */
    protected JComponent buildAuditInfoPanel() {
        // create the labels as BOLD
        createdDateLabel = UIFactory.createBoldLabel("");
        createdByLabel = UIFactory.createBoldLabel("");
        modifiedDateLabel = UIFactory.createBoldLabel("");
        modifiedByLabel = UIFactory.createBoldLabel("");

        FormLayout layout = new FormLayout("right:max(40dlu;pref), 3dlu, 100dlu, 7dlu, "
                                           + "right:max(40dlu;pref), 3dlu, 100dlu", "p, 3dlu, p, 3dlu, p, 3dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        int row = 1;
        builder.addLabel(Resources.getString("label.createdby") + ": ", cc.xy(1, row));
        builder.add(createdByLabel, cc.xy(3, row));
        builder.addLabel(Resources.getString("label.createddate") + ": ", cc.xy(5, row));
        builder.add(createdDateLabel, cc.xy(7, row++));
        row++;

        builder.addLabel(Resources.getString("label.modifiedby") + ": ", cc.xy(1, row));
        builder.add(modifiedByLabel, cc.xy(3, row));
        builder.addLabel(Resources.getString("label.modifieddate") + ": ", cc.xy(5, row));
        builder.add(modifiedDateLabel, cc.xy(7, row++));
        row++;
        
        JComponent component = builder.getPanel();
        component.setVisible(this.getSettings().isAuditInfo());

        return component;
    }

    /**
     * Builds the hint area panel where validation hints are displayed.
     * <p>
     * @return the panel to display the hints
     */
    protected JComponent buildHintAreaPane() {
        hintLabel = new JLabel(ValidationResultViewFactory.getInfoIcon());
        hintArea = new JTextArea(1, 38);
        hintArea.putClientProperty(HINT, HINT);
        hintArea.setEditable(false);
        hintArea.setOpaque(false);

        FormLayout layout = new FormLayout("pref, 2dlu, default", "pref");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        builder.add(hintLabel, cc.xy(1, 1));
        builder.add(hintArea, cc.xy(3, 1));

        hintAreaPane = builder.getPanel();
        hintAreaPane.setVisible(false);
        return hintAreaPane;
    }

    /**
     * Does this editor pass validation right now. True if so false otherwise.
     * <p>
     * @return true if passes validation
     */
    protected boolean hasErrors() {
        return getValidationModel().getValidationResultModel().getResult().hasErrors();
    }

    /**
     * Initializes any event handling.
     */
    protected void initEventHandling() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(new FocusChangeHandler());
    }


    /**
     * Displays an input hint for components that get the focus permanently.
     */
    private final class FocusChangeHandler
        implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if (!"permanentFocusOwner".equals(propertyName)) {
                return;
            }
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            String focusHint = (focusOwner instanceof JComponent)
                               ? (String)ValidationComponentUtils.getInputHint((JComponent)focusOwner) : null;
                        
            hintArea.setText(focusHint);
            hintAreaPane.setVisible(focusHint != null);
        }
    }

}