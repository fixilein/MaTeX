package at.fhooe.mc.android.mare.document;

import android.content.Context;

import androidx.annotation.NonNull;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Document {

    private final String title;
    private File file;

    public Document(String _title) {
        title = _title;
        file = getFileFromName(_title);
    }

    /**
     * Returns a list of files, sorted by last modified date.
     *
     * @return
     */
    public static LinkedList<Document> getDocumentList() {

        File[] files = new File("/data/data/at.fhooe.mc.android.mare").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) { // search for directories starting with "app_"
                return pathname.getName().startsWith("app_");
            }
        });


        Arrays.parallelSort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) { // sort by last modified date
                FileFilter ff = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.toString().endsWith(".md");
                    }
                };

                if (o1.listFiles(ff)[0].lastModified() < o2.listFiles(ff)[0].lastModified())
                    return 0;
                return -1;
            }
        });

        LinkedList<Document> list = new LinkedList<>();

        for (File f : files)
            list.add(new Document(f.getName().replace("app_", "")));

        return list;
    }

    public static String getDefaultHeader(String _title) {
        return "---\n" +
                "title: " + _title + "\n" +
                "author: \n" +
                "subtitle: \n" +
                "toc: true\n" +
                "date: \\today\n" +
                "geometry: \"left=45mm,right=45mm,top=45mm,bottom=45mm\"\n" +
                "documentclass: extarticle\n" +
                "fontSize: 11pt\n" +
                "...\n\n";
    }

    public static Document createDocument(String _title, Context context) {
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
        return new Document(_title);
    }

    public static File getFileFromName(String _title) {
        return new File("/data/data/at.fhooe.mc.android.mare/app_" + _title + "/" + _title + ".md");
    }

    public static File getDirectoryFromName(String _title) {
        return new File("/data/data/at.fhooe.mc.android.mare/app_" + _title + "/");
    }

    public static LinkedList<String> getDocumentNamesList() {
        LinkedList<String> l = new LinkedList<>();
        for (Document d : getDocumentList()) {
            l.add(d.title);
        }
        return l;
    }

    public List<String> getImageNamesList() {
        return Arrays.asList(getImageDir().list());
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

    public String getFirstFewLines() {
        return readFile()[1].split("\n", 1)[0];
    }

    public File getFile() {
        return file;
    }

    public String getContent() {
        return readFile()[1];
    }

    public DocHeader getHeader() { // TODO regex???
        String[] h = readFile()[0].split("\n");
        String title = h[1].substring(h[1].indexOf("title: ") + 7);
        String author = h[2].substring(h[2].indexOf("author: ") + 8);
        String subtitle = h[3].substring(h[3].indexOf("subtitle: ") + 10);
        boolean toc = Boolean.parseBoolean(h[4].substring(h[4].indexOf("toc: ") + 5));
        String date = h[5].substring(h[5].indexOf("date: ") + 6);

        int hor = Integer.parseInt(h[6].substring(h[6].indexOf("left=") + 5, h[6].indexOf("mm,right")));
        int ver = Integer.parseInt(h[6].substring(h[6].indexOf("top=") + 4, h[6].indexOf("mm,bottom")));

        int fontSize = Integer.parseInt(h[8].substring(h[8].indexOf("fontSize: ") + 10, h[8].indexOf("pt")));

        return new DocHeader(title, author, subtitle, date, toc, fontSize, ver, hor);
    }

    public String getLastModifiedDate() {
        return new PrettyTime().format(new Date(file.lastModified()));
    }

    public String getTitle() {
        return title;
    }

    public File getPDFFile() {
        File dir = getDirectoryFromName(title);
        return new File(dir, "pdf.pdf");
    }

    public File getImageDir() {
        File img = new File(getDirectoryFromName(title), "img");
        img.mkdirs();
        return img;
    }

    public void deleteFiles() {
        deleteFiles(getDirectoryFromName(title));
    }

    // delete
    private void deleteFiles(File file) {
        File[] allContents = file.listFiles();
        if (allContents != null) {
            for (File f : allContents) {
                deleteFiles(f);
            }
        }
        file.delete();
    }

    public int getImageCount() {
        return getImageDir().listFiles().length;
    }

    public void deleteUnusedImages() {
        String content = getContent();

        for (File f : getImageDir().listFiles()) {
            if (!content.contains(f.getName()))
                f.delete();
        }
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
                    "geometry: \"left=" + marginLeftRight + "mm,right=" + marginLeftRight +
                    "mm,top=" + marginTopBot + "mm,bottom=" + marginTopBot + "mm\"\n" +
                    "documentclass: extarticle\n" +
                    "fontSize: " + fontSize + "pt\n" +
                    "...\n\n";
        }
    }
}

