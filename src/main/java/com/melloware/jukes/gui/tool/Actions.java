package com.melloware.jukes.gui.tool;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uifextras.help.HelpBroker;
import com.melloware.jukes.db.orm.Catalog;
import com.melloware.jukes.gui.Jukes;

/**
 * Provides all UI <code>Actions</code> and their IDs. Therefore it declares
 * static fields for action ids and implementations of the <code>Action</code>
 * interface. These actions are registered to the <code>ActionManager</code>.
 * <p>
 * This class demos three different styles to implement <code>Action</code>;
 * they differ in readability, number of classes loaded, and the ability to
 * write more or less large action bodies, while preserving good code
 * formatting:
 * <ol>
 * <li>anonymous classes: This approach provides a literally compact way to
 * write down a bunch of action implementations, which will work better, if you
 * have tiny method bodys. In our demo, all method bodies just delegate to the
 * application controller. Does not provide human readable name in case of an
 * error and so, may be more difficult to debug. Requires a class for every
 * action.
 * <li>nested top-level classes: This approach requires a little bit more
 * writing than the first approach. Writing larger method bodies does not affect
 * the registering method. The extra class names help you find bugs. Requires a
 * class for every action.
 * <li>instances of a single dispatching class: This approach leads to good
 * readability in the registering method and requires only one class to load for
 * all actions. Works better with small action bodies. May lead to runtime
 * errors, if you haven't defined a dispatch for an action ID.
 * </ol>
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * AZ Development 2010
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * @see ActionManager
 * @see Action
 * @see AbstractAction
 * @see MainController
 */
public final class Actions {

   private static final Log LOG = LogFactory.getLog(Actions.class);

