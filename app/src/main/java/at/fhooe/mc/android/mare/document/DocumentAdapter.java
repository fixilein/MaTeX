package at.fhooe.mc.android.mare.document;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yydcdut.markdown.MarkdownConfiguration;
import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.syntax.text.TextFactory;

import java.util.List;

import at.fhooe.mc.android.mare.R;

public class DocumentAdapter extends ArrayAdapter<Document> {

    List<Document> mList;
    MarkdownConfiguration mMdConfig;

    public DocumentAdapter(@NonNull Context context, List<Document> list) {
        super(context, -1);
        mList = list;

        createMarkdownConfig();
    }

    private void createMarkdownConfig() {
        MarkdownConfiguration.Builder mdcoonfig = new MarkdownConfiguration.Builder(getContext());
        mdcoonfig.setHeader1RelativeSize(1.4f)
                .setHeader2RelativeSize(1.3f)
                .setHeader3RelativeSize(1.2f)
                .setHeader4RelativeSize(1.1f)
                .setHeader5RelativeSize(1.0f)
                .setHeader6RelativeSize(1.0f);
        mMdConfig = mdcoonfig.build();
    }

    @NonNull
    @Override
    public View getView(int _position, @Nullable View _convertView, @NonNull ViewGroup _parent) {
        if (_convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _convertView = inflater.inflate(R.layout.fragment_documentitem, null);
        }

        Document d = getItem(_position);

        TextView tv;
        tv = _convertView.findViewById(R.id.list_item_text_view_name);
        tv.setText(d.toString());
        tv.setMaxLines(1);

        tv = _convertView.findViewById(R.id.list_item_text_view_content);
        tv.setMaxLines(3);

        MarkdownProcessor markdownProcessor = new MarkdownProcessor(getContext());
        markdownProcessor.factory(TextFactory.create());

        markdownProcessor.config(mMdConfig);
        tv.setText(markdownProcessor.parse(d.getFirstFewLines()));

        tv = _convertView.findViewById(R.id.list_item_text_view_modified);
        tv.setText(d.getLastModifiedDate());

        return _convertView;
    }


}
