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

final class HtmlEventsAttr {
    private String onclick;
    private String ondblclick;
    private String onmousedown;
    private String onmouseup;
    private String onmouseover;
    private String onmousemove;
    private String onmouseout;
    private String onkeypress;
    private String onkeydown;
    private String onkeyup;
   
    /**
     * @return the onclick
     */
    public String getOnclick() {
        return onclick;
    }
    /**
     * @param onclick the onclick to set
     */
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }
    /**
     * @return the ondblclick
     */
    public String getOndblclick() {
        return ondblclick;
    }
    /**
     * @param ondblclick the ondblclick to set
     */
    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }
    /**
     * @return the onmousedown
     */
    public String getOnmousedown() {
        return onmousedown;
    }
    /**
     * @param onmousedown the onmousedown to set
     */
    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }
    /**
     * @return the onmouseup
     */
    public String getOnmouseup() {
        return onmouseup;
    }
    /**
     * @param onmouseup the onmouseup to set
     */
    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }
    /**
     * @return the onmouseover
     */
    public String getOnmouseover() {
        return onmouseover;
    }
    /**
     * @param onmouseover the onmouseover to set
     */
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }
    /**
     * @return the onmousemove
     */
    public String getOnmousemove() {
        return onmousemove;
    }
    /**
     * @param onmousemove the onmousemove to set
     */
    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }
    /**
     * @return the onmouseout
     */
    public String getOnmouseout() {
        return onmouseout;
    }
    /**
     * @param onmouseout the onmouseout to set
     */
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }
    /**
     * @return the onkeypress
     */
    public String getOnkeypress() {
        return onkeypress;
    }
    /**
     * @param onkeypress the onkeypress to set
     */
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }
    /**
     * @return the onkeydown
     */
    public String getOnkeydown() {
        return onkeydown;
    }
    /**
     * @param onkeydown the onkeydown to set
     */
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }
    /**
     * @return the onkeyup
     */
    public String getOnkeyup() {
        return onkeyup;
    }
    /**
     * @param onkeyup the onkeyup to set
     */
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((onclick == null) ? 0 : onclick.hashCode());
        result = prime * result + ((ondblclick == null) ? 0 : ondblclick.hashCode());
        result = prime * result + ((onkeydown == null) ? 0 : onkeydown.hashCode());
        result = prime * result + ((onkeypress == null) ? 0 : onkeypress.hashCode());
        result = prime * result + ((onkeyup == null) ? 0 : onkeyup.hashCode());
        result = prime * result + ((onmousedown == null) ? 0 : onmousedown.hashCode());
        result = prime * result + ((onmousemove == null) ? 0 : onmousemove.hashCode());
        result = prime * result + ((onmouseout == null) ? 0 : onmouseout.hashCode());
        result = prime * result + ((onmouseover == null) ? 0 : onmouseover.hashCode());
        result = prime * result + ((onmouseup == null) ? 0 : onmouseup.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof HtmlEventsAttr))
            return false;
        HtmlEventsAttr other = (HtmlEventsAttr) obj;
        if (onclick == null) {
            if (other.onclick != null)
                return false;
        } else if (!onclick.equals(other.onclick))
            return false;
        if (ondblclick == null) {
            if (other.ondblclick != null)
                return false;
        } else if (!ondblclick.equals(other.ondblclick))
            return false;
        if (onkeydown == null) {
            if (other.onkeydown != null)
                return false;
        } else if (!onkeydown.equals(other.onkeydown))
            return false;
        if (onkeypress == null) {
            if (other.onkeypress != null)
                return false;
        } else if (!onkeypress.equals(other.onkeypress))
            return false;
        if (onkeyup == null) {
            if (other.onkeyup != null)
                return false;
        } else if (!onkeyup.equals(other.onkeyup))
            return false;
        if (onmousedown == null) {
            if (other.onmousedown != null)
                return false;
        } else if (!onmousedown.equals(other.onmousedown))
            return false;
        if (onmousemove == null) {
            if (other.onmousemove != null)
                return false;
        } else if (!onmousemove.equals(other.onmousemove))
            return false;
        if (onmouseout == null) {
            if (other.onmouseout != null)
                return false;
        } else if (!onmouseout.equals(other.onmouseout))
            return false;
        if (onmouseover == null) {
            if (other.onmouseover != null)
                return false;
        } else if (!onmouseover.equals(other.onmouseover))
            return false;
        if (onmouseup == null) {
            if (other.onmouseup != null)
                return false;
        } else if (!onmouseup.equals(other.onmouseup))
            return false;
        return true;
    }

}
