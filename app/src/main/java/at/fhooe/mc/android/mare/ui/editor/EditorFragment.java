package at.fhooe.mc.android.mare.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.yydcdut.markdown.MarkdownEditText;
import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.syntax.edit.EditFactory;

import at.fhooe.mc.android.mare.EditorActivity;
import at.fhooe.mc.android.mare.R;
import at.fhooe.mc.android.mare.document.Document;

public class EditorFragment extends Fragment {

    private EditorViewModel editorViewModel;

    private Document mDocument;
    private String mHeader;
    private MarkdownEditText ed;

    public View onCreateView(@NonNull LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
        editorViewModel = ViewModelProviders.of(this).get(EditorViewModel.class);
        View root = _inflater.inflate(R.layout.fragment_editor, _container, false);
        setHasOptionsMenu(false);

        mDocument = EditorActivity.mDocument;
        ed = root.findViewById(R.id.fragment_editor_editText_editor);
        readFile(); // set text from file
        // (needs to be done before creating the MarkdownProcessor to get formatting properly)

        MarkdownProcessor markdownProcessor = new MarkdownProcessor(getContext());
        //markdownProcessor.config(markdownConfiguration);
        markdownProcessor.factory(EditFactory.create());
        markdownProcessor.live(ed);


        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveFile();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_editor, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_fragment_item_save) {
            saveFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readFile() {
        String[] doc = mDocument.readFile();
        mHeader = doc[0];
        ed.setText(doc[1]);
    }


    private void saveFile() {
        if (mDocument.getFile().exists() && // file hasn't been deleted
                !mDocument.getContent().equals(ed.getText().toString())) // file has been changed
            mDocument.saveFile(mDocument.getHeader().toString(), ed.getText().toString());
    }
}