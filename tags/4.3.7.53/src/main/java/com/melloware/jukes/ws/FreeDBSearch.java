package com.melloware.jukes.ws;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import entagged.freedb.Freedb;
import entagged.freedb.FreedbException;
import entagged.freedb.FreedbQueryResult;
import entagged.freedb.FreedbReadResult;
import entagged.freedb.FreedbSettings;

import com.melloware.jukes.util.MessageUtil;
import com.melloware.jukes.exception.WebServiceException;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Static methods for performing web services calls to FreeDB
 * 
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2010
 */

@SuppressWarnings("unchecked")
public final class FreeDBSearch {
   private static final Log LOG = LogFactory.getLog(FreeDBSearch.class);

   /**
    * Default constructor. Private so no instantiation.
    */
   private FreeDBSearch() {
      super();
   }
  

   /**
    * Finds a collection of albums at FreeDB
    * <p>
    * @param  float[] trackLength - a set of track lengths to search at FreeDB for.
    * @return a collection of items matching the search criteria
    * @throws WebServiceException if any error occurs querying the FreeDB service
    */
   public static Collection findItemsAtFreeDB(float[] trackLength)
            throws WebServiceException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("FreeDB search");
      }
      Collection collection = null;
      Freedb freedb;
      //Create default FreeDB settings
      FreedbSettings fdbs = new FreedbSettings();
      try {
    	freedb = new Freedb(fdbs);
	  
		FreedbQueryResult[] fdbqr = freedb.query(trackLength);
        if (fdbqr.length ==0) {
      	  MessageUtil.showInformation(null, Resources.getString("messages.NoItemsFound"));  
        }
        	
		collection = loadItems(freedb, fdbqr);
      } catch (FreedbException e) {
          LOG.error(e.getMessage(), e);
          MessageUtil.showInformation(null, "FreeDB: " + e.getMessage());
      } catch (Exception ex) {
         LOG.error(ex.getMessage(), ex);
         throw new WebServiceException(ex);
      } 

      return collection;
   }

   /**
    * Loads a collection with FreeDB Items from a list of FreeDB-response.
    * <p>
    * @param Freedb aFreeDB 
    * @param FreedbQueryResult[] aFreeDBReaults the list of FreeDB query results
    * @return a collection of FreeDBItem objects or an empty collection
    * @throws WebServiceException 
    */
   private static Collection loadItems(Freedb aFreeDB, FreedbQueryResult[] aFreeDBResults) throws WebServiceException {
      ArrayList collection = new ArrayList();
    if (aFreeDBResults != null) {
    	try {
        for (int i=0; i<aFreeDBResults.length; i=i+1){
        	FreedbReadResult freedbResult = aFreeDB.read(aFreeDBResults[i]);  
            collection.add(new FreeDBItem(freedbResult));
         }      
    	} catch (FreedbException ex){
            LOG.error(ex.getMessage(), ex);
            throw new WebServiceException(ex);	
    	}
       } 
      return collection;
   }

}