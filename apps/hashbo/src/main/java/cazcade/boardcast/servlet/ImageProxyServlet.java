package cazcade.boardcast.servlet;

import cazcade.common.Logger;
import com.cazcade.billabong.image.CacheResponse;
import com.cazcade.billabong.image.ImageService;
import com.cazcade.billabong.image.ImageSize;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * @author neilellis@cazcade.com
 */
public class ImageProxyServlet extends HttpServlet {
    @Nullable
    private ClassPathXmlApplicationContext applicationContext;
    private ImageService imageService;

    @Nonnull
    private static final Logger log = Logger.getLogger(ImageProxyServlet.class);

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        System.setProperty("cloudfiles.apikey", "bee5705ff5df90d7731eabf83b05f7a5");
        System.setProperty("cloudfiles.username", "cazcade");
        final boolean initialised = initialise();
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
        }
        else {
            return true;
        }
    }

    @Override
    protected void doGet(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp)
            throws ServletException, IOException {
//        super.doGet(req, resp);
        final int width = req.getParameter("width") != null ? Integer.parseInt(req.getParameter("width")) : 1024;
        final int height = req.getParameter("height") != null ? Integer.parseInt(req.getParameter("height")) : -1;

        String url = req.getParameter("url");
        final String size = req.getParameter("size");
        final String delayStr = req.getParameter("delay");
        final String waitForWindowStatus = req.getParameter("windowStatus");
        final int delay = delayStr == null ? 10 : Integer.parseInt(delayStr);
        final String text = req.getParameter("text");

        if (url == null || url.isEmpty()) {
            sendMissing(resp, width, height, text);
            return;
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")
                ) {
            url= "http://"+url;
        }
        final String urlCompareStr = url.toLowerCase();
        //todo: bit of a hack
        final boolean isImage = req.getParameter("isImage") != null ||
                                urlCompareStr.contains("cloudfiles.rackspacecloud.com") ||
                                urlCompareStr.endsWith(".jpeg") ||
                                urlCompareStr.endsWith(".gif") ||
                                urlCompareStr.endsWith(".jpg") ||
                                urlCompareStr.endsWith(".png") ||
                                urlCompareStr.endsWith(".tiff");

        if (imageService == null) {
            log.warn("Sending alternate image as image service not ready yet.");
            sendAlternate(resp, width, height, url, isImage, 60 * 1000);
            return;
        }
        try {
            CacheResponse response;

            final ImageSize imageSize;
            if (size != null) {
                imageSize = ImageSize.valueOf(size);
            }
            else {
                imageSize = ImageSize.LARGE;
            }
            final URI imageUrl;
            try {
                imageUrl = new URI(url);
            } catch (URISyntaxException e) {
                log.warn(e.getMessage(), e);
                resp.sendError(500, e.getMessage());
                return;
            }
            response = getCachedImage(imageSize, imageUrl, delay, isImage, waitForWindowStatus);
            int count = 0;
            while (response.getRefreshIndicator() > 0 && count++ < 100) {
                Thread.sleep(1000);
//                Thread.sleep(response.getRefreshIndicator());
                response = getCachedImage(imageSize, imageUrl, delay, isImage, waitForWindowStatus);
            }

            if (response.getRefreshIndicator() > 0) {
                sendAlternate(resp, width, height, url, isImage, response.getRefreshIndicator());
            }
            else {
                req.setAttribute("url", response.getURI().toString());
                req.getRequestDispatcher("_image-scale").forward(req, resp);
//                log.debug("Scaled {0} to {1}.", url, response.getURI().toString());
//                resp.setStatus(301);
//                //cache for about a week (in reality this is a permanent redirect)
//                resp.setHeader("Cache-Control", "max-age=604800");
//                resp.setHeader("Location", scaledImageLocation(response.getURI().toString(), width, height));
//                resp.setHeader("Connection", "close");
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Nonnull
    private String scaledImageLocation(final String url, final int width, final int height) throws UnsupportedEncodingException {
        return "/_image-scale?url=" + URLEncoder.encode(url, "utf8") + "&width=" + width + "&height=" + height;
    }

    private void sendAlternate(@Nonnull final HttpServletResponse resp, final int width, final int height, final String url,
                               final boolean image, final long refreshIndicator) throws UnsupportedEncodingException {
        final String refreshInSecs = String.valueOf(refreshIndicator / 1000);
        if (image) {
            log.warn("Failed to scale {0}.", url);
            //We temporarily redirect to the unscaled image until the image becomes available in it's scaled form.
            resp.setStatus(307);
            resp.setHeader("Cache-Control", "max-age=" + refreshInSecs);
            resp.setHeader("Location", scaledImageLocation(url, width, height));
            resp.setHeader("Connection", "close");
        }
        else {
            log.warn("Failed to snapshot {0}.", url);
            sendNotReady(resp, width, height, refreshInSecs);

        }
    }

    private void sendNotReady(@Nonnull final HttpServletResponse resp, final int width,
                              final int height, final String refreshInSecs) throws UnsupportedEncodingException {
        resp.setStatus(307);
        resp.setHeader("Location", "http://placehold.it/" + width + "x" + (height > 0 ? height : 0) + "&text=Image+Not+Available");
        resp.setHeader("Cache-Control", "max-age=" + refreshInSecs);
        //noinspection VariableNotUsedInsideIf
        resp.setHeader("Refresh", refreshInSecs);
        resp.setHeader("Connection", "close");
    }

    private void sendMissing(@Nonnull final HttpServletResponse resp, final int width, final int height,
                             @Nullable final String text) throws UnsupportedEncodingException {
        resp.setStatus(307);
        resp.setHeader("Location", "http://placehold.it/" +
                                   width +
                                   "x" +
                                   height +
                                   "&text=" +
                                   (text != null && !text.isEmpty() ? URLEncoder.encode(text, "utf-8") : "Image+Missing")
                      );
        resp.setHeader("Connection", "close");
    }


    private void sendUrl2PngAlternate(@Nonnull final HttpServletResponse resp, final int width, final int height, String url,
                                      final String refreshInSecs) throws UnsupportedEncodingException {
        resp.setStatus(307);
        url = URLEncoder.encode(url, "utf-8");
        final String url2pngUrl = "http://api.url2png.com/v3/P4EAE9DEAC5242/" +
                                  DigestUtils.md5Hex("SA5EC9AA3853DA+" + url) +
                                  "/" +
                                  width +
                                  "x" +
                                  height +
                                  "/" +
                                  url;
        resp.setHeader("Cache-Control", "max-age=" + refreshInSecs);
        resp.setHeader("Location", url2pngUrl);
//        if (response == null) {
//            resp.setHeader("Refresh", String.valueOf(response.getRefreshIndicator() / 1000));
//        }
        resp.setHeader("Connection", "close");
    }

    private String encodeForUrl2PNG(String url) {
        url = url.replaceAll("%", "%25");
        url = url.replaceAll(" ", "%20");
        url = url.replaceAll("&", "%26");
        url = url.replaceAll("#", "%23");
        url = url.replaceAll("\\?", "%3F");
        return url;
    }

    private CacheResponse getCachedImage(final ImageSize imageSize, final URI imageUrl, int delay, final boolean isImage,
                                         String waitForWindowStatus) {
        final CacheResponse response;
        if (isImage) {
            response = imageService.getCacheURIForImage(imageUrl, imageSize, true);
        }
        else {
            response = imageService.getCacheURI(imageUrl, imageSize, delay, waitForWindowStatus, true);
        }
        return response;
    }
}
