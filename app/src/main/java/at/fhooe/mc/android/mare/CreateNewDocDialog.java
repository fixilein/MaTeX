package at.fhooe.mc.android.mare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import at.fhooe.mc.android.mare.document.Document;


// TODO add margin to text field or move to fragment


public class CreateNewDocDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_create_new, null);
        builder.setView(view);

        builder.setTitle(getString(R.string.create_dialog_create_new))
                .setMessage(getString(R.string.create_dialog_enter_title))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et = view.findViewById(R.id.dialog_create_editText_title);
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
        return builder.create();
    }


}
