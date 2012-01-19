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
import java.util.Date;

/**
 * @author neilelliz@cazcade.com
 */
public class ObjectiveCDictionaryServlet extends HttpServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(ObjectiveCDictionaryServlet.class);

    public void doGet(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setContentType("text/plain");
            final LSDAttribute[] keys = LSDAttribute.values();
            Arrays.sort(keys);
            final LSDDictionaryTypes[] types = LSDDictionaryTypes.values();
            final PrintWriter out = response.getWriter();
            out.printf("//Begin server generated section  (This was generated from %s on %s)%n", request.getRequestURL().toString(),
                       new Date()
                      );
            out.println();
            out.println();
            out.printf("//Server recognized entity types %n");
            out.println();
            out.println();
            for (final LSDDictionaryTypes type : types) {
                final String name = type.name();
                final StringBuffer newName = convertEnumToCamelCase(name);

                out.printf("#define kEntityType%s @\"%s\" //%s%n", newName, type.getValue(), type.getDescription());
            }
            out.println();
            out.println();

            out.printf("//Server recognized entity attributes %n");
            out.println();
            out.println("//////////////////////////////");
            out.println("//   Updateable attributes  //");
            out.println("//////////////////////////////");
            out.println();
            for (final LSDAttribute key : keys) {
                if (key.isUpdateable() && !key.isHidden() && !key.isSubEntity()) {
                    writeOutAttribute(out, key, convertEnumToCamelCase(key.name()));
                }
            }
            out.println();
            out.println();
            out.println("////////////////////////////////");
            out.println("// Non-updateable attributes  //");
            out.println("////////////////////////////////");
            out.println();
            for (final LSDAttribute key : keys) {
                if (!key.isUpdateable() && !key.isHidden() && !key.isSubEntity()) {
                    writeOutAttribute(out, key, convertEnumToCamelCase(key.name()));
                }
            }
            out.println("//End server generated section");

            out.println();
            out.println();
            out.println("////////////////////////////////");
            out.println("//      Sub Entity Keys      //");
            out.println("////////////////////////////////");
            out.println();
            for (final LSDAttribute key : keys) {
                if (!key.isHidden() && key.isSubEntity()) {
                    writeOutAttribute(out, key, convertEnumToCamelCase(key.name()));
                }
            }
            out.println("//End server generated section");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeOutAttribute(@Nonnull final PrintWriter out, @Nonnull final LSDAttribute key, final StringBuffer newName) {
        out.printf("//%s%n", key.getDescription());
        out.printf("#define kAttr%s @\"%s\" //Format is '%s'%n", newName, key.getKeyName(), key.getFormatValidationString());
        out.println();
    }

    @Nonnull
    private StringBuffer convertEnumToCamelCase(@Nonnull final String name) {
        final StringBuffer newName = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            if (i == 0) {
                newName.append(Character.toUpperCase(name.charAt(i)));
            }
            else if (name.charAt(i) == '_') {
                newName.append(Character.toUpperCase(name.charAt(++i)));
            }
            else {
                newName.append(Character.toLowerCase(name.charAt(i)));
            }
        }
        return newName;
    }
}
