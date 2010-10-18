package com.melloware.jukes.tray;

import java.awt.Image;

/**
 * Common interface for tray icons on Windows and KDE.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public interface ITrayIcon {
	
	public boolean isAvailable();
	
	public void cleanUp();
	
	public void showWindow();
	
	public void hideWindow();
	
	public void changeImage(Image aImage);
	
	public void setToolTip(String aTip);

}
