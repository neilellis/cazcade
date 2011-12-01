package com.peepwl.sociagraph.scrape;

/**
 * @author Neil Ellis
 */

public class ScrapeException extends RuntimeException {
    public ScrapeException() {
        super();
    }

    public ScrapeException(final String s) {
        super(s);
    }

    public ScrapeException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public ScrapeException(final Throwable throwable) {
        super(throwable);
    }
}
