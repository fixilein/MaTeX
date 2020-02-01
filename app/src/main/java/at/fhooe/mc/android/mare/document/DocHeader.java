package at.fhooe.mc.android.mare.document;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Document Header Data Class.
 */
public class DocHeader {
    private String title, author = "", subtitle = "";
    private String date = DATE_DEFAULT, fontFamily = FONT_FAMILY_DEFAULT;
    private boolean toc = true;
    private int fontSize = FONT_SIZE_DEFAULT;
    private int marginTopBot = MARGIN_DEFAULT, marginLeftRight = MARGIN_DEFAULT;

    private static final String DATE_DEFAULT = "\\today";
    private static final int FONT_SIZE_DEFAULT = 11;
    private static final String FONT_FAMILY_DEFAULT = "lmodern";
    private static final int MARGIN_DEFAULT = 45;
    private boolean linkColor = true;

    /**
     * Constructor for DocHeader.
     * Get a default header for a Document.
     *
     * @param _title The title of the Document.
     * @see Document
     */
    DocHeader(String _title) {
        this.title = _title;
    }

    private static String matchSimple(String text, String p) {
        return match(text, "\n" + p + ": (.*)\n");
    }

    private static String match(String text, String p) {
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getToc() {
        return toc;
    }

    public void setToc(boolean toc) {
        this.toc = toc;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getMarginTopBot() {
        return marginTopBot;
    }

    public void setMarginTopBot(int marginTopBot) {
        this.marginTopBot = marginTopBot;
    }

    public int getMarginLeftRight() {
        return marginLeftRight;
    }

    public void setMarginLeftRight(int marginLeftRight) {
        this.marginLeftRight = marginLeftRight;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public boolean getLinkColor() {
        return linkColor;
    }

    public void setLinkColor(boolean linkColor) {
        this.linkColor = linkColor;
    }

    static final String HEADER_START = "---\n";
    static final String HEADER_END = "\n...\n\n";


    /**
     * Parse a String to DocHeader
     *
     * @param headerText String of a Document Header
     * @return DocHeader object
     * @see Document
     */
    public static DocHeader fromString(String headerText) {
        String docTitle = matchSimple(headerText, "title");
        DocHeader docHeader = new DocHeader(docTitle);

        docHeader.setSubtitle(matchSimple(headerText, "subtitle"));
        docHeader.setAuthor(matchSimple(headerText, "author"));
        docHeader.setDate(matchSimple(headerText, "date"));
        docHeader.setToc(Boolean.parseBoolean(matchSimple(headerText, "toc")));
        docHeader.setLinkColor(Boolean.parseBoolean(matchSimple(headerText, "colorlinks")));

        docHeader.setFontSize(Integer.parseInt(match(headerText, "\nfontsize: (\\d+)pt\n")));
        docHeader.setFontFamily(matchSimple(headerText, FONT_CONFIG));
        docHeader.setMarginLeftRight(Integer.parseInt((match(headerText,
                "\ngeometry: \"left=(\\d+)mm.*\n"))));
        docHeader.setMarginTopBot(Integer.parseInt((match(headerText,
                "\ngeometry: .*top=(\\d+)mm.*\n"))));

        return docHeader;

    }

    private static final String FONT_CONFIG = "mainfont";

    /**
     * Get the Header as a String.
     *
     * @return String as the header would be in a Document.
     */
    @NonNull
    @Override
    public String toString() {
        return HEADER_START +
                "title: " + title + "\n" +
                "author: " + author + "\n" +
                "subtitle: " + subtitle + "\n" +
                "toc: " + toc + "\n" +
                "date: " + date + "\n" +
                "geometry: \"left=" + marginLeftRight + "mm,right=" + marginLeftRight +
                "mm,top=" + marginTopBot + "mm,bottom=" + marginTopBot + "mm\"\n" +
                "documentclass: extarticle\n" +
                "header-includes:\n" +
                "    - \\usepackage[" + getFontCode(fontFamily) + "]{" + fontFamily + "}\n" +
                "    - \\usepackage[T1]{fontenc}\n" +
                FONT_CONFIG + ": " + fontFamily + "\n" +
                "fontsize: " + fontSize + "pt\n" +
                "colorlinks: " + linkColor +
                HEADER_END;
    }

    // TODO check if all fonts are valid
    public static HashMap<String, String> fontFamilyMap() {
        HashMap<String, String> fontMap = new HashMap<>();
        fontMap.put("lmodern", "Latin Modern Roman (Default)");
        fontMap.put("palatino", "Palatino");
        fontMap.put("bookman", "Bookman");
        fontMap.put("charter", "Charter");
        fontMap.put("mathptmx", "Times");
        fontMap.put("roboto", "Roboto");
        fontMap.put("arev", "Arev Sans");
        fontMap.put("chancery", "Chancery");
        fontMap.put("merriweather", "Merriweather");
        fontMap.put("bera", "Bera");
        fontMap.put("quattrocento", "Quattrocento");
        // fontMap.put("times", "Times"); // no image preview

        return fontMap;
    }

    // this really is just here for roboto
    public static String getFontCode(String family) {
        HashMap<String, String> map = new HashMap<>();
        map.put("roboto", "sfdefault");

        String code = map.get(family);
        if (code == null)
            code = "";

        return code;

    }
}