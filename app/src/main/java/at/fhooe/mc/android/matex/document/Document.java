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

    private static Context mContext = null;

    public Document(String _title) {
        title = _title;
        file = getFileFromName(_title);
    }

    private static File getDocDirectory() {
        if (mContext == null)
            throw new NoContextException();
        File dir = new File(mContext.getFilesDir(), "Documents");
        dir.mkdirs();
        return dir;
    }

    /**
     * Returns a list of files, sorted by last modified date.
     *
     * @return
     */
    public static List<Document> getDocumentList() {
        File dir = getDocDirectory();
        File[] files = dir.listFiles();

        if (files == null)
            return new LinkedList<>();

        Arrays.parallelSort(files, (o1, o2) -> { // sort by last modified date
            FileFilter ff = pathname -> pathname.toString().endsWith(".md");
            if (o1.listFiles(ff)[0].lastModified() < o2.listFiles(ff)[0].lastModified())
                return 0;
            return -1;
        });

        LinkedList<Document> list = new LinkedList<>();

        for (File f : files)
            list.add(new Document(f.getName()));

        return list;
    }

    public static Document createDocument(String _title) {
        String filename = _title + ".md";

        File dir = getDirectoryFromName(_title);

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
        return new Document(_title);
    }

    public static DocHeader defaultHeader(String title) {
        return new DocHeader(title);
    }

    public static File getFileFromName(String _title) {
        return new File(getDocDirectory(), _title + File.separator + _title + ".md");
    }

    public static File getDirectoryFromName(String _title) {
        File dir = new File(getDocDirectory(), _title);
        dir.mkdirs();
        return dir;
    }

    public static LinkedList<String> getDocumentNamesList() {
        LinkedList<String> l = new LinkedList<>();
        for (Document d : getDocumentList()) {
            l.add(d.title);
        }
        return l;
    }

    public static void setmContext(Context _context) {
        Document.mContext = _context;
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
        return readFile()[1].split("\n", 1)[0];
    }

    public File getFile() {
        return file;
    }

    public String getContent() {
        return readFile()[1];
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

    public File getPDFFile() {
        File dir = getDirectoryFromName(title);
        return new File(dir, "pdf.pdf");
    }

    public File getImageDir() {
        File img = new File(getDirectoryFromName(title), "img");
        img.mkdirs();
        return img;
    }

    /**
     * Deletes the document and all associated files.
     */
    public void deleteFiles() {
        deleteFiles(getDirectoryFromName(title));
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

    public void deleteUnusedImages() {
        if (!file.exists())
            return;

        String content = getContent();

        for (File f : getImageDir().listFiles()) {
            if (!content.contains(f.getName()))
                f.delete();
        }
    }

    /**
     * Get a zip File with images (if any) and the.md file.
     *
     * @return ZIP File
     */
    public File getZipFile() {
        deleteUnusedImages();
        File f = new File(getDocDirectory(), getTitle() +
                "/" + getTitle() + ".zip");
        try {
            ZipFile zip = new ZipFile(f);
            zip.addFile(getFile(), new ZipParameters());
            for (File img : getImageDir().listFiles()) {
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

