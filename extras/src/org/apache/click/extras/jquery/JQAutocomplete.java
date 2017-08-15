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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * Provides a JQuery Autocomplete Widget TextField control.
 */
public class JQAutocomplete extends TextField {

    private static final long serialVersionUID = 1L;

    /** The Autocomplete options. */
    protected Map<String, String> options = new HashMap<String, String>();

    // Constructors -----------------------------------------------------------

    /**
     * Create an JQuery Autocomplete text field with the given name.
     *
     * @param name the field name
     */
    public JQAutocomplete(String name) {
        super(name);
    }

    /**
     * Create an JQuery Autocomplete text field with the given name.
     *
     * @param name the field name
     * @param label the field label
     */
    public JQAutocomplete(String name, String label) {
        super(name, label);
    }

    // Public Methods --------------------------------------------------------

    /**
     * Add the given name and value to the JQuery Autocomplete JavaScript
     * function options.
     *
     * @param name the option name
     * @param value the option value
     */
    public void addOption(String name, String value) {
        options.put(name, value);
    }

    /**
     * Add the given name and value to the JQuery Autocomplete JavaScript
     * function options.
     *
     * @param name the option name
     * @param value the option value
     */
    public void addOption(String name, boolean value) {
        options.put(name, String.valueOf(value));
    }

    /**
     * Add the given name and value to the JQuery Autocomplete JavaScript
     * function options.
     *
     * @param name the option name
     * @param value the option value
     */
    public void addOption(String name, Number value) {
        options.put(name, String.valueOf(value));
    }

    /**
     * Return the JQuery Autocomplete JavaScript function options.
     *
     * @return the JQuery Autocomplete JavaScript function options.
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
            buffer.append(" jQuery(\"#").append(getId()).append("\").autocomplete({\n");

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


}
