package at.fhooe.mc.android.matex.document;

import android.content.Context;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Document {

    private final String title;
    private File file;

    public Document(Context context, String _title) {
        title = _title;
        file = getFileFromName(context, _title);
    }

    private static File getDocDirectory(Context context) {
        if (context == null)
            throw new NoContextException();
        File dir = new File(context.getFilesDir(), "Documents");
        dir.mkdirs();
        return dir;
    }

    /**
     * Returns a list of files, sorted by last modified date.
     *
     * @return list of files, sorted by last modified date
     */
    public static List<Document> getDocumentList(Context context) {
        File dir = getDocDirectory(context);
        File[] files = dir.listFiles();

        if (files == null)
            return new LinkedList<>();

        Arrays.parallelSort(files, (o1, o2) -> { // sort by last modified date
            FileFilter ff = pathname -> pathname.toString().endsWith(".md");

            File[] files1 = o1.listFiles(ff);
            File[] files2 = o2.listFiles(ff);
            if (files1 == null || files2 == null)
                return -1;
            if (files1.length == 0 || files2.length == 0)
                return 0;
            if (files1[0].lastModified() < files2[0].lastModified())
                return 0;
            return -1;
        });

        LinkedList<Document> list = new LinkedList<>();

        for (File f : files)
            list.add(new Document(context, f.getName()));

        return list;
    }

    public static Document createDocument(Context context, String _title) {
        String filename = _title + ".md";

        File dir = getDirectoryFromName(context, _title);

        // getDir creates folder if needed.
        File file = new File(dir, filename);

        // touching the file
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(defaultHeader(_title).toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Document(context, _title);
    }

    public static DocHeader defaultHeader(String title) {
        return new DocHeader(title);
    }

    public static File getFileFromName(Context context, String _title) {
        return new File(getDocDirectory(context), _title + File.separator + _title + ".md");
    }

    public static File getDirectoryFromName(Context context, String _title) {
        File dir = new File(getDocDirectory(context), _title);
        dir.mkdirs();
        return dir;
    }

    public static LinkedList<String> getDocumentNamesList(Context context) {
        LinkedList<String> l = new LinkedList<>();
        for (Document d : getDocumentList(context)) {
            l.add(d.title);
        }
        return l;
    }

    public List<String> getImageNamesList(Context context) {
        return Arrays.asList(getImageDir(context).list());
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
        if (text.length() == 0) // no file found, wrong name
            return new String[0];
        int headerEnd = text.indexOf(DocHeader.HEADER_END) + DocHeader.HEADER_END.length();
        String header = text.substring(0, headerEnd);
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
        String[] strings = readFile();
        if (strings.length == 0) // invalid file
            return "";
        return strings[1].split("\n", 1)[0];
    }

    public File getFile() {
        return file;
    }

    public String getContent() {
        String[] strings = readFile();
        if (strings.length == 0) // invalid file
            return "";
        return strings[1];
    }

    public DocHeader getHeader() {
        String headerText = readFile()[0];
        return DocHeader.fromString(headerText);
    }

    public long getLastModifiedDate() {
        return file.lastModified();
    }

    public String getTitle() {
        return title;
    }

    public File getPDFFile(Context context) {
        File dir = getDirectoryFromName(context, title);
        return new File(dir, "pdf.pdf");
    }

    public File getImageDir(Context context) {
        File img = new File(getDirectoryFromName(context, title), "img");
        img.mkdirs();
        return img;
    }

    /**
     * Deletes the document and all associated files.
     */
    public void deleteFiles(Context context) {
        deleteFiles(getDirectoryFromName(context, title));
    }

    /**
     * Recursive delete
     *
     * @param file File to delete.
     */
    private void deleteFiles(File file) {
        File[] allContents = file.listFiles();
        if (allContents != null) {
            for (File f : allContents) {
                deleteFiles(f);
            }
        }
        file.delete();
    }

    public void deleteUnusedImages(Context context) {
        if (!file.exists())
            return;

        String content = getContent();

        for (File f : getImageDir(context).listFiles()) {
            if (!content.contains(f.getName()))
                f.delete();
        }
    }

    /**
     * Get a zip File with images (if any) and the.md file.
     *
     * @return ZIP File
     */
    public File getZipFile(Context context) {
        deleteUnusedImages(context);
        File f = new File(getDocDirectory(context), getTitle() +
                "/" + getTitle() + ".zip");
        try {
            ZipFile zip = new ZipFile(f);
            zip.addFile(getFile(), new ZipParameters());
            for (File img : getImageDir(context).listFiles()) {
                zip.addFile(img, new ZipParameters());
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return f;
    }

    private static class NoContextException extends RuntimeException {
        @Override
        public String getMessage() {
            return "Document class has no Context.\n\n\n" + super.getMessage();
        }
    }


}

