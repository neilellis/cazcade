package com.peepwl.sociagraph.scrape;

import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.variables.Variable;
import org.xml.sax.InputSource;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;

/**
 * @author Neil Ellis
 */

public class FeedScraper implements FeedScrapeResult {
    private static ScraperConfiguration config =
            new ScraperConfiguration(new InputSource(FeedScraper.class.getResourceAsStream("/rss-scrape.xml")));

    private List<String> feeds = new ArrayList<String>();
    private String url;
    private String page;

    public FeedScraper(String url) {
        this.url = url;
    }

    public FeedScrapeResult scrape() {
        final File tempFile;
        try {
            tempFile = File.createTempFile("scraper", ".html");
        } catch (IOException e) {
            throw new RuntimeException("Could not create tempfile for scraping : " + e.getMessage(), e);
        }
        try {
            org.webharvest.runtime.Scraper scraper = new org.webharvest.runtime.Scraper(config, tempFile.getAbsolutePath());
            scraper.getHttpClientManager().getHttpClient().getParams().setParameter("http.useragent", "www.peepwl.com - social search");

            scraper.addVariableToContext("url", url);

            scraper.setDebug(false);

            scraper.execute();

            // takes variable created during execution
            Variable result = (Variable) scraper.getContext().get("feeds");
            page = ((Variable) scraper.getContext().get("page")).toString();

            feeds.clear();
            for (Object o : result.toList()) {
                final String s = o.toString();
                if (!feeds.contains(s) && s.length() > 0) {
                    feeds.add(o.toString());
                }
            }
            return this;
        } finally {
            tempFile.delete();

        }
    }

    public List<String> getFeeds() {
        return feeds;
    }

    public String getUrl() {
        return url;
    }

    public String getPage() {
        return page;
    }
}
