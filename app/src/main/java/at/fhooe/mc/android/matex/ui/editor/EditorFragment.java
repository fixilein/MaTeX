package at.fhooe.mc.android.matex.ui.editor;

import android.app.AlertDialog;
import android.content.ClipboardManager;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yydcdut.markdown.MarkdownConfiguration;
import com.yydcdut.markdown.MarkdownEditText;
import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.syntax.edit.EditFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import at.fhooe.mc.android.matex.R;
import at.fhooe.mc.android.matex.activities.EditorActivity;
import at.fhooe.mc.android.matex.dialogs.ImportImageDialog;
import at.fhooe.mc.android.matex.document.Document;

import static android.content.Context.CLIPBOARD_SERVICE;

public class EditorFragment extends Fragment implements View.OnClickListener {

    private Document mDocument;
    private MarkdownEditText mMDEditText;
    MarkdownProcessor mMarkdownProcessor;
    private View mView;

    public View onCreateView(@NonNull LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
        View root = _inflater.inflate(R.layout.fragment_editor, _container, false);
        setHasOptionsMenu(true);
        mView = root;
        mDocument = EditorActivity.mDocument;
        mMDEditText = mView.findViewById(R.id.fragment_editor_editText_editor);
        mMDEditText.setText(mDocument.getContent());

        initMarkdownProcessor();

        assignButtonListeners(root);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void assignButtonListeners(View _view) {
        _view.findViewById(R.id.fragment_editor_button_bold).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_italic).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_strikethrough).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_heading_add).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_heading_sub).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_ordered_list).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_unordered_list).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_horizontal_line).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_quote).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_code).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_link).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_image).setOnClickListener(this);
        _view.findViewById(R.id.fragment_editor_button_function).setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveFile();
        mDocument.deleteUnusedImages();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_editor, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // save on file share
        saveFile();

        return super.onOptionsItemSelected(item);
    }

    private void saveFile() {
        if (mDocument != null && mDocument.getFile().exists() && // file hasn't been deleted
                !mDocument.getContent().equals(mMDEditText.getText().toString())) // file has been changed
            mDocument.saveFile(mDocument.getHeader().toString(), mMDEditText.getText().toString());
    }

    @Override
    public void onClick(View v) {

        int selStart = mMDEditText.getSelectionStart();
        int selEnd = mMDEditText.getSelectionEnd();
        Editable editable = mMDEditText.getText();

        switch (v.getId()) {
            case R.id.fragment_editor_button_bold: {
                inlineMarkdownFormat(mMDEditText, selStart, selEnd, "**");
                break;
            }
            case R.id.fragment_editor_button_italic: {
                inlineMarkdownFormat(mMDEditText, selStart, selEnd, "*");
                break;
            }
            case R.id.fragment_editor_button_strikethrough: {
                inlineMarkdownFormat(mMDEditText, selStart, selEnd, "~~");
                break;
            }
            case R.id.fragment_editor_button_heading_add: {
                changeHeading(mMDEditText, selStart, selEnd, true);
                break;
            }
            case R.id.fragment_editor_button_heading_sub: {
                changeHeading(mMDEditText, selStart, selEnd, false);
                break;
            }
            case R.id.fragment_editor_button_ordered_list: {
                prefixMarkdownFormat(mMDEditText, selStart, selEnd, "1. ");
                break;
            }
            case R.id.fragment_editor_button_unordered_list: {
                prefixMarkdownFormat(mMDEditText, selStart, selEnd, "- ");
                break;
            }
            case R.id.fragment_editor_button_horizontal_line: {
                editable.insert(selStart, "\n---\n");
                break;
            }
            case R.id.fragment_editor_button_function: {
                inlineMarkdownFormat(mMDEditText, selStart, selEnd, "\n$$\n");
                break;
            }
            case R.id.fragment_editor_button_link: {
                insertLink();
                break;
            }
            case R.id.fragment_editor_button_image: {
                insertImage();
                break;
            }
            case R.id.fragment_editor_button_quote: {
                prefixMarkdownFormat(mMDEditText, selStart, selEnd, "> ");
                break;
            }
            case R.id.fragment_editor_button_code: {
                inlineMarkdownFormat(mMDEditText, selStart, selEnd, "\n```\n");
                break;
            }
            default: {
                break;
            }
        }
        refreshMarkdownProcessor();

    }

    private void initMarkdownProcessor() {
        mMarkdownProcessor = new MarkdownProcessor(getContext());
        MarkdownConfiguration markdownConfiguration = new MarkdownConfiguration.Builder(getContext())
                .setBlockQuotesLineColor(0) // no quotes
                .build();
        mMarkdownProcessor.config(markdownConfiguration);
        mMarkdownProcessor.factory(EditFactory.create());
        mMDEditText.clear();
        mMarkdownProcessor.live(mMDEditText);
    }

    private void refreshMarkdownProcessor() {
        mMDEditText.clear();
        mMarkdownProcessor.live(mMDEditText);
    }

    private void insertLink() {
        int selStart = mMDEditText.getSelectionStart();
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
        if (clipboardManager.getPrimaryClip() != null) {
            String clipboard = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
            try {
                new URL(clipboard);
                new AlertDialog.Builder(getActivity())
                        .setTitle(getContext().getString(R.string.use_as_link))
                        .setMessage(String.format(getContext().getString(R.string.use_s_as_link), clipboard))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> insertLinkText(clipboard, selStart))
                        .setNegativeButton(android.R.string.no, (dialog, which) -> insertLinkText("url", selStart))
                        .setOnCancelListener(dialog -> insertLinkText("url", selStart))
                        .create().show();
            } catch (MalformedURLException e) {
                // not a valid url
                insertLinkText("url", selStart);
            }
        }
    }

    private void insertLinkText(String url, int selStart) {
        mMDEditText.getText().insert(selStart, "\n[text](" + url + ")\n");
        mMDEditText.setSelection(selStart + 2, selStart + 6);
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


    private void prefixMarkdownFormat(MarkdownEditText editText, int selStart, int selEnd, String format) {
        String text = editText.getText().toString();
        int lineStart = findLineStart(selStart, text);

        String before = text.substring(0, lineStart);
        String after = text.substring(lineStart);

        if (after.startsWith(format)) { // remove format
            after = after.replaceFirst(format, "");
            String newString = before + after;
            editText.setText(newString);
            editText.setSelection(selStart - format.length());
        } else { // add format
            after = format + after;
            String newString = before + after;
            editText.setText(newString);
            editText.setSelection(selStart + format.length());
        }
    }

    private void inlineMarkdownFormat(MarkdownEditText editText, int selStart, int selEnd, String format) {
        String text = editText.getText().toString();
        String before = text.substring(0, selStart);
        String selection = text.substring(selStart, selEnd);
        String after = text.substring(selEnd);
        int length = format.length();


        if (before.endsWith(format) && after.startsWith(format)) {
            String escapedFormat = Pattern.quote(format); // escape special characters like '*' that regex would otherwise match
            after = after.replaceFirst(escapedFormat, "");
            before = replaceLast(before, escapedFormat, "");
            String newText = before + selection + after;
            editText.setText(newText);
            editText.setSelection(selStart - length, selEnd - length);
        } else {
            String newText = before + format + selection + format + after;
            editText.setText(newText);
            editText.setSelection(selStart + length, selEnd + length);
        }
    }

    private int findLineStart(int start, String text) {
        for (int i = start - 1; i >= 0; i--) {
            if (text.charAt(i) == '\n')
                return i + 1;
        }
        return 0;
    }


    private static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    private void changeHeading(MarkdownEditText editText, int selStart, int selEnd, boolean add) {
        String text = editText.getText().toString();
        int lineStart = findLineStart(selStart, text);
        int newSelection = selStart;
        String before = text.substring(0, lineStart);
        String after = text.substring(lineStart);

        String regex = "^(#{1,6})(\\s)";


        if (add) {
            if (after.startsWith("#")) {
                after = after.replaceFirst(regex, "$1#$2");
                newSelection++;
            } else {
                after = "# " + after;
                newSelection += 2;
            }
        } else { // remove
            if (after.startsWith("# ")) { // last heading
                after = after.replaceFirst("# ", "");
                newSelection -= 2;
            } else if (after.startsWith("#")) {
                after = after.replaceFirst("#", "");
                newSelection--;
            }

        }

        String newText = before + after;
        editText.setText(newText);
        editText.setSelection(newSelection);

    }

}