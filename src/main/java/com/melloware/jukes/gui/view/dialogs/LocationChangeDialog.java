package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.sql.Connection;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.melloware.jukes.db.Database;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.MessageUtil;

/**
 * Changes a location in the DISC.LOCATION, DISC.COVER_URL, and TRACK.TRACK_URL
 * fields globally.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class LocationChangeDialog extends AbstractDialog {

   private static final Log LOG = LogFactory.getLog(LocationChangeDialog.class);
   private static final String SQL_DISC_LOCATION = ResourceUtils.getString("sql.update.disc.location");
   private static final String SQL_DISC_COVER = ResourceUtils.getString("sql.update.disc.cover");
   private static final String SQL_TRACK_LOCATION = ResourceUtils.getString("sql.update.track.location");
   private JButton buttonApply;
   private JButton buttonClose;
   private JComponent buttonBar;
   private JTextComponent replaceField;
   private JTextComponent searchField;
   private final Settings settings;

   /**
    * Constructs a default about dialog using the given owner.
    * @param owner the dialog's owner
    */
   public LocationChangeDialog(Frame owner, Settings settings) {
      super(owner);
      LOG.debug("Location Changer created.");
      this.settings = settings;
      this.settings.getDatabaseLocation();
      // since we are doing a find we want to compact the database on shutdown
      HibernateUtil.setCompact(true);
   }

   /*
    * (non-Javadoc)
    * @see com.jgoodies.swing.AbstractDialog#doApply()
    */
   @SuppressWarnings("deprecation")
   public void doApply() {
      LOG.debug("Apply pressed.");
      if ((StringUtils.isBlank(searchField.getText())) || (StringUtils.isBlank(replaceField.getText()))) {
         MessageUtil.showwarn(this, Resources.getString("messages.Fieldsmusthaveavalue"));
      }
      final String replace = replaceField.getText().trim();
      final String search = searchField.getText().trim();
      final String replacesize = String.valueOf(search.length() + 1);
      final String searchsize = String.valueOf(search.length());

      if (MessageUtil.confirmUpdate(this)) {
         try {
            GuiUtil.setBusyCursor(this, true);
            HibernateUtil.beginTransaction();
            final Connection connection = HibernateUtil.getSession().connection();
            String sql = MessageFormat.format(SQL_DISC_LOCATION, new Object[] { StringEscapeUtils.escapeSql(replace),
                     replacesize, searchsize, StringEscapeUtils.escapeSql(search) });

            Database.executeSQL(connection, sql);

            sql = MessageFormat.format(SQL_DISC_COVER, new Object[] { StringEscapeUtils.escapeSql(replace),
                     replacesize, searchsize, StringEscapeUtils.escapeSql(search) });

            Database.executeSQL(connection, sql);

            sql = MessageFormat.format(SQL_TRACK_LOCATION, new Object[] { StringEscapeUtils.escapeSql(replace),
                     replacesize, searchsize, StringEscapeUtils.escapeSql(search) });

            Database.executeSQL(connection, sql);

            HibernateUtil.commitTransaction();
            ActionManager.get(Actions.CONNECT_ID).actionPerformed(null);
            ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
            MessageUtil.showSuccess(this);
         } catch (InfrastructureException ex) {
        	 final String errorMessage = Resources.getString("messages.ErrorUpdatingLocation");
        	 MessageUtil.showError(this, errorMessage);
             LOG.error(errorMessage + ex.getMessage(), ex);
             HibernateUtil.rollbackTransaction();
         } finally {
            GuiUtil.setBusyCursor(this, false);
         }
      }
   }

   /**
    * Builds and answers the dialog's content.
    * @return the dialog's content with tabbed pane and button bar
    */
   protected JComponent buildContent() {
      JPanel content = new JPanel(new BorderLayout());
      content.add(buildPanel(), BorderLayout.NORTH);
      content.add(buttonBar, BorderLayout.SOUTH);
      return content;
   }

   /**
    * Builds and returns the dialog's header.
    * @return the dialog's header component
    */
   protected JComponent buildHeader() {
      return new HeaderPanel(Resources.getString("label.GlobalLocationChange"),
    	          	         Resources.getString("label.GlobalLocationChangeMessage"),
                             Resources.LOCATION_TOOL_ICON);
   }

   /**
    * Resizes the given component to give it a quadratic aspect ratio.
    * @param component the component to be resized
    */
   protected void resizeHook(JComponent component) {
      // Resizer.ONE2ONE.resizeDialogContent(component);
   }

   /**
    * Builds the directory selection panel.
    * <p>
    * @return the panel used to select the directory.
    */
   private JComponent buildPanel() {
      JButton[] buttons = new JButton[2];
      JButton button = createApplyButton();
      button.setText(Resources.getString("label.Update"));
      buttonApply = button;
      buttonClose = createCloseButton(true);
      buttons[0] = buttonApply;
      buttons[1] = buttonClose;
      buttonClose.setText(Resources.getString("label.Close"));
      buttonBar = ButtonBarFactory.buildRightAlignedBar(buttons);
      searchField = new JTextField();
      replaceField = new JTextField();
      ((JTextField) searchField).setColumns(55);
      ((JTextField) replaceField).setColumns(55);
      FormLayout layout = new FormLayout("right:max(14dlu;pref), 4dlu, fill:pref:grow", "p, 4px, p, 4px"); // extra
                                                                                                            // bottom
                                                                                                            // space
                                                                                                            // for
                                                                                                            // icons
      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();

      builder.addLabel(Resources.getString("label.Search")+": ", cc.xy(1, 1));
      builder.add(searchField, cc.xy(3, 1));
      builder.addLabel(Resources.getString("label.Replace")+": ", cc.xy(1, 3));
      builder.add(replaceField, cc.xy(3, 3));

      return builder.getPanel();
   }

}