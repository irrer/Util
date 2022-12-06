package edu.umro.util;

/*
 * Copyright 2012 Regents of the University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * General purpose methods.
 *
 * @author Jim Irrer  irrer@umich.edu
 */

public class Utility {

    /**
     * Number of bytes in a single buffer for reading.
     */
    private final static int BUFFER_SIZE = 1024 * 1024;

    /**
     * Format a date in a thread safe way.  Note that the entire application has to use this or threads could
     * interfere with each other.  This is just a thread-safe wrapper for the standard function.
     *
     * @param format Specifies format.
     * @param date   The date to format.
     * @return Date formatted as string.
     */
    public synchronized String formatDate(SimpleDateFormat format, Date date) {
        return format.format(date);
    }

    /**
     * Parse a date in a thread safe way.  Note that the entire application has to use this or threads could
     * interfere with each other.  This is just a thread-safe wrapper for the standard function.
     *
     * @param format Specifies format.
     * @param text   The formatted date to format.
     * @return Date formatted as string.
     */
    public synchronized Date parseDate(SimpleDateFormat format, String text) throws ParseException {
        return format.parse(text);
    }

    /**
     * This datetime format complies with oracle db datetime format.
     * convert a Date to Java date string in the following format
     *
     * @param date Get for this date.
     * @return Oracle compatible date.
     * @throws UMROException If something goes wrong.
     */
    public static String getDateTimeString(Date date) throws UMROException {

        String sDateFormat = "yyyy/MM/dd HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(sDateFormat);
        String sStartDateTime = dateFormat.format(date);

        //create a oracle database date time string for insert
        String sOracleDateTimeFormat = "yyyy/mm/dd hh24:mi:ss";
        sStartDateTime = "to_date('" + sStartDateTime + "','" + sOracleDateTimeFormat + "')";

        return sStartDateTime;
    }


    public static boolean isWordPresentInString(final String sSource,
                                                final String sSearch) throws UMROException {
        boolean bPresent = false;
        if (sSource == null || sSource.length() == 0) {
            throw new UMROException("The parameter, 'Source' is not set.");
        }

        int nPos = sSource.toUpperCase().indexOf(sSearch);
        if (nPos != -1)
            bPresent = true;

        return bPresent;
    }

    public static String printClassMethodMessage(final String sClassName,
                                                 final String sMethodName) throws UMROException {
        String sMessage = "";
        sMessage += "class: '" + sClassName + "': method: '" + sMethodName + "' called.";

        return sMessage;
    }


