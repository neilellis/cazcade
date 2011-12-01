package com.peepwl.sociagraph.scrape;

import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.ScraperContext;
import org.xml.sax.InputSource;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * @author Neil Ellis
 */

public class Scraper {
    @Nonnull
    private final ScraperConfiguration config;
    private String url;
    private String page;

    public Scraper(final String configFile) {
        config = new ScraperConfiguration(new InputSource(Scraper.class.getResourceAsStream(configFile)));
    }

    public ScraperContext scrape(String url) {

        final File tempFile;
        try {
            tempFile = File.createTempFile("scraper", ".html");
        } catch (IOException e) {
            throw new ScrapeException("Could not create tempfile for scraping : " + e.getMessage(), e);
        }
        try {
            org.webharvest.runtime.Scraper scraper = new org.webharvest.runtime.Scraper(config, tempFile.getAbsolutePath());
            scraper.getHttpClientManager().getHttpClient().getParams().setParameter("http.useragent", "www.cazcade.com - multi-touch social networking");
            scraper.addVariableToContext("url", url);
            scraper.setDebug(false);

            scraper.execute();
            return scraper.getContext();
        } catch (Exception e) {
            throw new ScrapeException(e.getMessage(), e);
        } finally {
            tempFile.delete();
        }
    }

}