package cazcade.vortex.common.client;

import cazcade.liquid.api.LiquidBoardURL;

/**
 * @author neilellis@cazcade.com
 */
public class FormatUtil {

    public static final String SMILEY_STRING = String.valueOf((char) 0x263a);
    public static final String SAD_SMILEY_STRING = String.valueOf((char) 0x2639);
    public static final String SMILEY_STRING_SLANT_MOUTH = String.valueOf((char) 0x30C4);
    public static final String RIGHT_POINT = String.valueOf((char) 0x261e);
    public static final String LEFT_POINT = String.valueOf((char) 0x261c);
    public static final String HEART = String.valueOf((char) 0x2665);
    public static final String PEACE = String.valueOf((char) 0x262E);
    public static final String CROSSBONES = String.valueOf((char) 0x2620);
    public static final String BIOHAZRD = String.valueOf((char) 0x2623);
    public static final String YING_YANG = String.valueOf((char) 0x262F);
    public static final String NUCLEAR = String.valueOf((char) 0x2620);
    public static final String WHEEL = String.valueOf((char) 0x2638);
    public static final String SUN = String.valueOf((char) 0x263C);
    public static final String NOTE = String.valueOf((char) 0x266C);
    public static final String RECYCLE = String.valueOf((char) 0x267B);
    public static final String STAR = String.valueOf((char) 0x2605);

    public static final String FEMALE = String.valueOf((char) 0x2640);
    public static final String MALE = String.valueOf((char) 0x2642);
    public static final String ANCHOR = String.valueOf((char) 0x2693);
    public static final String BLACK_FLAG = String.valueOf((char) 0x2691);
    public static final String MOON = String.valueOf((char) 0x263E);
    public static final String TELEPHONE = String.valueOf((char) 0x260E);
    public static final String CLOUD = String.valueOf((char) 0x2601);

    public static final String COFFEE = String.valueOf((char) 0x2615);
    public static final String UMBRELLA = String.valueOf((char) 0x2602);
    public static final String SNOWMAN = String.valueOf((char) 0x2603);

    public static final String BUNNY = String.valueOf((char) 0x2649);
    public static final String DISABLED = String.valueOf((char) 0x267f);
    public static final String NEWLINE_MARKER = "~~~NEWLINE~~~";


    private FormatUtil() {
    }

    public static FormatUtil getInstance() {
        return new FormatUtil();
    }


    public String sanitize(String text) {
        if(text == null) {
            return null;
        }
        text = text.replaceAll("\\{", "@LB@");
        text = text.replaceAll("\\}", "@RB@");
        text = text.replaceAll("<br/?>", "{br/}");
        text = text.replaceAll("<(/?)div[^>]*>", "{$1div}");
        text = text.replaceAll("<(/?)p[^>]*>", "{$1p}");
        text = text.replaceAll("<(/?)h1[^>]*>", "{$1h1}");
        text = text.replaceAll("<(/?)h2[^>]*>", "{$1h2}");
        text = text.replaceAll("<(/?)h3[^>]*>", "{$1h3}");
        text = text.replaceAll("<(/?)h4[^>]*>", "{$1h4}");
        text = text.replaceAll("<(/?)h5[^>]*>", "{$1h5}");
        text = text.replaceAll("<(/?)h6[^>]*>", "{$1h6}");
        text = text.replaceAll("<b[^>]*>(.*)</b>", "{b}$1{/b}");
        text = text.replaceAll("<i[^>]*>(.*)</i>", "{i}$1{/i}");
        text = text.replaceAll("<u[^>]*>(.*)</u>", "{u}$1{/u}");
        text = text.replaceAll("<[^>]*>", "");
        text = text.replaceAll("\\{", "<");
        text = text.replaceAll("\\}", ">");
        text = text.replaceAll("@LB@", "\\{");
        text = text.replaceAll("@RB@", "\\}");

        return text;
    }


    public String formatRichText(String text) {
        if (text == null) {
            return "";
        }
        String[] split = text.split("`");
        String result = "";
        int count = 0;
        for (String s : split) {

            if (count++ % 2 == 0) {
                result += formatTextInternal(s);
            } else {
                result += "<pre><code class='prettyprint'>" + prettyPrint(s) + "</pre></code>";
            }

        }
        return result;
    }

