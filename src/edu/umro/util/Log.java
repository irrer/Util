package edu.umro.util;

/*
 * Copyright 2012 Regents of the University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * Support logging services based on the <code>java.util.logging</code> package.
 * <p>
 * In general, all logging is sent to files that are overwritten on a rotating
 * basis.
 * 8.0008.000
 *
 * @author Jim Irrer irrer@umich.edu
 * @deprecated This functionality has been superseded by the more flexible slf4j
 * approach. See <code>edu.umro.ScalaUtil.Logging</code> for
 * reference.
 */
@Deprecated
public class Log {

    /**
     * Logger for this service.
     */
    private volatile static Logger serviceLogger = null;

    /**
     * Puts log messages into files.
     */
    private volatile static Handler handler = null;

    /**
     * Formats log entries in a detailed way.
     */
    private volatile static LogFormatter serviceFormatter = null;

    /**
     * Get the service-wide logger.
     *
     * @return Logger for this service.
     */
    public static Logger get() {
        initLogging();
        return serviceLogger;
    }

    /**
     * Set the logging level.
     *
     * @param level New logging level.
     * @throws IOException
     * @throws SecurityException
     */
    public static void setLevel(Level level) throws SecurityException, IOException {
        initLogging();
        handler.setLevel(level);
    }

    /**
     * Determine if the logging is viable, meaning that it is ready for use. This
     * method is used in situations where logging may have not yet been initialized,
     * usually in the early startup phase of the program.
     *
     * @return True if logging is safe to use.
     */
    public static boolean isViable() {
        return serviceLogger != null;
    }

    /**
     * Attempt to make any parent directories required by the file to be used for
     * logging. On failure, quietly give up.
     */
    private static void makeLogDir() {
        try {
            String loggingPropertiesFile = System.getProperty("java.util.logging.config.file");
            File file = new File(loggingPropertiesFile);
            if (file.canRead()) {
                Properties loggingProperties = new Properties();
                loggingProperties.load(new FileInputStream(file));
                File logFile = new File(loggingProperties.getProperty("edu.umro.util.LogFileHandler.pattern"));
                File parent = (logFile.getParentFile() == null) ? new File(".") : logFile.getParentFile();
                System.out.println("Using log directory: " + parent.getAbsolutePath());
                parent.mkdirs();
            } else {
                System.out.println("Unable to read logging properties file: " + file.getAbsolutePath()
                        + "  proceeding without logging.");
            }
        } catch (Exception e) {
            ;
        }
    }

    /**
     * Format a <code>Throwable</code>.
     *
     * @param throwable Contains description and stack trace.
     * @return Human readable version of <code>Throwable</code> and stack trace.
     */
    public static String fmtEx(Throwable throwable) {
        StringBuffer buf = new StringBuffer(throwable.toString());
        for (StackTraceElement ste : throwable.getStackTrace())
            buf.append("\n    " + ste);
        return buf.toString();
    }

    /**
     * Initialize logging by setting up all loggers to use the same handler.
     */
    public static void initLogging() {
        if (serviceLogger == null) {
            try {
                makeLogDir();
                Handler hndlr = new LogFileHandler();
                Logger sl = Logger.getLogger(Log.class.getName());
                serviceFormatter = new LogFormatter();
                hndlr.setFormatter(serviceFormatter);

                handler = hndlr;
                serviceLogger = sl;
            } catch (SecurityException ex) {
                System.err.println("SecurityException - Failed to set up logging to file: " + ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                System.err.println("IOException - Failed to set up logging to file: " + ex);
                ex.printStackTrace();
            } catch (Exception ex) {
                System.err.println("Exception - Failed to set up logging to file: " + ex);
                ex.printStackTrace();
            }
        }

        if (serviceLogger == null) {
            System.err.println("Unable to log to file.  Logging to console instead.");
            Logger logger = Logger.getLogger(Log.class.getName());
            Handler hndlr = new ConsoleHandler();
            hndlr.setFormatter(new SimpleFormatter());
            logger.addHandler(hndlr);
            logger.setLevel(Level.ALL);
            hndlr.setLevel(Level.ALL);

            handler = hndlr;
            serviceLogger = logger;
        }
    }

}
