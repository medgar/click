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

import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.extras.control.Menu;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a JQuery Desktop style Menu.
 * <p/>
 * This menu is based on the JQuery Menu plug-in by Roman Weich.
 *
 * @see Menu
 */
public class JQMenu extends Menu {

    private static final long serialVersionUID = 1L;

    // Constructors -----------------------------------------------------------

    /**
     * Create a JQuery Desktop Menu object.
     */
    public JQMenu() {
    }

    /**
     * Create a JQuery Desktop Menu object with the given name.
     *
     * @param name the name of the menu
     */
    public JQMenu(String name) {
        super(name);
    }

    // Public Methods --------------------------------------------------------

    /**
     * Return the HTML href attribute value.
     *
     * @return the HTML href attribute value
     */
    @Override
    public String getHref() {
        if (getPath() == null) {
            setPath("#");
        } else if (hasChildren() && "".equals(getPath())) {
            setPath("#");
        }
        return super.getHref();
    }

    /**
     * Return the Menu HEAD elements to be included in the page. The menu
     * resources are configured in the properties file.
     *
     * <pre>
     * /org/apache/click/extras/jquery/jquery.properties
     * </pre>
     *
     * @see Menu#getHeadElements()
     *
     * @return the HTML HEAD elements for the control
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = new ArrayList<Element>();

            ResourceBundle bundle = ClickUtils.getBundle("click-jquery");

            headElements.add(new CssImport(bundle.getString("jquery.desktopmenu.css")));
            headElements.add(new JsImport(bundle.getString("jquery.core.js")));
            headElements.add(new JsImport(bundle.getString("jquery.desktopmenu.js")));

            JsScript jsScript = getJsTemplate(bundle);
            headElements.add(jsScript);

            // IE6 browser div and select burn through fix
            String userAgent = getContext().getRequest().getHeader("User-Agent");
            if (userAgent != null && userAgent.contains("MSIE 6.0")) {

                JsImport jsImport = new JsImport("/click/menu-fix-ie6.js");
                jsImport.setConditionalComment(JsImport.IF_LESS_THAN_IE7);
                headElements.add(jsImport);

                // ID is created by jQuery menu plugin
                String id = "root-menu-div";

                HtmlStringBuffer buffer = new HtmlStringBuffer();
                buffer.append("jQuery(document).ready( function() {\n");
                buffer.append(" if(typeof Click != 'undefined' && typeof Click.menu != 'undefined') {\n");
                buffer.append("   if(typeof Click.menu.fixHiddenMenu != 'undefined') {\n");
                buffer.append("     Click.menu.fixHiddenMenu(\"").append(id).append("\");\n");
                buffer.append("   }\n");
                buffer.append(" }\n");
                buffer.append("});\n");
                jsScript = new JsScript(buffer.toString());
                headElements.add(jsScript);
            }
        }

        return headElements;
    }

    /**
     * Render the HTML representation of the Menu.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        buffer.elementStart("ul");

        int depth = 0;
        renderMenuClassAttribute(buffer, this, depth);
        String id = getAttribute("id");
        id = (id != null) ? id : getName();
        buffer.appendAttribute("id", id);
        buffer.closeTag();
        buffer.append("\n");

        renderMenu(buffer, this, depth);

        buffer.elementEnd("ul");
    }

    /**
     * Render the HTML representation of the Menu.
     *
     * @return the HTML representation of the Menu
     */
    @Override
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(256);
        render(buffer);
        return buffer.toString();
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Return the JS menu template for the request context.
     *
     * @param bundle the resource bundle
     * @return the JsScript element include
     */
    protected JsScript getJsTemplate(ResourceBundle bundle) {
        String id = getAttribute("id");
        id = (id != null) ? id : getName();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("context", getContext().getRequest().getContextPath());
        model.put("selector", '#' + id);
        JsScript jsScript = new JsScript(bundle.getString("jquery.desktopmenu.template"), model);
        return jsScript;
    }

    /**
     * Render the given menu.
     *
     * @param buffer the buffer to render to
     * @param menu the menu to render
     * @param depth the depth of the menu in the hierarchy
     */
    protected void renderMenu(HtmlStringBuffer buffer, Menu menu, int depth) {
        for (Menu child : menu.getChildren()) {

            if (displayMenu(child, depth + 1)) {
                buffer.elementStart("li");
                renderMenuItemClassAttribute(buffer, child, depth);
                buffer.closeTag();
                renderMenuLink(buffer, child);
                if (child.getChildren().size() > 0) {
                    buffer.elementStart("ul");
                    renderMenuClassAttribute(buffer, child, depth + 1);
                    buffer.closeTag();
                    buffer.append("\n");
                    renderMenu(buffer, child, depth + 1);
                    buffer.elementEnd("ul");
                    buffer.append("\n");
                }
                buffer.elementEnd("li");
                buffer.append("\n");
            }
        }
    }

