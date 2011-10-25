package com.peepwl.sociagraph.scrape;

import cazcade.common.Logger;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author Neil Ellis
 */

public class AssetScrapeTestRemoved extends TestCase {

    final static Logger log = Logger.getLogger(AssetScrapeTestRemoved.class);

    public void testBBCFeed() {
        final List<String> feeds = new AssetScraper("http://www.bbc.co.uk/news/").scrape().getFeeds();
        for (String feed : feeds) {
            log.info(feed);
        }
        assertTrue(feeds.contains("rss:http://feeds.bbci.co.uk/news/rss.xml"));
    }

    public void testAppleFeed() {
        final List<String> feeds = new AssetScraper("http://apple.com").scrape().getFeeds();
        for (String feed : feeds) {
            log.info(feed);
        }
        assertTrue(feeds.contains("rss:http://images.apple.com/main/rss/hotnews/hotnews.rss"));
    }


    public void testYouTubePage() {
        final List<String> videos = new AssetScraper("http://www.youtube.com/watch?v=Gck5qZWe8pc&feature=youtu.be").scrape().getYouTubeVideos();
        for (String video : videos) {
            log.info(video);
        }
        assertTrue(videos.contains("Gck5qZWe8pc"));
    }


    public void testVideo() {
        final List<String> videos = new AssetScraper("http://mashable.com/2010/07/11/home-improvement-videos/").scrape().getYouTubeVideos();
        for (String video : videos) {
            log.info(video);
        }
        assertTrue(videos.contains("3A11hPLrOIo"));
    }

    public void testImages() {
        final List<String> images = new AssetScraper("http://www.wikipedia.org/").scrape().getImages();
        for (String image : images) {
            log.info(image);
        }
        assertTrue("Did not contain large image.", images.contains("http://upload.wikimedia.org/wikipedia/commons/6/62/174px-Wikipedia-word1_7.png"));
//        assertFalse("Contained small image.", images.contains("http://widgets.digg.com/img/button/diggThisCompact.png"));
    }


}