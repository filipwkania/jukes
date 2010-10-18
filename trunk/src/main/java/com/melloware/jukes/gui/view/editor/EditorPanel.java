package com.melloware.jukes.gui.view.editor;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uifextras.util.UIFactory;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.view.component.ComplexInternalFrame;

/**
 * A container that wraps <code>Editor</code> instances with a
 * <code>JScrollPane</code>, which in turn is wrapped by
 * a <code>SimpleInternalFrame</code>.<p>
 *
 * It keeps track of a collection of editors to be able to update
 * the UIs of invisible editors when the look&amp;feel changes.
 * <code>SwingUtilities#updateComponentTreeUI</code> updates
 * only editors that are in the component tree. Since we have only
 * the active editor in the component tree, we must update other
 * editors <em>by hand</em>.
 * An alternative implementation is to use a <code>CardPanel</code>
 * that has all editors in the component tree, but displays one of them
 * and hides all others.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 *
 * @see MainModule
 * @see Editor
 * @see javax.swing.SwingUtilities#updateComponentTreeUI(java.awt.Component)
 */
@SuppressWarnings("unchecked")
public final class EditorPanel
    extends ComplexInternalFrame { 

    private static final Log LOG = LogFactory.getLog(EditorPanel.class);

    private JScrollPane scrollPane;

    /**
     * Holds a list of all editors to update the UI of invisible editors
     * in case the look&amp;feel changes. Contains some editors that
     * are not registered with the editor registry, for example the
     * WelcomePanel that has no associated domain class.
     *
     * @see #updateUI()
     */
    private final List editors;

    /**
     * Maps domain classes to <code>Editor</code> instances.
     *
     * @see #addEditor(Editor)
     */
    private final Map editorRegistry;

    /**
     * Constructs a <code>EditorPanel</code>.
     *
     * @param mainModule   used to observe selection changes
     */
    public EditorPanel(MainModule mainModule) {
        super("Editor");
        editorRegistry = new HashMap();
        editors = new LinkedList();
        setContent(buildContent());

        mainModule.addPropertyChangeListener(MainModule.PROPERTYNAME_SELECTION, new SelectionChangeHandler());
    }

    /**
     * Gets the editors.
     * <p>
     * @return Returns the editors.
     */
    public List getEditors() {
        return this.editors;
    }

    /**
     * Shows the specified <code>Editor</code>: sets the icon,
     * title, tool bar, and finally switches the viewport's view.
     *
     * @param newEditor   the editor to be set
     */
    public void setActiveEditor(Editor newEditor) {
        setFrameIcon(newEditor.getIcon());
        setTitle(newEditor.getTitle());
        setToolBar(newEditor.getToolBar());
        setHeaderToolBar(newEditor.getHeaderToolBar());
        scrollPane.setViewportView((Component)newEditor);
    }

    /**
     * Adds a <code>Editor</code> to this <code>EditorPanel</code>.
     *
     * @param editor   the editor to add
     */
    public void addEditor(Editor editor) {
        Class domainClass = editor.getDomainClass();
        if (domainClass != null) {
            registerEditor(domainClass, editor);
        }

        editors.add(editor);
    }
    
    /**
     * Clears all editors out.
     */
    public void clearEditors() {
    	editors.clear();
    }

    /**
     * The look&amp;feel has changed. Updates the UI.
     * In addition to the superclass behavior, this method
     * updates the component trees for the invisible editors.
     */
    public void updateUI() {
        super.updateUI();
        if (editors == null) {
            return;
        }

        for (Iterator i = editors.iterator(); i.hasNext();) {
            JComponent viewer = (JComponent)i.next();
            // The Swing Suite provides class MySwingUtilities
            // that fixes bugs with l&f switching at runtime.
            SwingUtilities.updateComponentTreeUI(viewer);
        }
    }

    /**
     * Returns the current <code>Editor</code>.
     *
     * @return the current <code>Editor</code>.
     */
    private Editor getActiveEditor() {
        return (Editor)scrollPane.getViewport().getView();
    }

    /**
     * Builds the content, which is a panel wrapped by a stripped
     * <code>JScrollPane</code>. The panel will be replaced later
     * by the different viewers, where we access the viewer via
     * the scrollpane's viewport view.
     */
    private JComponent buildContent() {
        return scrollPane = UIFactory.createStrippedScrollPane(new JPanel());
    }

    /**
     * Returns the <code>Editor</code> that has been registered
     * for the given domain object class, <code>null</code> if none.
     *
     * @param domainClass   the class associated with the editor to lookup
     * @return the editor associated with the given domain class
     */
    private Editor lookupEditor(Class domainClass) {
        return (Editor)editorRegistry.get(domainClass);
    }

    /**
     * Registers a <code>Editor</code> for a domain class.
     *
     * @param domainClass   the key used to lookup the editor later
     * @param editor        the value stored under the given domain class
     */
    private void registerEditor(Class domainClass, Editor editor) {
        Object oldValue = editorRegistry.put(domainClass, editor);
        if (oldValue != null) {
            LOG.debug("Duplicate editor registered for " + domainClass);
        }
    }

    /**
     * Deactivates the current editor, looks up appropriate viewer for
     * the new selection, sets the model, activates it and makes it visible.
     *
     * @param selection   the new domain selection
     */
    private void updateActiveEditor(Object selection) {
        if (selection == null) {
            return;
        }

        getActiveEditor().deactivate();
        Editor editor = lookupEditor(selection.getClass());
        if (editor != null) {
            editor.setModel(selection);
            editor.activate();
            setActiveEditor(editor);
        }
    }

    // Listens to changes in the main module's selection and updates the editor.
    private class SelectionChangeHandler
        implements PropertyChangeListener {

        /**
         * The main module's selection has changed. Look up the editor
         * registered for the selection's domain class, deactivate the active
         * editor, activate the new editor and set is as active editor.
         *
         * @param evt   describes the property change
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() == null) {
                // gets the first editor in the list if no event found
                setActiveEditor((Editor)getEditors().get(0));
            } else {
                updateActiveEditor(evt.getNewValue());
            }
        }
    }

}