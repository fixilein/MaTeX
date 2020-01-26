package at.fhooe.mc.android.mare.ui.editor;

import android.content.Intent;
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
import at.fhooe.mc.android.mare.ImportImageDialog;
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
        mMDEditText.setText(mDocument.getContent()); // set text from file
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
        root.findViewById(R.id.fragment_editor_button_quote).setOnClickListener(this);
        root.findViewById(R.id.fragment_editor_button_code).setOnClickListener(this);
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
        if (item.getItemId() == R.id.menu_fragment_item_save) {
            saveFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        String text = editable.toString();

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
            case R.id.fragment_editor_button_heading_add: {

                replaceLast(text, "^", "\n#");
                mMDEditText.setText(text);
                // ^(#{1,6})\s
                //changeHeading(editable, selStart, selEnd, true);
                break;
            }
            case R.id.fragment_editor_button_heading_sub: {
                changeHeading(editable, selStart, selEnd, false);
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
            case R.id.fragment_editor_button_horizontal_line: {
                editable.insert(selStart, "\n---\n");
                mMDEditText.refreshDrawableState();
                break;
            }
            case R.id.fragment_editor_button_function: {
                format(editable, selStart, selEnd, "$");
                break;
            }
            case R.id.fragment_editor_button_link: {
                break;
            }
            case R.id.fragment_editor_button_image: {
                insertImage();
                break;
            }
            case R.id.fragment_editor_button_quote: {
                // >
                break;
            }
            case R.id.fragment_editor_button_code: {
                // ```
                break;
            }
            default: {
                break;
            }
        }

    }

    private void insertImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private static final int PICK_IMAGE = 1764;

    public void insertImageLink(String name) {
        int selStart = mMDEditText.getSelectionStart();
        mMDEditText.getText().insert(selStart, "\n![desc](" + name + ")\n");
        mMDEditText.setSelection(selStart + 3, selStart + 7);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE && data != null) {

            ImportImageDialog dialog = new ImportImageDialog()
                    .setDocument(mDocument)
                    .setFragment(this)
                    .setIntentData(data);
            dialog.show(getFragmentManager(), "image_dialog");
        }
    }


    private static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    private void changeHeading(Editable editable, int selStart, int selEnd, boolean add) {
        /* String format = "#";
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
            editable.replace(i, i + 1, "\n" + format + " ");
        else
            editable.replace(i, i + len + 1, "\n");
         */
    }

    private void formatStartOfLine(Editable editable, int selStart, int selEnd, String format) {
    /*    String text = editable.toString();
        int len = format.length();
        int i = text.substring(0, selStart).lastIndexOf("\n");
        if (i == -1) { // start of file
            if (text.substring(0, len).equals(format))
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
            */
    }


    // BIG TODO
    private void format(Editable editable, int selStart, int selEnd, String format) {
        int len = format.length();


        // this works in every case
        /*editable.insert(selEnd, format);
        editable.insert(selStart, format);
        mMDEditText.setSelection(selStart + len, selEnd + len); */


    }
}