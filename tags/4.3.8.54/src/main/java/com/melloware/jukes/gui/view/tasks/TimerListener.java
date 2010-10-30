package com.melloware.jukes.gui.view.tasks;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.ResourceUtils;
import com.melloware.jukes.util.MessageUtil;

/**
 * Timer listener for Long tasks.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class TimerListener
    implements ActionListener {

    private static final Log LOG = LogFactory.getLog(TimerListener.class);
    private LongTask task;
    private final ProgressMonitor progressMonitor;
    private final Timer timer;

    /**
     * Default contructor
     */
    public TimerListener(ProgressMonitor aProgressMonitor, LongTask aTask, Timer aTimer) {
        super();
        this.progressMonitor = aProgressMonitor;
        this.task = aTask;
        this.timer = aTimer;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
    	LOG.debug("Current " + task.getCurrent());
        progressMonitor.setProgress(task.getCurrent());
        String message = task.getMessage();
        if (message != null) {
            progressMonitor.setNote(message);
            LOG.debug(message);
        }
        if (progressMonitor.isCanceled() || task.isDone()) {
            progressMonitor.close();
            task.stop();
            Toolkit.getDefaultToolkit().beep();
            timer.stop();
            if (task.isDone()) {
                if (task.hasWarning()) {//AZ
                    LOG.debug("Task completed with errors.");
                    MessageUtil.showwarn(Application.getDefaultParentFrame(), 
                		  ResourceUtils.getString("messages.TaskWithWarning"));
                                   	
                } else {
                	LOG.debug("Task completed.");
                    MessageUtil.showTaskCompleted(Application.getDefaultParentFrame());
                }
            } else {
                LOG.debug("Task canceled.");
            }
        }

        // was cancelled due to error
        if (task.isCancelled()) {
            progressMonitor.close();
            timer.stop();
        }
    }

	/**
	 * Gets the task.
	 * <p>
	 * @return Returns the task.
	 */
	public LongTask getTask() {
		return this.task;
	}

	/**
	 * Sets the task.
	 * <p>
	 * @param aTask The task to set.
	 */
	public void setTask(LongTask aTask) {
		this.task = aTask;
	}

}