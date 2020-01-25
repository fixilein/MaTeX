package at.fhooe.mc.android.mare.ui.pdfpreview;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

import at.fhooe.mc.android.mare.EditorActivity;
import at.fhooe.mc.android.mare.R;
import at.fhooe.mc.android.mare.network.RetrofitGetPdfTask;

public class PDFPreviewFragment extends Fragment {

    public PDFView mPDFView;
    public View mView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pdf_preview, container, false);

        mPDFView = root.findViewById(R.id.pdfView);
        //new FetchPDFTask(this, EditorActivity.mDocument).execute();

        new RetrofitGetPdfTask(this, EditorActivity.mDocument).execute();

        mView = root;
        setLoading(true);
        return root;
    }

    public void setLoading(boolean b) {
        View icon = mView.findViewById(R.id.fragment_pdf_loading);
        if (b) {
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }
    }

    public void setError(final String error) {
        getActivity().runOnUiThread(
                () -> new AlertDialog.Builder(PDFPreviewFragment.this.getActivity()).
                        setTitle("Error while generating PDF")
                        .setMessage(error)
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show()
        );
    }
}