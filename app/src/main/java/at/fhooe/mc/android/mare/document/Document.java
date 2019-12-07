package at.fhooe.mc.android.mare.document;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Document {

    private final String title;
    private File file;

    public Document(String _title) {
        title = _title;
        file = getFileFromName(_title);
    }

    public static String getDefaultHeader(String _title) {
        return "---\n" +
                "title: " + _title + "\n" +
                "author: \n" +
                "subtitle: \n" +
                "toc: true\n" +
                "date: \\today\n" +
                "geometry: \"left=3cm,right=3cm,top=2cm,bottom=2cm\"\n" +
                "fontSize: 11pt\n" +
                "...\n\n";
    }

    public static void createDocument(String _title, Context context) {
// TODO check if already exists!
        String filename = _title + ".md";

        // getDir creates folder if needed.
        File file = new File(context.getDir(_title, Context.MODE_PRIVATE), filename);

        // touching the file
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(Document.getDefaultHeader(_title).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getFileFromName(String _title) {
        return new File("/data/data/at.fhooe.mc.android.mare/app_" + _title + "/" + _title + ".md");
    }

    public static File getDirectoryFromName(String _title) {
        return new File("/data/data/at.fhooe.mc.android.mare/app_" + _title + "/");
    }

    @Override
    public String toString() {
        return title;
    }

    /**
     * Read the file and return Header and Content of Document.
     *
     * @return String array. [0]=header, [1]=content
     */
    public String[] readFile() {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int headerEnd = text.indexOf("\n...\n\n") + 6;
        String header = text.substring(text.indexOf("---"), headerEnd);
        String contents = text.substring(headerEnd);
        return new String[]{header, contents};
    }

    public void saveFile(String header, String content) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(header.getBytes());
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFirstViewLines() {
        String[] split = readFile()[1].split("\n", 1);
        return split[0];
    }

    public File getFile() {
        return file;
    }

    public String getContent() {
        return readFile()[1];
    }

    public DocHeader getHeader() {
        String[] h = readFile()[0].split("\n");
        String title = h[1].substring(h[1].indexOf("title: ") + 7);
        String author = h[2].substring(h[2].indexOf("author: ") + 8);
        String subtitle = h[3].substring(h[3].indexOf("subtitle: ") + 10);
        boolean toc = Boolean.parseBoolean(h[4].substring(h[4].indexOf("toc: ") + 5));
        String date = h[5].substring(h[5].indexOf("date: ") + 6);
        // 6 geometry

        int fontSize = Integer.parseInt(h[7].substring(h[7].indexOf("fontSize: ") + 10, h[7].indexOf("pt")));

        return new DocHeader(title, author, subtitle, date, toc, fontSize, 0, 0);
    }

    public class DocHeader {
        String title, author, subtitle, date;
        boolean toc;
        int fontSize, marginTopBot, marginLeftRight;

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

        @NonNull
        @Override
        public String toString() {
            return "---\n" +
                    "title: " + title + "\n" +
                    "author: " + author + "\n" +
                    "subtitle: " + subtitle + "\n" +
                    "toc: " + toc + "\n" +
                    "date: " + date + "\n" +
                    "geometry: \"left=" + marginLeftRight + "cm,right=" + marginLeftRight +
                    "cm,top=" + marginTopBot + "cm,bottom=" + marginTopBot + "cm\"\n" +
                    "fontSize: " + fontSize + "pt\n" +
                    "...\n\n";
        }
    }
}

