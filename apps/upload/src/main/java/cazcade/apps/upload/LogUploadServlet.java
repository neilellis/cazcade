package cazcade.apps.upload;

import cazcade.common.Logger;
import cazcade.liquid.impl.LSDMarshaler;
import cazcade.liquid.impl.LSDMarshallerFactory;
import com.cazcade.billabong.store.impl.UserContentStore;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class LogUploadServlet extends HttpServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(LogUploadServlet.class);
    @Nonnull
    private static final String TMP_DIR_PATH = "/tmp";
    private File tmpDir;
    private ClassPathXmlApplicationContext applicationContext;
    private UserContentStore contentStore;
    private final Executor storeExecutor = Executors.newCachedThreadPool();

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        applicationContext = new ClassPathXmlApplicationContext(
                "liquid-spring-config.xml",
                "spring/image-service.xml");
        contentStore = (UserContentStore) applicationContext.getBean("userContentStore");

        tmpDir = new File(TMP_DIR_PATH);


    }

    protected void doPost(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        final LSDMarshaler marshaler = ((LSDMarshallerFactory) applicationContext.getBean("marshalerFactory")).getMarshalers().get("xml");
        response.setContentType(marshaler.getMimeType());

        final DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        /*
           *Set the size threshold, above which content will be stored on disk.
           */
        fileItemFactory.setSizeThreshold(1 * 1024 * 1024); //1 MB
        /*
           * Set the temporary directory to store the uploaded files of size above threshold.
           */
        fileItemFactory.setRepository(tmpDir);

        final ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        try {
            /*
                * Parse the request
                */
            final List items = uploadHandler.parseRequest(request);
            for (final Object item1 : items) {
                final FileItem item = (FileItem) item1;
                item.getInputStream();
                final String message = request.getParameter("message");
                final String summary = request.getParameter("summary");
                final String description = request.getParameter("description");
                final String session = request.getParameter("session");
                final String user = request.getParameter("user");
                log.setSession(session, user);
                log.sendToJira(message, log.hash(summary), "BUG (CLIENT): " + summary, description, "maelstrom", item.get(), item.getName());
            }
        } catch (FileUploadException ex) {
            log("Error encountered while parsing the request", ex);
        } catch (Exception ex) {
            log("Error encountered while uploading file", ex);
        }

    }

}