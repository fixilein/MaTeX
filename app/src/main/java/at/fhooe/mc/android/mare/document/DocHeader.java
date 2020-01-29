package at.fhooe.mc.android.mare.document;

import androidx.annotation.NonNull;

/**
 * Document Header Data Class.
 */
public class DocHeader {
    private String title, author, subtitle, date;
    private boolean toc;
    private int fontSize, marginTopBot, marginLeftRight;

    DocHeader(String title, String author, String subtitle, String date, boolean toc, int fontsize, int marginTopBot, int marginLeftRight) {
        this.title = title;
        this.author = author;
        this.subtitle = subtitle;
        this.date = date;
        this.toc = toc;
        this.fontSize = fontsize;
        this.marginTopBot = marginTopBot;
        this.marginLeftRight = marginLeftRight;
    }

    /**
     * Get a default header for a Document.
     *
     * @param _title The title of the Document.
     * @return DocHeader object with standard values.
     * @see Document
     */
    public static DocHeader defaultHeader(String _title) { // default Header
        return new DocHeader(_title, "", "", "\\today",
                true, 11, 45, 45);
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
                "fontSize: " + fontSize + "pt\n" +
                "...\n\n";
    }
}