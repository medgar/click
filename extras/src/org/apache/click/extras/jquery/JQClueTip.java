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

import java.util.List;
import java.util.ResourceBundle;

import org.apache.click.Context;
import org.apache.click.control.Field;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.util.ClickUtils;

/**
 * Provides a JQuery clueTip popup help message.
 *
 * http://plugins.learningjquery.com/cluetip/
 */
public class JQClueTip {

    /**
     * Enable JQueryclueTip popup help message for the given field.
     *
     * @param field the field to enhance with JQuery clueTip popup help message.
     */
    public static void addHelpTip(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("Null field parameter");
        }

        String tip = field.getHelp();
        if (tip == null) {
            String msg = "Cannot enable JQuery cluetip as field '" + field.getName() + "' has no help property.";
            if (field.getPage() == null) {
                msg += " Please note field cannot resolve parent Page object, and resource properties.";
                throw new IllegalStateException(msg);

            } else {
                throw new IllegalStateException(msg);
            }
        }

        ResourceBundle bundle = ClickUtils.getBundle("click-jquery");

        StringBuilder img = new StringBuilder();
        img.append("<img src=\"");
        img.append(Context.getThreadLocalContext().getRequest().getContextPath());
        img.append(bundle.getString("jquery.cluetip.img"));
        img.append("\" class=\"tooltipImg\" data-title=\"");
        img.append(tip);
        img.append("\"/>");
        field.setHelp(img.toString());

        // Add header elements
        List<Element> headElements = field.getHeadElements();

        headElements.add(new CssImport(bundle.getString("jquery.cluetip.css")));
        headElements.add(new JsImport(bundle.getString("jquery.core.js")));
        headElements.add(new JsImport(bundle.getString("jquery.cluetip.js")));

        JsScript script = new JsScript();
        script.setId("cluetip-setup");
        StringBuilder js = new StringBuilder();
        js.append("$(document).ready(function() { $('.tooltipImg').cluetip({ splitTitle: '|', titleAttribute: 'data-title', showTitle: true, cluetipClass: 'default', dropShadow: false, width: 300, arrows: false }); })");
        script.setContent(js.toString());
        headElements.add(script);
    }

}
