/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

/**
 * @author neilelliz@cazcade.com
 */
public class TypeDefException extends RuntimeException {
    public TypeDefException(final String message, final Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TypeDefException(final String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TypeDefException(final Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TypeDefException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
