/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.servlet;

import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * @author neilelliz@cazcade.com
 */
public class DictionaryServlet extends HttpServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(DictionaryServlet.class);

    public void doGet(final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        try {
            final LSDAttribute[] keys = LSDAttribute.values();
            Arrays.sort(keys);
            final LSDDictionaryTypes[] types = LSDDictionaryTypes.values();
            final PrintWriter out = response.getWriter();
            out.println("<html<head><title>Dictionary</title></head><body>");
            out.println("<h1>Types</h1>");
            out.println("<table>");

            for (final LSDDictionaryTypes type : types) {
                out.printf("<tr><td>%s</td><td>%s</td></tr>%n", type.getValue(), type.getDescription());
            }

            out.println("</table>");

            out.println("<h1>Keys</h1>");
            out.println("<table>");
            for (final LSDAttribute key : keys) {
                if (!key.isUpdateable()) {
                    out.print("<i>");
                }
                out.printf("<tr><td>%s</td><td>%s</td><td>%s</td></tr>%n", key.getKeyName(), key.getFormatValidationString(), key.getDescription());
                if (!key.isUpdateable()) {
                    out.print("</i>");
                }
            }
            out.println("</table>");
            out.println("</body></html>");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
