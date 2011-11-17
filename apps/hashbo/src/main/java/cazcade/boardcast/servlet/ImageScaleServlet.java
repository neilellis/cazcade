package cazcade.boardcast.servlet;

import cazcade.common.Logger;
import com.mortennobel.imagescaling.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private static final Logger logger = Logger.getLogger(ImageScaleServlet.class.getName());

    private Cache scaleCache;
    private static final String IMAGE_SCALE_CACHE = "image-scale-cache";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (!CacheManager.getInstance().cacheExists(IMAGE_SCALE_CACHE)) {
            CacheManager.getInstance().addCache(IMAGE_SCALE_CACHE);
        }
        scaleCache = CacheManager.getInstance().getCache(IMAGE_SCALE_CACHE);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final boolean debug = !Logger.isProduction() || req.getParameter("debug") != null;
        long start = System.currentTimeMillis(); // Just for debugging

        // Get the requested uri as an inputstream
        final String url = req.getParameter("url");
        final String w = req.getParameter("w");
        final String h = req.getParameter("h");
        final String key = url + ":" + w + ":" + h;

        if (scaleCache.get(key) == null) {
            InputStream is = new URL(url).openStream();
            if (is == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Could not find the requested resource (%s)", req.getRequestURI()));
                return;
            }

            BufferedImage originalImage = ImageIO.read(is);

            // Compute the dimentions of the scaled image:
            DimensionConstrain dims = getDimentionConstrainFromRequest(req, originalImage.getWidth(), originalImage.getHeight());

            // Initialize a resample operation based on the computed dims
            ResampleOp op = new ResampleOp(dims);

            // If the user defined a filter parameter set it on the operation:
            String filter = req.getParameter("f");
            if (filter != null) op.setFilter(getResampleFilterByName(filter));

            // If the user defined a unsharpenmask parameter set it on the operation:
            String unsharpenmask = req.getParameter("um");
            if (unsharpenmask != null) op.setUnsharpenMask(getUnsharpenMaskByName(unsharpenmask));

            // Create a scaled image:
            BufferedImage scaledImage = op.filter(originalImage, null);
            logger.debug(String.format("Serving image %sx%s scalled to %sx%s with filter '%s' and unshurpenmask '%s' within %smillis", originalImage.getWidth(), originalImage.getHeight(), scaledImage.getWidth(), scaledImage.getHeight(), op.getFilter().getClass(), op.getUnsharpenMask(), System.currentTimeMillis() - start));

            if (debug) {
                resp.setHeader("X-InitialDimensions", String.format("%sx%s", originalImage.getWidth(), originalImage.getHeight()));
                resp.setHeader("X-ScaledDimensions", String.format("%sx%s", scaledImage.getWidth(), scaledImage.getHeight()));
                resp.setHeader("X-ScaledFilter", op.getFilter().getClass().getSimpleName());
                resp.setHeader("X-ScalledUnsharpenMask", op.getUnsharpenMask().toString());
                resp.setHeader("X-ScaledTimeMillis", String.valueOf(System.currentTimeMillis() - start));
            }

            resp.setContentType("image/jpeg");

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(scaledImage, "jpg", byteArrayOutputStream);
            final byte[] bytes = byteArrayOutputStream.toByteArray();
            scaleCache.put(new Element(key, bytes));
            IOUtils.closeQuietly(is);
            IOUtils.write(bytes, resp.getOutputStream());
        } else {
            if (debug) {
                resp.setHeader("X-ScaledImageCacheKey", key);
                resp.setHeader("X-ScaledTimeMillis", String.valueOf(System.currentTimeMillis() - start));
                resp.setHeader("X-ScaledImageCacheStats", scaleCache.getStatistics().toString());
            }
            resp.setContentType("image/jpeg");
            IOUtils.write((byte[]) (scaleCache.get(key).getValue()), resp.getOutputStream());
        }
        resp.flushBuffer();
    }

    private DimensionConstrain getDimentionConstrainFromRequest(HttpServletRequest req,
                                                                int defwidth,
                                                                int defheight) {
        final String w = req.getParameter("w");
        int width = getInt(w, defwidth);
        final String h = req.getParameter("h");
        int height = getInt(h, defheight);

        return DimensionConstrain.createMaxDimension(width, height);
    }

    private int getInt(String val, int defaultvalue) {
        try {
            final int i = Integer.parseInt(val);
            return i > 0 ? i : defaultvalue;
        } catch (Exception e) {
            return defaultvalue;
        }
    }

    private ResampleFilter getResampleFilterByName(String filter) {
        if ("bicubic".equalsIgnoreCase(filter)) return ResampleFilters.getBiCubicFilter();
        if ("bicubichfr".equalsIgnoreCase(filter)) return ResampleFilters.getBiCubicHighFreqResponse();
        if ("bell".equalsIgnoreCase(filter)) return ResampleFilters.getBellFilter();
        if ("box".equalsIgnoreCase(filter)) return ResampleFilters.getBoxFilter();
        if ("bspline".equalsIgnoreCase(filter)) return ResampleFilters.getBSplineFilter();
        if ("hermite".equalsIgnoreCase(filter)) return ResampleFilters.getHermiteFilter();
        if ("mitchell".equalsIgnoreCase(filter)) return ResampleFilters.getMitchellFilter();
        if ("triangle".equalsIgnoreCase(filter)) return ResampleFilters.getTriangleFilter();

        // Return the default filter:
        return ResampleFilters.getLanczos3Filter();
    }

    private AdvancedResizeOp.UnsharpenMask getUnsharpenMaskByName(String mask) {
        try {
            return AdvancedResizeOp.UnsharpenMask.valueOf(mask);
        } catch (Exception e) {
            return AdvancedResizeOp.UnsharpenMask.None;
        }
    }
}