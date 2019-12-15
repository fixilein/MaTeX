package at.fhooe.mc.android.mare.ui.editor;

import android.os.Bundle;
import android.text.Editable;
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

public class EditorFragment extends Fragment implements View.OnClickListener {

    private EditorViewModel editorViewModel;

    private Document mDocument;
    private MarkdownEditText mMDEditText;

    public View onCreateView(@NonNull LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
        editorViewModel = ViewModelProviders.of(this).get(EditorViewModel.class);
        View root = _inflater.inflate(R.layout.fragment_editor, _container, false);
        setHasOptionsMenu(false);

        mDocument = EditorActivity.mDocument;
        mMDEditText = root.findViewById(R.id.fragment_editor_editText_editor);
        readFile(); // set text from file
        // (needs to be done before creating the MarkdownProcessor to get formatting properly)

        MarkdownProcessor markdownProcessor = new MarkdownProcessor(getContext());
        //markdownProcessor.config(markdownConfiguration);
        markdownProcessor.factory(EditFactory.create());
        markdownProcessor.live(mMDEditText);

        assignButtonListeners(root);

        return root;
    }

    private void assignButtonListeners(View root) {
        root.findViewById(R.id.fragment_editor_button_bold).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_italic).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_strikethrough).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_heading_add).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_heading_sub).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_ordered_list).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_unordered_list).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_horizontal_line).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_link).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_image).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_function).setOnClickListener(this);
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
        mMDEditText.setText(doc[1]);
    }


    private void saveFile() {
        if (mDocument.getFile().exists() && // file hasn't been deleted
                !mDocument.getContent().equals(mMDEditText.getText().toString())) // file has been changed
            mDocument.saveFile(mDocument.getHeader().toString(), mMDEditText.getText().toString());
    }

    @Override
    public void onClick(View v) {

        int selStart = mMDEditText.getSelectionStart();
        int selEnd = mMDEditText.getSelectionEnd();
        Editable editable = mMDEditText.getText();

        // Toast.makeText(getContext(), "start: " + selStart + ", end: " + selEnd, Toast.LENGTH_SHORT).show();

        switch (v.getId()) {
            case R.id.fragment_editor_button_bold: {
                format(editable, selStart, selEnd, "**");
                break;
            }
            case R.id.fragment_editor_button_italic: {
                format(editable, selStart, selEnd, "*");
                break;
            }
            case R.id.fragment_editor_button_strikethrough: {
                format(editable, selStart, selEnd, "~~");
                break;
            }
            case R.id.fragment_editor_button_function: {
                format(editable, selStart, selEnd, "$");
                break;
            }
            case R.id.fragment_editor_button_heading_add: {
                changeHeading(editable, selStart, selEnd, true);
                break;
            }
            case R.id.fragment_editor_button_heading_sub: {
                changeHeading(editable, selStart, selEnd, false);
                break;
            }
            case R.id.fragment_editor_button_horizontal_line: {
                editable.insert(selStart, "\n---\n");
                mMDEditText.refreshDrawableState();
                break;
            }
            case R.id.fragment_editor_button_ordered_list: {
                formatStartOfLine(editable, selStart, selEnd, "1. ");
                break;
            }
            case R.id.fragment_editor_button_unordered_list: {
                formatStartOfLine(editable, selStart, selEnd, "- ");
                break;
            }
            default: {
                break;
            }
        }

    }

    private void changeHeading(Editable editable, int selStart, int selEnd, boolean add) {
        String format = "#";
        String text = editable.toString();
        int len = format.length();

        int i = text.substring(0, selStart).lastIndexOf("\n");
        if (i == -1) { // start of file
            if (add)
                editable.replace(0, 0, format + " ");
            else
                editable.replace(0, len, "");
            return;
        }

        String substring = text.substring(i, i + len + 1);
        if (add)
            editable.replace(i, i + 1, "\n" + format +" ");
        else
            editable.replace(i, i + len + 1, "\n");
    }

    private void formatStartOfLine(Editable editable, int selStart, int selEnd, String format) {
        String text = editable.toString();
        int len = format.length();
        int i = text.substring(0, selStart).lastIndexOf("\n");
        if (i == -1) { // start of file
            if (text.substring(0, len).equals(format))6
                editable.replace(0, len, "");
            else
                editable.replace(0, 0, format);
            return;
        }

        String substring = text.substring(i, i + len + 1);
        if (substring.equals("\n" + format))
            editable.replace(i, i + len + 1, "\n");
        else
            editable.replace(i, i + 1, "\n" + format);
    }


    // BIG TODO
    private void format(Editable editable, int selStart, int selEnd, String format) {
        int len = format.length();

        // case remove format


        // case new format:


        // this works in every case
        editable.insert(selEnd, format);
        editable.insert(selStart, format);
        mMDEditText.setSelection(selStart + len, selEnd + len);





        /*
        try {


            if (selStart < len) {
                editable.insert(selEnd, format);
                editable.insert(0, format);
                mMDEditText.setSelection(selStart + len, selEnd + len);
                return;
            }


            CharSequence st = editable.subSequence(selStart - len, selStart);
            CharSequence se = editable.subSequence(selEnd, selEnd + len);
            CharSequence c = editable.subSequence(selStart, selEnd);

            if (st.toString().equals(format) && se.toString().equals(format)) { // selection is surrounded by format
                editable.replace(selStart - len, selEnd + len, c);
                mMDEditText.setSelection(selStart - len, selEnd - len);
                return;
            } else {

            }
        } catch (Exception e) {

        }
        */
    }
}