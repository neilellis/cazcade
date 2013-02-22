/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.servlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class LessCompilationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String url = request.getParameter("url");
        String data = IOUtils.toString(new URL(url));
        String filename = DigestUtils.md5Hex(data);
        File inputFile = new File(System.getProperty("user.home", ".") + "/data/phantom/tmp", filename + ".less");
        FileUtils.write(inputFile, data);

        File outputFile = new File(System.getProperty("user.home", ".") + "/data/phantom/tmp", filename + ".css");
        outputFile.getParentFile().mkdirs();
        ProcessBuilder processBuilder = new ProcessBuilder("phantomjs", "--disk-cache=yes", System.getProperty("user.home", ".")
                                                                                            + "/etc/compileless.js", inputFile.toString(), outputFile
                .toString());
        int result = 0;
        try {
            result = ProcessExecutor.execute(processBuilder, System.currentTimeMillis() + 100 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }


        if (!outputFile.exists()) {
            if (result != 0) {
                resp.sendError(503, "Process exited with value " + result);
                throw new RuntimeException("Failed to capture URI image successfully: " +
                                           url + " result was " + result);
            } else {
                resp.sendError(503, "Failed to capture URI image successfully: " +
                                    url + ", image not found.");
            }
        }
        FileInputStream input = new FileInputStream(outputFile);
        try {
            IOUtils.copy(input, resp.getOutputStream());
            return;
        } finally {
            IOUtils.closeQuietly(input);
        }

    }
}
