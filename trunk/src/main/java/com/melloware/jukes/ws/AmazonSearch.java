package com.melloware.jukes.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.melloware.jukes.util.MessageUtil;
import com.melloware.jukes.exception.WebServiceException;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Static methods for performing web services calls to the Amazon E-Commerce Web
 * Services application.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2010
 */

@SuppressWarnings("unchecked")
public final class AmazonSearch {
   private static final Log LOG = LogFactory.getLog(AmazonSearch.class);

   // the sort order (orig-rel-date, artistrank, titlerank)
   public static final String SORT_TITLE = "titlerank";
   public static final String SORT_ARTIST = "artistrank";
   public static final String SORT_DATE = "orig-rel-date";

   // the area to search amazon
   private static final String SEARCH_INDEX = "Music";

   // the data to bring back
   private static final String RG_IMAGES = "Images";
   private static final String RG_ITEMS = "ItemAttributes";
   private static final String RG_TRACKS = "Tracks";
   
   private static final String AWS_ACCESS_KEY_ID = "AKIAJWPLM2OQCW444Q5A"; //AZ Development Key

   /*
    * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
    * Your Account page.
    */
   private static final String AWS_SECRET_KEY = "MrhTu2j6htEUEh+i1iLpJkuqwatnefZ+K3hI4hWf"; //AZ Development Key
 
   /**
    * Default constructor. Private so no instantiation.
    */
   private AmazonSearch() {
      super();
   }
  
   /**
    * Finds a collection of Amazon products by Disc Name.
    * <p>
    * @param aDiscTitle the name of the disc to search for
    * @param endPoint one of the end-points, according to the region of interest
    * @return a collection of AmazonItems matching the search criteria
    * @throws WebServiceException if any error occurs querying the AmazonWebService
    */
   public static Collection findItemsByDisc(String aDiscTitle, String endPoint) throws WebServiceException {
      return findItemsByArtistDiscSort("", aDiscTitle, endPoint, SORT_TITLE);
   }

   /**
    * Finds a collection of Amazon products by Artist Name.
    * <p>
    * @param aArtistName the name of the artist to search for
    * @param endPoint one of the end-points, according to the region of interest
    * @return a collection of AmazonItems matching the search criteria
    * @throws WebServiceException if any error occurs querying the AmazonWebService
    */
   public static Collection findItemsByArtist(String aArtistName, String endPoint) throws WebServiceException {
      return findItemsByArtistDiscSort(aArtistName, "", endPoint, SORT_TITLE);
   }

   /**
    * Finds a collection of Amazon products by artist and disc name.
    * <p>
    * @param aArtistName the name of the artist to search for
    * @param aDiscTitle the name of the disc to search for
    * @param endPoint one of the end-points, according to the region of interest    * @return a collection of AmazonItems matching the search criteria
    * @throws WebServiceException if any error occurs querying the AmazonWebService
    */
   public static Collection findItemsByArtistDisc(String aArtistName, String aDiscTitle, String endPoint) throws WebServiceException {
      return findItemsByArtistDiscSort(aArtistName, aDiscTitle, endPoint, SORT_TITLE);
   }

   /**
    * Finds a collection of Amazon products by artist and disc name.
    * <p>
    * @param aArtistName the name of the artist to search for
    * @param aDiscTitle the name of the disc to search for
    * @param endPoint one of the following end-points, according to the region you are interested in.
    *      US: ecs.amazonaws.com
    *      CA: ecs.amazonaws.ca
    *      UK: ecs.amazonaws.co.uk
    *      DE: ecs.amazonaws.de
    *      FR: ecs.amazonaws.fr
    *      JP: ecs.amazonaws.jp 
    * @param aSort what field to sort by
    * @return a collection of AmazonItems matching the search criteria
    * @throws WebServiceException if any error occurs querying the AmazonWebService
    */
   public static Collection findItemsByArtistDiscSort(String aArtistName, String aDiscTitle, String endPoint, String aSort)
            throws WebServiceException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Amazon findItemsByArtistDiscSort: " + aArtistName + " - " + aDiscTitle + " - " + aSort);
      }

      SignedRequestsHelper helper;
      String requestUrl = null;
      Collection collection = null;

      try {
    	  helper = SignedRequestsHelper.getInstance(endPoint, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY); 
          //request parameters are stored in a map.
          Map<String, String> params = new HashMap<String, String>();
          params.put("Service", "AWSECommerceService");
          params.put("Version", "2009-03-31");
          params.put("Operation", "ItemSearch");
          params.put("SearchIndex", SEARCH_INDEX);
          params.put("Artist", aArtistName);
          params.put("Title", aDiscTitle);
          params.put("ResponseGroup", RG_IMAGES + "," + RG_ITEMS + "," + RG_TRACKS);
          params.put("Sort", aSort);

          requestUrl = helper.sign(params);
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
          Document doc = db.parse(requestUrl);
          //Find error messages in Amazon response
          NodeList errorList = doc.getElementsByTagName("Error");
          String errorMessage = null;
          if (errorList.getLength() != 0) {
        	  Node n = errorList.item(0);
        	  final NodeList nList = n.getChildNodes();
		      if (nList.getLength() != 0) {
		    	  for (int i=0; i<nList.getLength(); i=i+1){
		    		  Node node = nList.item(i);	
		    		  if (node.getNodeName().equalsIgnoreCase("Message")) {
		    			 errorMessage = node.getTextContent();  
		    		  }
		    	  }
		      }	
		    MessageUtil.showInformation(null, Resources.getString("label.AmazonResponse") + errorMessage);   
          } else {
          //Find Items in Amazon response 	  
          NodeList nodeList = doc.getElementsByTagName("Item");
          if (nodeList.getLength()==0) {
        	  MessageUtil.showInformation(null, Resources.getString("messages.NoItemsFound"));  
          }
         //Loads a collection with AmazonItems from an Item node-list
         collection = loadItems(nodeList);
         }
      } catch (NullPointerException e) {
          LOG.error(e.getMessage(), e);
          throw new WebServiceException(e); 
      } catch (Exception ex) {
         LOG.error(ex.getMessage(), ex);
         throw new WebServiceException(ex);
      } 

      return collection;
   }

   /**
    * Loads a collection with AmazonItems from an Item node-list of AWS-response.
    * <p>
    * @param aItems the items to load
    * @return a collection of AmazonItem objects or an empty collection
    */
   private static Collection loadItems(NodeList aItems) {
      ArrayList collection = new ArrayList();
    if (aItems != null) {
        for (int i=0; i<aItems.getLength(); i=i+1){
            collection.add(new AmazonItem(aItems.item(i)));
         }
      }
      return collection;
   }

}