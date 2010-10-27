package com.melloware.jukes.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Static class of file utilities.
 * <p>
 * Copyright: Copyright (c) 2006
 * Company: Melloware, Inc.
 * @author Emil A. Lefkof III
 * @version 4.0
 */
public final class FileUtil {

    private static final Log LOG = LogFactory.getLog(FileUtil.class);
    public static final String WINDOWS_COMMAND = "attrib -R ";
    public static final String NIX_COMMAND = "chmod u+w ";
    private static final String INVALID_OUTPUT_NAME_REGEX = "[\\\\\\/\\:\\*\\\"\\<\\>\\|\\?]";
    private static final char[] CAPS_CHARS = new char[] { ' ', '.', '"', '(', '[', ',', ':', '-', ']', ')', '/', '\\'};

    /**
     * Private constructor for no instantiation of static class
     */
    private FileUtil() {
    	//empty constructor
    }

    /**
     * Sets a file read only or not using the OS specific commands.  For JDK6 
     * this is not needed as it has File.setReadable(true);
     * <p>
     * @param aFile the file to set readonly
     * @param flag true for readonly, false for writeable
     */
    public static void setReadOnly(final File aFile, final boolean flag) {
        if (flag) {
            aFile.setReadOnly();
        } else {
            String path = aFile.getAbsolutePath();
            if (path.indexOf(' ') >= 0) {
                path = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf('"')))).append(path).append('"')));
            }

            final String command = getCommand() + path;
            try {
                LOG.debug(command);
                Runtime.getRuntime().exec(command).waitFor();
            } catch (IOException ioexception) {
                LOG.error(Resources.getString("messages.ErrorClearReadOnlyAttribute").concat(String.valueOf(String.valueOf(path))),
                          ioexception);
            } catch (InterruptedException interruptedexception) {
                LOG.error(Resources.getString("messages.ErrorClearReadOnlyAttribute").concat(String.valueOf(String.valueOf(path))),
                          interruptedexception);
            }
        }
    }

    /**
     * Provides title casing for a string and a set of characters to always
     * capitalize after like "(".
     * <p>
     * @param aStringToCapitalize the string to capitalize
     * @return the captilized string
     */
    public static String capitalize(final String aStringToCapitalize) {
    	String word = WordUtils.capitalizeFully(aStringToCapitalize, CAPS_CHARS);
    	word = StringUtils.replace(word, "Ii", "II");
    	word = StringUtils.replace(word, "IIi", "III");
    	word = StringUtils.replace(word, "Iv", "IV");
        return word;
    }

    /**
     * Checks a file name for invalid characters \\, /, :, , *, ?, ", <, >, or |
     * <p>
     * @param aFilename the filename to check
     */
    public static void checkValidFileName(final String aFilename, final String message) {
        if (aFilename.matches(INVALID_OUTPUT_NAME_REGEX)) {
            throw new InfrastructureException(message);
        }
    }

    /**
     * Corrects a file name for invalid characters.
     * e.g. \\, /, :, , *, ?, ", <, >, or |
     * <p>
     * @param aFilename the filename to check
     * @return the corrected file name
     */
    public static String correctFileName(final String aFilename) {
        return aFilename.replaceAll(INVALID_OUTPUT_NAME_REGEX, "_");
    }

    /**
     * Deep copy of a Map.
     * <p>
     * @param src the source Map
     * @return return the deep copy.
     */
    @SuppressWarnings("unchecked")
    public static Map deepCopy(final Map src) {
        final HashMap map = new HashMap();
        if (src != null) {
            final Iterator it = src.keySet().iterator();
            while (it.hasNext()) {
                final Object key = it.next();
                final Object value = src.get(key);
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * Renames a file.
     * <p>
     * @param sOldname The current (old) name of the file.
     * @param sNewname The new name of the file.
     * @return true/false if the rename was successful.
     */
    public static boolean rename(final String sOldname, final String sNewname) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Renaming: '" + sOldname + "' to '" + sNewname + "'");
        }

        final File oldFile = new File(sOldname);
        final File newFile = new File(sNewname);

        final boolean success = oldFile.renameTo(newFile);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Completed reanaming: " + success);
        }

        return success;
    }    // End rename

    /**
     * Gets the correct command for setting a file read only in the correct
     * operating system.
     * <p>
     * @return the command line string
     */
    private static String getCommand() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS_COMMAND;
        } else {
            return NIX_COMMAND;
        }
    }

}