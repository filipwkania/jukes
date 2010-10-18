package com.melloware.jukes.file.tag;

import java.io.File;
import java.util.Map;

/**
 * Interface for all implemented tag formats.   
 * <p>
 * Copyright (c) 2006
 * Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public interface ITag {
	
	public String getArtist();
	
	public Long getBitRate();
	
	public String getComment();
	
	public String getDisc();
	
	public String getEncodedBy();
	
	public String getGenre();
	
	public String getTitle();
	
	public String getTrack();
	
	public long getTrackLength();
	
	public String getYear();
	
	public String getLayer();
	
	public String getVersion();
	
	public String getFrequency();
	
	public String getMode();
	
	public String getEmphasis();
	
	public String getCopyrighted();
	
	public Map getHeader();
	
	public File getFile();
	
	public boolean isVBR();
	
	public void setArtist(String aArtist);
	
	public void setComment(String aComment);
	
	public void setDisc(String aDisc);
	
	public void setEncodedBy(String aEncodedBy);
	
	public void setGenre(String aGenre);
	
	public void setTitle(String aTitle);
	
	public void setTrack(String aTrack);
	
	public void setTrack(String aTrack, int padding);
	
	public void setYear(String aYear);

}
