package at.fhooe.mc.android.mare.document;

import androidx.annotation.NonNull;

import java.util.HashMap;

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

    /**
     * Get the Header as a String.
     *
     * @return String as the header would be in a Document.
     */
    @NonNull
    @Override
    public String toString() {
        return "---\n" +
                "title: " + title + "\n" +
                "author: " + author + "\n" +
                "subtitle: " + subtitle + "\n" +
                "toc: " + toc + "\n" +
                "date: " + date + "\n" +
                "geometry: \"left=" + marginLeftRight + "mm,right=" + marginLeftRight +
                "mm,top=" + marginTopBot + "mm,bottom=" + marginTopBot + "mm\"\n" +
                "documentclass: extarticle\n" +
                "fontfamily: " + fontFamily + "\n" +
                "fontsize: " + fontSize + "pt\n" +
                "...\n\n";
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public static HashMap<String, String> fontMap() {
        HashMap<String, String> fontMap = new HashMap<>();
        fontMap.put("lmodern", "Latin Modern Roman (Default)");
        fontMap.put("tgtermes", "TEX Gyre Termes");
        fontMap.put("tgpagella", "TEX Gyre Pagella");
        fontMap.put("tgbonum", "TEX Gyre Bonum");
        fontMap.put("tgschola", "TEX Gyre Schola");
        fontMap.put("mathptmx", "Times");
        fontMap.put("palatino", "Palatino");
        fontMap.put("bookman", "Bookman");
        fontMap.put("tgadventor", "TEX Gyre Adventor");
        fontMap.put("tgheros", "TEX Gyre Heros");
        fontMap.put("helvet", "Helvetica");
        fontMap.put("tgcursor", "TEX Gyre Cursor");
        fontMap.put("courier", "Courier");

        return fontMap;
    }
}