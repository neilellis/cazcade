package cazcade.boardcast.servlet;

import cazcade.common.Logger;
import com.mortennobel.imagescaling.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * That means that all the requests ending with .jpg will get served by our servlet. The
 * servlet will check for some parameters in order to decide what to do. The parameters
 * that can be passed are:
 * <p/>
 * <ul>
 * <li><strong>url</strong>: Defining the url of the original image</li>
 * <li><strong>w</strong>: Defining the width of the scaled image</li>
 * <li><strong>h</strong>: Defining the height of the scaled image</li>
 * <li><strong>f</strong>: Defining the a {@link ResampleFilter} to be used with the resize
 * operation and can get one of the following values: bicubic, bicubichfr, bell, box, bspline
 * hermite, mitchell, triangle</li>
 * <li><strong>um</strong>: Defining a {@link AdvancedResizeOp.UnsharpenMask} and can take a value
 * that will evaluated by {@link AdvancedResizeOp.UnsharpenMask#valueOf(String)}</li>
 * </ul>
 * <p/>
 * The resulted image will be a jpg
 *
 * @author valotas@gmail.com
 */
public class ImageScaleServlet extends HttpServlet {
    private static final long serialVersionUID = 896323877253822771L;
    @Nonnull
    private static final Logger logger = Logger.getLogger(ImageScaleServlet.class.getName());
    //todo get this from the original image, or make param or something :-)
    public static final int DEFAULT_SCALED_IMAGE_TTL_SECS = 24 * 3600;
    @Nonnull
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    private Cache scaleCache;
    @Nonnull
    private static final String IMAGE_SCALE_CACHE = "image-scale-cache";

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        if (!CacheManager.getInstance().cacheExists(IMAGE_SCALE_CACHE)) {
            CacheManager.getInstance().addCache(IMAGE_SCALE_CACHE);
        }
        scaleCache = CacheManager.getInstance().getCache(IMAGE_SCALE_CACHE);
    }


    @Override
    protected void doHead(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp)
            throws ServletException, IOException {
        super.doHead(req, resp);
        final String url = req.getAttribute("url") == null ? req.getParameter("url") : (String) req.getAttribute("url");
        final String w = req.getParameter("width");
        final String h = req.getParameter("height");
        String type = getType(req);
        final String key = url + ":" + w + ":" + h + ":" + type;
        if (scaleCache.get(key) != null && req.getHeader(IF_MODIFIED_SINCE) != null) {
            final Element element = scaleCache.get(key);
            element.getCreationTime();
            if (req.getDateHeader(IF_MODIFIED_SINCE) > element.getCreationTime() - 1000) {
                resp.setStatus(304);
            }
            else {
                resp.setStatus(200);
            }
        }
        else {
            resp.setStatus(200);
        }
    }

    private String getType(HttpServletRequest req) {
        String type = req.getParameter("type");
        if (type == null) {
            type = "jpeg";
        }
        return type;
    }

    @Override
    protected void doGet(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp)
            throws ServletException, IOException {
        final boolean debug = !Logger.isProduction() || req.getParameter("debug") != null;
        final long start = System.currentTimeMillis(); // Just for debugging

        // Get the requested uri as an inputstream
        final String url = req.getAttribute("url") == null ? req.getParameter("url") : (String) req.getAttribute("url");
        final String w = req.getParameter("width");
        final String h = req.getParameter("height");
        final String key = url + ":" + w + ":" + h;
        String type = getType(req);

        if (scaleCache.get(key) == null) {

            final InputStream is = new URL(url).openStream();
            if (is == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Could not find the requested resource (%s)",
                                                                               req.getRequestURI()
                                                                              )
                              );
                return;
            }

            BufferedImage originalImage = ImageIO.read(is);
            if (originalImage.getColorModel().getTransparency() != Transparency.OPAQUE) {
                originalImage = fillTransparentPixels(originalImage, Color.WHITE);
            }

            // Compute the dimentions of the scaled image:
            final DimensionConstrain dims = getDimentionConstrainFromRequest(req, originalImage.getWidth(),
                                                                             originalImage.getHeight()
                                                                            );

            // Initialize a resample operation based on the computed dims
            final ResampleOp op = new ResampleOp(dims);

            // If the user defined a filter parameter set it on the operation:
            final String filter = req.getParameter("f");
            if (filter != null) {
                op.setFilter(getResampleFilterByName(filter));
            }

            // If the user defined a unsharpenmask parameter set it on the operation:
            final String unsharpenmask = req.getParameter("um");
            if (unsharpenmask != null) {
                op.setUnsharpenMask(getUnsharpenMaskByName(unsharpenmask));
            }

            // Create a scaled image:
            final BufferedImage scaledImage = op.filter(originalImage, null);
            logger.debug(String.format(
                    "Serving image %sx%s scalled to %sx%s with filter '%s' and unshurpenmask '%s' within %smillis",
                    originalImage.getWidth(), originalImage.getHeight(), scaledImage.getWidth(), scaledImage.getHeight(),
                    op.getFilter().getClass(), op.getUnsharpenMask(), System.currentTimeMillis() - start
                                      )
                        );

            if (debug) {
                resp.setHeader("X-InitialDimensions", String.format("%sx%s", originalImage.getWidth(), originalImage.getHeight()));
                resp.setHeader("X-ScaledDimensions", String.format("%sx%s", scaledImage.getWidth(), scaledImage.getHeight()));
                resp.setHeader("X-ScaledFilter", op.getFilter().getClass().getSimpleName());
                resp.setHeader("X-ScalledUnsharpenMask", op.getUnsharpenMask().toString());
                resp.setHeader("X-ScaledTimeMillis", String.valueOf(System.currentTimeMillis() - start));
            }

            resp.setContentType("image/" + type);

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(scaledImage, type, byteArrayOutputStream);
            final byte[] bytes = byteArrayOutputStream.toByteArray();
            final Element element = new Element(key, bytes, false, DEFAULT_SCALED_IMAGE_TTL_SECS, DEFAULT_SCALED_IMAGE_TTL_SECS);
            scaleCache.put(element);
            setDateHeaders(resp, element);
            IOUtils.closeQuietly(is);
            IOUtils.write(bytes, resp.getOutputStream());
        }
        else {
            if (debug) {
                resp.setHeader("X-ScaledImageCacheKey", key);
                resp.setHeader("X-ScaledTimeMillis", String.valueOf(System.currentTimeMillis() - start));
                resp.setHeader("X-ScaledImageCacheStats", scaleCache.getStatistics().toString());
            }
            //
            final Element element = scaleCache.get(key);
            setDateHeaders(resp, element);
            if (req.getDateHeader(IF_MODIFIED_SINCE) > element.getCreationTime() - 1000) {
                resp.setStatus(304);
            }
            else {
                resp.setContentType("image/" + type);
                IOUtils.write((byte[]) element.getValue(), resp.getOutputStream());
            }

        }
        resp.flushBuffer();
    }

    private void setDateHeaders(@Nonnull final HttpServletResponse resp, @Nonnull final Element element) {
        resp.setDateHeader("Date", System.currentTimeMillis());
        resp.setDateHeader("Last-Modified", element.getLatestOfCreationAndUpdateTime());
        resp.setDateHeader("Expires", element.getExpirationTime());
        resp.setHeader("Cache-Control", "max-age=" + element.getTimeToLive());
    }

    private DimensionConstrain getDimentionConstrainFromRequest(@Nonnull final HttpServletRequest req,
                                                                final int defwidth,
                                                                final int defheight) {
        final String w = req.getParameter("width");
        final int width = getInt(w, defwidth);
        final String h = req.getParameter("height");
        final int height = getInt(h, defheight);

        return DimensionConstrain.createMaxDimension(width, height);
    }

    private int getInt(final String val, final int defaultvalue) {
        try {
            final int i = Integer.parseInt(val);
            return i > 0 ? i : defaultvalue;
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    private ResampleFilter getResampleFilterByName(final String filter) {
        if ("bicubic".equalsIgnoreCase(filter)) {
            return ResampleFilters.getBiCubicFilter();
        }
        if ("bicubichfr".equalsIgnoreCase(filter)) {
            return ResampleFilters.getBiCubicHighFreqResponse();
        }
        if ("bell".equalsIgnoreCase(filter)) {
            return ResampleFilters.getBellFilter();
        }
        if ("box".equalsIgnoreCase(filter)) {
            return ResampleFilters.getBoxFilter();
        }
        if ("bspline".equalsIgnoreCase(filter)) {
            return ResampleFilters.getBSplineFilter();
        }
        if ("hermite".equalsIgnoreCase(filter)) {
            return ResampleFilters.getHermiteFilter();
        }
        if ("mitchell".equalsIgnoreCase(filter)) {
            return ResampleFilters.getMitchellFilter();
        }
        if ("triangle".equalsIgnoreCase(filter)) {
            return ResampleFilters.getTriangleFilter();
        }

        // Return the default filter:
        return ResampleFilters.getLanczos3Filter();
    }

    private AdvancedResizeOp.UnsharpenMask getUnsharpenMaskByName(final String mask) {
        try {
            return AdvancedResizeOp.UnsharpenMask.valueOf(mask);
        } catch (Exception e) {
            return AdvancedResizeOp.UnsharpenMask.None;
        }
    }

    @Nonnull
    public static BufferedImage fillTransparentPixels(@Nonnull final BufferedImage image,
                                                      final Color fillColor) {
        final int w = image.getWidth();
        final int h = image.getHeight();
        final BufferedImage image2 = new BufferedImage(w, h,
                                                       BufferedImage.TYPE_INT_RGB
        );
        final Graphics2D g = image2.createGraphics();
        g.setColor(fillColor);
        g.fillRect(0, 0, w, h);
        g.drawRenderedImage(image, null);
        g.dispose();
        return image2;
    }
}