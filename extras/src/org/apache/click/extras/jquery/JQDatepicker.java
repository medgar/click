/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.jquery;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.click.control.TextField;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a JQuery Datepicker Widget TextField control.
 */
public class JQDatepicker extends TextField {

    private static final long serialVersionUID = 1L;

    // Instance Variables -----------------------------------------------------

    /** The DateField's date value. */
    protected Date date;

    /** The date format. */
    protected SimpleDateFormat dateFormat;

    /** The date format pattern value. */
    protected String formatPattern;

    /** The JQ Datepicker function option dateFormat. */
    protected String optionDateFormat;

    /** The DatePicker options. */
    protected Map<String, String> options = new HashMap<String, String>();

    // Constructors -----------------------------------------------------------

    /**
     * Construct the JQuery Datepicker with the given name.
     *
     * @param name the name of the field
     */
    public JQDatepicker(String name) {
        super(name);
    }

    /**
     * Construct the JQuery Datepicker with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public JQDatepicker(String name, String label) {
        super(name, label);
    }

    /**
     * Construct the JQuery Datepicker with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public JQDatepicker(String name, boolean required) {
        this(name);
        setRequired(required);
    }

    /**
     * Construct the JQuery Datepicker with the given name, label and required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public JQDatepicker(String name, String label, boolean required) {
        super(name, label, required);
    }

    /**
     * Construct the JQuery Datepicker with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public JQDatepicker(String name, String label, int size) {
        this(name, label);
        setSize(size);
    }

    /**
     * Construct the JQuery Datepicker with the given name, label, size and
     * required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     * @param required the field required status
     */
    public JQDatepicker(String name, String label, int size, boolean required) {
        this(name, label, required);
        setSize(size);
    }

    /**
     * Create a JQuery Datepicker with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public JQDatepicker() {
        super();
    }

    // Public Methods ---------------------------------------------------------
    /**
     * Return the field Date value, or null if value was empty or a parsing
     * error occurred.
     *
     * @return the field Date value
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the field Date value.
     *
     * @param date the Date value to set
     */
    public void setDate(Date date) {
        this.date = date;
        if (date != null) {
            super.setValue(getDateFormat().format(date));
        } else {
            super.setValue(null);
        }
    }

    /**
     * Return the SimpleDateFormat for the {@link #formatPattern} property.
     *
     * @return the SimpleDateFormat for the formatPattern
     */
    public SimpleDateFormat getDateFormat() {
        if (dateFormat == null) {
            String formatPattern = getFormatPattern();
            dateFormat = new SimpleDateFormat(formatPattern, getLocale());
        }
        return dateFormat;
    }

    /**
     * Return the date format pattern. If the date format pattern is not defined
     * it will be loaded through the method, {@link #loadFormatPattern()}.
     *
     * @return the date format pattern
     */
    public String getFormatPattern() {
        if (formatPattern == null) {
            loadFormatPattern();
        }
        return formatPattern;
    }

