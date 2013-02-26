/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

/**
 * @author neilelliz@cazcade.com
 */
public class TypeException extends RuntimeException {
    public TypeException(final String message, final Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TypeException(final String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TypeException(final Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TypeException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }
}