package cazcade.apps.download;

import cazcade.common.Logger;
import com.cazcade.billabong.image.CacheResponse;
import com.cazcade.billabong.image.ImageService;
import com.cazcade.billabong.image.ImageSize;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.*;

/**
 * @author neilellis@cazcade.com
 */
public class SnapshotServlet extends HttpServlet {

    @Nonnull
    private final ExecutorService snapshotExecutor = new ThreadPoolExecutor(3, 20, MAX_SNAPSHOT_RETRIES, TimeUnit.SECONDS,
                                                                            new ArrayBlockingQueue<Runnable>(10000),
                                                                            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static final int MAX_SNAPSHOT_RETRIES = 3;

    private ClassPathXmlApplicationContext applicationContext;
    private ImageService imageService;

    @Nonnull
    private static final Logger log = Logger.getLogger(SnapshotServlet.class);

    public void init(final ServletConfig config) throws ServletException {
        try {
            super.init(config);
            applicationContext = new ClassPathXmlApplicationContext(new String[]{
                    "spring/image-service.xml"
            }
            );
            imageService = (ImageService) applicationContext.getBean("imageService");
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    protected void doGet(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp)
            throws ServletException, IOException {
//        super.doGet(req, resp);

        final String url = req.getParameter("url");
        final String size = req.getParameter("size");

        try {
            final CacheResponse response;
            final ImageSize imageSize = ImageSize.valueOf(size);
            final URI imageUrl;
            try {
                imageUrl = new URI(url);
            } catch (URISyntaxException e) {
                log.warn(e.getMessage(), e);
                resp.sendError(500, e.getMessage());
                return;
            }
            response = getWebsiteSnapshot(url, imageSize, true).get();
            if (response.getRefreshIndicator() > 0) {
                log.warn("Failed to snapshot {0}.", url);
                resp.setStatus(408);
            }
            else {
                log.debug("Snapshot for {0} is {1}.", url, response.getURI().toString());
                resp.setStatus(301);
                resp.setHeader("Location", response.getURI().toString());
                resp.setHeader("Connection", "close");
            }
        } catch (Exception e) {
            log.error(e);
        }
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

}
