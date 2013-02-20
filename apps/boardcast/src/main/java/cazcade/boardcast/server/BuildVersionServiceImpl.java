/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.server;

import cazcade.boardcast.client.BuildVersionService;
import cazcade.common.Logger;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author neilellis@cazcade.com
 */
public class BuildVersionServiceImpl extends RemoteServiceServlet implements BuildVersionService {
    @Nonnull
    private static final Logger log = Logger.getLogger(BuildVersionServiceImpl.class);
    private final Properties properties;

    public BuildVersionServiceImpl() throws IOException {
        super();
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("/build.properties"));
    }

    @Override
    public HashMap<String, String> getBuildVersion() {
        HashMap<String, String> map = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            map.put((String) entry.getKey(), (String) entry.getValue());
        }
        return map;
    }

    private String getVersionFromManifest() {
        final String appServerHome = getServletContext().getRealPath("/");

        final File manifestFile = new File(appServerHome, "META-INF/MANIFEST.MF");

        final Manifest mf = new Manifest();
        try {
            mf.read(new FileInputStream(manifestFile));
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        final Attributes atts = mf.getMainAttributes();

        return atts.getValue("Implementation-Build");
    }
}