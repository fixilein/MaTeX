package at.fhooe.mc.android.mare.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample title for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DocumentContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Document> ITEMS = new ArrayList<Document>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Document> ITEM_MAP = new HashMap<String, Document>();

    private static final int COUNT = 25;

    static {
        // TODO read notes from storage here??
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Document item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static Document createDummyItem(int position) {
        return new Document(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


    public static class Document {
        public final String id;
        public final String title;
        public final String details;

        public Document(String id, String content, String details) {
            this.id = id;
            this.title = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}