package at.fhooe.mc.android.mare.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import at.fhooe.mc.android.mare.EditorActivity;
import at.fhooe.mc.android.mare.R;
import at.fhooe.mc.android.mare.document.Document;

public class EditorFragment extends Fragment {

    private EditorViewModel editorViewModel;

    private Document mDocument;
    private String mHeader;
    private EditText tv;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        editorViewModel = ViewModelProviders.of(this).get(EditorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_editor, container, false);
        setHasOptionsMenu(true);

        tv = root.findViewById(R.id.fragment_editor_editText_editor);

        mDocument = new Document(EditorActivity.mTitle);

        readFile();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        tv.setText(doc[1]);
    }


    private void saveFile() {
        mDocument.saveFile(mHeader, tv.getText().toString());
    }
}