   public static final String LANG_ENGLISH_ID = "language.english";
   public static final String LANG_FRENCH_ID = "language.french";
   public static final String LANG_GERMAN_ID = "language.german";
   public static final String LANG_SPANISH_ID = "language.spanish";
   public static final String LANG_PORTUGEUSE_ID = "language.portuguese";
   public static final String LANG_NORWEGIAN_ID = "language.norwegian";
   public static final String LANG_DUTCH_ID = "language.dutch";
   public static final String LANG_ITALIAN_ID = "language.italian";
   public static final String LANG_SWEDISH_ID = "language.swedish";
   public static final String LANG_FINNISH_ID = "language.finnish";
   public static final String LANG_RUSSIAN_ID = "language.russian";
   public static final String LANG_UKRAINIAN_ID = "language.ukrainian";
   public static final String LANG_CHINESE_ID = "language.chinese";
   public static final String LANG_KOREAN_ID = "language.korean";
   public static final String LANG_TAMIL_ID = "language.tamil";
   public static final String LANG_TELEGU_ID = "language.telegu";
   public static final String LANG_HINDHI_ID = "language.hindhi";
   public static final String LANG_INDONESIAN_ID = "language.indonesian";
   public static final String HELP_CONTENTS_ID = "openHelpContents";
   public static final String HELP_TIP_OF_THE_DAY_ID = "openTipOfTheDay";
   public static final String HELP_ABOUT_DIALOG_ID = "openAboutDialog";
   public static final String HELP_FORUMS_ID = "help.forums";
   public static final String HELP_DONATE_ID = "help.donate";
   public static final String HELP_CONTACT_ID = "help.contact";
   public static final String TITLECASE_ID = "titlecase";
   public static final String FILE_RENAME_ID = "file.rename";
   public static final String CATALOG_EXPORT_ID = "export.list";
   public static final String DIRECTORY_ID = "directorychooser";
   public static final String FILE_CHOOSER_ID = "filechooser";
   public static final String COMMIT_ID = "commit";
   public static final String ROLLBACK_ID = "rollback";
   public static final String UNLOCK_ID = "unlock";
   public static final String REFRESH_ID = "refresh";
   public static final String PREFERENCES_ID = "preferences";
   public static final String PREFERENCES_IMPORT_ID = "preferences.import";
   public static final String PREFERENCES_EXPORT_ID = "preferences.export";
   public static final String CONNECT_ID = "connect";
   public static final String DELETE_ID = "delete";
   public static final String DISC_COVER_ID = "disc.cover";
   public static final String DISC_ADD_ID = "disc.add";
   public static final String DISC_ADD_TITLECASE_ID = "disc.add.titlecase";
   public static final String DISC_ADD_COMMENTS_ID = "disc.add.comments";
   public static final String DISC_ADD_RESET_NUMBERS_ID = "disc.add.resettracks";
   public static final String DISC_ADD_RESET_FROM_FILENAME_ID = "disc.add.resetfromfilename";
   public static final String DISC_FINDER_ID = "disc.finder";
   public static final String DISC_REMOVER_ID = "disc.remover";
   public static final String APP_HIDE_ID = "application.hide";
   public static final String APP_SHOW_ID = "application.show";
   public static final String TOOL_STATISTICS_ID = "statistics";
   public static final String TOOL_MEMORY_ID = "memory";
   public static final String TOOL_DIFFERENCE_ID = "difference.tool";
   public static final String TOOL_LOCATION_ID = "location.tool";
   public static final String TOOL_BACKUP_ID = "backup.tool";
   public static final String TOOL_CHECK_GENRES_ID = "genres.tool"; //AZ
   public static final String TRACK_PLAY_IMMEDIATE_ID = "play.immediate";
   public static final String PLAYER_QUEUE_ID = "player.queue";
   public static final String PLAYER_QUEUE_NEXT_ID = "player.queuenext";
   public static final String PLAYER_PLAY_ID = "player.play";
   public static final String PLAYER_PAUSE_ID = "player.pause";
   public static final String PLAYER_STOP_ID = "player.stop";
   public static final String PLAYER_PREVIOUS_ID = "player.previous";
   public static final String PLAYER_NEXT_ID = "player.next";
   public static final String FILTER_SHOW_ID = "filter.show";
   public static final String FILTER_APPLY_ID = "filter.apply";
   public static final String FILTER_CLEAR_ID = "filter.clear";
   public static final String FILTER_CLOSE_ID = "filter.close";
   public static final String PLAYLIST_CLOSE_ID = "playlist.close";
   public static final String PLAYLIST_SHOW_ID = "playlist.show";
   public static final String PLAYLIST_TOGGLE_ID = "playlist.toggle";
   public static final String PLAYLIST_SHUFFLE_LIST_ID = "playlist.shufflelist";
   public static final String PLAYLIST_SHUFFLE_CATALOG_ID = "playlist.shufflecatalog";
   public static final String PLAYLIST_MOVEUP_ID = "playlist.moveup";
   public static final String PLAYLIST_MOVEDOWN_ID = "playlist.movedown";
   public static final String PLAYLIST_MOVEOVER_ID = "playlist.moveover";
   public static final String PLAYLIST_REMOVE_TRACK_ID = "playlist.removetrack";
   public static final String PLAYLIST_CLEAR_ID = "playlist.clear";
   public static final String PLAYLIST_GOTO_ID = "playlist.goto";
   public static final String PLAYLIST_SAVE_ID = "playlist.save";
   public static final String PLAYLIST_LOAD_ID = "playlist.load";
   public static final String SEARCH_ID = "search";
   public static final String REPORT_CATALOG_ID = "report.catalog";
   public static final String REPORT_CATALOG_BY_GENRES_ID = "report.catalogbygenres"; //AZ
   public static final String REPORT_NOCOVERART_ID = "report.nocoverart";
   public static final String REPORT_BITRATE_ID = "report.bitrate";
   public static final String REPORT_ALBUMS_FOR_ARTIST_ID = "report.albumsforartist";//AZ
   public static final String REPORT_GENRES_ID = "report.genres"; //AZ
   public static final String EXIT_ID = "exit";
   public static final String DISCLIST_CLOSE_ID = "disclist.close";//AZ
   public static final String DISCLIST_SHOW_ID = "disclist.show";//AZ
   public static final String DISCLIST_MOVEUP_ID = "disclist.moveup";//AZ
   public static final String DISCLIST_MOVEDOWN_ID = "disclist.movedown";//AZ
   public static final String DISCLIST_REMOVE_DISC_ID = "disclist.removedisc";//AZ
   public static final String DISCLIST_CLEAR_ID = "disclist.clear";//AZ
   public static final String DISCLIST_GOTO_ID = "disclist.goto";//AZ
   public static final String DISCLIST_SAVE_ID = "disclist.save";//AZ
   public static final String DISCLIST_LOAD_ID = "disclist.load";//AZ
   public static final String DISCLIST_QUEUE_ID = "disclist.queue";//AZ
   public static final String DISCLIST_SET_CURRENT_ID = "disclist.setcurrent";//AZ
   public static final String TRACK_ADD_ID = "track.add"; //AZ
   public static final String FREE_DB_ID = "free.db"; //AZ
   public static final String DB_XML_EXPORT_ID = "db.xml.export"; //AZ
   public static final String DB_XML_IMPORT_ID = "db.xml.import"; //AZ
   

