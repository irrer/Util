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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;

/**
 * Define an exception that is targeted for user consumption.
 * 
 * @author Jim Irrer  irrer@umich.edu 
 *
 */
public class UMROException extends Exception {

    /** Serialization ID. */
    private static final long serialVersionUID = -744294766916142471L;
    
    public static final long UNDEFINED_CODE = -1;

    /** Error code identifying the instance of this exception. */
    public long code = UNDEFINED_CODE;
    
    /** Description of error. */
    public String what = "";
    
    /** Scope of the impact of the error. */
    public String scope = "";
    
    /** Corrective action to be taken. */
    public String action = "";
    
    /** Additional background or suggestions. */
    public String more = "";

    /** Technical description of error to aid software developers. */
    public String progammerMessage = null;

    /** Name of service that generated this exception. */
    public String serviceName = null;

    /** Technical description of error to aid software developers. */
    public String serviceVersion = null;

    /** Message time stamp format. */
    static final SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss.SSS");

    /**
     * Construct an exception with all of the expected parameters.
     * 
     * @param code Unique code identifying this type of exception.
     * @param what Description of error.
     * @param scope Scope of the impact of the error.
     * @param action Corrective action to be taken.
     * @param more Additional background or suggestions.
     * @param progammerMessage Technical description of error to aid software developers.
     */
    public UMROException(long code, String what, String scope, String action, String more, String progammerMessage) {
        super(toXML(null, code, what, scope, action, more, progammerMessage));
        
        this.code = code;
        this.what = what;
        this.scope = scope;
        this.action = action;
        this.more = more;
        this.progammerMessage = progammerMessage;
    }
    
    /**
     * Construct an exception with just the programmer message.
     * @param msg
     */
    public UMROException(String msg) {
        super(msg);
        progammerMessage = msg;
    }


    public UMROException(Node node) throws UMROException {
        super(XML.domToString(node));
        try {
            String codeText = XML.getValue(node, "Code/text()");
            if (codeText == null) {
                code = 0;
            }
            else {
                code = Long.parseLong(codeText);
            }
        }
        catch (NumberFormatException ex) {
            code = -1;
        }
        what   = XML.getValue(node, "UserMessage/What/text()");
        scope  = XML.getValue(node, "UserMessage/Scope/text()");
        action = XML.getValue(node, "UserMessage/Action/text()");
        more   = XML.getValue(node, "UserMessage/More/text()");
        progammerMessage = XML.getValue(node, "ProgrammerMessage/text()");
    }

    
    /**
     * Format an XML version of an exception.
     * 
     * @param code Unique code identifying this type of exception.
     * 
     * @param what Description of error.
     * 
     * @param scope Scope of the impact of the error.
     * 
     * @param action Corrective action to be taken.
     * 
     * @param more Additional background or suggestions.
     * 
     * @param progammerMessage Technical description of error to aid software developers.
     * 
     * @return XML string representing the exception.
     */
    public static String toXML(Throwable throwable, long code, String what, String scope, String action, String more, String progammerMessage) {
        String codeString = (code == -1) ? "" : (" Code='" + code + "'");
        throwable = (throwable == null) ? (new Exception(progammerMessage)) : throwable;
        String text =
            "<Exception" + codeString + " Name='" + throwable.getClass().getName() + "'>\n"               +
            "  <Code>" + code + "</Code>\n"                                                               +
            "  <UserMessage>\n"                                                                           +
            "    <What>"   + XML.escapeSpecialChars(what)   + "</What>\n"                                 +
            "    <Scope>"  + XML.escapeSpecialChars(scope)  + "</Scope>\n"                                +
            "    <Action>" + XML.escapeSpecialChars(action) + "</Action>\n"                               +
            "    <More>"   + XML.escapeSpecialChars(more)   + "</More>\n"                                 +
            "  </UserMessage>\n"                                                                          +
            "  <ProgrammerMessage>" + XML.escapeSpecialChars(progammerMessage) + "</ProgrammerMessage>\n" +
            "</Exception>";
        return text;
    }



    /**
     * Format this exception as an XML string, and add on the current stack to
     * aid in tracking down errors.
     * 
     * @return This exception as an XML string.
     */
    public String toXML() {
        StringWriter sw = new StringWriter();
        printStackTrace(new PrintWriter(sw));
        return toXML(this, code, what, scope, action, more, progammerMessage + "\n" + sw.toString());
    }



    /**
     * Construct a serialized response for an exception.
     * 
     * @param service Name of originating service.
     * 
     * @param serviceVersion UtilVersion of originating service.
     * 
     * @param method Server method that was invoked.
     * 
     * @param exception Exception that was thrown.
     * 
     * @return XML string encapsulating the exception.
     */
    public static String serializedResponse(String service, String serviceVersion, String method, Exception exception) {

        String content = null;
        if (exception instanceof UMROException) {
            content = ((UMROException)exception).toXML();
        }
        else {
            content = toXML(exception, UNDEFINED_CODE, "", "", "", "", exception.getMessage());
        }

        return
        "<UMROEnvelope Time='" + timeStampFormat.format(new Date()) + "'>\n" +
        "  <Response>\n"                                                     +
        "    <" + service + " UtilVersion='" + serviceVersion + "'>\n"           +
        "      <" + method + ">\n"                                           +
              content                                                        +
        "      </" + method + ">\n"                                          +
        "    </" + service + ">\n"                                           +
        "  </Response>\n"                                                    +
        "</UMROEnvelope>\n";
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + progammerMessage;
    }

}