    private String formatTextInternal(String text) {
        if (text == null) {
            return "";
        }
//        text = text.replaceAll("`([^`]*)`", "<pre><code>$1</code></pre>");
        text = text.replaceAll("(^|\\W+)\\_([^_]+)\\_", "$1<u>$2</u>");
        text = text.replaceAll("(^|\\W+)(http[s]?://[^<> ]+)", "$1<a href='$2' class='external-link'>$2</a>");
//        text = text.replaceAll("^(#[a-zA-Z]+[a-zA-Z0-9./_\\-\\+@]*[a-zA-Z0-9]+)", " #$1");
        text = text.replaceAll("\\*\\*([^\\*]+)\\*\\*", "<b>$1</b>");
        text = text.replaceAll("\\*([^\\*]+)\\*", "<i>$1</i>");
        text = text.replaceAll("&nbsp;", " ");
        text = text.replaceAll("(^|>)#### ([^#]+) ####($|<)", "$1<h4>$2</h4>$3");
        text = text.replaceAll("(^|>)### ([^#]+) ###($|<)", "$1<h3>$2</h3>$3");
        text = text.replaceAll("(^|>)## ([^#]+) ##($|<)", "$1<h2>$2</h2>$3");
        text = text.replaceAll("(^|>)# ([^#]+) #($|<)", "$1<h1>$2</h1>$3");
        text = text.replaceAll("\\*{4,}", "<hr/>");
        text = text.replaceAll("-{4,}", "<hr/>");
        text = text.replaceAll("(- ){4,}", "<hr/>");
        text = text.replaceAll("(\\* ){4,}", "<hr/>");
        text = text.replaceAll("&#39;", "'");
        text = text.replaceAll("<a\\s", "<a target='_new' ");
        text = text.replaceAll("(^|[^\\w]+)(#[a-zA-Z]+[a-zA-Z0-9./_\\-\\+@]*[a-zA-Z0-9]+)", "$1<a href='$2' class='board-link'>$2</a>");
        text = text.replaceAll("(^|[^\\w]+)(@[a-zA-Z]+[a-zA-Z0-9./_\\-\\+]*[a-zA-Z0-9]+)", "$1<a href='#$2' class='person-link'>$2</a>");

//        html = html.replaceAll("(^|\\s+)#\\.([a-zA-Z0-9./_\\+]*[a-zA-Z0-9]+)", "$1<a href='#" + boardName + ".$2'>#.$2</a>");


//        html = html.replaceAll("(^|\\s+)(![a-zA-Z]+)", "$1<a href='#$2'>$2</a>");
//        html = html.replaceAll("(^|[^\\w]+)_(\\w.*\\w)_($|\\s+)", "$1<u>$2</u>$3");
//        html = html.replaceAll("(^|\\W+)\\*([^*]+)\\*", "$1<b>$2</b>");

        text = translateNamedEmoticonSymbol(text, "peace", PEACE);
        text = translateNamedEmoticonSymbol(text, "bio", BIOHAZRD);
        text = translateNamedEmoticonSymbol(text, "yingyang", YING_YANG);
        text = translateNamedEmoticonSymbol(text, "nuclear", NUCLEAR);
        text = translateNamedEmoticonSymbol(text, "wheel", WHEEL);
        text = translateNamedEmoticonSymbol(text, "sun", SUN);
        text = translateNamedEmoticonSymbol(text, "note", NOTE);
        text = translateNamedEmoticonSymbol(text, "recycle", RECYCLE);
        text = translateNamedEmoticonSymbol(text, "star", STAR);
        text = translateNamedEmoticonSymbol(text, "skull", CROSSBONES);

        text = translateNamedEmoticonSymbol(text, "female", FEMALE);
        text = translateNamedEmoticonSymbol(text, "male", MALE);
        text = translateNamedEmoticonSymbol(text, "anchor", ANCHOR);
        text = translateNamedEmoticonSymbol(text, "flag", BLACK_FLAG);
        text = translateNamedEmoticonSymbol(text, "moon", MOON);
        text = translateNamedEmoticonSymbol(text, "tel", TELEPHONE);
        text = translateNamedEmoticonSymbol(text, "cloud", CLOUD);
        text = translateNamedEmoticonSymbol(text, "coffee", COFFEE);
        text = translateNamedEmoticonSymbol(text, "umbrella", UMBRELLA);
        text = translateNamedEmoticonSymbol(text, "snowman", SNOWMAN);

        text = translateNamedEmoticonSymbol(text, "disabled", DISABLED);
        text = translateNamedEmoticonSymbol(text, "bunnyhead", BUNNY);


        text = translateEmoticonSymbol(text, "->", RIGHT_POINT);
        text = translateEmoticonSymbol(text, "<-", LEFT_POINT);
        text = translateEmoticonSymbol(text, "<3", HEART);

        text = translateEmoticonSymbol(text, "-&gt;", RIGHT_POINT);
        text = translateEmoticonSymbol(text, "&lt;-", LEFT_POINT);
        text = translateEmoticonSymbol(text, "&lt;3", HEART);

        text = text.replaceAll("(^|\\W]*)(:\\-/)", "$1" + SMILEY_STRING_SLANT_MOUTH);
        text = text.replaceAll("(^|\\W]*)(:\\-\\))", "$1" + SMILEY_STRING);
        text = text.replaceAll("(^|\\W]*)(:\\))", "$1" + SMILEY_STRING);
        text = text.replaceAll("(^|\\W]*)(:\\-\\()", "$1" + SAD_SMILEY_STRING);
        text = text.replaceAll("(^|\\W]*)(:\\()", "$1" + SAD_SMILEY_STRING);


        return text;
    }

    public String formatPoolName(String pool) {
        if (pool == null) {
            return "";
        }
        return LiquidBoardURL.convertToShort(pool);
    }

    public String translateNamedEmoticonSymbol(String html, String symbol, String unicode) {
        return html.replaceAll("(\\(" + symbol + "\\))", "<span class='emoticon emoticon-" + symbol + "'>" + unicode + "</span>");
    }

    public String translateEmoticonSymbol(String html, String symbol, String unicode) {
        return html.replaceAll("(" + symbol + ")", "<span class='emoticon'>" + unicode + "</span>");
    }

    private static native String prettyPrint(String s) /*-{
        return $wnd.prettyPrintOne(s);
    }-*/;
}
