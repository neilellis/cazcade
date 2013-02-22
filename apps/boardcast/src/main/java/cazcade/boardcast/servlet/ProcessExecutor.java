/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class ProcessExecutor {
    public static int execute(ProcessBuilder processBuilder, long maxEndTime) throws InterruptedException {
        int result = 0;
        System.out.println(processBuilder.command());
        processBuilder.redirectErrorStream(true);
        try {
            Process captureProcess = processBuilder.start();
            InputStreamReader inputStream = new InputStreamReader(new BufferedInputStream(captureProcess.getInputStream()));
            boolean done = false;
            try {
                StringBuffer output = new StringBuffer();
                char[] buffer = new char[4096];
                while (!done && System.currentTimeMillis() < maxEndTime) {
                    try {
                        result = captureProcess.exitValue();
                        done = true;
                        System.out.println(output);
                        return result;

                    } catch (IllegalThreadStateException e) {
                        //expected - work not yet done...
                        //The only case I've yet found where an empty catch block may be justified.
                    }
                    if (inputStream.ready()) {
                        int length = inputStream.read(buffer);
                        if (length >= 0) {
                            output.append(buffer, 0, length);
                        }
                    } else {
                        Thread.sleep(100);
                    }

                }
                System.err.println(output);
            } finally {
                inputStream.close();
                if (!done) {
                    System.out.println("Timed out destroying process.");
                    captureProcess.destroy();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }
}