   /**
    * Refers to the controller that is used to forward all action behavior to.
    * @see #getController()
    */
   private final MainController controller;

   /**
    * Initializes the actions used in this application. Registers all actions
    * with the <code>ActionManager</code> and observes changes in the main
    * module's selection and project to update the enablement of some actions.
    * @param mainModule provides bound properties for the selection and project
    * @param controller used to forward all action behavior
    */
   private Actions(MainModule mainModule, MainController controller) {
      LOG.debug("Actions created.");
      this.controller = controller;
      // set the locale
      ActionManager.setBundle(ResourceBundle.getBundle("Action", new Locale(MainModule.SETTINGS.getLocale()),
               Jukes.class.getClassLoader()));
      registerActions();
      mainModule.addPropertyChangeListener(new ModuleChangeHandler());

      updateCatalogActionEnablement(null);
   }

   /**
    * Initializes the actions used in this application. Registers all actions
    * with the <code>ActionManager</code> and observes changes in the main
    * module's selection and project to update the enablement of some actions.
    * @param mainModule provides bound properties for the selection and project
    * @param controller used to forward all action behavior
    */
   public static void initializeFor(MainModule mainModule, MainController controller) {
      new Actions(mainModule, controller);
   }

   public MainController getController() {
      return controller;
   }

   /**
    * Registers <code>Action</code>s at the <code>ActionManager</code>
    * using three different styles for demoing purposes, see class comment.
    */
   private void registerActions() {
      registerActionsViaAnonymousClasses();
   }

