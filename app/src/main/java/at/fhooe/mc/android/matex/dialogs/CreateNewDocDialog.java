package at.fhooe.mc.android.matex.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.LinkedList;

import at.fhooe.mc.android.matex.R;
import at.fhooe.mc.android.matex.activities.EditorActivity;
import at.fhooe.mc.android.matex.document.Document;


public class CreateNewDocDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_create_new, null);

        final LinkedList<String> list = Document.getDocumentNamesList(getContext());
        final EditText et = view.findViewById(R.id.dialog_create_editText_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.create_dialog_create_new))
                .setMessage(getString(R.string.create_dialog_enter_title))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String t = treat(et.getText().toString());

                    Document.createDocument(getContext(), t);

                    Intent i = new Intent(getActivity(), EditorActivity.class);
                    i.putExtra("DocumentTitle", t);
                    startActivity(i);
                })
                .setNegativeButton(android.R.string.no, null)
                .setCancelable(false) // cant close this dialog by pressing outside it
        ;

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        final TextView tv = view.findViewById(R.id.dialog_create_textView_already_exits);


        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String treatedTitle = treat(s.toString());
                if (list.contains(treatedTitle)) {
                    // Disable ok button
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    tv.setVisibility(View.VISIBLE);
                } else if (treatedTitle.equals("")) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    tv.setVisibility(View.GONE);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    tv.setVisibility(View.GONE);
                }

            }
        });

        dialog.setOnShowListener(dialog1 -> ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false));

        return dialog;
    }

    private static String treat(String s){
        return s
                .replace("/", "")
                .replace("*", "")
                .replace(" ", "_")
                .trim();
    }



}
