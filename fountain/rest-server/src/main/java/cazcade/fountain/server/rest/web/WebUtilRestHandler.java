/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.web;


import cazcade.fountain.server.rest.AbstractRestHandler;

/**
 * @author Neil Ellis
 */

public class WebUtilRestHandler extends AbstractRestHandler {
    /*
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

//    public TransferEntity expand(Map<String, String[]> parameters) throws URISyntaxException {
//        checkForSingleValueParams(parameters, "url");
//        final String url = parameters.get("url")[0];
//        URI uri = shortener.getFullURI(new URI(url));
//        SimpleEntity entity = SimpleEntity.create(Types.WEBPAGE);
//        entity.$(LSDAttributes.SOURCE, uri.toString());
//        return entity;
//    }

    @Nonnull
    public Entity get(@Nonnull final Map<String, String[]> parameters)
            throws URISyntaxException, ExecutionException, InterruptedException {
        checkForSingleValueParams(parameters, "url", "size");
        final String url = parameters.get("url")[0];
        final String size = parameters.get("size")[0];
        final boolean generate = parameters.containsKey("generate");
        return createWebsiteEntity(url, ImageSize.valueOf(size), generate);
    }

    @Nonnull
    private TransferEntity createWebsiteEntity(final String url, final ImageSize size, final boolean generate)
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
                    response = imageService.getCacheURI(new URI(url), size, 0, null, generate);
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
    private TransferEntity convertSnapshotToEntity(final String url, @Nonnull final Future<CacheResponse> futureResponse)
            throws InterruptedException, ExecutionException {
        final TransferEntity responseEntity = SimpleEntity.createEmpty();
        responseEntity.id(UUIDFactory.randomUUID());
        responseEntity.setType(Types.WEBPAGE);
        responseEntity.$(Attribute.NAME, "webpage" + System.currentTimeMillis());
        responseEntity.$(Attribute.SOURCE, url);
        responseEntity.$(Attribute.LINK_EXTERNAL_URL, url);
        final CacheResponse response = futureResponse.get();
        responseEntity.$(Attribute.IMAGE_URL, response.uri().toString());
        responseEntity.$(Attribute.IMAGE_REFRESH, String.valueOf(response.getRefreshIndicator()));
        responseEntity.$(Attribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        responseEntity.$(Attribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        if (response.getImageSize() != null) {
            if (response.getImageSize().x() > 0) {
                responseEntity.$(Attribute.IMAGE_WIDTH, String.valueOf(response.getImageSize().x()));
            }
            if (response.getImageSize().y() > 0) {
                responseEntity.$(Attribute.IMAGE_HEIGHT, String.valueOf(response.getImageSize().y()));
            }
        }
        return responseEntity;
    }

    @Nonnull
    public Entity scrape(@Nonnull final Map<String, String[]> parameters)
            throws URISyntaxException, ExecutionException, InterruptedException {
        final ArrayList<Entity> entities = new ArrayList<Entity>();
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
                final TransferEntity snapshotEntity = convertSnapshotToEntity(url, futureResponse);
                if (favicon != null) {
                    snapshotEntity.$(Attribute.ICON_URL, favicon);
                }
                entities.add(0, snapshotEntity);
            }
        }
        final TransferEntity collectionEntity = SimpleEntity.createEmpty();
        collectionEntity.setType(Types.ENTITY_LIST);
        collectionEntity.$children(Attribute.CHILD, entities);
        return collectionEntity;
    }

    private void addFeeds(@Nonnull final ArrayList<Entity> entities, @Nonnull final List<String> feeds) {
        int count = 1;
        for (final String feed : feeds) {
            try {
                final TransferEntity entity = SimpleEntity.createEmpty();
                if (feed.startsWith("atom")) {
                    entity.setType(Types.ATOM_FEED);
                }
                else if (feed.startsWith("rss")) {
                    entity.setType(Types.RSS_FEED);
                }
                else {
                    entity.setType(Types.RSS_OR_ATOM_FEED);
                }
                final String feedUrl = feed.substring(feed.indexOf(':') + 1);
                entity.$(Attribute.NAME, "feed_" + System.currentTimeMillis() + "_" + count++ + "_" + getNameFromURL(
                        feedUrl
                                                                                                                                  )
                                   );
                entity.$(Attribute.SOURCE, feedUrl);
                final SyndFeedInput input = new SyndFeedInput();
                final SyndFeed syndFeed = input.build(new XmlReader(new URL(feedUrl)));
                if (syndFeed.getAuthor() != null) {
                    final TransferEntity author = SimpleEntity.createEmpty();
                    author.$(Attribute.FULL_NAME, syndFeed.getAuthor());
                    author.id(UUIDFactory.randomUUID());
                    entity.$child(Attribute.AUTHOR, author, true);
                }
//                entity.$(LSDDictionary.CATEGORY_TERM, syndFeed.getCategories());
                entity.$notnull(Attribute.RIGHTS, syndFeed.getCopyright());
                entity.$notnull(Attribute.DESCRIPTION, syndFeed.description());
                entity.$notnull(Attribute.LOCALE_LANGUAGE, syndFeed.getLanguage());
                final ArrayList stringLinks = new ArrayList();
                for (final Object o : syndFeed.getLinks()) {
                    final SyndLink link = (SyndLink) o;
                    stringLinks.add(link.getHref());
                }
                entity.$(Attribute.LINK_EXTERNAL_URL, stringLinks);
                entity.$notnull(Attribute.TITLE, syndFeed.getTitle());
                if (syndFeed.getPublishedDate() != null) {
                    entity.$notnull(Attribute.PUBLISHED, String.valueOf(syndFeed.getPublishedDate().getTime()));
                }
                if (syndFeed.getImage() != null) {
                    entity.$notnull(Attribute.IMAGE_URL, syndFeed.getImage().getUrl());
                    entity.$notnull(Attribute.ICON_URL, syndFeed.getImage().getUrl());
                }
                entity.id(UUIDFactory.randomUUID());
                entities.add(entity);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private String getNameFromURL(final String url) {
        try {
            final String path = new URL(url).path();
            return path.substring(path.lastIndexOf('/') + 1).replaceAll("[^a-zA-Z0-9_]+", "_").toLowerCase();
        } catch (MalformedURLException e) {
            log.warn(e.getMessage(), e);
            return "object";
        }
    }

    private void addYouTubeVideos(@Nonnull final ArrayList<Entity> entities, @Nonnull final List<String> videos) {
        int count = 1;
        for (final String video : videos) {
            try {
                final TransferEntity entity = SimpleEntity.createEmpty();
                entity.$(Attribute.NAME,
                                    "youtube_" + System.currentTimeMillis() + "_" + count++ + "_" + video.replaceAll("[^a-zA-Z0-9]",
                                                                                                                     ""
                                                                                                                    ).toLowerCase()
                                   );
                entity.setType(Types.YOUTUBE_MOVIE);
                entity.$(Attribute.SOURCE, "http://www.youtube.com/v/" + video);
                final YouTubeService service = new YouTubeService("Cazcade", CommonConstants.YOUTUBE_DEVELOPER_KEY);
                log.debug("Video is {0}", video);
                final String apiURL = "http://gdata.youtube.com/feeds/api/videos/" + video;
                final VideoEntry videoEntry = service.getEntry(new URL(apiURL), VideoEntry.class);
                final List<Person> authors = videoEntry.getAuthors();
                final ArrayList<Entity> authorEntities = new ArrayList<Entity>();
                for (final Person author : authors) {
                    final TransferEntity authorEntity = SimpleEntity.createEmpty();
                    authorEntity.setType(Types.ALIAS);
                    authorEntity.$notnull(Attribute.FULL_NAME, author.name());
                    authorEntity.$notnull(Attribute.EMAIL_ADDRESS, author.getEmail());
                    authorEntity.$notnull(Attribute.URI, "alias:youtube:" + author.name());
                    authorEntity.id(UUIDFactory.randomUUID());
                    authorEntities.add(authorEntity);
                }

                if (!authorEntities.isEmpty()) {
                    entity.$children(Attribute.AUTHOR, authorEntities);
                }
                if (videoEntry.getGeoCoordinates() != null) {
                    entity.$notnull(Attribute.LOCATION_LAT, String.valueOf(
                            videoEntry.getGeoCoordinates().getLatitude()
                                                                                             )
                                                   );
                    entity.$notnull(Attribute.LOCATION_LONG, String.valueOf(
                            videoEntry.getGeoCoordinates().getLongitude()
                                                                                              )
                                                   );
                }
//                entity.$(LSDDictionary.CATEGORY_TERM, syndFeed.getCategories());
                if (videoEntry.getRights() != null) {
                    entity.$notnull(Attribute.RIGHTS, videoEntry.getRights().getPlainText());
                }
                if (videoEntry.getSummary() != null) {
                    entity.$notnull(Attribute.DESCRIPTION, videoEntry.getSummary().getPlainText());
                }
//                entity.$notnull(LSDDictionary.LOCALE_LANGUAGE, videoEntry.get);
                if (videoEntry.getTitle() != null) {
                    entity.$notnull(Attribute.TITLE, videoEntry.getTitle().getPlainText());
                }
                if (videoEntry.published() != null) {
                    entity.$notnull(Attribute.PUBLISHED, String.valueOf(videoEntry.published().getValue()));
                }
                if (videoEntry.getMediaGroup() != null) {
                    final List<MediaThumbnail> thumbnails = videoEntry.getMediaGroup().getThumbnails();
                    if (!thumbnails.isEmpty()) {
                        final String thumbUrl = thumbnails.get(0).getUrl();
                        final int thumbWidth = thumbnails.get(0).width();
                        final int thumbHeight = thumbnails.get(0).height();

                        entity.$notnull(Attribute.IMAGE_URL, thumbUrl);
                        entity.$notnull(Attribute.IMAGE_WIDTH, String.valueOf(thumbWidth));
                        entity.$notnull(Attribute.IMAGE_HEIGHT, String.valueOf(thumbHeight));
                    }
                }
                final TransferEntity view = SimpleEntity.createEmpty();
                view.id(UUIDFactory.randomUUID());
                view.setType(Types.VIEW);
                view.$(Attribute.VIEW_WIDTH, "430");
                view.$(Attribute.VIEW_HEIGHT, "385");
                entity.$child(Attribute.VIEW, view, true);
                entity.id(UUIDFactory.randomUUID());
                entities.add(entity);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private void addImages(@Nonnull final ArrayList<Entity> entitiesToAddTo, @Nonnull final List<String> images) {
        final ArrayList<Entity> entities = new ArrayList<Entity>();
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
                        final TransferEntity entity = SimpleEntity.createEmpty();
                        final String photoName = getNameFromURL(image);
                        entity.$(Attribute.NAME,
                                            "image_" + System.currentTimeMillis() + "_" + count++ + "_" + photoName
                                           );
                        entity.setType(Types.BITMAP_IMAGE_2D);
                        entity.$(Attribute.SOURCE, image);
                        entity.$(Attribute.IMAGE_URL, image);
                        entity.$(Attribute.MEDIA_SIZE, sizeStr);
                        entity.$(Attribute.FORMAT, mimeType);
                        entity.id(UUIDFactory.randomUUID());
                        entities.add(entity);
                    }
                }
            } catch (UnknownHostException e) {
                log.debug(e, "Unknown host exception so skipping {0}", image);
            } catch (Exception e) {
                log.error(e);
            }
        }
        Collections.sort(entities, new Comparator<Entity>() {
            public int compare(@Nonnull final Entity o1, @Nonnull final Entity o2) {
                final String sizeStr1 = o1.$(Attribute.MEDIA_SIZE);
                final long size1 = Long.parseLong(sizeStr1);
                final String sizeStr2 = o2.$(Attribute.MEDIA_SIZE);
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
    public Entity board(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "url");
        final String url = parameters.get("url")[0];
        final URI uri = shortener.getShortenedURI(url);
        final TransferEntity entity = SimpleEntity.createNewTransferEntity(Types.WEBPAGE,
                                                                                 UUIDFactory.randomUUID()
                                                                                );
        entity.$(Attribute.SOURCE, uri.toString());
        return entity;
    }

    public void setImageService(final DefaultImageService imageService) {
        this.imageService = imageService;
    }

    public void setShortener(final Shortener shortener) {
        this.shortener = shortener;
    }
    */
}