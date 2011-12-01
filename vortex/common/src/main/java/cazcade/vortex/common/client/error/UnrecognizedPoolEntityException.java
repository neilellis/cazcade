package cazcade.vortex.common.client.error;

/**
 * @author neilellis@cazcade.com
 */
public class UnrecognizedPoolEntityException extends RuntimeException {

    public UnrecognizedPoolEntityException() {
        super();
    }

    public UnrecognizedPoolEntityException(final String s) {
        super(s);
    }

    public UnrecognizedPoolEntityException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public UnrecognizedPoolEntityException(final Throwable throwable) {
        super(throwable);
    }
}
