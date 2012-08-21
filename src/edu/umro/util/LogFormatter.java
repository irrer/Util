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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This custom formatter formats parts of a log record to a single line
 * 
 * @author Jim Irrer  irrer@umich.edu 
 *
 */
class LogFormatter extends Formatter {
    
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:SS.sss");

    
    public LogFormatter() {
        super();
    }
    
    // This method is called for every log records
    public String format(LogRecord rec) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
                
        printStream.format("%-7s %-24s %-80s  %-40s Seq:%d Thread:%d\n",
                rec.getLevel(),
                SIMPLE_DATE_FORMAT.format(new Date(rec.getMillis())),
                rec.getMessage(),
                rec.getSourceClassName() + "." + rec.getSourceMethodName(),
                rec.getSequenceNumber(),
                rec.getThreadID());
        
        return byteArrayOutputStream.toString();
    }


    public String toString() {
        return LogFormatter.class.getName() + " Jim";
    }
}