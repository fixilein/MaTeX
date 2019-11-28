package at.fhooe.mc.android.mare.document;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
    public class Document {
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

