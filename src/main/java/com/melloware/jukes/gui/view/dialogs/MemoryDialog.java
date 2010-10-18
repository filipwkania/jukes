package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Sizes;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.util.Resizer;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Provides JVM Memory usage and garbage collection option.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class MemoryDialog extends AbstractDialog {

   private static final Log LOG = LogFactory.getLog(MemoryDialog.class);
   private JLabel totalMemory;
   private JLabel maxMemory;
   private JLabel freeMemory;
   private JPanel panel;
   private JButton buttonClose;
   private JComponent buttonBar;

   /**
    * Constructs a default about dialog using the given owner.
    * @param owner the dialog's owner
    */
   public MemoryDialog(Frame owner) {
      super(owner);
      LOG.debug("Memory Dialog created.");
   }

   /**
    * Builds and answers the dialog's content.
    * @return the dialog's content with tabbed pane and button bar
    */
   @Override
   protected JComponent buildContent() {
      JPanel content = new JPanel(new BorderLayout());
      content.add(buildMainPanel(), BorderLayout.CENTER);
      buttonClose = createCancelButton();// AZ
      buttonClose.setText(Resources.getString("label.Close")); // AZ
      buttonClose.setEnabled(true);
      buttonBar = ButtonBarFactory.buildRightAlignedBar(buttonClose);
      // buttonBar = buildButtonBarWithClose();
      content.add(buttonBar, BorderLayout.SOUTH);
      return content;
   }

   /**
    * Builds and returns the dialog's header.
    * @return the dialog's header component
    */
   @Override
   protected JComponent buildHeader() {
      return new HeaderPanel(Resources.getString("label.Memory"), Resources.getString("label.MemoryMessage"),
               Resources.MEMORY_LARGE_ICON);
   }

   /**
    * Builds and returns the dialog's pane.
    * @return the dialog's pane component
    */
   protected JComponent buildMainPanel() {
      totalMemory = UIFactory.createBoldLabel(convertMemoryNumber(Runtime.getRuntime().totalMemory()));
      maxMemory = UIFactory.createBoldLabel(convertMemoryNumber(Runtime.getRuntime().maxMemory()));
      freeMemory = UIFactory.createBoldLabel(convertMemoryNumber(Runtime.getRuntime().maxMemory()
               - Runtime.getRuntime().totalMemory()));

      JButton buttonGC = new JButton(Resources.getString("label.GarbageCollection"));
      buttonGC.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            garbageCollect();
         }
      });

      FormLayout layout = new FormLayout("7dlu, left:pref, right:pref, left:0:grow", "pref, 2dlu, pref, 14dlu, "
               + "pref, 2dlu, pref, pref, pref," + "pref, 2dlu, pref, 14dlu, pref");

      PanelBuilder builder = new PanelBuilder(layout);
      builder.setDefaultDialogBorder();
      CellConstraints cc = new CellConstraints();
      int row = 1;
      builder.addSeparator(Resources.getString("label.MemoryUsage"), cc.xyw(1, row++, 4));
      row++;
      builder.addLabel(Resources.getString("label.MaxMemory") + ": ", cc.xy(2, row));
      builder.add(maxMemory, cc.xy(3, row++));
      builder.addLabel(Resources.getString("label.UsedMemory") + ": ", cc.xy(2, row));
      builder.add(totalMemory, cc.xy(3, row++));
      builder.addLabel(Resources.getString("label.FreeMemory") + ": ", cc.xy(2, row));
      builder.add(freeMemory, cc.xy(3, row++));
      row++;
      row++;
      builder.add(buttonGC, cc.xy(2, row));
      panel = builder.getPanel();
      int width = Sizes.dialogUnitXAsPixel(220, panel);
      panel.setPreferredSize(Resizer.DEFAULT.fromWidth(width));
      panel.setBorder(Borders.DIALOG_BORDER);
      return panel;
   }

   /**
    * Updates the labels with new memory settings.
    */
   private void updateMemoryStats() {
      totalMemory.setText(convertMemoryNumber(Runtime.getRuntime().totalMemory()));
      maxMemory.setText(convertMemoryNumber(Runtime.getRuntime().maxMemory()));
      freeMemory.setText(convertMemoryNumber(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory()));
   }

   /**
    * Converts a number like 134217728 to 128 MB.
    * <p>
    * @param aMemoryNumber the number to convert
    * @return the String representation
    */
   private String convertMemoryNumber(long aMemoryNumber) {
      return FileUtils.byteCountToDisplaySize(aMemoryNumber);
   }

   /**
    * Runs the garbage collector.
    */
   private void garbageCollect() {
      LOG.debug("Running garbage collection.");
      System.gc();
      updateMemoryStats();
      panel.updateUI();
   }

   /**
    * Resizes the given component to give it a quadratic aspect ratio.
    * @param component the component to be resized
    */
   @Override
   protected void resizeHook(JComponent component) {
      Resizer.ONE2ONE.resizeDialogContent(component);
   }

}