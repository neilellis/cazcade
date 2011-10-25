package cazcade.liquid.api.lsd;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDTypeException extends RuntimeException{
    public LSDTypeException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public LSDTypeException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public LSDTypeException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public LSDTypeException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}