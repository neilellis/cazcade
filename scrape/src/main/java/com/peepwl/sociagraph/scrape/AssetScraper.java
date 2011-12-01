package com.peepwl.sociagraph.scrape;

import cazcade.common.Logger;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.variables.Variable;
import org.xml.sax.InputSource;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Neil Ellis
 */

public class AssetScraper implements EntityScrapeResult {
    @Nonnull
    private final static Logger log = Logger.getLogger(AssetScraper.class);
    @Nonnull
    private static final ScraperConfiguration config =
            new ScraperConfiguration(new InputSource(AssetScraper.class.getResourceAsStream("/asset-scrape.xml")));

    @Nonnull
    private final List<String> feeds = new ArrayList<String>();
    @Nonnull
    private final List<String> videos = new ArrayList<String>();
    @Nonnull
    private final List<String> images = new ArrayList<String>();
    private final String url;
    private String page;
    private String favicon;


    public static final int IMAGE_SIZE_THRESHOLD = 5000;
    @Nonnull
    public static final String YOU_TUBE_PREFIX = "http://www.youtube.com/watch?v=";
    @Nonnull
    private static final String YOU_TUBE_DIRECT_PREFIX = "http://www.youtube.com/v/";
    @Nonnull
    private static final String MOBILE_YOU_TUBE_PREFIX = "http://m.youtube.com";
    @Nonnull
    public static final String YOU_TUBE_WATCH_PREFIX = YOU_TUBE_PREFIX;
    public static final Pattern VIDEO_PATTERN = Pattern.compile("&v=[a-zA-Z0-9]+");

    public AssetScraper(String url) {
        this.url = url;
    }

    @Nonnull
    public EntityScrapeResult scrape() {

        final File tempFile;
        try {
            tempFile = File.createTempFile("scraper", ".html");
        } catch (IOException e) {
            throw new RuntimeException("Could not create tempfile for scraping : " + e.getMessage(), e);
        }
        String lowerUrl = this.url.toLowerCase();
        try {
            if (lowerUrl.endsWith(".png") || lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".gif")) {
                images.add(url);
                return this;
            } else if (lowerUrl.endsWith(".rss") || lowerUrl.endsWith(".atom") || lowerUrl.endsWith(".xml")) {
                feeds.add(url);
                return this;
                //http://m.youtube.com/#/watch?desktop_uri=http%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3Dxw31AI9N8BY&v=xw31AI9N8BY&gl=GB
            } else if (lowerUrl.startsWith(YOU_TUBE_PREFIX) || lowerUrl.startsWith(MOBILE_YOU_TUBE_PREFIX)) {
                extractYouTubeVideoParam(this.url);
            } else if (lowerUrl.startsWith(YOU_TUBE_DIRECT_PREFIX)) {
                addDirectYouTubeVideoURL(url);
                return this;
            }

            org.webharvest.runtime.Scraper scraper = new org.webharvest.runtime.Scraper(config, tempFile.getAbsolutePath());
            scraper.getHttpClientManager().getHttpClient().getParams().setParameter("http.useragent", "www.cazcade.com - multi-touch social-networking ");

            scraper.addVariableToContext("url", url);

            scraper.setDebug(false);

            scraper.execute();

            // takes variable created during execution
            page = scraper.getContext().get("page").toString();
            favicon = scraper.getContext().get("favicon").toString();
            convertToStringList((Variable) scraper.getContext().get("feeds"), feeds);
            convertToStringList((Variable) scraper.getContext().get("images"), images);
            for (Object video : ((Variable) scraper.getContext().get("videos")).toList()) {
                try {
                    addDirectYouTubeVideoURL(video.toString());
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }

            return this;
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            tempFile.delete();

        }
    }

    private void addDirectYouTubeVideoURL(String urlMinusPrefix) {
        urlMinusPrefix = urlMinusPrefix.substring(urlMinusPrefix.indexOf("/v/") + 3);
        videos.add(urlMinusPrefix.split("&")[0].split("\\?")[0]);
    }

    private void extractYouTubeVideoParam(@Nonnull String url) throws URISyntaxException {
        final int videoParamIndex = url.indexOf("v=");
        if (videoParamIndex < 0) {
            return;
        }
        int nextParamIndex = url.indexOf('&', videoParamIndex + 1);
        if (nextParamIndex < 0) {
            nextParamIndex = url.length();
        }
        videos.add(url.substring(videoParamIndex + 2, nextParamIndex));
    }

    private static void convertToStringList(@Nonnull Variable result, @Nonnull List<String> target) {
        target.clear();
        for (Object o : result.toList()) {
            final String s = o.toString();
            if (!target.contains(s) && s.length() > 0) {
                target.add(o.toString());
            }
        }
    }

    @Nonnull
    public List<String> getFeeds() {
        return feeds;
    }

    @Nonnull
    public List<String> getYouTubeVideos() {
        return videos;
    }

    @Nonnull
    public List<String> getImages() {
        return images;
    }

    public String getUrl() {
        return url;
    }

    public String getPage() {
        return page;
    }

    public String getFavicon() {
        return favicon;
    }
}