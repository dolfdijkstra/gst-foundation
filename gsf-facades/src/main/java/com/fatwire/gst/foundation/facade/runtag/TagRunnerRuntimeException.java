package com.fatwire.gst.foundation.facade.runtag;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.CSRuntimeException;

/**
 * Exception that is thrown when a TagRunner is invoked and the
 *
 * @author Dolf.Dijkstra
 */
public class TagRunnerRuntimeException extends CSRuntimeException
{
    private final String pageName;
    private final String elementName;
    private final FTValList arguments;
    private static final long serialVersionUID = 5392042951880858120L;

    /**
     * @param msg
     * @param errno
     */
    public TagRunnerRuntimeException(String msg, int errno, final FTValList arguments)
    {
        this(msg, errno, arguments, null, null, null);
    }

    public TagRunnerRuntimeException(String msg, int errno, final FTValList arguments, ftErrors complexError, String pagename, String elementname)
    {
        super(msg, complexError, errno);
        this.arguments = arguments;
        this.pageName = pagename;
        this.elementName = elementname;
    }

    /**
     * @return the pagename that generated this exception or null if this was
     *         not provided
     */
    public String getPageName()
    {
        return pageName;
    }

    /**
     * @return the name of the element that generated this exception or null if
     *         this was not provided
     */
    public String getElementName()
    {
        return elementName;
    }

    public FTValList getArguments()
    {
        return arguments;
    }

    @Override
    public String getMessage()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getMessage());
        builder.append("|");
        builder.append(arguments);
        builder.append("|");
        builder.append(getPageName());
        builder.append("|");
        builder.append(getElementName());
        if(getComplexError() != null)
        {
            builder.append("|");
            builder.append("reason: ").append(getComplexError().getReason());
            builder.append("|message: ");
            builder.append(getComplexError().getMessage());

            int details = getComplexError().details();
            if(details > 0)
            {
                builder.append("|");
            }
            for(int i = 0; i < details; i++)
            {
                builder.append(" ");
                builder.append(getComplexError().detail(i));
            }
        }
        return builder.toString();
    }
}
