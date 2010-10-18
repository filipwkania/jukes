package com.melloware.jukes.gui.view.tasks;


/**
 * Abstract class that uses a SwingWorker to perform a time-consuming task.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * 2010 AZ Development
 */
@SuppressWarnings("PMD")
abstract public class LongTask {

    protected boolean canceled = false;
    protected boolean done = false;
    protected boolean warning = false;//AZ
    protected int current = 0;
    protected int lengthOfTask = 0;
    protected String statMessage;

    /**
     * Constructor that needs a config object and a filename to work on.
     * <p>
     */
    public LongTask() {
    	super();
    }

    /**
     * Called to start the task.
     */
    abstract public void go();

    /**
     * Called to find out how much has been done.
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Called to find out how much work needs
     * to be done.
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Returns the most recent status message, or null
     * if there is no current status message.
     */
    public String getMessage() {
        return statMessage;
    }

    /**
     * Called to find out if the task has been canceled.
     */
    public boolean isCancelled() {
        return canceled;
    }

    /**
     * Called to find out if the task has completed.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Stops the current long task.
     */
    public void stop() {
        canceled = true;
        statMessage = null;
    }
    
    /**AZ
     * Called to find out if the task has completed with errors or warnings.
     */
    public boolean hasWarning() {
        return warning;
    }

}