package com.peepwl.sociagraph.scrape;

import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public interface EntityScrapeResult {
    List<String> getFeeds();

    List<String> getYouTubeVideos();

    List<String> getImages();

}
