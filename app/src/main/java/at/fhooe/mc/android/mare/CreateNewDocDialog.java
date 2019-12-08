package at.fhooe.mc.android.mare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import at.fhooe.mc.android.mare.document.Document;


public class CreateNewDocDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_create_new, null);

        final LinkedList<String> list = Document.getDocumentNamesList();
        final EditText et = view.findViewById(R.id.dialog_create_editText_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.create_dialog_create_new))
                .setMessage(getString(R.string.create_dialog_enter_title))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String t = et.getText().toString().trim();


                        Document.createDocument(t, getContext());

                        Intent i = new Intent(getActivity(), EditorActivity.class);
                        i.putExtra("DocumentTitle", t);
                        startActivity(i);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
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
                if (list.contains(s.toString().trim())) {
                    // Disable ok button
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    tv.setVisibility(View.VISIBLE);

                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    tv.setVisibility(View.GONE);

                }

            }
        });


        return dialog;
    }


}
