package cazcade.apps.upload;

import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDMarshaler;
import cazcade.liquid.impl.LSDMarshallerFactory;
import com.cazcade.billabong.store.impl.UserContentStore;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
    private final static Logger log = Logger.getLogger(LogUploadServlet.class);
    private static final String TMP_DIR_PATH = "/tmp";
    private File tmpDir;
    private ClassPathXmlApplicationContext applicationContext;
    private UserContentStore contentStore;
    private Executor storeExecutor = Executors.newCachedThreadPool();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        applicationContext = new ClassPathXmlApplicationContext(new String[]{
                "liquid-spring-config.xml",
                "spring/image-service.xml"
        }
        );
        contentStore = (UserContentStore) applicationContext.getBean("userContentStore");

        tmpDir = new File(TMP_DIR_PATH);


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final LSDMarshaler marshaler = ((LSDMarshallerFactory) applicationContext.getBean("marshalerFactory")).getMarshalers().get("xml");
        response.setContentType(marshaler.getMimeType());

        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        /*
           *Set the size threshold, above which content will be stored on disk.
           */
        fileItemFactory.setSizeThreshold(1 * 1024 * 1024); //1 MB
        /*
           * Set the temporary directory to store the uploaded files of size above threshold.
           */
        fileItemFactory.setRepository(tmpDir);

        ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        try {
            /*
                * Parse the request
                */
            List items = uploadHandler.parseRequest(request);
            for (Object item1 : items) {
                final FileItem item = (FileItem) item1;
                item.getInputStream();
                String message = request.getParameter("message");
                String summary = request.getParameter("summary");
                String description = request.getParameter("description");
                String session = request.getParameter("session");
                String user = request.getParameter("user");
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