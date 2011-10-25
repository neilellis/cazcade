package cazcade.fountain.server.rest.servlet;

import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;

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

    private final static Logger log = Logger.getLogger(DictionaryServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            LSDAttribute[] keys = LSDAttribute.values();
            Arrays.sort(keys);
            LSDDictionaryTypes[] types = LSDDictionaryTypes.values();
            PrintWriter out = response.getWriter();
            out.println("<html<head><title>Dictionary</title></head><body>");
            out.println("<h1>Types</h1>");
            out.println("<table>");

            for (LSDDictionaryTypes type : types) {
                out.printf("<tr><td>%s</td><td>%s</td></tr>%n", type.getValue(), type.getDescription());
            }

            out.println("</table>");

            out.println("<h1>Keys</h1>");
            out.println("<table>");
            for (LSDAttribute key : keys) {
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
