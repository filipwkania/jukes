package com.melloware.jukes.gui.view;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;

import com.jgoodies.looks.BorderStyle;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.builder.MenuBuilder;
import com.jgoodies.uif.builder.PopupMenuBuilder;
import com.jgoodies.uif.component.UIFMenuItem;
import com.jgoodies.uif.osx.OSXApplicationMenu;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Builds the <code>JMenuBar</code> and pull-down menus in Jukes.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com> 
 * @version 4.0
 * AZ - 2009, 2010 
 */
public final class MainMenuBuilder {

	public static final String CATALOG = Resources.getString("menu.catalog");
	private static final String PLAYER = Resources.getString("menu.player");
    private static final String REPORTS = Resources.getString("menu.reports");
	private static final String TOOLS = Resources.getString("menu.tools");
	private static final String HELP = Resources.getString("menu.help");
	private static final String LANGUAGE = Resources.getString("menu.language");
	
    
    /**
     * Builds and returns the Player.
     */
    public static JPopupMenu buildPlayerPopupMenu(Component aComponent) {
        final PopupMenuBuilder popup = new PopupMenuBuilder("Player");
        UIFMenuItem menuItem = null;
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYER_QUEUE_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYER_QUEUE_NEXT_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.TRACK_PLAY_IMMEDIATE_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        //AZ
        menuItem = new UIFMenuItem(ActionManager.get(Actions.DISCLIST_QUEUE_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        return popup.getPopupMenu();
    }
    
    /**
     * Builds and returns the Playlist Menu.
     * <p>
     * @param aComponent the panel that owns this menu
     * @param aList a JList that this menu item is acting on.
     */
    public static JPopupMenu buildPlaylistPopupMenu(Component aComponent, Component aList) {
        final PopupMenuBuilder popup = new PopupMenuBuilder("Playlist");
        UIFMenuItem menuItem = null;
        menuItem = new UIFMenuItem(ActionManager.get(Actions.TRACK_PLAY_IMMEDIATE_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aList);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYLIST_MOVEUP_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYLIST_MOVEDOWN_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYLIST_MOVEOVER_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYLIST_REMOVE_TRACK_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYLIST_CLEAR_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYLIST_GOTO_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aList);
        popup.add(menuItem);
          
        return popup.getPopupMenu();
    }

    /**
     * Configures, composes, and returns the menu bar.
     */
    JMenuBar build() {
        JMenuBar menuBar = new JMenuBar();

        // Set a hint so that JGoodies Looks will detect it as being in the header.
        menuBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        // Unlike the default, use a separator border.
        menuBar.putClientProperty(WindowsLookAndFeel.BORDER_STYLE_KEY, BorderStyle.SEPARATOR);
        menuBar.putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY, BorderStyle.SEPARATOR);

        menuBar.add(buildCatalogMenu());
        menuBar.add(buildPlayerMenu());
        menuBar.add(buildReportsMenu());
        menuBar.add(buildLanguageMenu());
        menuBar.add(buildToolsMenu());
        menuBar.add(buildHelpMenu());
        return menuBar;
    }

    /**
     * Builds and returns the Catalog menu.
     */
    private JMenu buildCatalogMenu() {
        MenuBuilder builder = new MenuBuilder(CATALOG, CATALOG.charAt(0));

        builder.add(ActionManager.get(Actions.TRACK_ADD_ID)); //AZ
        builder.add(ActionManager.get(Actions.DISC_ADD_ID));
        builder.add(ActionManager.get(Actions.DISC_FINDER_ID));
        builder.add(ActionManager.get(Actions.DISC_REMOVER_ID));
        builder.addSeparator();
        builder.add(ActionManager.get(Actions.SEARCH_ID));
        builder.add(ActionManager.get(Actions.PLAYLIST_SHOW_ID));
        builder.add(ActionManager.get(Actions.DISCLIST_SHOW_ID)); //AZ
        builder.add(ActionManager.get(Actions.FILTER_SHOW_ID));
        builder.add(ActionManager.get(Actions.CATALOG_EXPORT_ID));
        builder.addSeparator();
        builder.add(ActionManager.get(Actions.CONNECT_ID));
        builder.add(ActionManager.get(Actions.REFRESH_ID));
        if (!OSXApplicationMenu.isRegisteredQuit()) {
            builder.addSeparator();
            builder.add(ActionManager.get(Actions.EXIT_ID));
        }
        return builder.getMenu();
    }
    
    /**
     * Builds and returns the Player menu.
     */
    private JMenu buildPlayerMenu() {
        MenuBuilder builder = new MenuBuilder(PLAYER, PLAYER.charAt(0));
        builder.add(ActionManager.get(Actions.PLAYER_PLAY_ID));
        builder.add(ActionManager.get(Actions.PLAYER_PAUSE_ID));
        builder.add(ActionManager.get(Actions.PLAYER_STOP_ID));
        builder.add(ActionManager.get(Actions.PLAYER_PREVIOUS_ID));
        builder.add(ActionManager.get(Actions.PLAYER_NEXT_ID));
        return builder.getMenu();
    }
    
    /**
     * Builds and returns the Reports menu.
     */
    private JMenu buildReportsMenu() {
        MenuBuilder builder = new MenuBuilder(REPORTS, REPORTS.charAt(0));
        builder.add(ActionManager.get(Actions.REPORT_CATALOG_ID));
        builder.add(ActionManager.get(Actions.REPORT_CATALOG_BY_GENRES_ID));//AZ
        builder.add(ActionManager.get(Actions.REPORT_ALBUMS_FOR_ARTIST_ID));//AZ
        builder.add(ActionManager.get(Actions.REPORT_NOCOVERART_ID));
        builder.add(ActionManager.get(Actions.REPORT_BITRATE_ID));
        builder.add(ActionManager.get(Actions.REPORT_GENRES_ID));//AZ
        builder.addSeparator();
        builder.add(ActionManager.get(Actions.CATALOG_EXPORT_ID));
        return builder.getMenu();
    }

    /**
     * Builds and returns the Help menu.
     */
    private JMenu buildHelpMenu() {
        MenuBuilder builder = new MenuBuilder(HELP, HELP.charAt(0));
        builder.add(ActionManager.get(Actions.HELP_CONTENTS_ID));
        builder.addSeparator();
        builder.add(ActionManager.get(Actions.HELP_DONATE_ID));
        builder.add(ActionManager.get(Actions.HELP_FORUMS_ID));
        builder.add(ActionManager.get(Actions.HELP_CONTACT_ID));
        builder.add(ActionManager.get(Actions.HELP_TIP_OF_THE_DAY_ID));
        if (!OSXApplicationMenu.isRegisteredAbout()) {
            builder.addSeparator();
            builder.add(ActionManager.get(Actions.HELP_ABOUT_DIALOG_ID));
        }
        return builder.getMenu();
    }

    /**
     * Builds and returns the Component menu.
     */
    private JMenu buildToolsMenu() {
        MenuBuilder builder = new MenuBuilder(TOOLS, TOOLS.charAt(0));
        if (!OSXApplicationMenu.isRegisteredPreferences()) {
            builder.add(ActionManager.get(Actions.PREFERENCES_ID));
        }
        builder.add(ActionManager.get(Actions.TOOL_BACKUP_ID));
        builder.add(ActionManager.get(Actions.PREFERENCES_IMPORT_ID));
        builder.add(ActionManager.get(Actions.PREFERENCES_EXPORT_ID));
        builder.add(ActionManager.get(Actions.TOOL_DIFFERENCE_ID));
        builder.add(ActionManager.get(Actions.TOOL_CHECK_GENRES_ID)); //AZ
        builder.add(ActionManager.get(Actions.TOOL_LOCATION_ID));
        builder.add(ActionManager.get(Actions.DB_XML_EXPORT_ID));//AZ
        builder.add(ActionManager.get(Actions.DB_XML_IMPORT_ID));//AZ 
        builder.addSeparator();
        builder.add(ActionManager.get(Actions.TOOL_STATISTICS_ID));
        builder.add(ActionManager.get(Actions.TOOL_MEMORY_ID));
        return builder.getMenu();
    }
    
    /**
     * Builds and returns the Language menu.
     */
    private JMenu buildLanguageMenu() {
        MenuBuilder builder = new MenuBuilder(LANGUAGE, LANGUAGE.charAt(0));
        builder.add(ActionManager.get(Actions.LANG_ENGLISH_ID));
        builder.add(ActionManager.get(Actions.LANG_SPANISH_ID));
        builder.add(ActionManager.get(Actions.LANG_GERMAN_ID));
        builder.add(ActionManager.get(Actions.LANG_INDONESIAN_ID));
        builder.add(ActionManager.get(Actions.LANG_KOREAN_ID));
        builder.add(ActionManager.get(Actions.LANG_NORWEGIAN_ID));
        builder.add(ActionManager.get(Actions.LANG_DUTCH_ID));
        builder.add(ActionManager.get(Actions.LANG_FRENCH_ID));
        builder.add(ActionManager.get(Actions.LANG_PORTUGEUSE_ID));
        builder.add(ActionManager.get(Actions.LANG_ITALIAN_ID));
        builder.add(ActionManager.get(Actions.LANG_SWEDISH_ID));
        builder.add(ActionManager.get(Actions.LANG_FINNISH_ID));
        builder.add(ActionManager.get(Actions.LANG_RUSSIAN_ID));
        builder.add(ActionManager.get(Actions.LANG_UKRAINIAN_ID));
        builder.add(ActionManager.get(Actions.LANG_CHINESE_ID));
        builder.add(ActionManager.get(Actions.LANG_HINDHI_ID));
        //builder.add(ActionManager.get(Actions.LANG_TAMIL_ID));
        //builder.add(ActionManager.get(Actions.LANG_TELEGU_ID));
        return builder.getMenu();
    }
    
     /**AZ
     * Builds and returns the Disclist Menu.
     * <p>
     * @param aComponent the panel that owns this menu
     * @param aList a JList that this menu item is acting on.
     */
    public static JPopupMenu buildDisclistPopupMenu(Component aComponent, Component aList) {
        final PopupMenuBuilder popup = new PopupMenuBuilder("Disclist");
        UIFMenuItem menuItem = null;
        menuItem = new UIFMenuItem(ActionManager.get(Actions.DISCLIST_MOVEUP_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.DISCLIST_MOVEDOWN_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.DISCLIST_REMOVE_DISC_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.DISCLIST_CLEAR_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.DISCLIST_GOTO_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aList);
        popup.add(menuItem);
          
        return popup.getPopupMenu();
    }

    /**AZ
     * Builds and returns the DiscExportableTable PopUp Menu.
     */
    public static JPopupMenu buildDiscExportableTablePopupMenu(Component aComponent) {
        final PopupMenuBuilder popup = new PopupMenuBuilder("DiscExportableTable");
        UIFMenuItem menuItem = null;
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYER_QUEUE_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.PLAYER_QUEUE_NEXT_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        menuItem = new UIFMenuItem(ActionManager.get(Actions.DISCLIST_QUEUE_ID));
        menuItem.putClientProperty(Resources.EDITOR_COMPONENT, aComponent);
        popup.add(menuItem);
        return popup.getPopupMenu();
    }
}