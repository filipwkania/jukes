package com.melloware.jukes.gui.view.dialogs;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;
import com.l2fprod.common.model.DefaultBeanInfoResolver;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.util.TimeSpan;

/**
 * A <code>PropertySheet</code> for the <code>Jukes</code> statistics.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class PropertySheetApplicationStats
    extends JPanel {

    public PropertySheetApplicationStats() {
        setLayout(LookAndFeelTweaks.createVerticalPercentLayout());

        final Bean data = new Bean();

        TimeSpan timespan = null;

        // counts of objects
        long discCount = HibernateDao.countAll(Disc.class);
        data.setCountArtists(Long.toString(HibernateDao.countAll(Artist.class)));
        data.setCountDiscs(Long.toString(discCount));
        data.setCountTracks(Long.toString(HibernateDao.countAll(Track.class)));

        // time stats
        long totalTime = 0;
        long totalSize = 0;
        long avgTimePerDisc = 0;
        long avgSizePerDisc = 0;
        try {
            totalTime = HibernateDao.sum(Disc.class, Disc.PROPERTYNAME_DURATION);
            totalSize = HibernateDao.sum(Track.class, Track.PROPERTYNAME_TRACK_SIZE);
            avgTimePerDisc = (totalTime / discCount) * 1000;
            avgSizePerDisc = (totalSize / discCount);
        } catch (RuntimeException ex) {
            totalTime = 0;
        }

        // calculate time fields to display properly
        timespan = new TimeSpan(totalTime * 1000);
        data.setTimeTotal(timespan.toString());
        timespan = new TimeSpan(avgTimePerDisc);
        data.setTimeAveragePerDisc(timespan.toString());

        // file stats
        data.setFileTotal(FileUtils.byteCountToDisplaySize(totalSize));
        data.setFileAveragePerDisc(FileUtils.byteCountToDisplaySize(avgSizePerDisc));

        DefaultBeanInfoResolver resolver = new DefaultBeanInfoResolver();
        BeanInfo beanInfo = resolver.getBeanInfo(data);

        PropertySheetPanel sheet = new PropertySheetPanel();
        sheet.setMode(PropertySheet.VIEW_AS_CATEGORIES);
        sheet.setProperties(beanInfo.getPropertyDescriptors());
        sheet.readFromObject(data);
        sheet.setDescriptionVisible(true);
        sheet.setSortingCategories(true);
        sheet.setSortingProperties(true);
        add(sheet, "*");

        // everytime a property change, update the button with it
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Property prop = (Property)evt.getSource();
                prop.writeToObject(data);
            }
        };
        sheet.addPropertySheetChangeListener(listener);
    }

    /**
     * Class used to hold the property info.
     */
    public static class Bean {

        private String countArtists;
        private String countDiscs;
        private String countTracks;
        private String fileTotal;
        private String fileAveragePerDisc;
        private String timeAveragePerDisc;
        private String timeTotal;

        /**
         * Gets the countArtists.
         * <p>
         * @return Returns the countArtists.
         */
        public String getCountArtists() {
            return this.countArtists;
        }

        /**
         * Gets the countDiscs.
         * <p>
         * @return Returns the countDiscs.
         */
        public String getCountDiscs() {
            return this.countDiscs;
        }

        /**
         * Gets the countTracks.
         * <p>
         * @return Returns the countTracks.
         */
        public String getCountTracks() {
            return this.countTracks;
        }

        /**
         * Gets the fileTotal.
         * <p>
         * @return Returns the fileTotal.
         */
        public String getFileTotal() {
            return this.fileTotal;
        }

        /**
         * Gets the timeAveragePerDisc.
         * <p>
         * @return Returns the timeAveragePerDisc.
         */
        public String getTimeAveragePerDisc() {
            return this.timeAveragePerDisc;
        }

        /**
         * Gets the timeTotal.
         * <p>
         * @return Returns the timeTotal.
         */
        public String getTimeTotal() {
            return this.timeTotal;
        }

        /**
         * Sets the countArtists.
         * <p>
         * @param aCountArtists The countArtists to set.
         */
        public void setCountArtists(String aCountArtists) {
            this.countArtists = aCountArtists;
        }

        /**
         * Sets the countDiscs.
         * <p>
         * @param aCountDiscs The countDiscs to set.
         */
        public void setCountDiscs(String aCountDiscs) {
            this.countDiscs = aCountDiscs;
        }

        /**
         * Sets the countTracks.
         * <p>
         * @param aCountTracks The countTracks to set.
         */
        public void setCountTracks(String aCountTracks) {
            this.countTracks = aCountTracks;
        }

        /**
         * Sets the fileTotal.
         * <p>
         * @param aFileTotal The fileTotal to set.
         */
        public void setFileTotal(String aFileTotal) {
            this.fileTotal = aFileTotal;
        }

        /**
         * Sets the timeAveragePerDisc.
         * <p>
         * @param aTimeAveragePerDisc The timeAveragePerDisc to set.
         */
        public void setTimeAveragePerDisc(String aTimeAveragePerDisc) {
            this.timeAveragePerDisc = aTimeAveragePerDisc;
        }

        /**
         * Sets the timeTotal.
         * <p>
         * @param aTimeTotal The timeTotal to set.
         */
        public void setTimeTotal(String aTimeTotal) {
            this.timeTotal = aTimeTotal;
        }

		/**
		 * Gets the fileAveragePerDisc.
		 * <p>
		 * @return Returns the fileAveragePerDisc.
		 */
		public String getFileAveragePerDisc() {
			return this.fileAveragePerDisc;
		}

		/**
		 * Sets the fileAveragePerDisc.
		 * <p>
		 * @param aFileAveragePerDisc The fileAveragePerDisc to set.
		 */
		public void setFileAveragePerDisc(String aFileAveragePerDisc) {
			this.fileAveragePerDisc = aFileAveragePerDisc;
		}

    }

    /**
     * Class used to hold the property sheet descriptor info.
     */
    public static class BeanBeanInfo
        extends BaseBeanInfo {

        public BeanBeanInfo() {
            super(Bean.class);
            ExtendedPropertyDescriptor descriptor = null;

            descriptor = addProperty("countArtists");
            descriptor.setCategory("Counts");
            descriptor.setDisplayName(Resources.getString("label.CountArtist"));
            descriptor.setShortDescription(Resources.getString("label.CountArtistMessage"));

            descriptor = addProperty("countDiscs");
            descriptor.setCategory("Counts");
            descriptor.setDisplayName(Resources.getString("label.CountDisc"));
            descriptor.setShortDescription(Resources.getString("label.CountDiscMessage"));

            descriptor = addProperty("countTracks");
            descriptor.setCategory("Counts");
            descriptor.setDisplayName(Resources.getString("label.CountTrack"));
            descriptor.setShortDescription(Resources.getString("label.CountTrackMessage"));

            descriptor = addProperty("timeTotal");
            descriptor.setCategory("Times");
            descriptor.setDisplayName(Resources.getString("label.TimeTotal"));
            descriptor.setShortDescription(Resources.getString("label.TimeTotalMessage"));

            descriptor = addProperty("timeAveragePerDisc");
            descriptor.setCategory("Times");
            descriptor.setDisplayName(Resources.getString("label.TimeAvg"));
            descriptor.setShortDescription(Resources.getString("label.TimeAvgMessage"));

            descriptor = addProperty("fileTotal");
            descriptor.setCategory("File");
            descriptor.setDisplayName(Resources.getString("label.FileTotal"));
            descriptor.setShortDescription(Resources.getString("label.FileTotalMessage"));
            
            descriptor = addProperty("fileAveragePerDisc");
            descriptor.setCategory("File");
            descriptor.setDisplayName(Resources.getString("label.FileAvg"));
            descriptor.setShortDescription(Resources.getString("label.FileAvgMessage"));
        }
    }

}