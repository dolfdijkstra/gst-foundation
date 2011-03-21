package com.fatwire.gst.foundation.controller.annotation;

import java.lang.reflect.Method;

import COM.FutureTense.Interfaces.ICS;

/**
 * This class resolves a specific method that handles a specific request as a
 * controller. The method needs to be annotated with the IcsVariable annotation
 * and need to accept one argument of type ICS.
 * <p/>
 * The name of the method can be freely choosen. The method needs to have public
 * visibility.
 * 
 * @author Dolf Dijkstra
 * @since Mar 21, 2011
 */
public class ControllerMappingResolver {

    /**
     * @param ics
     * @param o object with method annations of type IcsVariable
     * @return
     */
    public Method findControllerMethod(ICS ics, Object o) {
        for (Method m : o.getClass().getMethods()) {
            IcsVariable p = m.getAnnotation(IcsVariable.class);
            if (p != null) {
                for (String param : p.var()) {
                    String[] split = param.split("=");
                    if (split[1].equals(ics.GetVar(split[0]))) {

                        if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0].equals(ICS.class))
                            return m;
                        throw new UnsupportedOperationException("Method " + m.getName()
                                + " does not have a single argument of type ICS though the method is annotated "
                                + "with a IcsVariable annation.");

                    }
                }
            }

        }
        return null;
    }
}
