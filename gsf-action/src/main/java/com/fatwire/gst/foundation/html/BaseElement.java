/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fatwire.gst.foundation.html;

/**
 * @author Dolf Dijkstra
 * @since Apr 16, 2011
 */
abstract class BaseElement {

    private final HtmlCoreAttr core = new HtmlCoreAttr();
    private final HtmlEventsAttr events = new HtmlEventsAttr();
    private final HtmlI18NAttr i18n = new HtmlI18NAttr();

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#getClassName()
     */
    public String getClassName() {
        return core.getClassName();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#getId()
     */
    public String getId() {
        return core.getId();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#getStyle()
     */
    public String getStyle() {
        return core.getStyle();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#getTitle()
     */
    public String getTitle() {
        return core.getTitle();
    }

    /**
     * @param className
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#setClassName(java.lang.String)
     */
    public void setClassName(final String className) {
        core.setClassName(className);
    }

    /**
     * @param id
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#setId(java.lang.String)
     */
    public void setId(final String id) {
        core.setId(id);
    }

    /**
     * @param style
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#setStyle(java.lang.String)
     */
    public void setStyle(final String style) {
        core.setStyle(style);
    }

    /**
     * @param title
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#setTitle(java.lang.String)
     */
    public void setTitle(final String title) {
        core.setTitle(title);
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnclick()
     */
    public String getOnclick() {
        return events.getOnclick();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOndblclick()
     */
    public String getOndblclick() {
        return events.getOndblclick();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnkeydown()
     */
    public String getOnkeydown() {
        return events.getOnkeydown();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnkeypress()
     */
    public String getOnkeypress() {
        return events.getOnkeypress();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnkeyup()
     */
    public String getOnkeyup() {
        return events.getOnkeyup();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmousedown()
     */
    public String getOnmousedown() {
        return events.getOnmousedown();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmousemove()
     */
    public String getOnmousemove() {
        return events.getOnmousemove();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmouseout()
     */
    public String getOnmouseout() {
        return events.getOnmouseout();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmouseover()
     */
    public String getOnmouseover() {
        return events.getOnmouseover();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmouseup()
     */
    public String getOnmouseup() {
        return events.getOnmouseup();
    }

    /**
     * @param onclick
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnclick(java.lang.String)
     */
    public void setOnclick(final String onclick) {
        events.setOnclick(onclick);
    }

    /**
     * @param ondblclick
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOndblclick(java.lang.String)
     */
    public void setOndblclick(final String ondblclick) {
        events.setOndblclick(ondblclick);
    }

    /**
     * @param onkeydown
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnkeydown(java.lang.String)
     */
    public void setOnkeydown(final String onkeydown) {
        events.setOnkeydown(onkeydown);
    }

    /**
     * @param onkeypress
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnkeypress(java.lang.String)
     */
    public void setOnkeypress(final String onkeypress) {
        events.setOnkeypress(onkeypress);
    }

    /**
     * @param onkeyup
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnkeyup(java.lang.String)
     */
    public void setOnkeyup(final String onkeyup) {
        events.setOnkeyup(onkeyup);
    }

    /**
     * @param onmousedown
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmousedown(java.lang.String)
     */
    public void setOnmousedown(final String onmousedown) {
        events.setOnmousedown(onmousedown);
    }

    /**
     * @param onmousemove
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmousemove(java.lang.String)
     */
    public void setOnmousemove(final String onmousemove) {
        events.setOnmousemove(onmousemove);
    }

    /**
     * @param onmouseout
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmouseout(java.lang.String)
     */
    public void setOnmouseout(final String onmouseout) {
        events.setOnmouseout(onmouseout);
    }

    /**
     * @param onmouseover
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmouseover(java.lang.String)
     */
    public void setOnmouseover(final String onmouseover) {
        events.setOnmouseover(onmouseover);
    }

    /**
     * @param onmouseup
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmouseup(java.lang.String)
     */
    public void setOnmouseup(final String onmouseup) {
        events.setOnmouseup(onmouseup);
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlI18NAttr#getDir()
     */
    public String getDir() {
        return i18n.getDir();
    }

    /**
     * @return
     * @see com.fatwire.gst.foundation.html.HtmlI18NAttr#getLang()
     */
    public String getLang() {
        return i18n.getLang();
    }

    /**
     * @param dir
     * @see com.fatwire.gst.foundation.html.HtmlI18NAttr#setDir(java.lang.String)
     */
    public void setDir(final String dir) {
        i18n.setDir(dir);
    }

    /**
     * @param lang
     * @see com.fatwire.gst.foundation.html.HtmlI18NAttr#setLang(java.lang.String)
     */
    public void setLang(final String lang) {
        i18n.setLang(lang);
    }

}
