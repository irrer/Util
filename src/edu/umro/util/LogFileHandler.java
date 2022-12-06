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
import java.util.logging.*;

/**
 * Wrap the <code>FileHandler</code> constructors so that they
 * all use the <code>LogFormatter</code> as their default.
 * <p>
 * My personal opinion is that this is a bug in java.util.logging,
 * that it does not allow you to set the formatter in the logging.properties
 * file to your own custom formatter.
 *
 * @author Jim Irrer  irrer@umich.edu
 */

public class LogFileHandler extends FileHandler {

    /**
     * Set the default formatter to a <code>LogFormatter</code>.
     */
    private void initialize() {
        setFormatter(new LogFormatter());
    }


    /**
     * Construct a default FileHandler.
     *
     * @throws IOException
     * @throws SecurityException
     */
    public LogFileHandler() throws SecurityException, IOException {
        super();
        initialize();
    }


    /**
     * Construct a FileHandler to write to the given filename.
     *
     * @param pattern
     * @throws IOException
     * @throws SecurityException
     */
    public LogFileHandler(String pattern) throws SecurityException, IOException {
        super(pattern);
        initialize();
    }


    /**
     * Construct a FileHandler to write to the given filename, with optional append.
     *
     * @param pattern
     * @param append
     * @throws IOException
     * @throws SecurityException
     */
    public LogFileHandler(String pattern, boolean append) throws SecurityException, IOException {
        super(pattern, append);
        initialize();
    }


    /**
     * Construct a FileHandler to write to a set of files.
     *
     * @param pattern
     * @param limit
     * @param count
     * @throws IOException
     * @throws SecurityException
     */
    public LogFileHandler(String pattern, int limit, int count) throws SecurityException, IOException {
        super(pattern, limit, count);
        initialize();
    }


    /**
     * Construct a FileHandler to write to a set of files with optional append.
     *
     * @param pattern
     * @param limit
     * @param count
     * @param append
     * @throws IOException
     * @throws SecurityException
     */
    public LogFileHandler(String pattern, int limit, int count, boolean append) throws SecurityException, IOException {
        super(pattern, limit, count, append);
        initialize();
    }

}

