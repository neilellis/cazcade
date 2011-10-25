package com.peepwl.sociagraph.scrape;

import java.util.List;

/**
 * @author Neil Ellis
 */

public interface FeedScrapeResult {
    
    List<String> getFeeds();

    String getUrl();

    String getPage();
}
