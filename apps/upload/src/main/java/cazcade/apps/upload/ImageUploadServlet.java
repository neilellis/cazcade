package cazcade.apps.upload;

import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.impl.LSDMarshaler;
import cazcade.liquid.impl.LSDMarshallerFactory;
import cazcade.liquid.impl.UUIDFactory;
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


public class ImageUploadServlet extends HttpServlet {
    @Nonnull
    private final static Logger log = Logger.getLogger(ImageUploadServlet.class);
    @Nonnull
    private static final String TMP_DIR_PATH = "/tmp";
    private File tmpDir;
    private File destinationDir;
    private ClassPathXmlApplicationContext applicationContext;
    private UserContentStore contentStore;
    private final Executor storeExecutor = Executors.newCachedThreadPool();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        applicationContext = new ClassPathXmlApplicationContext(new String[]{
                "liquid-spring-config.xml",
                "spring/image-service.xml"
        }
        );
        contentStore = (UserContentStore) applicationContext.getBean("userContentStore");

        tmpDir = new File(TMP_DIR_PATH);
        if (!tmpDir.isDirectory()) {
            throw new ServletException(TMP_DIR_PATH + " is not a directory");
        }
    }

    protected void doPost(HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
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
                log.debug("Uploading {0} (size={1})", item.getName(), item.getSize());
                /*
                     * Handle Form Fields.
                     */
                LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.BITMAP_IMAGE_2D, UUIDFactory.randomUUID());
                entity.setAttribute(LSDAttribute.MEDIA_SIZE, String.valueOf(item.getSize()));
                String suffix = item.getName().substring(item.getName().lastIndexOf("."));
                final String id = entity.getUUID().toString() + suffix;
                String url = contentStore.getStoreURI(id).toString();
                entity.setAttribute(LSDAttribute.IMAGE_URL, url);
                entity.setAttribute(LSDAttribute.SOURCE, url);
                entity.setAttribute(LSDAttribute.NAME, id);
                storeExecutor.execute(new Runnable() {
                    public void run() {
                        try {
                            contentStore.placeInStore(id, item.getInputStream(), item.getContentType(), true);
                            log.debug("Stored {0}", contentStore.getStoreURI(id));
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                });
                marshaler.marshal(entity, response.getOutputStream());
            }
        } catch (FileUploadException ex) {
            log("Error encountered while parsing the request", ex);
        } catch (Exception ex) {
            log("Error encountered while uploading file", ex);
        }

    }

}