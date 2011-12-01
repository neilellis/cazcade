package cazcade.apps.download;

import cazcade.common.Logger;
import com.cazcade.billabong.image.CacheResponse;
import com.cazcade.billabong.image.ImageService;
import com.cazcade.billabong.image.ImageSize;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
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

    @Nonnull
    private static final Logger log = Logger.getLogger(ImageProxyServlet.class);

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
    protected void doGet(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);

        final String url = req.getParameter("url");
        final String size = req.getParameter("size");

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
            response = imageService.getCacheURIForImage(imageUrl, imageSize, true);
            int count = 0;
            while (response.getRefreshIndicator() > 0 && count++ < 4) {
                Thread.sleep(500);
//                Thread.sleep(response.getRefreshIndicator());
                response = imageService.getCacheURIForImage(imageUrl, imageSize, true);
            }

            if (response.getRefreshIndicator() > 0) {
                log.warn("Failed to scale {0}.", url);
                //We temporarily redirect to the unscaled image until the image becomes available in it's scaled form.
                resp.setStatus(307);
                resp.setHeader("Location", url);
                resp.setHeader("Connection", "close");
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
}