   /**
    * Registers actions in the <code>ActionManager</code> using a bunch of
    * anonymous classes.
    */
   private void registerActionsViaAnonymousClasses() {
      ActionManager.register(LANG_ENGLISH_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "en");
         }
      });
      ActionManager.register(LANG_FRENCH_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "fr");
         }
      });
      ActionManager.register(LANG_GERMAN_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "de");
         }
      });
      ActionManager.register(LANG_SPANISH_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "es");
         }
      });
      ActionManager.register(LANG_PORTUGEUSE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "pt");
         }
      });
      ActionManager.register(LANG_NORWEGIAN_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "no");
         }
      });
      ActionManager.register(LANG_DUTCH_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "nl");
         }
      });
      ActionManager.register(LANG_ITALIAN_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "it");
         }
      });
      ActionManager.register(LANG_SWEDISH_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "sv");
         }
      });
      ActionManager.register(LANG_FINNISH_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "fi");
         }
      });
      ActionManager.register(LANG_RUSSIAN_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "ru");
         }
      });
      ActionManager.register(LANG_UKRAINIAN_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "uk");
         }
      });
      ActionManager.register(LANG_CHINESE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "zh");
         }
      });
      ActionManager.register(LANG_KOREAN_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "ko");
         }
      });
      ActionManager.register(LANG_TAMIL_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "ta");
         }
      });
      ActionManager.register(LANG_TELEGU_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "te");
         }
      });
      ActionManager.register(LANG_HINDHI_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "hi");
         }
      });
      ActionManager.register(LANG_INDONESIAN_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().language(event, "in");
         }
      });
      ActionManager.register(PREFERENCES_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().preferences();
         }
      });
      ActionManager.register(PREFERENCES_IMPORT_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().preferencesImport(event);
         }
      });
      ActionManager.register(PREFERENCES_EXPORT_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().preferencesExport(event);
         }
      });
      ActionManager.register(FILTER_APPLY_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().filter(event);
         }
      });
      ActionManager.register(PLAYLIST_TOGGLE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistToggle(event);
         }
      });
      ActionManager.register(PLAYLIST_SHUFFLE_LIST_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistShuffleList(event);
         }
      });
      ActionManager.register(PLAYLIST_SHUFFLE_CATALOG_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistShuffleCatalog(event);
         }
      });
      ActionManager.register(PLAYLIST_MOVEUP_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistMoveUp(event);
         }
      });
      ActionManager.register(PLAYLIST_REMOVE_TRACK_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistRemoveTracks(event);
         }
      });
      ActionManager.register(PLAYLIST_CLEAR_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistClear(event);
         }
      });
      ActionManager.register(PLAYLIST_GOTO_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistGoto(event);
         }
      });
      ActionManager.register(PLAYLIST_SAVE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistSave(event);
         }
      });
      ActionManager.register(PLAYLIST_LOAD_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistLoad(event);
         }
      });
      ActionManager.register(PLAYLIST_MOVEDOWN_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistMoveDown(event);
         }
      });
      ActionManager.register(PLAYLIST_MOVEOVER_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistMoveOver(event);
         }
      });
      ActionManager.register(FILTER_SHOW_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().filterDisplay(event);
         }
      });
      ActionManager.register(FILTER_CLEAR_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().filterClear(event);
         }
      });
      ActionManager.register(SEARCH_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().search(event);
         }
      });
      ActionManager.register(TRACK_PLAY_IMMEDIATE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playImmediately(event);
         }
      });
      ActionManager.register(PLAYER_QUEUE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().queue(event);
         }
      });
      ActionManager.register(PLAYER_QUEUE_NEXT_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().queueNext(event);
         }
      });
      ActionManager.register(PLAYER_PLAY_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playerPlay(event);
         }
      }).setEnabled(false);
      ActionManager.register(PLAYER_PAUSE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playerPause(event);
         }
      }).setEnabled(false);
      ActionManager.register(PLAYER_STOP_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playerStop(event);
         }
      }).setEnabled(false);
      ActionManager.register(PLAYER_NEXT_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playerNext(event);
         }
      }).setEnabled(false);
      ActionManager.register(PLAYER_PREVIOUS_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playerPrevious(event);
         }
      }).setEnabled(false);
      ActionManager.register(APP_SHOW_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().showMainWindow();
         }
      }).setEnabled(false);
      ActionManager.register(APP_HIDE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().hideMainWindow();
         }
      }).setEnabled(true);
      ActionManager.register(EXIT_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().aboutToExitApplication();
         }
      });
      ActionManager.register(PLAYLIST_CLOSE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistClose(event);
         }
      });
      ActionManager.register(PLAYLIST_SHOW_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().playlistDisplay(event);
         }
      });
      ActionManager.register(FILTER_CLOSE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().filterClose(event);
         }
      });
      ActionManager.register(TITLECASE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().titleCase(event);
         }
      });
      ActionManager.register(FILE_RENAME_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().fileRename(event);
         }
      });
      ActionManager.register(DIRECTORY_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().chooseDirectory(event);
         }
      });
      ActionManager.register(FILE_CHOOSER_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().chooseFile(event);
         }
      });
      ActionManager.register(COMMIT_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().commit(event);
         }
      });
      ActionManager.register(ROLLBACK_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().rollback(event);
         }
      });
      ActionManager.register(UNLOCK_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().unlock(event);
         }
      });
      ActionManager.register(REFRESH_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().refresh(event);
         }
      });
      ActionManager.register(CONNECT_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().connect(event);
         }
      });
      ActionManager.register(DELETE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().delete(event);
         }
      });
      ActionManager.register(TOOL_STATISTICS_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().statistics(event);
         }
      });
      ActionManager.register(TOOL_DIFFERENCE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().differenceTool(event);
         }
      });
      ActionManager.register(TOOL_LOCATION_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().locationTool(event);
         }
      });
      ActionManager.register(TOOL_BACKUP_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().backupTool(event);
         }
      });
      //AZ
      ActionManager.register(TOOL_CHECK_GENRES_ID, new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
             getController().genresTool(event);
          }
       });
      ActionManager.register(CATALOG_EXPORT_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().exportCatalog(event);
         }
      });
      ActionManager.register(TOOL_MEMORY_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().memory(event);
         }
      });
      
      ActionManager.register(DISC_COVER_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().discCoverImage(event);
         }
      });
      ActionManager.register(DISC_ADD_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().discAdd(event);
         }
      });
      ActionManager.register(DISC_ADD_TITLECASE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().discAddTitleCase(event);
         }
      });
      ActionManager.register(DISC_ADD_COMMENTS_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().discAddComments(event);
         }
      });
      ActionManager.register(DISC_ADD_RESET_FROM_FILENAME_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().discAddResetFromFilename(event);
         }
      });
      ActionManager.register(DISC_ADD_RESET_NUMBERS_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().discAddResetTrackNumbers(event);
         }
      });
      ActionManager.register(DISC_FINDER_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().discFinder(event);
         }
      });
      ActionManager.register(DISC_REMOVER_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().discRemover(event);
         }
      });
      ActionManager.register(REPORT_CATALOG_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().reportCatalog(event);
         }
      });
      //AZ
      ActionManager.register(REPORT_CATALOG_BY_GENRES_ID, new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
             getController().reportCatalogByGenres(event);
          }
       });
      ActionManager.register(REPORT_NOCOVERART_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().reportNoCoverArt(event);
         }
      });
      ActionManager.register(REPORT_BITRATE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().reportBitrate(event);
         }
      });
      //AZ
      ActionManager.register(REPORT_ALBUMS_FOR_ARTIST_ID, new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
             getController().reportAlbumsForArtist(event);
          }
       });
      //AZ
      ActionManager.register(REPORT_GENRES_ID, new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
             getController().reportGenres(event);
          }
       });
      ActionManager.register(HELP_CONTACT_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().contactUs(event);
         }
      });
      ActionManager.register(HELP_CONTENTS_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            HelpBroker.openDefault();
         }
      });
      ActionManager.register(HELP_TIP_OF_THE_DAY_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().openTipOfTheDayDialog();
         }
      });
      ActionManager.register(HELP_ABOUT_DIALOG_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().helpAbout();
         }
      });
      ActionManager.register(HELP_DONATE_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().donate();
         }
      });
      ActionManager.register(HELP_FORUMS_ID, new AbstractAction() {
         public void actionPerformed(ActionEvent event) {
            getController().forums();
         }
      });
      //AZ Development - DiscList actions
         ActionManager.register(DISCLIST_MOVEUP_ID, new AbstractAction() {
             public void actionPerformed(ActionEvent event) {
                getController().disclistMoveUp(event);
             }
          });
          ActionManager.register(DISCLIST_REMOVE_DISC_ID, new AbstractAction() {
             public void actionPerformed(ActionEvent event) {
                getController().disclistRemoveTracks(event);
             }
          });
          ActionManager.register(DISCLIST_CLEAR_ID, new AbstractAction() {
             public void actionPerformed(ActionEvent event) {
                getController().disclistClear(event);
             }
          });
          ActionManager.register(DISCLIST_GOTO_ID, new AbstractAction() {
             public void actionPerformed(ActionEvent event) {
                getController().disclistGoto(event);
             }
          });
          ActionManager.register(DISCLIST_SAVE_ID, new AbstractAction() {
             public void actionPerformed(ActionEvent event) {
                getController().disclistSave(event);
             }
          });
          ActionManager.register(DISCLIST_LOAD_ID, new AbstractAction() {
             public void actionPerformed(ActionEvent event) {
                getController().disclistLoad(event);
             }
          });
          ActionManager.register(DISCLIST_MOVEDOWN_ID, new AbstractAction() {
             public void actionPerformed(ActionEvent event) {
                getController().disclistMoveDown(event);
             }   
      });
          ActionManager.register(DISCLIST_CLOSE_ID, new AbstractAction() {
              public void actionPerformed(ActionEvent event) {
                 getController().disclistClose(event);
              }
           });
           ActionManager.register(DISCLIST_SHOW_ID, new AbstractAction() {
              public void actionPerformed(ActionEvent event) {
                 getController().disclistDisplay(event);
              }
           });
           ActionManager.register(DISCLIST_QUEUE_ID, new AbstractAction() {
               public void actionPerformed(ActionEvent event) {
                  getController().addToDisclist(event);
               }
            });   
           ActionManager.register(DISCLIST_SET_CURRENT_ID, new AbstractAction() {
               public void actionPerformed(ActionEvent event) {
                  getController().setCurrent(event);
               }
            }); 
           //AZ
           ActionManager.register(TRACK_ADD_ID, new AbstractAction() {
               public void actionPerformed(ActionEvent event) {
                  getController().trackAdd(event);
               }
            });
           
           //AZ
           ActionManager.register(FREE_DB_ID, new AbstractAction() {
               public void actionPerformed(ActionEvent event) {
                  getController().freeDB(event);
               }
            });
           
           //AZ
           ActionManager.register(DB_XML_EXPORT_ID, new AbstractAction() {
               public void actionPerformed(ActionEvent event) {
                  getController().xmlExport(event);
               }
            });
           
           //AZ
           ActionManager.register(DB_XML_IMPORT_ID, new AbstractAction() {
               public void actionPerformed(ActionEvent event) {
                  getController().xmlImport(event);
               }
            });
   }

   /**
    * Updates the enablement of actions that are related to the catalog state.
    * @param project the current catalog
    */
   private void updateCatalogActionEnablement(Catalog catalog) {
      boolean enabled = catalog != null;
      ActionManager.get(Actions.REFRESH_ID).setEnabled(enabled);
      ActionManager.get(Actions.DISC_ADD_ID).setEnabled(enabled);
      ActionManager.get(Actions.DISC_FINDER_ID).setEnabled(enabled);
      ActionManager.get(Actions.DISC_REMOVER_ID).setEnabled(enabled);
      ActionManager.get(Actions.TOOL_DIFFERENCE_ID).setEnabled(enabled);
      ActionManager.get(Actions.TOOL_STATISTICS_ID).setEnabled(enabled);
      ActionManager.get(Actions.CATALOG_EXPORT_ID).setEnabled(enabled);
      ActionManager.get(Actions.SEARCH_ID).setEnabled(enabled);
      ActionManager.get(Actions.TOOL_LOCATION_ID).setEnabled(enabled);
      ActionManager.get(Actions.TOOL_BACKUP_ID).setEnabled(enabled);
      ActionManager.get(Actions.TOOL_CHECK_GENRES_ID).setEnabled(enabled); //AZ
      ActionManager.get(Actions.FREE_DB_ID).setEnabled(enabled);//AZ
      ActionManager.get(Actions.DB_XML_EXPORT_ID).setEnabled(enabled);//AZ
      ActionManager.get(Actions.DB_XML_IMPORT_ID).setEnabled(enabled);//AZ
   }

   // Listens to changes in the navigation selection and update enablements.
   private class ModuleChangeHandler implements PropertyChangeListener {

      /**
       * The selection in the navigation tree has changed. Updates the add and
       * delete actions.
       * @param evt describes the property change
       */
      public void propertyChange(PropertyChangeEvent evt) {
         String propertyName = evt.getPropertyName();
         if (MainModule.PROPERTYNAME_CATALOG.equals(propertyName)) {
            updateCatalogActionEnablement((Catalog) evt.getNewValue());
         }
      }
   }

}