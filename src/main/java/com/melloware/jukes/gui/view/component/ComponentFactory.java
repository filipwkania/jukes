package com.melloware.jukes.gui.view.component;

import java.text.DateFormat;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JFormattedTextField;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.JTextComponent;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.component.ToolBarButton;
import com.jgoodies.validation.formatter.RelativeDateFormatter;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Consists only of static methods that vend formatted text fields used
 * to edit dates that are bound to an underlying ValueModel.
 * Extends the Binding library's BasicComponentFactory to inherit
 * all factory metods from that class.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 *
 * @see com.jgoodies.binding.adapter.BasicComponentFactory
 * @see com.jgoodies.binding.adapter.Bindings
 */
public final class ComponentFactory
    extends BasicComponentFactory {

    private ComponentFactory() {
        // Suppresses default constructor, ensuring non-instantiability.
    }



    /**
     * Creates and returns a formatted text field that is bound
     * to the Date value of the given <code>ValueModel</code>.<p>
     *
     * The JFormattedTextField is configured with an AbstractFormatter
     * that uses two different DateFormats to edit and display the Date.
     * A <code>SHORT</code> DateFormat with strict checking is used to edit
     * (parse) a date; the DateFormatter's default DateFormat is used to
     * display (format) a date. In both cases <code>null</code> Dates are
     * mapped to the empty String.<p>
     *
     * In addition to formatted Dates, the parser accepts positive and
     * negative integers and interprets them as Dates relative to today.
     * For example -1 is yesterday, 1 is tomorrow, and 7 is "in a week".<p>
     *
     * Yesterday, today, and tomorrow are displayed as these Strings,
     * not as formatted Dates.
     *
     * @param valueModel  the model that holds the value to be edited
     * @return a formatted text field for Date instances that is bound
     * @throws NullPointerException if the model is <code>null</code>
     */
    public static JFormattedTextField createDateField(ValueModel valueModel) {
        return createDateField(valueModel, true);
    }

    /**
     * Creates and returns a formatted text field that is bound
     * to the Date value of the given <code>ValueModel</code>.<p>
     *
     * The JFormattedTextField is configured with an AbstractFormatter
     * that uses two different DateFormats to edit and display the Date.
     * A <code>SHORT</code> DateFormat with strict checking is used to edit
     * (parse) a date; the DateFormatter's default DateFormat is used to
     * display (format) a date. In both cases <code>null</code> Dates are
     * mapped to the empty String.<p>
     *
     * In addition to formatted Dates, the parser accepts positive and
     * negative integers and interprets them as Dates relative to today.
     * For example -1 is yesterday, 1 is tomorrow, and 7 is "in a week".<p>
     *
     * If <code>enableShortcuts</code> is set to <code>true</code>,
     * yesterday, today, and tomorrow are displayed as these Strings,
     * not as formatted Dates.
     *
     * @param valueModel  the model that holds the value to be edited
     * @param enableShortcuts true to display yesterday, today, and tomorrow
     *     with natural language strings
     * @return a formatted text field for Date instances that is bound
     * @throws NullPointerException if the model is <code>null</code>
     */
    public static JFormattedTextField createDateField(ValueModel valueModel, boolean enableShortcuts) {
        return createDateField(valueModel, enableShortcuts, false);
    }

    /**
     * Creates and returns a formatted text field that is bound
     * to the Date value of the given <code>ValueModel</code>.<p>
     *
     * The JFormattedTextField is configured with an AbstractFormatter
     * that uses two different DateFormats to edit and display the Date.
     * A <code>SHORT</code> DateFormat with strict checking is used to edit
     * (parse) a date; the DateFormatter's default DateFormat is used to
     * display (format) a date. In both cases <code>null</code> Dates are
     * mapped to the empty String.<p>
     *
     * In addition to formatted Dates, the parser accepts positive and
     * negative integers and interprets them as Dates relative to today.
     * For example -1 is yesterday, 1 is tomorrow, and 7 is "in a week".<p>
     *
     * If <code>enableShortcuts</code> is set to <code>true</code>,
     * yesterday, today, and tomorrow are displayed as these Strings,
     * not as formatted Dates.
     *
     * @param valueModel  the model that holds the value to be edited
     * @param enableShortcuts true to display yesterday, today, and tomorrow
     *     with natural language strings
     * @param commitsOnValidEdit   true to commit on valid edit,
     *     false to commit on focus lost
     * @return a formatted text field for Date instances that is bound
     * @throws NullPointerException if the model is <code>null</code>
     */
    public static JFormattedTextField createDateField(ValueModel valueModel,
                                                      boolean enableShortcuts,
                                                      boolean commitsOnValidEdit) {
        DateFormat shortFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        shortFormat.setLenient(false);

        DefaultFormatter defaultFormatter = new RelativeDateFormatter(shortFormat, false, true);
        defaultFormatter.setCommitsOnValidEdit(commitsOnValidEdit);

        JFormattedTextField.AbstractFormatter displayFormatter = new RelativeDateFormatter(enableShortcuts, true);

        DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(defaultFormatter, displayFormatter);

        JFormattedTextField textField = new JFormattedTextField(formatterFactory);
        Bindings.bind(textField, valueModel);
        return textField;
    }

    /**
     * Create a new button that is a directory chooser button and associates
     * it with a text component to put the selected directory in.
     * <p>
     * @param aTextComponent the text component to title case
     * @return an Abstract button that is the title case button
     */
    public static AbstractButton createDirectoryChooserButton(JTextComponent aTextComponent) {
        final AbstractAction action = (AbstractAction)ActionManager.get(Actions.DIRECTORY_ID);
        AbstractButton button = new ToolBarButton(action);
        button.putClientProperty(Resources.TEXT_COMPONENT, aTextComponent);
        button.setOpaque(false);
        button.setVerifyInputWhenFocusTarget(true);
        return button;
    }

    /**
     * Create a new button that is a file chooser button and associates
     * it with a text component to put the selected file in.
     * <p>
     * @param aTextComponent the text component to title case
     * @param aTitle the title of the chooser dialog
     * @param aFilter the file filter for the chooser
     * @return an Abstract button that is the title case button
     */
    public static AbstractButton createFileChooserButton(JTextComponent aTextComponent,
                                                         String aTitle,
                                                         FileFilter aFilter) {
        final AbstractAction action = (AbstractAction)ActionManager.get(Actions.FILE_CHOOSER_ID);
        AbstractButton button = new ToolBarButton(action);
        button.putClientProperty(Resources.TEXT_COMPONENT, aTextComponent);
        button.putClientProperty(Resources.FILE_CHOOSER_FILTER, aFilter);
        button.putClientProperty(Resources.FILE_CHOOSER_TITLE, aTitle);
        button.setOpaque(false);
        button.setVerifyInputWhenFocusTarget(true);
        return button;
    }

    /**
     * Create a new button that is a title case button and associates it with a
     * text component to actually perform the title casing on.
     * <p>
     * @param aTextComponent the text component to title case
     * @return an Abstract button that is the title case button
     */
    public static AbstractButton createTitleCaseButton(JTextComponent aTextComponent) {
        final AbstractAction action = (AbstractAction)ActionManager.get(Actions.TITLECASE_ID);
        AbstractButton button = new ToolBarButton(action);
        button.putClientProperty(Resources.TEXT_COMPONENT, aTextComponent);
        button.setOpaque(false);
        button.setVerifyInputWhenFocusTarget(true);
        return button;
    }

    /**
     * Create a new tool bar button from an Action.
     * <p>
     * @param aActionId the actionId to create the button for
     * @return an Abstract button that is the title case button
     */
    public static AbstractButton createToolBarButton(String aActionId) {
        final AbstractAction action = (AbstractAction)ActionManager.get(aActionId);
        AbstractButton button = new ToolBarButton(action);
        button.setOpaque(false);
        button.setVerifyInputWhenFocusTarget(true);
        return button;
    }

    /**
     * Create a new tool bar toggle button from an Action.
     * <p>
     * @param aActionId the actionId to create the button for
     * @return an Abstract button that is the title case button
     */
    public static AbstractButton createToolBarToggleButton(String aActionId) {
        final AbstractAction action = (AbstractAction)ActionManager.get(aActionId);
        AbstractButton button = new JToggleButton(action);
        button.setText("");
        button.setOpaque(false);
        button.setVerifyInputWhenFocusTarget(true);
        return button;
    }

}