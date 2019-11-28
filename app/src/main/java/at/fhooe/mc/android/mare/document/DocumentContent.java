package at.fhooe.mc.android.mare.document;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentContent {
    public static final List<Document> ITEMS = new ArrayList<Document>();
    public static final Map<String, Document> ITEM_MAP = new HashMap<String, Document>();

    static {
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

        for (File f : files)
            addItem(new Document(f.getName().replace("app_", "")));
    }

    private static void addItem(Document item) {
        ITEMS.add(item);
        ITEM_MAP.put("1", item);
    }

    public static class Document {
        public final String title;

        public Document(String _title) {
            title = _title;
        }

        @Override
        public String toString() {
            return title;
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
}
