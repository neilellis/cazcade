package cazcade.fountain.messaging;

/**
 * @author neilellis@cazcade.com
 */
public class FountainPubSubException extends RuntimeException {
    public FountainPubSubException() {
        super();
    }

    public FountainPubSubException(String s) {
        super(s);
    }

    public FountainPubSubException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FountainPubSubException(Throwable throwable) {
        super(throwable);
    }
}
