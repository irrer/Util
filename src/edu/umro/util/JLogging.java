package edu.umro.util;

import org.slf4j.*;

/**
 * Log messages for Java. Extend this trait and use the <code>logger</code>
 * value to log messages.
 * <p>
 * To bind slf4j to log4j, the following VM properties should be set, as in:
 *
 * <pre>
 *
 *     -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager
 *     -Dlog4j2.configurationFile=src\main\resources\log4j2.xml
 *
 * </pre>
 * <p>
 * Examples:
 *
 * <h3>Java</h3>
 *
 * <pre>
 *
 * class Foo extends JLogging {
 * 	Foo() {
 * 		logger.info("Hello from Foo");
 *    }
 * }
 *
 * new Foo();
 *
 * </pre>
 */

class JLogging {
    protected Logger logger = LoggerFactory.getLogger("");

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

    public static void main(String[] args) {

        class Foo extends JLogging {
            Foo() {
                logger.info("Hello from Foo");
            }
        }

        new Foo();
    }
}