    /**
     * Set the SimpleDateFormat pattern. Please note JQuery Datepicker only
     * supports a limited set of Java SimpleDateFormat patterns, and does
     * not support any time components.
     *
     * <h4>SimpleDateFormat Pattern Characters</h4>
     *
     *  <table border="1" cellspacing="0" cellpadding="3">
     *  <tr bgcolor="#ccccff">
     *           <th align=left>Letter
     *           <th align=left>Date or Time Component
     *           <th align=left>Presentation
     *           <th align=left>Examples
     *       <tr bgcolor="#eeeeff">
     *           <td><code>y</code>
     *           <td>Year
     *           <td>Year
     *           <td><code>1996</code>; <code>96</code>
     *       <tr>
     *           <td><code>M</code>
     *           <td>Month in year
     *           <td>Month
     *           <td><code>July</code>; <code>Jul</code>; <code>07</code>
     *       <tr>
     *           <td><code>d</code>
     *           <td>Day in month
     *           <td>Number
     *           <td><code>10</code>
     *           <td><code>-0800</code>
     *   </table>
     *
     * @param pattern the SimpleDateFormat pattern
     */
    public void setFormatPattern(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Null pattern parameter");
        }
        formatPattern = pattern;
        optionDateFormat = parseDateFormatPattern(pattern);
    }

    /**
     * Return the Timestamp value, or null if value was empty
     * or a parsing error occurred.
     *
     * @return the Timestamp value
     */
    public Timestamp getTimestamp() {
        Date date = getDate();

        if (date != null) {
            return new Timestamp(date.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the DateField value.
     *
     * @param value the DateField value
     */
    @Override
    public JQDatepicker setValue(String value) {
        if (value != null && value.length() > 0) {
            try {
                Date parsedDate = getDateFormat().parse(value);

                // Cache date for subsequent retrievals
                date = new Date(parsedDate.getTime());

            } catch (ParseException pe) {
                date = null;
            }

        } else {
            date = null;
        }
        super.setValue(value);
        return this;
    }

    /**
     * Return the field Date value, or null if value was empty or a parsing
     * error occurred.
     *
     * @return the Date object representation of the Field value
     */
    @Override
    public Object getValueObject() {
        return getDate();
    }

    /**
     * Set the date value of the field using the given object.
     *
     * @param object the object value to set
     */
    @Override
    public JQDatepicker setValueObject(Object object) {
        if (object != null) {
            if (Date.class.isAssignableFrom(object.getClass())) {
                setDate((Date) object);

            } else {
                String msg =
                    "Invalid object class: " + object.getClass().getName();
                throw new IllegalArgumentException(msg);
            }
        }
        return this;
    }

    /**
     * Return the JQuery Datepicker function option dateFormat  pattern.
     * <p/>
     * If the date format pattern is not defined it will be loaded through the
     * method {@link #loadFormatPattern()}.
     *
     * @return the JavaScript Calendar pattern
     */
    public String getOptionDateFormat() {
        if (optionDateFormat == null) {
            loadFormatPattern();
        }
        return optionDateFormat;
    }

    /**
     * Add the given name and value to the JQuery datepicker JavaScript
     * function options.
     *
     * @param name the option name
     * @param value the option value
     */
    public void addOption(String name, String value) {
        options.put(name, value);
    }

    /**
     * Add the given name and value to the JQuery datepicker JavaScript
     * function options.
     *
     * @param name the option name
     * @param value the option value
     */
    public void addOption(String name, boolean value) {
        options.put(name, String.valueOf(value));
    }

    /**
     * Add the given name and value to the JQuery datepicker JavaScript
     * function options.
     *
     * @param name the option name
     * @param value the option value
     */
    public void addOption(String name, Number value) {
        options.put(name, String.valueOf(value));
    }

    /**
     * Return the JQuery datepicker JavaScript function options.
     *
     * @return the JQuery datepicker JavaScript function options.
     */
    public Map<String, String> getOptions() {
        return options;
    }

    /**
     * Return the Datepicker HEAD elements to be included in the page. The menu
     * resources are configured in the properties file.
     *
     * <pre>
     * /org/apache/click/extras/jquery/jquery.properties
     * </pre>
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML HEAD elements for the control
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = new ArrayList<Element>();

            ResourceBundle bundle = ClickUtils.getBundle("click-jquery");

            headElements.add(new CssImport(bundle.getString("jquery.theme.css")));
            headElements.add(new JsImport(bundle.getString("jquery.core.js")));
            headElements.add(new JsImport(bundle.getString("jquery.custom.js")));

            HtmlStringBuffer buffer = new HtmlStringBuffer();
            buffer.append("jQuery(document).ready( function() {\n");
            buffer.append(" jQuery(\"#").append(getId()).append("\").datepicker({\n");

            boolean optionAdded = false;
            for (String optionName : getOptions().keySet()) {
                if (optionAdded) {
                    buffer.append(",\n   ");
                } else {
                    buffer.append("   ");
                }
                buffer.append("\"");
                buffer.append(optionName);
                buffer.append("\": ");
                buffer.append(getOptions().get(optionName));
                optionAdded = true;
            }

            // Add date format option
            if (!getOptions().containsKey("dateFormat")) {
                if (optionAdded) {
                    buffer.append(",\n   ");
                } else {
                    buffer.append("   ");
                }
                buffer.append("\"dateFormat\": \"");
                buffer.append(optionDateFormat);
                buffer.append("\"");
                optionAdded = true;
            }

            // Set disabled option
            if (isDisabled()) {
                if (optionAdded) {
                    buffer.append(",\n   ");
                } else {
                    buffer.append("   ");
                }
                buffer.append("\"disabled\": true");
                optionAdded = true;
            }

            buffer.append("\n");

            buffer.append("  });\n");
            buffer.append("});\n");
            JsScript jsScript = new JsScript(buffer.toString());
            headElements.add(jsScript);
        }

        return headElements;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Validate the DateField request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>field-required-error</li>
     *     </ul>
     *   </li>
     *   <li>/org/apache/click/extras/control/DateField.properties
     *     <ul>
     *       <li>date-format-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    @Override
    public void validate() {
        String formatPattern = getFormatPattern();

        if (formatPattern == null) {
            String msg = "dateFormat attribute is null for field: " + getName();
            throw new IllegalStateException(msg);
        }

        super.validate();

        if (isValid() && getValue().length() > 0) {
            SimpleDateFormat dateFormat = getDateFormat();
            dateFormat.setLenient(false);

            try {
                dateFormat.parse(getValue()).getTime();

            } catch (ParseException pe) {
                Object[] args = new Object[] {
                    getErrorLabel(), formatPattern
                };
                setError(getMessage("date-format-error", args));
            }
        }
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Returns the <tt>Locale</tt> that should be used in this control.
     *
     * @return the locale that should be used in this control
     */
    protected Locale getLocale() {
        return getContext().getLocale();
    }

    /**
     * Load the default date format pattern. The format pattern is set to the
     * message <tt>"date-format-pattern"</tt> defined in the resource bundle
     * <tt>/org/apache/click/extras/control/DateField.properties</tt>.
     * <p/>
     * The default date format pattern for English is: <tt>"dd MMM yyyy"</tt>.
     */
    protected void loadFormatPattern() {
        String dateFormatPattern = getMessage("date-format-pattern");
        setFormatPattern(dateFormatPattern);
    }

    /**
     * Return the JQuery Datepicker JS date pattern for the given Java DateFormat
     * pattern.
     *
     * @param pattern the Java DateFormat pattern
     * @return JQuery Datepicker JS date pattern
     */
    protected String parseDateFormatPattern(String pattern) {
        HtmlStringBuffer jsPattern = new HtmlStringBuffer(20);
        int tokenStart = -1;
        int tokenEnd = -1;
        boolean debug = false;

        for (int i = 0; i < pattern.length(); i++) {
            char aChar = pattern.charAt(i);
            if (debug) {
                System.err.print("[" + i + "," + tokenStart + "," + tokenEnd
                                 + "]=" + aChar);
            }

            // If character is in SimpleDateFormat pattern character set
            if ("GyMwWDdFEaHkKhmsSzZ".indexOf(aChar) == - 1) {
                if (debug) {
                    System.err.println(" N");
                }
                if (tokenStart > - 1) {
                    tokenEnd = i;
                }
            } else {
                if (debug) {
                    System.err.println(" Y");
                }
                if (tokenStart == - 1) {
                    tokenStart = i;
                }
            }

            if (tokenStart > -1) {

                if (tokenEnd == -1 && i == pattern.length() - 1) {
                    tokenEnd = pattern.length();
                }

                if (tokenEnd > -1) {
                    String token = pattern.substring(tokenStart, tokenEnd);

                    if ("yyyy".equals(token)) {
                        jsPattern.append("yy");
                    } else if ("yy".equals(token)) {
                        jsPattern.append("y");
                    } else if ("MMMM".equals(token)) {
                        jsPattern.append("MM");
                    } else if ("MMM".equals(token)) {
                        jsPattern.append("M");
                    } else if ("MM".equals(token)) {
                        jsPattern.append("mm");
                    } else if ("M".equals(token)) {
                        jsPattern.append("m");
                    } else if ("dd".equals(token)) {
                        jsPattern.append("dd");
                    } else if ("d".equals(token)) {
                        jsPattern.append("d");
                    } else if ("EEEE".equals(token)) {
                        jsPattern.append("DD");
                    } else if ("EEE".equals(token)) {
                        jsPattern.append("D");
                    } else if ("EE".equals(token)) {
                        jsPattern.append("D");
                    } else if ("E".equals(token)) {
                        jsPattern.append("D");
                    } else {
                        if (debug) {
                            System.err.println("Not mapped:" + token);
                        }
                    }

                    if (debug) {
                        System.err.println("token[" + tokenStart + ","
                                           + tokenEnd + "]='" + token + "'");
                    }
                    tokenStart = -1;
                    tokenEnd = -1;
                }
            }

            if (tokenStart == -1 && tokenEnd == -1) {
                if ("GyMwWDdFEaHkKhmsSzZ".indexOf(aChar) == -1) {
                    jsPattern.append(aChar);
                }
            }
        }

        return jsPattern.toString();
    }

}
