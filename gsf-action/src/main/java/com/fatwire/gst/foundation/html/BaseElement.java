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
     * @return classname
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#getClassName()
     */
    public final String getClassName() {
        return core.getClassName();
    }

    /**
     * @return id
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#getId()
     */
    public  final String getId() {
        return core.getId();
    }

    /**
     * @return style
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#getStyle()
     */
    public  final String getStyle() {
        return core.getStyle();
    }

    /**
     * @return title
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#getTitle()
     */
    public  final String getTitle() {
        return core.getTitle();
    }

    /**
     * @param className string value of class name
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#setClassName(java.lang.String)
     */
    public  final void setClassName(final String className) {
        core.setClassName(className);
    }

    /**
     * @param id asset id
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#setId(java.lang.String)
     */
    public  final void setId(final String id) {
        core.setId(id);
    }

    /**
     * @param style string value of style
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#setStyle(java.lang.String)
     */
    public  final void setStyle(final String style) {
        core.setStyle(style);
    }

    /**
     * @param title string value of title
     * @see com.fatwire.gst.foundation.html.HtmlCoreAttr#setTitle(java.lang.String)
     */
    public  final void setTitle(final String title) {
        core.setTitle(title);
    }

    /**
     * @return onclick
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnclick()
     */
    public  final String getOnclick() {
        return events.getOnclick();
    }

    /**
     * @return ondblclick
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOndblclick()
     */
    public  final String getOndblclick() {
        return events.getOndblclick();
    }

    /**
     * @return onkeydown
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnkeydown()
     */
    public  final String getOnkeydown() {
        return events.getOnkeydown();
    }

    /**
     * @return event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnkeypress()
     */
    public  final String getOnkeypress() {
        return events.getOnkeypress();
    }

    /**
     * @return event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnkeyup()
     */
    public  final String getOnkeyup() {
        return events.getOnkeyup();
    }

    /**
     * @return event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmousedown()
     */
    public  final String getOnmousedown() {
        return events.getOnmousedown();
    }

    /**
     * @return event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmousemove()
     */
    public  final String getOnmousemove() {
        return events.getOnmousemove();
    }

    /**
     * @return event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmouseout()
     */
    public  final String getOnmouseout() {
        return events.getOnmouseout();
    }

    /**
     * @return event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmouseover()
     */
    public  final String getOnmouseover() {
        return events.getOnmouseover();
    }

    /**
     * @return event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#getOnmouseup()
     */
    public final  String getOnmouseup() {
        return events.getOnmouseup();
    }

    /**
     * @param onclick string value of onClick event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnclick(java.lang.String)
     */
    public  final void setOnclick(final String onclick) {
        events.setOnclick(onclick);
    }

    /**
     * @param ondblclick string value of onDoubleClick event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOndblclick(java.lang.String)
     */
    public  final void setOndblclick(final String ondblclick) {
        events.setOndblclick(ondblclick);
    }

    /**
     * @param onkeydown string value of on key down event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnkeydown(java.lang.String)
     */
    public  final void setOnkeydown(final String onkeydown) {
        events.setOnkeydown(onkeydown);
    }

    /**
     * @param onkeypress string value of key press event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnkeypress(java.lang.String)
     */
    public final void setOnkeypress(final String onkeypress) {
        events.setOnkeypress(onkeypress);
    }

    /**
     * @param onkeyup string value of key up event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnkeyup(java.lang.String)
     */
    public final void setOnkeyup(final String onkeyup) {
        events.setOnkeyup(onkeyup);
    }

    /**
     * @param onmousedown string value of mouse down event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmousedown(java.lang.String)
     */
    public final void setOnmousedown(final String onmousedown) {
        events.setOnmousedown(onmousedown);
    }

    /**
     * @param onmousemove mouse move event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmousemove(java.lang.String)
     */
    public final void setOnmousemove(final String onmousemove) {
        events.setOnmousemove(onmousemove);
    }

    /**
     * @param onmouseout string value of mouse out event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmouseout(java.lang.String)
     */
    public final void setOnmouseout(final String onmouseout) {
        events.setOnmouseout(onmouseout);
    }

    /**
     * @param onmouseover string value of mouseover event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmouseover(java.lang.String)
     */
    public final void setOnmouseover(final String onmouseover) {
        events.setOnmouseover(onmouseover);
    }

    /**
     * @param onmouseup string value of mouse up event
     * @see com.fatwire.gst.foundation.html.HtmlEventsAttr#setOnmouseup(java.lang.String)
     */
    public final void setOnmouseup(final String onmouseup) {
        events.setOnmouseup(onmouseup);
    }

    /**
     * @return dir string value of dir attribute
     * @see com.fatwire.gst.foundation.html.HtmlI18NAttr#getDir()
     */
    public final String getDir() {
        return i18n.getDir();
    }

    /**
     * @return lang string value of internationalization attribute language
     * @see com.fatwire.gst.foundation.html.HtmlI18NAttr#getLang()
     */
    public final String getLang() {
        return i18n.getLang();
    }

    /**
     * @param dir string value of dir
     * @see com.fatwire.gst.foundation.html.HtmlI18NAttr#setDir(java.lang.String)
     */
    public final void setDir(final String dir) {
        i18n.setDir(dir);
    }

    /**
     * @param lang string value of internationalization to set lang
     * @see com.fatwire.gst.foundation.html.HtmlI18NAttr#setLang(java.lang.String)
     */
    public final void setLang(final String lang) {
        i18n.setLang(lang);
    }

}
