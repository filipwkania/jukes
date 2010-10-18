package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.util.Resizer;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uif.util.Worker;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.MessageUtil;

/**
 * Import database objects from XML-file
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ 2010
 */
public final class XMLImportDialog extends AbstractDialog {

   private static final Log LOG = LogFactory.getLog(SearchDialog.class);
   private JButton buttonCancel;
   private JButton buttonSearch;
   private JButton buttonStop;
   private JComponent buttonBar;
   private JProgressBar progressBar;
   private final Settings settings;
   private Worker worker;
   private final File file;
   private int count = 0;
   private int artistCount = 0;
   private int discCount = 0;
   private int progressCount = 0;
   private Artist artist;
   private Disc disc;
   private Track track;
   final Session s = HibernateUtil.getSession();

   /**
    * Constructs XMLImport dialog using the given owner.
    * @param owner the dialog's owner
    */
   public XMLImportDialog(Frame owner, Settings settings, File file) {
      super(owner);
      LOG.debug("XMLImport Dialog created.");
      this.settings = settings;
      this.settings.getDatabaseLocation();
      this.file = file;

      this.setPreferredSize(new Dimension(700, 300));
   }

   /*
    * (non-Javadoc)
    * @see com.jgoodies.swing.AbstractDialog#doCancel()
    */
   @Override
   public void doCancel() {
      LOG.debug("Cancel Pressed.");
      super.doCancel();
   }

   /**
    * Builds and answers the dialog's content.
    * @return the dialog's content with table pane and button bar
    */
   @Override
   protected JComponent buildContent() {
      JPanel content = new JPanel(new BorderLayout());
      JButton[] buttons = new JButton[3];
      // Create Cancel button
      buttonCancel = createCancelButton();
      buttonCancel.setText(Resources.getString("label.Close"));

      // Create an action with an icon
      Action search = new AbstractAction(Resources.getString("label.Start"), Resources.THREAD_START_ICON) {
         public void actionPerformed(ActionEvent evt) {
            doSearch();
         }
      };
      buttonSearch = new JButton(search);

      // Create an action with an icon
      Action stop = new AbstractAction(Resources.getString("label.Cancel"), Resources.THREAD_STOP_ICON) {
         public void actionPerformed(ActionEvent evt) {
            doStop();
         }
      };
      buttonStop = new JButton(stop);
      buttonStop.setEnabled(false);

      buttons[0] = buttonSearch;
      buttons[1] = buttonStop;
      buttons[2] = buttonCancel;
      buttonBar = ButtonBarFactory.buildCenteredBar(buttons);

      content.add(buildMainPanel(), BorderLayout.CENTER);
      content.add(buttonBar, BorderLayout.SOUTH);
      return content;
   }

   /**
    * Builds and returns the dialog's header.
    * @return the dialog's header component
    */
   @Override
   protected JComponent buildHeader() {
      final HeaderPanel header = new HeaderPanel(Resources.getString("label.XMLimport"), Resources
               .getString("label.XMLimportText"), Resources.XML_EXPORT_ICON);

      return header;
   }

   /**
    * Builds and returns the dialog's pane.
    * @return the dialog's pane component
    */
   protected JComponent buildMainPanel() {
      FormLayout layout = new FormLayout("fill:pref:grow", "p, 4px, p, 4px");
      PanelBuilder builder = new PanelBuilder(layout);
      builder.setDefaultDialogBorder();
      CellConstraints cc = new CellConstraints();
      builder.add(buildProgressPanel(), cc.xy(1, 1));
      return builder.getPanel();
   }

   /**
    * Builds the progress-bar panel.
    * <p>
    * @return the panel
    */
   private JComponent buildProgressPanel() {
      progressBar = new JProgressBar();
      progressBar.setIndeterminate(false);
      progressBar.setStringPainted(true);
      progressBar.setString(" ");

      final FormLayout layout = new FormLayout("right:max(14dlu;pref), 4dlu, p, fill:pref:grow", "p, 4px, p");
      final PanelBuilder builder = new PanelBuilder(layout);
      final CellConstraints cc = new CellConstraints();

      builder.addLabel(Resources.getString("label.Progress"), cc.xy(1, 1));
      builder.add(progressBar, cc.xyw(3, 1, 2));

      return builder.getPanel();
   }