    /**
     * Write the given text to a file.
     *
     * @param file Write to this file.
     * @param text Text to write.
     */
    public static void writeFile(File file, byte[] text) throws UMROException {
        FileOutputStream out = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            out.write(text);
            out.flush();
            out.close();
            out = null;
        } catch (FileNotFoundException ex) {
            throw new UMROException("Could not find file " + file + " : " + ex);
        } catch (IOException ex) {
            throw new UMROException("Unable to write to file " + file + " : " + ex);
        } catch (SecurityException ex) {
            throw new UMROException("Not permitted to create file " + file + " : " + ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                throw new UMROException("Unable to close to file " + file + " : " + ex);
            }
        }
    }

    public static void writeFile(final String sFilePath, final String content) throws UMROException {
        writeFile(new File(sFilePath), content.getBytes());
    }


    public static boolean isFileExist(final String sFilePath) throws UMROException {

        boolean bExist = false;
        FileInputStream fis = null;
        File f = new File(sFilePath);
        try {
            // force test for file existing.
            fis = new FileInputStream(f);
            if (f.exists()) {
                bExist = true;
            }

        } catch (FileNotFoundException ex) {
            throw new UMROException(ex.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Throwable t) {
                    // do nothing
                }
            }
        }
        return bExist;
    }


    /**
     * Read a file into a String.  If there is any problem, throw an exception.
     *
     * @param file The file to read.
     * @return Contents of file, or null if not found, null file
     * name, can not read, etc.
     */
    public static String readFile(File file) throws UMROException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            return readInputStream(fileInputStream);
        } catch (FileNotFoundException ex) {
            throw new UMROException("Error, file '" + file.getAbsolutePath() + "' not found. Exception: " + ex);
        } catch (IOException ex) {
            throw new UMROException("Error while reading file '" + file.getAbsolutePath() + "'. Exception: " + ex);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                    fileInputStream = null;
                } catch (IOException ex) {
                    throw new UMROException("Error while reading file '" + file.getAbsolutePath() + "'. Exception: " + ex);
                }
            }
        }

    }


    /**
     * Read a file verbatim and return it as an array.
     *
     * @param file File to read.
     * @return Contents of file.
     * <p>
     * throws RemoteException If file does not exist, has no read permission, etc..
     */
    public static byte[] readBinFile(File file) {
        FileInputStream fis = null;
        try {
            byte[] buffer = new byte[(int) file.length()];
            fis = new FileInputStream(file);
            long actual = fis.read(buffer);
            if (actual != buffer.length) {
                throw new RuntimeException("Error reading file " + file.getAbsolutePath() + " .  Expected " + buffer.length + " bytes but got " + actual);
            }
            return buffer;
        } catch (Exception e) {
            throw new RuntimeException("Error reading file " + file.getAbsolutePath() + " : " + e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }


    /**
     * Read the entire given input stream into a string.
     *
     * @param inputStream Read from here.
     * @return String representation of input.
     * @throws IOException
     */
    public static String readInputStream(InputStream inputStream) throws IOException {
        byte[] data = new byte[BUFFER_SIZE];
        StringBuffer text = new StringBuffer("");
        int size = data.length;
        while (size == data.length) {
            size = inputStream.read(data);
            text.append(new String(data, 0, size));
        }

        return text.toString();
    }

    /**
     * Recursively delete all of the files in a directory tree. The directory
     * itself will be deleted.
     *
     * @param directory Top level file or directory whose files will be deleted. If
     *                  this is a regular file, then just this file will be deleted.
     * @throws SecurityException If a file can not be deleted.
     */
    public static void deleteFileTree(File directory) throws SecurityException {
        if (directory.isDirectory()) {
            for (File child : directory.listFiles())
                deleteFileTree(child);
        }
        directory.delete();
        if (directory.exists()) throw new SecurityException("Unable to delete " + directory.getAbsolutePath());
    }

    /**
     * Recursively copy all of the files in a directory tree.
     *
     * @param src  Source file/directory
     * @param dest Destination file/directory is expected to not exist.
     * @throws SecurityException If a file can not be copied.
     *                           throws IOExceptionIf
     *                           a file can not be copied.
     * @throws UMROException     If a file can not be copied.
     */
    public static void copyFileTree(File src, File dest) throws SecurityException, IOException, UMROException {
        if (src.isDirectory()) {
            dest.mkdirs();
            for (File child : src.listFiles())
                copyFileTree(child, new File(dest, child.getName()));
        } else {
            dest.createNewFile();
            writeFile(dest, readBinFile(src));
        }
    }

    /**
     * Compare two files to determine if they have exactly the same content.
     *
     * @param a First file.
     * @param b Second file.
     * @return True if they are the same, false if any differences.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean compareFiles(File a, File b) throws FileNotFoundException, IOException {
        if (a.length() != b.length()) return false;
        FileInputStream fisA = new FileInputStream(a);
        FileInputStream fisB = new FileInputStream(b);
        final int BUF_LEN = 1024 * 1024;
        byte[] bufferA = new byte[BUF_LEN];
        byte[] bufferB = new byte[BUF_LEN];

        int lenA;
        int lenB;
        try {
            while (((lenA = fisA.read(bufferA)) != -1) && ((lenB = fisB.read(bufferB)) != -1)) {
                if (lenA != lenB) return false;
                for (int l = 0; l < lenA; l++)
                    if (bufferA[l] != bufferB[l]) return false;
            }
        } finally {
            try {
                fisA.close();
            } finally {
                fisB.close();
            }
        }

        return true;
    }

    /**
     * Recursively compare two directories or files and throw an exception if their content is different.
     *
     * @param a One file or directory.
     * @param b The other file or directory.
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void compareDirs(File a, File b) throws FileNotFoundException, IOException {
        if (a.isDirectory() && b.isDirectory()) {
            File[] aList = a.listFiles();
            File[] bList = b.listFiles();
            if (aList.length != bList.length) {
                throw new RuntimeException("Missing file");
            }
            for (File aa : aList) {
                compareDirs(aa, new File(b, aa.getName()));
            }
        } else {
            if (!(a.isFile() && b.isFile() && compareFiles(a, b))) {
                throw new RuntimeException("files not equal");
            }
        }
    }

    /**
     * Recursively compare two directories or files and throw an exception if
     * their content is different.  Any file access errors will result in a return of 'false';
     *
     * @param a One file or directory.
     * @param b The other file or directory.
     * @return True if the same, false if different.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean compareFolders(File a, File b) {
        try {
            compareDirs(a, b);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static void cmprStuff(int i) {
        System.out.println("cmprStuff i: " + i);
    }

    public static String newnessly() {
        return "better newnosity string";
    }

    public static void main(String[] args) throws SecurityException, IOException, UMROException {
        String srcName = "D:\\tmp\\copy\\src";
        String destName = "D:\\tmp\\copy\\dest";

        //copyFileTree(new File(srcName), new File(destName));
        System.out.println("comp dir: " + compareFolders(new File(srcName), new File(destName)));
    }

    /**
     * Standard date format
     */
    public static final SimpleDateFormat standardDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

}
