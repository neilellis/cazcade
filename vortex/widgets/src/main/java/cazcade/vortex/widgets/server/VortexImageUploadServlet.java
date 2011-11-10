package cazcade.vortex.widgets.server;

import com.cazcade.billabong.store.impl.UserContentStore;
import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author neilellis@cazcade.com
 */
public class VortexImageUploadServlet extends UploadAction {
    private final Logger log = LoggerFactory.getLogger(VortexImageUploadServlet.class);
    private static final String TMP_DIR_PATH = "/tmp";

    private UserContentStore contentStore;

    private Executor storeExecutor = Executors.newCachedThreadPool();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{
                "classpath:liquid-spring-config.xml",
                "classpath:spring/image-service.xml"
        }
        );
        contentStore = (UserContentStore) applicationContext.getBean("userContentStore");

    }


    @Override
    public String executeAction(final HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
        try {
            String commaSeparated = "";
            for (final FileItem item : sessionFiles) {
                log.debug("Uploading {0} (size={1})", item.getName(), item.getSize());
                /*
                * Handle Form Fields.
                */
                final int suffixIndex = item.getName().lastIndexOf(".");
                String suffix = suffixIndex >= 0 ? item.getName().substring(suffixIndex) : "";

                final String id = UUID.randomUUID().toString() + suffix;
                String url = contentStore.getStoreURI(id).toString();
                try {
                    contentStore.placeInStore(id, item.getInputStream(), item.getContentType(), true);
                    log.debug("Stored {0}", contentStore.getStoreURI(id));
                    commaSeparated = commaSeparated + url + ",";
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            removeSessionFileItems(request);
            if (commaSeparated.isEmpty()) {
                return "";
            } else {
                return commaSeparated.substring(0, commaSeparated.length() - 1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }


}