   /**
    * Executes the search.
    */
   protected void doSearch() {
      LOG.debug("Importing...");

      // GuiUtil.setBusyCursor(this, true);
      buttonSearch.setEnabled(false);
      buttonCancel.setEnabled(false);
      buttonStop.setEnabled(true);

      /*
       * Invoking start() on the SwingWorker causes a new Thread to be created
       * that will call construct(), and then finished(). Note that finished()
       * is called even if the worker is interrupted because we catch the
       * InterruptedException in doWork().
       */
      worker = new Worker() {
         @Override
         public Object construct() {
            doWork();
            return null;
         }

         @Override
         public void finished() {
            threadFinished(get());
         }
      };
      worker.start();
   }

   /**
    * Stops the search.
    */
   protected void doStop() {
      GuiUtil.setBusyCursor(this, false);
      worker.interrupt();
      buttonSearch.setEnabled(true);
      buttonCancel.setEnabled(true);
      buttonStop.setEnabled(false);
   }

   /**
    * Resizes the given component to give it a quadratic aspect ratio.
    * @param component the component to be resized
    */
   @Override
   protected void resizeHook(JComponent component) {
      Resizer.ONE2ONE.resizeDialogContent(component);
   }

   /**
    * When the thread is finished this method is called.
    * <p>
    * @param result the Object return from the doWork thread.
    */
   protected void threadFinished(Object result) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Thread Finished");
      }
      buttonSearch.setEnabled(true);
      buttonCancel.setEnabled(true);
      buttonStop.setEnabled(false);
      GuiUtil.setBusyCursor(this, false);

      // refresh the tree
      ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
      LOG.info("XML Import: Finished");
      LOG.info("XML Import: Total Discs Processed " + count);
      LOG.info("XML Import: Total New Artists Imported " + artistCount);
      LOG.info("XML Import: Total New Discs Imported " + discCount);
   }

   /**
    * This method represents the application code that we'd like to run on a
    * separate thread.
    */
   private void doWork() {
      final String LINE_BREAK = "\n";
      final String ERROR_READING_FILE = Resources.getString("label.Errorreadingfile");
      artistCount = 0;
      discCount = 0;
      count = 0;
      progressCount = 0;

      LOG.info("XML Import: Starting");
      // Set Progress Bar
      progressBar.setMaximum(100);
      progressBar.setValue(0);
      try {
         // open file for import, create XML-parser
         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();

         InputStream inputStream = new FileInputStream(file);
         Reader reader = new InputStreamReader(inputStream, "UTF-8");

         InputSource inputSource = new InputSource(reader);
         inputSource.setEncoding("UTF-8");

         DefaultHandler handler = XMLHandler();
         saxParser.parse(inputSource, handler);
         progressBar.setValue(100);
      } catch (IOException ex) {
         final String message = ERROR_READING_FILE + LINE_BREAK + ex.getMessage(); // AZ
         LOG.error(ERROR_READING_FILE + LINE_BREAK + ex.getMessage(), ex);
         MessageUtil.showError(null, message);
      } catch (SAXException ex) {
         final String message = "SAXException: " + ex.getMessage();
         // If InterruptedException then do nothing
         if (!(ex.getMessage().equals("InterruptedException"))) {
            LOG.error("SAXException: " + ex.getMessage() + LINE_BREAK, ex);
            MessageUtil.showError(null, message);
         }
      } catch (Exception ex) {
         final String message = ERROR_READING_FILE + ": " + ex.getMessage(); // AZ
         LOG.error("Unexpected error reading file.", ex);
         MessageUtil.showError(null, message);
      }
   }

   public DefaultHandler XMLHandler() {
      DefaultHandler handler = new DefaultHandler() {
         boolean toPersist = false;
         boolean toPersistDisc = false;
         ArrayList<String> discNames = new ArrayList();
         final ArrayList<Disc> discs = new ArrayList();
         StringBuffer buf = new StringBuffer();

         @Override
         public void startElement(String uri, String localName, String qName, Attributes attributes)
                  throws SAXException {
            try {
               if (Thread.interrupted()) {// Thread interrupted by user
                  LOG.info("XML Import: Cancelled by User");
                  throw new InterruptedException();
               }
               buf.setLength(0);// Clear buffer

               if (qName.equalsIgnoreCase("ARTIST")) {
                  artist = new Artist();
               }

               if (qName.equalsIgnoreCase("DISC")) {
                  disc = new Disc();
               }

               if (qName.equalsIgnoreCase("TRACK")) {
                  track = new Track();
               }

            } catch (InterruptedException ex) {
               throw new SAXException("InterruptedException");
            }
         }

         @Override
         public void endElement(String uri, String localName, String qName) throws SAXException {
            Artist foundArtist;
            String nodeString = new String(buf);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

            if (qName.equalsIgnoreCase("ARTIST")) {
               // commit changes
               if (toPersist) {
                  try {
                     HibernateUtil.beginTransaction();
                     HibernateDao.saveOrUpdate(artist);
                     HibernateUtil.commitTransaction();

                     // free memory
                     s.evict(artist);
                  } catch (Exception ex) {
                     final String message = "Error writing database: " + ex.getMessage();
                     LOG.error(message, ex);
                     throw new SAXException(message);
                  }
               }
               toPersist = false;
            }

            if (qName.equalsIgnoreCase("ArtistName")) {
               if (nodeString.length() > 100) {
                  nodeString = nodeString.substring(0, 99);
                  LOG.info("XML Import: Truncate String - " + nodeString);
               }
               artist.setName(nodeString);
               // find or create the artist
               try {
                  String resource = ResourceUtils.getString("hql.artist.find");
                  String hql = MessageFormat.format(resource, new Object[] { StringEscapeUtils.escapeSql(artist
                           .getName()) });
                  foundArtist = (Artist) HibernateDao.findUniqueByQuery(hql);

                  if (foundArtist == null) {
                     artistCount = artistCount + 1;
                     toPersist = true;
                     LOG.info("XML Import: New Artist - " + artist.getName());
                     discNames.clear();
                  } else {
                     artist = foundArtist;
                     LOG.info("XML Import: Artist exists already - " + artist.getName());
                     discs.addAll(artist.getDiscs());
                     discNames.clear();
                     for (final Iterator iter = discs.iterator(); iter.hasNext();) {
                        final Disc disc = (Disc) iter.next();
                        discNames.add(disc.getName());
                     }
                  }
               } catch (Exception ex) {
                  final String message = "Unexpected error occured performing import: " + ex.getMessage();
                  LOG.error(message, ex);
                  throw new SAXException(message);
               }
            }
            if (qName.equalsIgnoreCase("ArtistNotes")) {
               if (nodeString.length() > 500) {
                  nodeString = nodeString.substring(0, 499);
                  LOG.info("XML Import: Truncate String - " + nodeString);
               }
               artist.setNotes(nodeString);
            }
            if (qName.equalsIgnoreCase("ArtistCreatedUser")) {
               artist.setCreatedUser(nodeString);
            }
            if (qName.equalsIgnoreCase("ArtistModifiedUser")) {
               artist.setModifiedUser(nodeString);
            }
            if (qName.equalsIgnoreCase("ArtistCreatedDate")) {
               try {
                  Date createdDate = df.parse(nodeString);
                  artist.setCreatedDate(createdDate);
               } catch (ParseException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("ArtistModifiedDate")) {
               try {
                  Date modifiedDate = df.parse(nodeString);
                  artist.setModifiedDate(modifiedDate);
               } catch (ParseException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("DISC")) {
               count = count + 1;// Total number of processed discs
               progressCount = progressCount + 1;// counter for progress bar
               if (progressCount > 100) {// Re-set Progress Bar Counter
                  progressCount = progressCount - 100;
               }
               progressBar.setValue(progressCount);
               progressBar.setString(Integer.toString(count));
               if (toPersistDisc) {
                  artist.addDisc(disc);
               }
               toPersistDisc = false;
            }
            if (qName.equalsIgnoreCase("DiscName")) {
               if (nodeString.length() > 100) {
                  nodeString = nodeString.substring(0, 99);
                  LOG.info("XML Import: Truncate String - " + nodeString);
               }
               disc.setName(nodeString);
               // find or create the disc
               try {
                  if ((!discNames.contains(nodeString)) || discNames.isEmpty()) {
                     discCount = discCount + 1;
                     toPersist = true;
                     toPersistDisc = true;
                     LOG.info("XML Import: New Disc - " + disc.getName());
                  } else {
                     LOG.info("XML Import: Disc exists already - " + disc.getName());
                  }
               } catch (Exception ex) {
                  final String message = "Unexpected error occured performing import: " + ex.getMessage();
                  LOG.error(message, ex);
                  throw new SAXException(message);
               }
            }
            if (qName.equalsIgnoreCase("DiscYear")) {
               disc.setYear(nodeString);
            }
            if (qName.equalsIgnoreCase("DiscGenre")) {
               if (nodeString.length() > 100) {
                  nodeString = nodeString.substring(0, 99);
                  LOG.info("XML Import: Truncate String - " + nodeString);
               }
               disc.setGenre(nodeString);
            }
            if (qName.equalsIgnoreCase("DiscBitrate")) {
               try {
                  Long LongBitrate = Long.parseLong(nodeString);
                  disc.setBitrate(LongBitrate);
               } catch (NumberFormatException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("DiscCoverUrl")) {
               disc.setCoverUrl(nodeString);
            }
            if (qName.equalsIgnoreCase("DiscCoverSize")) {
               try {
                  Long LongCoverSize = Long.parseLong(nodeString);
                  disc.setCoverSize(LongCoverSize);
               } catch (NumberFormatException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("DiscDurationTime")) {
               disc.setDurationTime(nodeString);
            }
            if (qName.equalsIgnoreCase("DiscDuration")) {
               try {
                  Long LongDuraton = Long.parseLong(nodeString);
                  disc.setDuration(LongDuraton);
               } catch (NumberFormatException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("DiscLocation")) {
               disc.setLocation(nodeString);
            }
            if (qName.equalsIgnoreCase("DiscNotes")) {
               if (nodeString.length() > 500) {
                  nodeString = nodeString.substring(0, 499);
                  LOG.info("XML Import: Truncate String - " + nodeString);
               }
               disc.setNotes(nodeString);
            }
            if (qName.equalsIgnoreCase("DiscCreatedUser")) {
               disc.setCreatedUser(nodeString);
            }
            if (qName.equalsIgnoreCase("DiscModifiedUser")) {
               disc.setModifiedUser(nodeString);
            }
            if (qName.equalsIgnoreCase("DiscCreatedDate")) {
               try {
                  Date createdDate = df.parse(nodeString);
                  disc.setCreatedDate(createdDate);
               } catch (ParseException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("DiscModifiedDate")) {
               try {
                  Date modifiedDate = df.parse(nodeString);
                  disc.setModifiedDate(modifiedDate);
               } catch (ParseException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("TRACK")) {
               if (toPersistDisc) {
                  disc.addTrack(track);
               }
            }
            if (qName.equalsIgnoreCase("TrackName")) {
               if (nodeString.length() > 100) {
                  nodeString = nodeString.substring(0, 99);
                  LOG.info("XML Import: Truncate String - " + nodeString);
               }
               track.setName(nodeString);
            }
            if (qName.equalsIgnoreCase("TrackNumber")) {
               track.setTrackNumber(nodeString);
            }
            if (qName.equalsIgnoreCase("TrackBitrate")) {
               try {
                  Long LongBitrate = Long.parseLong(nodeString);
                  track.setBitrate(LongBitrate);
               } catch (NumberFormatException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("TrackUrl")) {
               track.setTrackUrl(nodeString);
            }
            if (qName.equalsIgnoreCase("TrackSize")) {
               try {
                  Long LongTrackSize = Long.parseLong(nodeString);
                  track.setTrackSize(LongTrackSize);
               } catch (NumberFormatException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("TrackDurationTime")) {
               track.setDurationTime(nodeString);
            }
            if (qName.equalsIgnoreCase("TrackDuration")) {
               try {
                  Long LongDuraton = Long.parseLong(nodeString);
                  track.setDuration(LongDuraton);
               } catch (NumberFormatException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("TrackComment")) {
               if (nodeString.length() > 254) {
                  nodeString = nodeString.substring(0, 253);
                  LOG.info("XML Import: Truncate String - " + nodeString);
               }
               track.setComment(nodeString);
            }
            if (qName.equalsIgnoreCase("TrackCreatedUser")) {
               track.setCreatedUser(nodeString);
            }
            if (qName.equalsIgnoreCase("TrackModifiedUser")) {
               track.setModifiedUser(nodeString);
            }
            if (qName.equalsIgnoreCase("TrackCreatedDate")) {
               try {
                  Date createdDate = df.parse(nodeString);
                  track.setCreatedDate(createdDate);
               } catch (ParseException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
            if (qName.equalsIgnoreCase("TrackModifiedDate")) {
               try {
                  Date modifiedDate = df.parse(nodeString);
                  track.setModifiedDate(modifiedDate);
               } catch (ParseException e) {
                  LOG.warn("Import XML: " + e.getMessage());
               }
            }
         }

         @Override
         public void characters(char ch[], int start, int length) throws SAXException {
            buf.append(ch, start, length);
         }

      };
      return handler;
   }

}