    /**
     * Return true if the menu item should be displayed.
     *
     * @param menu the menu item to test
     * @param depth the menu item depth
     * @return true if the menu item should be displayed
     */
    protected boolean displayMenu(Menu menu, int depth) {
        if (isHidden(menu)) {
            return false;
        }

        if (menu.isUserInRoles() || menu.isUserInChildMenuRoles()) {
            return true;
        }
        return false;
    }

    /**
     * Render the given menu as a link.
     *
     * @param buffer the buffer to render to
     * @param menu the menu to render as a link
     */
    @Override
    protected void renderMenuLink(HtmlStringBuffer buffer, Menu menu) {
        if (menu.isSeparator()) {
            renderSeparator(buffer, menu);
            return;
        }

        buffer.elementStart("a");
        String id = menu.getAttribute("id");
        if (id != null) {
            buffer.appendAttribute("id", id);

        }

        String href = menu.getHref();
        buffer.appendAttribute("href", href);
        if (menu.hasAttributes()) {
            buffer.appendAttributes(menu.getAttributes());
        }
        if ("#".equals(href)) {
            //If hyperlink does not return false here, clicking on it will scroll
            //to the top of the page.
            buffer.appendAttribute("onclick", "return false;");
        }
        String menuTitle = menu.getTitle();
        String menuLabel = menu.getLabel();

        if (StringUtils.isNotBlank(menu.getTarget())) {
            buffer.appendAttribute("target", menu.getTarget());
        }

        buffer.appendAttribute("title", menuTitle);
        buffer.closeTag();
        buffer.append("\n");

        if (StringUtils.isNotBlank(menu.getImageSrc())) {
            buffer.elementStart("img");
            buffer.appendAttribute("border", "0");
            buffer.appendAttribute("class", "link");

            if (menuTitle != null) {
                buffer.appendAttributeEscaped("alt", menuTitle);
            } else {
                buffer.appendAttributeEscaped("alt", menuLabel);
            }

            String src = menu.getImageSrc();
            if (StringUtils.isNotBlank(src)) {
                if (src.charAt(0) == '/') {
                    src = getContext().getRequest().getContextPath() + src;
                }
                buffer.appendAttribute("src", src);
            }

            buffer.elementEnd();

            if (menuLabel != null) {
                buffer.append(menuLabel);
            }

        } else {
            buffer.append(menuLabel);
        }

        buffer.elementEnd("a");
        buffer.append("\n");
    }

    /**
     * Return the menu css class that is applied to the &lt;ul&gt; element.
     *
     * @param buffer the buffer to render the class attribute to
     * @param menu the menu to render
     * @param depth the depth of the menu in the hierarchy
     */
    protected void renderMenuClassAttribute(HtmlStringBuffer buffer, Menu menu, int depth) {
        if (depth == 0) {
            buffer.appendAttribute("class", "desktopmenu");
        } else {
            buffer.appendAttribute("class", "submenu");
        }
        buffer.append(" ");
    }

    /**
     * Return the menu item css class that is applied to the &lt;li&gt; element.
     *
     * @param buffer the buffer to render the class attribute to
     * @param menu the menu to render
     * @param depth the depth of the menu in the hierarchy
     */
    protected void renderMenuItemClassAttribute(HtmlStringBuffer buffer, Menu menu, int depth) {
        buffer.append(" class=\"menuitem");
        if (depth == 0) {
            buffer.append(" topitem");

            if (menu.isSelected()) {
                buffer.append(" activetarget");

            } else {
                for (Menu child : menu.getChildren()) {
                    if (child.isSelected()) {
                        buffer.append(" activetarget");
                    }
                }
            }

            if (!menu.hasChildren()) {
                buffer.append(" empty");
            }
        }

        buffer.append("\" ");
    }

    /**
     * Render the menu separator.
     *
     * @param buffer the HTML string buffer to render to
     * @param menu the menu item separator to render
     */
    @Override
    protected void renderSeparator(HtmlStringBuffer buffer, Menu menu) {
    }

    /**
     * Return true if the menu should item should hidden not be rendered. By
     * nesting page hidden menu items under a parent menu you can have the
     * top level menu item highlighted.
     *
     * @param menu the menu item to test
     * @return true if the menu should item should not be rendered.
     */
    protected boolean isHidden(Menu menu) {
        return "hidden".equalsIgnoreCase(menu.getLabel());
    }

}
