package com.fatwire.gst.foundation.controller.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annatation to be used in a Controller to map requests to controller methods.
 * 
 * @author Dolf Dijkstra
 * @since Mar 21, 2011
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IcsVariable {

    String[] var() default {};

}
