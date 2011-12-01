package com.peepwl.sociagraph.scrape;

import cazcade.common.CommonConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Neil Ellis
 */

public class ScrapeUtil {
    public static String cleanURI(final String url) {
        final String s;
        try {
            s = URLEncoder.encode(url, CommonConstants.STRING_ENCODING).replace("%3A", ":").replace("%2F", "/");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return s;
    }
}
