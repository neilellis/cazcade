package com.peepwl.sociagraph.scrape;

/**
 * @author Neil Ellis
 */

public class ScrapeException extends RuntimeException {
    public ScrapeException() {
        super();
    }

    public ScrapeException(String s) {
        super(s);
    }

    public ScrapeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ScrapeException(Throwable throwable) {
        super(throwable);
    }
}
