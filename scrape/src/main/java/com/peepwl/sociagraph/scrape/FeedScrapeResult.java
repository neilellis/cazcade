package com.peepwl.sociagraph.scrape;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Neil Ellis
 */

public interface FeedScrapeResult {

    @Nonnull
    List<String> getFeeds();

    String getUrl();

    String getPage();
}
