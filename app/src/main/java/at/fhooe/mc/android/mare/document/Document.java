package at.fhooe.mc.android.mare.document;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Document {

    public static String getDefaultHeader(String _title) {
        return "---\n" +
                "title:\"" + _title + "\"\n" +
                "author:\n" +
                "subtitle:\n" +
                "toc: true\n" +
                "date: \\today\n" +
                "geometry: \"left=3cm,right=3cm,top=2cm,bottom=2cm\"\n" +
                "fontsize: 11pt\n" +
                "...\n\n";
    }

    private final String title;
    private File file;

    public Document(String _title) {
        title = _title;
        file = getFileFromName(_title);
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

    @Override
    public String toString() {
        return title;
    }

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
        return getFileFromName(title);
    }

    public static String getFilenameFromTitle(String _title) {
        return _title + ".md";
    }

    public static File getFileFromName(String _title) {
        return new File("/data/data/at.fhooe.mc.android.mare/app_" + _title + "/" + _title + ".md");
    }

    public static File getDirectoryFromName(String _title) {
        return new File("/data/data/at.fhooe.mc.android.mare/app_" + _title + "/");
    }

}

