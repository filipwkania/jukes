package com.melloware.jukes.db.orm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.util.ResourceUtils;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.HibernateUtil;
import com.melloware.jukes.gui.tool.Settings;

/**
 * References all relevant project data: the project description and a list of
 * artists. The artists refer to their child components.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ modifications 2009
 */
public final class Catalog extends AbstractJukesObject {

   private static final Log LOG = LogFactory.getLog(Catalog.class);

   /**
    * Holds a list of artists
    */
   private final List artists;

   // since this is static it will be used by all instances of MP3Tag created
   /** EAL **/
   public static Settings settings = null;

   /** EAL **/
   // call this method only once to store the static settings variable from the
   // MainModule code where its set
   public static void setSettings(Settings aSettings) {
      settings = aSettings;
   }

   /**
    * Constructs a <code>Catalog</code> with the given name and and loads all
    * the artists.
    * @param aFilter the filter if there is one to apply
    */
   public Catalog(String aFilter) {
      super();
      LOG.debug("Catalog created.");

      HibernateUtil.getSession().clear();
      // if a filter was applied then use the filter HQL
      if (StringUtils.isNotBlank(aFilter)) {
         final String resource;
         // AZ Show Empty Artist Nodes
         if ((Catalog.settings.isShowEmptyNode()) & (aFilter.indexOf("disc.") == -1)) {
            resource = ResourceUtils.getString("hql.filter.artist.including.empty");
            if (aFilter.startsWith("and")) {
               aFilter = aFilter.substring(4);
            }
         } else {
            resource = ResourceUtils.getString("hql.filter.artist");
         }
         final String hql = MessageFormat.format(resource, new Object[] { aFilter });
         this.artists = HibernateDao.findByQuery(hql);
      } else {
         LOG.debug("Start findAllArtists");
         if (Catalog.settings.isShowDefaultTree()) {
            if (Catalog.settings.isShowEmptyNode()) { // AZ Show Empty Artist
                                                      // Nodes
               this.artists = findAllArtistsIncludingEmpty();
            } else {
               this.artists = findAllArtists();
            }
         } else { // AZ: set empty artists list
            this.artists = new ArrayList();
         }
         LOG.debug("End findAllArtists");
      }
      LOG.debug("Artist Count = " + this.artists.size());
   }

   /**
    * Loads all artists (excluding empty ones) in the catalog and will return
    * quickly if they are already cached.
    * <p>
    * @return the complete list of artists
    */
   public static List findAllArtists() {
      LOG.debug("Loading ALL artists");
      // now just get the cached artists
      final String hql = ResourceUtils.getString("hql.artist.all");
      return HibernateDao.findByQuery(hql);
   }

   /**
    * AZ Loads all artists (including empty ones) in the catalog and will return
    * quickly if they are already cached.
    * <p>
    * @return the complete list of artists
    */
   public static List findAllArtistsIncludingEmpty() {
      LOG.debug("Loading ALL artists (including empty ones)");
      // now just get the cached artists
      final String hql = ResourceUtils.getString("hql.artist.all.including.empty");
      return HibernateDao.findByQuery(hql);
   }

   /**
    * Gets the artists.
    * <p>
    * @return Returns the artists.
    */
   public List getArtists() {
      return this.artists;
   }

   /**
    * This date determines how the NEW flag is checked in the isNew() function.
    */
   @Override
   public Date getAuditDate() {
      return new Date();
   }

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.gui.domain.Model#getCount()
    */
   @Override
   public int getChildCount() {
      if (artists == null) {
         return 0;

      } else {
         return artists.size();
      }
   }

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.db.orm.AbstractJukesObject#getCreatedDate()
    */
   @Override
   public Date getCreatedDate() {
      return new Date();
   }

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.db.orm.AbstractJukesObject#getId()
    */
   @Override
   public Long getId() {
      return Long.valueOf(-99);
   }

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.db.orm.AbstractJukesObject#getModifiedDate()
    */
   @Override
   public Date getModifiedDate() {
      return new Date();
   }

   @Override
   public String getName() {
      return "Catalog";
   }

   /*
    * (non-Javadoc)
    * @see com.melloware.jukes.db.orm.AbstractJukesObject#isValid()
    */
   @Override
   public boolean isValid() {
      return true;
   }

}