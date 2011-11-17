package cazcade.boardcast.servlet;

import com.mortennobel.imagescaling.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final boolean debug = true;

    private ServletContext context;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context = config.getServletContext();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = System.currentTimeMillis(); // Just for debugging

        // Get the requested uri as an inputstream
        InputStream is = context.getResourceAsStream(req.getParameter("url"));
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

        if (logger.isLoggable(Level.FINE))
            logger.log(Level.FINE, String.format("Serving image %sx%s scalled to %sx%s with filter '%s' and unshurpenmask '%s' within %smillis", originalImage.getWidth(), originalImage.getHeight(), scaledImage.getWidth(), scaledImage.getHeight(), op.getFilter().getClass(), op.getUnsharpenMask(), System.currentTimeMillis() - start));

        if (debug) {
            resp.setHeader("X-InitialDimensions", String.format("%sx%s", originalImage.getWidth(), originalImage.getHeight()));
            resp.setHeader("X-ScaledDimensions", String.format("%sx%s", scaledImage.getWidth(), scaledImage.getHeight()));
            resp.setHeader("X-ScaledFilter", op.getFilter().getClass().getSimpleName());
            resp.setHeader("X-ScalledUnsharpenMask", op.getUnsharpenMask().toString());
            resp.setHeader("X-ScaledTimeMillis", String.valueOf(System.currentTimeMillis() - start));
        }

        resp.setContentType("image/jpeg");
        ImageIO.write(scaledImage, "jpg", resp.getOutputStream());
        resp.flushBuffer();
    }

    private DimensionConstrain getDimentionConstrainFromRequest(HttpServletRequest req,
                                                                int defwidth,
                                                                int defheight) {
        int width = getInt(req.getParameter("w"), defwidth);
        int height = getInt(req.getParameter("h"), defheight);

        return DimensionConstrain.createMaxDimension(width, height);
    }

    private int getInt(String val, int defaultvalue) {
        try {
            return Integer.parseInt(val);
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