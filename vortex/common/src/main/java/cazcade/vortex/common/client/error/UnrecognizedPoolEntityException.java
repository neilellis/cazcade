package cazcade.vortex.common.client.error;

/**
 * @author neilellis@cazcade.com
 */
public class UnrecognizedPoolEntityException extends RuntimeException {

    public UnrecognizedPoolEntityException() {
        super();
    }

    public UnrecognizedPoolEntityException(String s) {
        super(s);
    }

    public UnrecognizedPoolEntityException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UnrecognizedPoolEntityException(Throwable throwable) {
        super(throwable);
    }
}
