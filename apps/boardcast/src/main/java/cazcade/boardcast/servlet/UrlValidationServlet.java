/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.servlet;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class UrlValidationServlet extends HttpServlet {
    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String url = request.getParameter("url");
        try {
            final URI uri;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                response.sendError(403, e.getMessage());
                return;
            }

            StatusLine statusLine = getStatusLineForMethod(new HttpHead(uri));
            //If 405 - Invalid Method - Server doesn't support HEAD so try GET instead (which it MUST support to be valid).
            if (statusLine.getStatusCode() == 405) {
                statusLine = getStatusLineForMethod(new HttpGet(uri));
            }
            response.sendError(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        } catch (UnknownHostException uhe) {
            response.sendError(403, "Invalid host " + uhe.getMessage());

        } catch (IOException e) {
            System.out.println("Error for " + url);
            e.printStackTrace();
            response.sendError(500, e.getMessage());
        }

    }

    private StatusLine getStatusLineForMethod(HttpRequestBase head) throws IOException {
        HttpContext context = new BasicHttpContext();
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(head, context);
        HttpHost currentHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
        HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
        return httpResponse.getStatusLine();
    }


}
