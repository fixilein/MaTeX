package at.fhooe.mc.android.matex.ui.pdfpreview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PDFPreviewViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PDFPreviewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Coming soon...");
    }

    public LiveData<String> getText() {
        return mText;
    }
}