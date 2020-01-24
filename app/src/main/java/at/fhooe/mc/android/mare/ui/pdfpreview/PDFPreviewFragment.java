package at.fhooe.mc.android.mare.ui.pdfpreview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

import at.fhooe.mc.android.mare.EditorActivity;
import at.fhooe.mc.android.mare.FetchPDFTask;
import at.fhooe.mc.android.mare.R;

public class PDFPreviewFragment extends Fragment {

    public PDFView mPDFView;
    public View mView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pdf_preview, container, false);

        mPDFView = root.findViewById(R.id.pdfView);
        new FetchPDFTask(this, EditorActivity.mDocument).execute();

        mView = root;
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

}