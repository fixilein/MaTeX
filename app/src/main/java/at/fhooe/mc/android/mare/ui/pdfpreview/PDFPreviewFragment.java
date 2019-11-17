package at.fhooe.mc.android.mare.ui.pdfpreview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import at.fhooe.mc.android.mare.R;

public class PDFPreviewFragment extends Fragment {

    private PDFPreviewViewModel PDFPreviewViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PDFPreviewViewModel = ViewModelProviders.of(this).get(PDFPreviewViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pdf_preview, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        PDFPreviewViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}