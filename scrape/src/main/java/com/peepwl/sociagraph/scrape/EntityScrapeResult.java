package com.peepwl.sociagraph.scrape;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public interface EntityScrapeResult {
    @Nonnull
    List<String> getFeeds();

    @Nonnull
    List<String> getYouTubeVideos();

    @Nonnull
    List<String> getImages();

}
