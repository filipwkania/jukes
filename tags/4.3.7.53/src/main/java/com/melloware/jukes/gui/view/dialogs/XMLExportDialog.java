package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.Resizer;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uif.util.Worker;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.MessageUtil;

/**
 * Export entire database into single XML-file
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ 2010
 */
public final class XMLExportDialog
    extends AbstractDialog {

    private static final Log LOG = LogFactory.getLog(SearchDialog.class);
    private JButton buttonCancel;
    private JButton buttonSearch;
    private JButton buttonStop;
    private JComponent buttonBar;
    private JProgressBar progressBar;
    private JCheckBox onlySelectedCheckBox;
    private final Settings settings;
    private Worker worker;
    private File file;

    /**
     * Constructs XMLExport dialog using the given owner.
     *
     * @param owner   the dialog's owner
     */
    public XMLExportDialog(Frame owner, Settings settings, File file) {
        super(owner);
        LOG.debug("XMLExport Dialog created.");
        this.settings = settings;
        this.settings.getDatabaseLocation();
        this.file = file;

        this.setPreferredSize(new Dimension(700, 300));
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doCancel()
     */
    public void doCancel() {
        LOG.debug("Cancel Pressed.");
        super.doCancel();
    }

    /**
     * Builds and answers the dialog's content.
     *
     * @return the dialog's content with table pane and button bar
     */
    protected JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        JButton[] buttons = new JButton[3];
        // Create Cancel button
        buttonCancel = createCancelButton();
        buttonCancel.setText(Resources.getString("label.Close")); //AZ
                
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
     *
     * @return the dialog's header component
     */
    protected JComponent buildHeader() {
        final HeaderPanel header = new HeaderPanel(Resources.getString("label.XMLexport"),
        		                                   Resources.getString("label.XMLexportText"),
        		                                   Resources.XML_EXPORT_ICON);

        return header;
    }

    /**
     * Builds and returns the dialog's pane.
     *
     * @return the dialog's  pane component
     */
    protected JComponent buildMainPanel() {
        FormLayout layout = new FormLayout("fill:pref:grow", "p, 4px, p, 4px");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.add(buildProgressPanel(), cc.xy(1, 1));
        onlySelectedCheckBox = new JCheckBox(Resources.getString("label.selectedonly"), false);
        builder.add(onlySelectedCheckBox, cc.xy(1, 3));
        return builder.getPanel();
    }

    /**
    * Executes the search.
    */
    protected void doSearch() {
        LOG.debug("Exporting...");
        
        GuiUtil.setBusyCursor(this, true);
        buttonSearch.setEnabled(false);
        buttonCancel.setEnabled(false);
        buttonStop.setEnabled(true);

        /* Invoking start() on the SwingWorker causes a new Thread
         * to be created that will call construct(), and then finished().  Note that finished() is called even if the
         * worker is interrupted because we catch the InterruptedException in doWork().
         */
        worker = new Worker() {
                public Object construct() {
                    doWork();
                    return null;
                }

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
        LOG.debug("Stopping...");
        GuiUtil.setBusyCursor(this, false);
        worker.interrupt();
        buttonSearch.setEnabled(true);
        buttonCancel.setEnabled(true);
        buttonStop.setEnabled(false);
    }

    /**
     * Resizes the given component to give it a quadratic aspect ratio.
     *
     * @param component   the component to be resized
     */
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
    }

    /**
    * This method represents the application code that we'd like to
    * run on a separate thread.
    */
    private void doWork() {
    	Artist artist;
        final String LINE_BREAK = "\n\n";
        final String ERROR_WRITING_FILE = Resources.getString("label.Errorwritingfile");
    	final String LINE_SEPARATOR = System.getProperty("line.separator");
        final Session s = HibernateUtil.getSession();
        boolean Stop = false;
        final Collection artists;

        try {
            // do the export
       	 	FileOutputStream outputStream = new FileOutputStream(file, false);
       	 	BufferedOutputStream bufferedStream = new BufferedOutputStream(outputStream);
       	 	OutputStreamWriter writer	= new OutputStreamWriter(bufferedStream, "UTF-8");
       	 	
            try {              
                //Initialize XML file
            	writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
           	 	writer.write(LINE_SEPARATOR);
           	 	writer.write("<!--This document was created with Jukes Database XML export utility-->");
           	 	writer.write(LINE_SEPARATOR);
           	 	writer.write("<Catalog>"+LINE_SEPARATOR);
           	 	writer.write("<artists>"+LINE_SEPARATOR);
           	 	writer.flush();
           	 	writer.close();
           	 	bufferedStream.close();
           	 	outputStream.close();
                //open file to output
                outputStream = new FileOutputStream(file, true);
                bufferedStream = new BufferedOutputStream(outputStream);
                writer	= new OutputStreamWriter(bufferedStream, "UTF-8");
        	    
                String aFilter = MainModule.SETTINGS.getFilter();
                if ((onlySelectedCheckBox.isSelected()) && (StringUtils.isNotBlank(aFilter))) {
                	// if a filter was applied then process only selected artists
            		String resource = ResourceUtils.getString("hql.filter.artist");	
                    final String hql = MessageFormat.format(resource, new Object[] { aFilter });
                    artists = HibernateDao.findByQuery(hql);              	
                } else {
                    // get a list of all artists in the system
                    artists = HibernateDao.findByQuery(Resources.getString("hql.artist.all"));
                }    

                int count = 0;
                // set the progress-bar bounds
                progressBar.setMaximum(artists.size());
                progressBar.setValue(0);
                progressBar.setIndeterminate(false);
                //Loop for Artists
                for (final Iterator iter = artists.iterator(); ((iter.hasNext())&&(!Stop));) {
                    artist = (Artist) iter.next();
                    count++;
                    writeArtistXML(artist, writer);
                    progressBar.setValue(count);
                    //free memory 
                    iter.remove();
                    s.evict(artist);
                    if (Thread.interrupted()) {
                    	Stop = true;
                    }
                }
                //Close xml-file
                writer.write("</artists>"+LINE_SEPARATOR);
                writer.write("</Catalog>");
        		writer.flush();
       		 	writer.close();
       		 	bufferedStream.close();
       		 	outputStream.close();
            } catch (RuntimeException ex) {
                final String message = "Unexpected error occured performing export.";
                LOG.debug(message, ex);
                throw new InterruptedException(message);
            }    
            if (Thread.interrupted()) {
                LOG.debug("Thread interrupted.");
                throw new InterruptedException();
            }

        } catch (InterruptedException e) {
        	//do nothing if interrupted
        } catch (IOException ex) {
    		final String message = ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage(); //AZ    	  
    		LOG.error(ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage(), ex);
            MessageUtil.showError(null, message);
    	} catch (Exception ex) {
    		final String message = ERROR_WRITING_FILE  + ex.getMessage(); //AZ 
    		LOG.error("Unexpected error writing file.", ex);
            MessageUtil.showError(null, message);
    	}
    }
    
    /**
     * 
     * Write artist data to XML-file 
     */     
    private void writeArtistXML(Artist artist, OutputStreamWriter writer){
        final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
    	final String LINE_SEPARATOR = System.getProperty("line.separator");
        final String LINE_BREAK = "\n\n";
        final String ERROR_WRITING_FILE = Resources.getString("label.Errorwritingfile");
        Disc disc;
        Track track;
        Collection  discs, tracks;
        String aFilter = MainModule.SETTINGS.getFilter();
        
        if ((onlySelectedCheckBox.isSelected()) && (StringUtils.isNotBlank(aFilter))) {
        	// if a filter was applied then process only selected discs
    		String resource = ResourceUtils.getString("hql.filter.disc");	
    		final String hql = MessageFormat.format(resource, new Object[] { artist.getId(), aFilter });
            discs = HibernateDao.findByQuery(hql);
        } else {
        	// else just get all discs for this artist
            discs = artist.getDiscs();
        }

        try {
        writer.write("  <artist>"+LINE_SEPARATOR);
        writer.write(CreateXmlNode(2,"ArtistName", escapeXml(artist.getName()), true, LINE_SEPARATOR));
        writer.write(CreateXmlNode(2,"ArtistCreatedUser", escapeXml(artist.getCreatedUser()), true, LINE_SEPARATOR));
        writer.write(CreateXmlNode(2,"ArtistModifiedUser", escapeXml(artist.getModifiedUser()), true, LINE_SEPARATOR));
        writer.write(CreateXmlNode(2,"ArtistCreatedDate", escapeXml(artist.getCreatedDate().toString()), true, LINE_SEPARATOR));
        writer.write(CreateXmlNode(2,"ArtistModifiedDate", escapeXml(artist.getModifiedDate().toString()), true, LINE_SEPARATOR));
        if (!(artist.getNotes()==null)) {
            writer.write(CreateXmlNode(2,"ArtistNotes", escapeXml(artist.getNotes()), true, LINE_SEPARATOR));
           }
        if (discs.size()>0){  //Loop for discs
            writer.write("  <discs>" + LINE_SEPARATOR);
            for (Iterator itr = discs.iterator(); itr.hasNext();) {
                disc = (Disc)itr.next();
                writer.write("    <disc>" + LINE_SEPARATOR);
                
                writer.write(CreateXmlNode(3,"DiscName", escapeXml(disc.getName()), true, LINE_SEPARATOR));
                writer.write(CreateXmlNode(3,"DiscYear", escapeXml(disc.getYear()), true, LINE_SEPARATOR));
                writer.write(CreateXmlNode(3,"DiscGenre", escapeXml(disc.getGenre()), true, LINE_SEPARATOR));
                writer.write(CreateXmlNode(3,"DiscBitrate", escapeXml(disc.getBitrate().toString()), true, LINE_SEPARATOR));
                if (!(disc.getCoverUrl()==null)){
                writer.write(CreateXmlNode(3,"DiscCoverUrl", escapeXml(disc.getCoverUrl()), true, LINE_SEPARATOR));
                }
                if (!(disc.getCoverUrl()==null)){
                    writer.write(CreateXmlNode(3,"DiscCoverSize", escapeXml(String.valueOf(disc.getCoverSize())), true, LINE_SEPARATOR));
                    }
                writer.write(CreateXmlNode(3,"DiscCreatedUser", escapeXml(disc.getCreatedUser()), true, LINE_SEPARATOR));
                writer.write(CreateXmlNode(3,"DiscModifiedUser", escapeXml(disc.getModifiedUser()), true, LINE_SEPARATOR));
                writer.write(CreateXmlNode(3,"DiscDurationTime", escapeXml(disc.getDurationTime()), true, LINE_SEPARATOR));
                writer.write(CreateXmlNode(3,"DiscDuration", escapeXml(String.valueOf(disc.getDuration())), true, LINE_SEPARATOR));
                writer.write(CreateXmlNode(3,"DiscLocation", escapeXml(disc.getLocation()), true, LINE_SEPARATOR));
                if (!(disc.getNotes()==null)){
                   writer.write(CreateXmlNode(3,"DiscNotes", escapeXml(disc.getNotes()), true, LINE_SEPARATOR));
                }
                writer.write(CreateXmlNode(3,"DiscCreatedDate", escapeXml(disc.getCreatedDate().toString()), true, LINE_SEPARATOR));
                writer.write(CreateXmlNode(3,"DiscModifiedDate", escapeXml(disc.getModifiedDate().toString()), true, LINE_SEPARATOR));
                tracks = disc.getTracks();
                if (tracks.size()>0) { //Loop for tracks
                    writer.write("    <tracks>" + LINE_SEPARATOR);                     
                    for (Iterator itera = tracks.iterator(); itera.hasNext();) {
                        track = (Track)itera.next();
                        writer.write("      <track>" + LINE_SEPARATOR);
                        writer.write(CreateXmlNode(4,"TrackName", escapeXml(track.getName()), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackNumber", escapeXml(track.getTrackNumber()), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackUrl", escapeXml(track.getTrackUrl()), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackBitrate", escapeXml(track.getBitrate().toString()), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackDurationTime", escapeXml(track.getDurationTime()), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackDuration", escapeXml(String.valueOf(track.getDuration())), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackModifiedUser", escapeXml(track.getModifiedUser()), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackCreatedUser", escapeXml(track.getCreatedUser()), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackCreatedDate", escapeXml(track.getCreatedDate().toString()), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackModifiedDate", escapeXml(track.getModifiedDate().toString()), true, LINE_SEPARATOR));
                        writer.write(CreateXmlNode(4,"TrackSize", escapeXml(String.valueOf(track.getTrackSize())), true, LINE_SEPARATOR));
                        if (!(track.getComment()==null)) {
                            writer.write(CreateXmlNode(4,"TrackComment", escapeXml(track.getComment()), true, LINE_SEPARATOR));
                            }
                        writer.write("      </track>" + LINE_SEPARATOR);
                    }
                    writer.write("    </tracks>" + LINE_SEPARATOR);
                }           
                writer.write("    </disc>" + LINE_SEPARATOR);
            }  
            writer.write("  </discs>" + LINE_SEPARATOR);
        }
        writer.write("  </artist>" + LINE_SEPARATOR);   	
        }  catch (RuntimeException ex) {
            final String message = "Unexpected error occured performing export.";
            LOG.debug(message, ex);
            MessageUtil.showError(mainFrame, message  + ex.getMessage());
    	} catch (IOException ex) {
    		final String message = ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage(); //AZ    	  
    		LOG.error(ERROR_WRITING_FILE + LINE_BREAK + ex.getMessage(), ex);
            MessageUtil.showError(mainFrame, message  + ex.getMessage());
    	} catch (Exception ex) {
    		final String message = ERROR_WRITING_FILE  + ex.getMessage(); //AZ 
    		LOG.error("Unexpected error writing file.", ex);
            MessageUtil.showError(mainFrame, message  + ex.getMessage());
    	}
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
        final FormLayout layout = new FormLayout("right:max(14dlu;pref), 4dlu, p, fill:pref:grow",
                                           "p, 4px, p");   
        final PanelBuilder builder = new PanelBuilder(layout);
        final CellConstraints cc = new CellConstraints();

        builder.addLabel(Resources.getString("label.Progress"), cc.xy(1, 1));
        builder.add(progressBar, cc.xyw(3, 1, 2));

        return builder.getPanel();
    }
    
    
    /**
     * Build String with xml node
     * <p>
     * @param Integer Offset the number of doubled empty symbols at the start of the string
     * @param String aName the name of xml node
     * @param String aEntity the value of xml node
     * @param Boolean aEnd determines if xml node must be closed or not
     * @param String stringEnd the ending symbols of xml node string (for example, LINE_SEPARATOR)
     */
    private String CreateXmlNode(Integer Offset, String aName, String aEntity, Boolean aEnd, String stringEnd) {
 	   String result = "";
 	   if (Offset != null){
 	    for (int i=0; i < Offset; i++){
 	     result = result + "  ";
 	    }
 	   }
 	   
 	   if (aEntity.length() != 0){
 	   result = result + "<" + aName + ">" + aEntity;
 	   if (aEnd){
 		   result = result + "</" + aName + ">";   
 	   } 
 	   } else {
 		   if (aEnd){
 		   result = result + "<" + aName + "/>";
 		   } else result = result + "<" + aName + ">" ;
 	   }
 	    return result + stringEnd;
    }
    
    /**
     * Replace XML escape sequences in string
     * Remove non-valid XML unicode characters
     * <p>
     * @param aString the string to replace XML escape sequences in
     */
    private String escapeXml(String aString) {
      StringBuffer stringBuffer = new StringBuffer();
      char aChar;
  	   
   	  if (aString == null || ("".equals(aString))) return "";

   	  //Remove non-valid XML unicode characters
      for (int i = 0; i < aString.length(); i++) {
    	  aChar = aString.charAt(i); 
          if ((aChar == 0x9) ||
              (aChar == 0xA) ||
              (aChar == 0xD) ||
              ((aChar >= 0x20) && (aChar <= 0xD7FF)) ||
              ((aChar >= 0xE000) && (aChar <= 0xFFFD)) ||
              ((aChar >= 0x10000) && (aChar <= 0x10FFFF)))
        	  stringBuffer.append(aChar);
      }
      aString = stringBuffer.toString();
      
       //Replace XML escape sequences
	   aString = aString.replaceAll("&","&amp;");
 	   aString = aString.replaceAll("<","&lt;");
 	   aString = aString.replaceAll(">","&gt;");
 	   aString = aString.replaceAll("\"","&quot;");
 	   aString = aString.replaceAll("'","&apos;");
 	   aString = aString.replaceAll(Character.toString((char)145), "&apos;");
 	   aString = aString.replaceAll(Character.toString((char)146), "&apos;");
 	   aString = aString.replaceAll(Character.toString((char)150), "-");
 	   aString = aString.replaceAll(Character.toString((char)151), "-");
 	   
      return aString;
    }    
        
}