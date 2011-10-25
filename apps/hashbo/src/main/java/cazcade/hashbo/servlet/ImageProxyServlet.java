package cazcade.hashbo.servlet;

import cazcade.common.Logger;
import com.cazcade.billabong.image.CacheResponse;
import com.cazcade.billabong.image.ImageService;
import com.cazcade.billabong.image.ImageSize;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author neilellis@cazcade.com
 */
public class ImageProxyServlet extends HttpServlet {
    private ClassPathXmlApplicationContext applicationContext;
    private ImageService imageService;

    private final static Logger log = Logger.getLogger(ImageProxyServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        boolean initialised = initialise();
//        if (!initialised) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (imageService == null) {
//                        if (initialise()) return;
//
//                    }
//                }
//            }).start();
//        }
        if (!initialised) {
            throw new ServletException("Could not initialise Image Proxy");
        }

    }

    private boolean initialise() {
        try {
            applicationContext = new ClassPathXmlApplicationContext(new String[]{
                    "classpath:spring/image-service.xml"
            }
            );
            imageService = (ImageService) applicationContext.getBean("imageService");
        } catch (Exception e) {
            log.error(e);
        }
        if (imageService == null) {
            if (applicationContext != null) {
                applicationContext.destroy();
                applicationContext = null;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        int width = req.getParameter("width") != null ? Integer.parseInt(req.getParameter("width")) : 300;
        int height = req.getParameter("height") != null ? Integer.parseInt(req.getParameter("height")) : 200;

        if (imageService == null) {
            sendDefault(resp, null, width, height);

        }
        final String url = req.getParameter("url");
        final String size = req.getParameter("size");

        final String urlCompareStr = url.toLowerCase();
        //todo: bit of a hack
        boolean isImage = req.getParameter("isImage") != null ||
                urlCompareStr.contains("cloudfiles.rackspacecloud.com") ||
                urlCompareStr.endsWith(".jpeg") ||
                urlCompareStr.endsWith(".gif") ||
                urlCompareStr.endsWith(".jpg") ||
                urlCompareStr.endsWith(".png") ||
                urlCompareStr.endsWith(".tiff");

        try {
            CacheResponse response;
            final ImageSize imageSize = ImageSize.valueOf(size);
            final URI imageUrl;
            try {
                imageUrl = new URI(url);
            } catch (URISyntaxException e) {
                log.warn(e.getMessage(), e);
                resp.sendError(500, e.getMessage());
                return;
            }
            response = getCachedImage(imageSize, imageUrl, isImage);
            int count = 0;
            while (response.getRefreshIndicator() > 0 && count++ < 4) {
                Thread.sleep(500);
//                Thread.sleep(response.getRefreshIndicator());
                response = getCachedImage(imageSize, imageUrl, isImage);
            }

            if (response.getRefreshIndicator() > 0) {
                if (isImage) {
                    log.warn("Failed to scale {0}.", url);
                    //We temporarily redirect to the unscaled image until the image becomes available in it's scaled form.
                    resp.setStatus(307);
                    resp.setHeader("Location", url);
                    resp.setHeader("Connection", "close");
                } else {
                    log.warn("Failed to snapshot {0}.", url);
                    sendDefault(resp, response, width, height);

                }
            } else {
                log.debug("Scaled {0} to {1}.", url, response.getURI().toString());
                resp.setStatus(301);
                resp.setHeader("Location", response.getURI().toString());
                resp.setHeader("Connection", "close");
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void sendDefault(HttpServletResponse resp, CacheResponse response, int width, int height) {
        resp.setStatus(307);
        resp.setHeader("Location", "http://placehold.it/" + width + "x" + height + "&text=Image+Not+Ready");
        if (response == null) {
            resp.setHeader("Refresh", String.valueOf(response.getRefreshIndicator() / 1000));
        }
        resp.setHeader("Connection", "close");
    }

    private CacheResponse getCachedImage(ImageSize imageSize, URI imageUrl, boolean isImage) {
        CacheResponse response;
        if (isImage) {
            response = imageService.getCacheURIForImage(imageUrl, imageSize, true);
        } else {
            response = imageService.getCacheURI(imageUrl, imageSize, true);
        }
        return response;
    }
}
