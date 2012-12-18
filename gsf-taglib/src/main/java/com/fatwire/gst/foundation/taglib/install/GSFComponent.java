package com.fatwire.gst.foundation.taglib.install;

public interface GSFComponent {

    boolean isInstalled();

    boolean install();
    
    String getDescription();

}