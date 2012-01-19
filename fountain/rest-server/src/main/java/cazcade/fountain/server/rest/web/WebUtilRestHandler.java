package cazcade.fountain.server.rest.web;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.common.error.ClientCausedException;
import cazcade.fountain.common.error.NormalFlowException;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.impl.UUIDFactory;
import com.cazcade.billabong.image.CacheResponse;
import com.cazcade.billabong.image.ImageSize;
import com.cazcade.billabong.image.impl.DefaultImageService;
import com.cazcade.culvert.Shortener;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.Person;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.youtube.VideoEntry;
import com.peepwl.sociagraph.scrape.AssetScraper;
import com.peepwl.sociagraph.scrape.EntityScrapeResult;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.HeadMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Neil Ellis
 */

public class WebUtilRestHandler extends AbstractRestHandler {
    public static final long MINIMUM_IMAGE_SIZE_IN_BYTES = 10000L;
    public static final int MAX_SNAPSHOT_RETRIES = 3;

    @Nonnull
    private static final Logger log = Logger.getLogger(WebUtilRestHandler.class);

    @Nonnull
    private final ExecutorService snapshotExecutor = new ThreadPoolExecutor(3, 20, MAX_SNAPSHOT_RETRIES, TimeUnit.SECONDS,
                                                                            new ArrayBlockingQueue<Runnable>(10000),
                                                                            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    private DefaultImageService imageService;
    private Shortener shortener;

    public WebUtilRestHandler() {
        super();
    }

//    public LSDTransferEntity expand(Map<String, String[]> parameters) throws URISyntaxException {
//        checkForSingleValueParams(parameters, "url");
//        final String url = parameters.get("url")[0];
//        URI uri = shortener.getFullURI(new URI(url));
//        LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(LSDTypes.WEBPAGE);
//        entity.setAttribute(LSDAttributes.SOURCE, uri.toString());
//        return entity;
//    }

    @Nonnull
    public LSDBaseEntity get(@Nonnull final Map<String, String[]> parameters)
            throws URISyntaxException, ExecutionException, InterruptedException {
        checkForSingleValueParams(parameters, "url", "size");
        final String url = parameters.get("url")[0];
        final String size = parameters.get("size")[0];
        final boolean generate = parameters.containsKey("generate");
        return createWebsiteEntity(url, ImageSize.valueOf(size), generate);
    }

    @Nonnull
    private LSDTransferEntity createWebsiteEntity(final String url, final ImageSize size, final boolean generate)
            throws URISyntaxException, ExecutionException, InterruptedException {
        final Future<CacheResponse> futureResponse = getWebsiteSnapshot(url, size, generate);
        return convertSnapshotToEntity(url, futureResponse);
    }

    private Future<CacheResponse> getWebsiteSnapshot(final String url, final ImageSize size, final boolean generate) {
        return snapshotExecutor.submit(new Callable<CacheResponse>() {
            @Nullable
            public CacheResponse call() throws Exception {
                CacheResponse response = null;
                //temporary, need to get the client to do the polling!
                int count = 0;
                while (response == null || response.getRefreshIndicator() > 0 && count++ < MAX_SNAPSHOT_RETRIES) {
                    response = imageService.getCacheURI(new URI(url), size, generate);
                    try {
                        if (response.getRefreshIndicator() > 0) {
                            Thread.sleep(response.getRefreshIndicator());
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                return response;
            }
        }
                                      );
    }

    @Nonnull
    private LSDTransferEntity convertSnapshotToEntity(final String url, @Nonnull final Future<CacheResponse> futureResponse)
            throws InterruptedException, ExecutionException {
        final LSDTransferEntity responseEntity = LSDSimpleEntity.createEmpty();
        responseEntity.setID(UUIDFactory.randomUUID());
        responseEntity.setType(LSDDictionaryTypes.WEBPAGE);
        responseEntity.setAttribute(LSDAttribute.NAME, "webpage" + System.currentTimeMillis());
        responseEntity.setAttribute(LSDAttribute.SOURCE, url);
        responseEntity.setAttribute(LSDAttribute.LINK_EXTERNAL_URL, url);
        final CacheResponse response = futureResponse.get();
        responseEntity.setAttribute(LSDAttribute.IMAGE_URL, response.getURI().toString());
        responseEntity.setAttribute(LSDAttribute.IMAGE_REFRESH, String.valueOf(response.getRefreshIndicator()));
        responseEntity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        responseEntity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        if (response.getImageSize() != null) {
            if (response.getImageSize().getX() > 0) {
                responseEntity.setAttribute(LSDAttribute.IMAGE_WIDTH, String.valueOf(response.getImageSize().getX()));
            }
            if (response.getImageSize().getY() > 0) {
                responseEntity.setAttribute(LSDAttribute.IMAGE_HEIGHT, String.valueOf(response.getImageSize().getY()));
            }
        }
        return responseEntity;
    }

    @Nonnull
    public LSDBaseEntity scrape(@Nonnull final Map<String, String[]> parameters)
            throws URISyntaxException, ExecutionException, InterruptedException {
        final ArrayList<LSDBaseEntity> entities = new ArrayList<LSDBaseEntity>();
        final HttpClient client = new HttpClient();
        checkForSingleValueParams(parameters, "url");
        final String url = parameters.get("url")[0];
        AssetScraper scraper = null;
        String favicon = null;
        Future<CacheResponse> futureResponse = null;
        try {
            futureResponse = getWebsiteSnapshot(url, ImageSize.CLIPPED_SMALL, true);
        } catch (Exception e) {
            log.error(e);
        }
        final HeadMethod headMethod;
        try {
            headMethod = new HeadMethod(url);
            final int status = client.executeMethod(headMethod);
            if (status >= 400) {
                throw new NormalFlowException("Could not scrape %s due to a http status of %s", url, status);
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new ClientCausedException("Client sent an invald url of %s", url);
        }

        try {
            scraper = new AssetScraper(url);
            final EntityScrapeResult scrapeResult = scraper.scrape();
            favicon = scraper.getFavicon();
            addFeeds(entities, scrapeResult.getFeeds());
            addYouTubeVideos(entities, scrapeResult.getYouTubeVideos());
            addImages(entities, scrapeResult.getImages());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        if (futureResponse != null) {
            //If the snapshot is ready return it, otherwise wait until next time.
            //The client hits us twice anyway!
            if (futureResponse.isDone()) {
                final LSDTransferEntity snapshotEntity = convertSnapshotToEntity(url, futureResponse);
                if (favicon != null) {
                    snapshotEntity.setAttribute(LSDAttribute.ICON_URL, favicon);
                }
                entities.add(0, snapshotEntity);
            }
        }
        final LSDTransferEntity collectionEntity = LSDSimpleEntity.createEmpty();
        collectionEntity.setType(LSDDictionaryTypes.ENTITY_LIST);
        collectionEntity.addSubEntities(LSDAttribute.CHILD, entities);
        return collectionEntity;
    }

    private void addFeeds(@Nonnull final ArrayList<LSDBaseEntity> entities, @Nonnull final List<String> feeds) {
        int count = 1;
        for (final String feed : feeds) {
            try {
                final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
                if (feed.startsWith("atom")) {
                    entity.setType(LSDDictionaryTypes.ATOM_FEED);
                }
                else if (feed.startsWith("rss")) {
                    entity.setType(LSDDictionaryTypes.RSS_FEED);
                }
                else {
                    entity.setType(LSDDictionaryTypes.RSS_OR_ATOM_FEED);
                }
                final String feedUrl = feed.substring(feed.indexOf(':') + 1);
                entity.setAttribute(LSDAttribute.NAME, "feed_" + System.currentTimeMillis() + "_" + count++ + "_" + getNameFromURL(
                        feedUrl
                                                                                                                                  )
                                   );
                entity.setAttribute(LSDAttribute.SOURCE, feedUrl);
                final SyndFeedInput input = new SyndFeedInput();
                final SyndFeed syndFeed = input.build(new XmlReader(new URL(feedUrl)));
                if (syndFeed.getAuthor() != null) {
                    final LSDTransferEntity author = LSDSimpleEntity.createEmpty();
                    author.setAttribute(LSDAttribute.FULL_NAME, syndFeed.getAuthor());
                    author.setID(UUIDFactory.randomUUID());
                    entity.addSubEntity(LSDAttribute.AUTHOR, author, true);
                }
//                entity.setValues(LSDDictionary.CATEGORY_TERM, syndFeed.getCategories());
                entity.setAttributeConditonally(LSDAttribute.RIGHTS, syndFeed.getCopyright());
                entity.setAttributeConditonally(LSDAttribute.DESCRIPTION, syndFeed.getDescription());
                entity.setAttributeConditonally(LSDAttribute.LOCALE_LANGUAGE, syndFeed.getLanguage());
                final ArrayList stringLinks = new ArrayList();
                for (final Object o : syndFeed.getLinks()) {
                    final SyndLink link = (SyndLink) o;
                    stringLinks.add(link.getHref());
                }
                entity.setValues(LSDAttribute.LINK_EXTERNAL_URL, stringLinks);
                entity.setAttributeConditonally(LSDAttribute.TITLE, syndFeed.getTitle());
                if (syndFeed.getPublishedDate() != null) {
                    entity.setAttributeConditonally(LSDAttribute.PUBLISHED, String.valueOf(syndFeed.getPublishedDate().getTime()));
                }
                if (syndFeed.getImage() != null) {
                    entity.setAttributeConditonally(LSDAttribute.IMAGE_URL, syndFeed.getImage().getUrl());
                    entity.setAttributeConditonally(LSDAttribute.ICON_URL, syndFeed.getImage().getUrl());
                }
                entity.setID(UUIDFactory.randomUUID());
                entities.add(entity);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private String getNameFromURL(final String url) {
        try {
            final String path = new URL(url).getPath();
            return path.substring(path.lastIndexOf('/') + 1).replaceAll("[^a-zA-Z0-9_]+", "_").toLowerCase();
        } catch (MalformedURLException e) {
            log.warn(e.getMessage(), e);
            return "object";
        }
    }

    private void addYouTubeVideos(@Nonnull final ArrayList<LSDBaseEntity> entities, @Nonnull final List<String> videos) {
        int count = 1;
        for (final String video : videos) {
            try {
                final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
                entity.setAttribute(LSDAttribute.NAME,
                                    "youtube_" + System.currentTimeMillis() + "_" + count++ + "_" + video.replaceAll("[^a-zA-Z0-9]",
                                                                                                                     ""
                                                                                                                    ).toLowerCase()
                                   );
                entity.setType(LSDDictionaryTypes.YOUTUBE_MOVIE);
                entity.setAttribute(LSDAttribute.SOURCE, "http://www.youtube.com/v/" + video);
                final YouTubeService service = new YouTubeService("Cazcade", CommonConstants.YOUTUBE_DEVELOPER_KEY);
                log.debug("Video is {0}", video);
                final String apiURL = "http://gdata.youtube.com/feeds/api/videos/" + video;
                final VideoEntry videoEntry = service.getEntry(new URL(apiURL), VideoEntry.class);
                final List<Person> authors = videoEntry.getAuthors();
                final ArrayList<LSDBaseEntity> authorEntities = new ArrayList<LSDBaseEntity>();
                for (final Person author : authors) {
                    final LSDTransferEntity authorEntity = LSDSimpleEntity.createEmpty();
                    authorEntity.setType(LSDDictionaryTypes.ALIAS);
                    authorEntity.setAttributeConditonally(LSDAttribute.FULL_NAME, author.getName());
                    authorEntity.setAttributeConditonally(LSDAttribute.EMAIL_ADDRESS, author.getEmail());
                    authorEntity.setAttributeConditonally(LSDAttribute.URI, "alias:youtube:" + author.getName());
                    authorEntity.setID(UUIDFactory.randomUUID());
                    authorEntities.add(authorEntity);
                }

                if (!authorEntities.isEmpty()) {
                    entity.addSubEntities(LSDAttribute.AUTHOR, authorEntities);
                }
                if (videoEntry.getGeoCoordinates() != null) {
                    entity.setAttributeConditonally(LSDAttribute.LOCATION_LAT, String.valueOf(
                            videoEntry.getGeoCoordinates().getLatitude()
                                                                                             )
                                                   );
                    entity.setAttributeConditonally(LSDAttribute.LOCATION_LONG, String.valueOf(
                            videoEntry.getGeoCoordinates().getLongitude()
                                                                                              )
                                                   );
                }
//                entity.setValues(LSDDictionary.CATEGORY_TERM, syndFeed.getCategories());
                if (videoEntry.getRights() != null) {
                    entity.setAttributeConditonally(LSDAttribute.RIGHTS, videoEntry.getRights().getPlainText());
                }
                if (videoEntry.getSummary() != null) {
                    entity.setAttributeConditonally(LSDAttribute.DESCRIPTION, videoEntry.getSummary().getPlainText());
                }
//                entity.setAttributeConditonally(LSDDictionary.LOCALE_LANGUAGE, videoEntry.get);
                if (videoEntry.getTitle() != null) {
                    entity.setAttributeConditonally(LSDAttribute.TITLE, videoEntry.getTitle().getPlainText());
                }
                if (videoEntry.getPublished() != null) {
                    entity.setAttributeConditonally(LSDAttribute.PUBLISHED, String.valueOf(videoEntry.getPublished().getValue()));
                }
                if (videoEntry.getMediaGroup() != null) {
                    final List<MediaThumbnail> thumbnails = videoEntry.getMediaGroup().getThumbnails();
                    if (!thumbnails.isEmpty()) {
                        final String thumbUrl = thumbnails.get(0).getUrl();
                        final int thumbWidth = thumbnails.get(0).getWidth();
                        final int thumbHeight = thumbnails.get(0).getHeight();

                        entity.setAttributeConditonally(LSDAttribute.IMAGE_URL, thumbUrl);
                        entity.setAttributeConditonally(LSDAttribute.IMAGE_WIDTH, String.valueOf(thumbWidth));
                        entity.setAttributeConditonally(LSDAttribute.IMAGE_HEIGHT, String.valueOf(thumbHeight));
                    }
                }
                final LSDTransferEntity view = LSDSimpleEntity.createEmpty();
                view.setID(UUIDFactory.randomUUID());
                view.setType(LSDDictionaryTypes.VIEW);
                view.setAttribute(LSDAttribute.VIEW_WIDTH, "430");
                view.setAttribute(LSDAttribute.VIEW_HEIGHT, "385");
                entity.addSubEntity(LSDAttribute.VIEW, view, true);
                entity.setID(UUIDFactory.randomUUID());
                entities.add(entity);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private void addImages(@Nonnull final ArrayList<LSDBaseEntity> entitiesToAddTo, @Nonnull final List<String> images) {
        final ArrayList<LSDBaseEntity> entities = new ArrayList<LSDBaseEntity>();
        final HttpClient client = new HttpClient();
        int count = 1;
        for (final String image : images) {
            try {
                final HeadMethod headMethod;
                try {
                    headMethod = new HeadMethod(image);
                } catch (IllegalArgumentException iae) {
                    log.warn(iae.getMessage());
                    continue;
                }
                final int status = client.executeMethod(headMethod);
                if (status < 400) {
                    final String sizeStr = headMethod.getResponseHeader("Content-Length") == null
                                           ? "0"
                                           : headMethod.getResponseHeader("Content-Length").getValue();
                    final String mimeType = headMethod.getResponseHeader("Content-Type") == null
                                            ? "application/octet-stream"
                                            : headMethod.getResponseHeader("Content-Type").getValue();
                    final long size = Long.parseLong(sizeStr);
                    if (size >= MINIMUM_IMAGE_SIZE_IN_BYTES) {
                        final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
                        final String photoName = getNameFromURL(image);
                        entity.setAttribute(LSDAttribute.NAME,
                                            "image_" + System.currentTimeMillis() + "_" + count++ + "_" + photoName
                                           );
                        entity.setType(LSDDictionaryTypes.BITMAP_IMAGE_2D);
                        entity.setAttribute(LSDAttribute.SOURCE, image);
                        entity.setAttribute(LSDAttribute.IMAGE_URL, image);
                        entity.setAttribute(LSDAttribute.MEDIA_SIZE, sizeStr);
                        entity.setAttribute(LSDAttribute.FORMAT, mimeType);
                        entity.setID(UUIDFactory.randomUUID());
                        entities.add(entity);
                    }
                }
            } catch (UnknownHostException e) {
                log.debug(e, "Unknown host exception so skipping {0}", image);
            } catch (Exception e) {
                log.error(e);
            }
        }
        Collections.sort(entities, new Comparator<LSDBaseEntity>() {
            public int compare(@Nonnull final LSDBaseEntity o1, @Nonnull final LSDBaseEntity o2) {
                final String sizeStr1 = o1.getAttribute(LSDAttribute.MEDIA_SIZE);
                final long size1 = Long.parseLong(sizeStr1);
                final String sizeStr2 = o2.getAttribute(LSDAttribute.MEDIA_SIZE);
                final long size2 = Long.parseLong(sizeStr2);
                if (size2 > size1) {
                    return 1;
                }
                else if (size2 < size1) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        }
                        );
        entitiesToAddTo.addAll(entities);
    }

    @Nonnull
    public LSDBaseEntity shorten(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "url");
        final String url = parameters.get("url")[0];
        final URI uri = shortener.getShortenedURI(url);
        final LSDTransferEntity entity = LSDSimpleEntity.createNewTransferEntity(LSDDictionaryTypes.WEBPAGE,
                                                                                 UUIDFactory.randomUUID()
                                                                                );
        entity.setAttribute(LSDAttribute.SOURCE, uri.toString());
        return entity;
    }

    public void setImageService(final DefaultImageService imageService) {
        this.imageService = imageService;
    }

    public void setShortener(final Shortener shortener) {
        this.shortener = shortener;
    }
}