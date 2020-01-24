package at.fhooe.mc.android.mare.ui.pdfpreview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.github.barteksc.pdfviewer.PDFView;

import at.fhooe.mc.android.mare.EditorActivity;
import at.fhooe.mc.android.mare.FetchPDFTask;
import at.fhooe.mc.android.mare.R;

public class PDFPreviewFragment extends Fragment {

    private PDFPreviewViewModel PDFPreviewViewModel;
    public PDFView mPDFView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PDFPreviewViewModel = ViewModelProviders.of(this).get(PDFPreviewViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pdf_preview, container, false);

        mPDFView = root.findViewById(R.id.pdfView);
        new FetchPDFTask(this, EditorActivity.mDocument).execute();

        return root;
    }

}