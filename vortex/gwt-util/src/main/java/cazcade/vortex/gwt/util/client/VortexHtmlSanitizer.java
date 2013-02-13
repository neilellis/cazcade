/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;

import com.google.gwt.safehtml.shared.HtmlSanitizer;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple and relatively inexpensive HTML sanitizer.
 * <p/>
 * <p/>
 * This sanitizer accepts the subset of HTML consisting of the following
 * attribute-free tags:
 * <p/>
 * <ul>
 * <li>{@code <b>}, {@code <em>}, {@code <i>}</li>
 * <li>{@code <h1>}, {@code <h2>}, {@code <h3>},
 * {@code <h4>}, {@code <h5>}, {@code <h6>}</li>
 * <li>{@code <ul>}, {@code <ol>}. {@code <li>}</li>
 * <li>{@code <hr>}</li>
 * </ul>
 * <p/>
 * as well as numeric HTML entities and HTML entity references. Any HTML
 * metacharacters that do not appear as part of markup in this subset will be
 * HTML-escaped.
 */
public final class VortexHtmlSanitizer implements HtmlSanitizer {

    private static final VortexHtmlSanitizer INSTANCE      = new VortexHtmlSanitizer();
    private static final Set<String>         TAG_WHITELIST = new HashSet<String>(Arrays.asList("br", "hr", "b", "em", "i", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "ul", "ol", "li"));

    // prevent external instantiation
    private VortexHtmlSanitizer() {
    }

    /**
     * Return a singleton SimpleHtmlSanitizer instance.
     *
     * @return the instance
     */
    public static VortexHtmlSanitizer getInstance() {
        return INSTANCE;
    }

    /**
     * HTML-sanitizes a string.
     * <p/>
     * <p/>
     * The input string is processed as described above. The result of sanitizing
     * the string is guaranteed to be safe to use (with respect to XSS
     * vulnerabilities) in HTML contexts, and is returned as an instance of the
     * {@link com.google.gwt.safehtml.shared.SafeHtml} type.
     *
     * @param html the input String
     * @return a sanitized SafeHtml instance
     */
    public static String sanitizeHtml(String html) {
        if (html == null) {
            throw new NullPointerException("html is null");
        }
        return simpleSanitize(html);
    }

  /*
   * Note: We purposely do not provide a method to create a SafeHtml from
   * another (arbitrary) SafeHtml via sanitization, as this would permit the
   * construction of SafeHtml objects that are not stable in the sense that for
   * a {@code SafeHtml s} it may not be true that {@code s.asString()} equals
   * {@code SimpleHtmlSanitizer.sanitizeHtml(s.asString()).asString()}. While
   * this is not currently an issue, it might become one and result in
   * unexpected behavior if this class were to become serializable and enforce
   * its class invariant upon deserialization.
   */

    /*
     * Sanitize a string containing simple HTML markup as defined above. The
     * approach is as follows: We split the string at each occurence of '<'. Each
     * segment thus obtained is inspected to determine if the leading '<' was
     * indeed the start of a whitelisted tag or not. If so, the tag is emitted
     * unescaped, and the remainder of the segment (which cannot contain any
     * additional tags) is emitted in escaped form. Otherwise, the entire segment
     * is emitted in escaped form.
     *
     * In either case, EscapeUtils.htmlEscapeAllowEntities is used to escape,
     * which escapes HTML but does not double escape existing syntactially valid
     * HTML entities.
     */
    // TODO(xtof): should this be in a utils class?
    private static String simpleSanitize(String text) {
        StringBuilder sanitized = new StringBuilder();

        boolean firstSegment = true;
        for (String segment : text.split("<", -1)) {
            if (firstSegment) {
        /*
         *  the first segment is never part of a valid tag; note that if the
         *  input string starts with a tag, we will get an empty segment at the
         *  beginning.
         */
                firstSegment = false;
                sanitized.append(SafeHtmlUtils.htmlEscapeAllowEntities(segment));
                continue;
            }

      /*
       *  determine if the current segment is the start of an attribute-free tag
       *  or end-tag in our whitelist
       */
            int tagStart = 0; // will be 1 if this turns out to be an end tag.
            int tagEnd = segment.indexOf('>');
            String tag = null;
            boolean isValidTag = false;
            if (tagEnd > 0) {
                if (segment.charAt(0) == '/') {
                    tagStart = 1;
                }
                tag = segment.substring(tagStart, tagEnd);
                if (tag.endsWith("/")) {
                    tag = tag.substring(0, tag.length() - 1);
                }
                if (TAG_WHITELIST.contains(tag.toLowerCase())) {
                    isValidTag = true;
                }
            }

            if (isValidTag) {
                // append the tag, not escaping it
                if (tagStart == 0) {
                    sanitized.append('<');
                }
                else {
                    // we had seen an end-tag
                    sanitized.append("</");
                }
                sanitized.append(tag).append('>');

                // append the rest of the segment, escaping it
                sanitized.append(SafeHtmlUtils.htmlEscapeAllowEntities(segment.substring(tagEnd + 1)));
            }
            else {
                // just escape the whole segment
                sanitized.append("&lt;").append(SafeHtmlUtils.htmlEscapeAllowEntities(segment));
            }
        }
        return sanitized.toString();
    }

    public SafeHtml sanitize(String html) {
        return SafeHtmlUtils.fromTrustedString(sanitizeHtml(html));
    }
}
