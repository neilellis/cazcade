package cazcade.boardcast.server;

import cazcade.boardcast.client.BuildVersionService;
import cazcade.common.Logger;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author neilellis@cazcade.com
 */
public class BuildVersionServiceImpl extends RemoteServiceServlet implements BuildVersionService {
    private final static Logger log = Logger.getLogger(BuildVersionServiceImpl.class);

    @Override
    public String getBuildVersion() {

        String appServerHome = getServletContext().getRealPath("/");

        File manifestFile = new File(appServerHome, "META-INF/MANIFEST.MF");

        Manifest mf = new Manifest();
        try {
            mf.read(new FileInputStream(manifestFile));
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        Attributes atts = mf.getMainAttributes();

        return atts.getValue("Implementation-Version");


    }
}