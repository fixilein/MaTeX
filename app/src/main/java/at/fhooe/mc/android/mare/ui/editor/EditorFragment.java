package at.fhooe.mc.android.mare.ui.editor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class EditorFragment extends Fragment {

    private EditorViewModel editorViewModel;
    private String mTitle;
    private File mFile;
    private EditText tv;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        editorViewModel = ViewModelProviders.of(this).get(EditorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_editor, container, false);

        tv = root.findViewById(R.id.fragment_editor_editText_editor);

        setHasOptionsMenu(true);

        mTitle = EditorActivity.mTitle;
        mFile = EditorActivity.mFile;

        readFile();


        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_editor, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==R.id.menu_fragment_item_save) {
            saveFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readFile() {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv.setText(text.toString());
    }


    private void saveFile() {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(mFile);
            outputStream.write(tv.getText().toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        readFile();
    }
}