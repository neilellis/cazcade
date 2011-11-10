package cazcade.boardcast.servlet;

import cazcade.common.Logger;
import com.cazcade.billabong.image.CacheResponse;
import com.cazcade.billabong.image.ImageService;
import com.cazcade.billabong.image.ImageSize;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

        final String url = req.getParameter("url");
        final String size = req.getParameter("size");
        final String text = req.getParameter("text");

        if (url == null || url.isEmpty()) {
            sendMissing(resp, width, height, text);
            return;
        }

        final String urlCompareStr = url.toLowerCase();
        //todo: bit of a hack
        boolean isImage = req.getParameter("isImage") != null ||
                urlCompareStr.contains("cloudfiles.rackspacecloud.com") ||
                urlCompareStr.endsWith(".jpeg") ||
                urlCompareStr.endsWith(".gif") ||
                urlCompareStr.endsWith(".jpg") ||
                urlCompareStr.endsWith(".png") ||
                urlCompareStr.endsWith(".tiff");

        if (imageService == null) {
            log.warn("Sending alternate image as image service not ready yet.");
            sendAlternate(resp, width, height, url, isImage);
        }
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
                sendAlternate(resp, width, height, url, isImage);
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

    private void sendAlternate(HttpServletResponse resp, int width, int height, String url, boolean image) throws UnsupportedEncodingException {
        if (image) {
            log.warn("Failed to scale {0}.", url);
            //We temporarily redirect to the unscaled image until the image becomes available in it's scaled form.
            resp.setStatus(307);
            resp.setHeader("Location", url);
            resp.setHeader("Connection", "close");
        } else {
            log.warn("Failed to snapshot {0}.", url);
            sendUrl2PngAlternate(resp, width, height, url);

        }
    }

    private void sendNotReady(HttpServletResponse resp, CacheResponse response, int width, int height) throws UnsupportedEncodingException {
        resp.setStatus(307);
        resp.setHeader("Location", "http://placehold.it/" + width + "x" + height + "&text=Image+Not+Ready");
        if (response == null) {
            resp.setHeader("Refresh", String.valueOf(response.getRefreshIndicator() / 1000));
        }
        resp.setHeader("Connection", "close");
    }

    private void sendMissing(HttpServletResponse resp, int width, int height, String text) throws UnsupportedEncodingException {
        resp.setStatus(307);
        resp.setHeader("Location", "http://placehold.it/" + width + "x" + height + "&text=" + (text != null && !text.isEmpty() ? URLEncoder.encode(text, "utf-8") : "Image+Missing"));
        resp.setHeader("Connection", "close");
    }


    private void sendUrl2PngAlternate(HttpServletResponse resp, int width, int height, String url) throws UnsupportedEncodingException {
        resp.setStatus(307);
        url = URLEncoder.encode(url, "utf-8");
        String url2pngUrl = "http://api.url2png.com/v3/P4EAE9DEAC5242/" + DigestUtils.md5Hex("SA5EC9AA3853DA+" + url) + "/" + width + "x" + height + "/" + url;
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
