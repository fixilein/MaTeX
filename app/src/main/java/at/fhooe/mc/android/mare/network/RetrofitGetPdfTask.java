package at.fhooe.mc.android.mare.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import at.fhooe.mc.android.mare.document.Document;
import at.fhooe.mc.android.mare.ui.pdfpreview.PDFPreviewFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class RetrofitGetPdfTask extends AsyncTask<Void, Void, Void> {

    private static final String server = "http://192.168.1.145:8080";

    private final PDFPreviewFragment mPdfFragment;
    private final Document mDocument;

    private MatexBackend mService;

    public RetrofitGetPdfTask(PDFPreviewFragment _pdfFragment, Document _document) {
        mPdfFragment = _pdfFragment;
        mDocument = _document;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("Matex", "doInBackground");
        mDocument.deleteUnusedImages();
        mDocument.getPDFFile().delete();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server)
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        mService = retrofit.create(MatexBackend.class);


        String id = "";
        try {
            id = getId();
            uploadMdFile(id);

            for (File f : mDocument.getImageDir().listFiles()) {
                uploadImage(id, f);
            }

            getPdf(id);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Matex", "id: " + id);

        return null;
    }


    private String getId() throws java.io.IOException {
        return mService.getId().execute().body();
    }

    private void uploadMdFile(String id) throws IOException {
        File mdFile = mDocument.getFile();
        RequestBody filePart = RequestBody.create(MediaType.get("text/markdown"), mdFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("upload", mdFile.getName(), filePart);

        mService.uploadMdFile(id, file).execute();
    }

    private void uploadImage(String id, File photo) throws IOException {
        RequestBody filePart = RequestBody.create(MediaType.get("image/png"), photo);
        MultipartBody.Part file = MultipartBody.Part.createFormData("image", photo.getName(), filePart);

        mService.uploadImage(id, file).execute();
    }

    private void getPdf(String id) throws IOException {
        File pdf = mDocument.getPDFFile();
        ResponseBody body = mService.downloadPdf(id).execute().body();
        writeResponseBodyToDisk(body, pdf);
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i("Matex", "onPostExecute");
        File pdf = mDocument.getPDFFile();
        mPdfFragment.mPDFView.fromFile(pdf).load();
        mPdfFragment.setLoading(false);
        super.onPostExecute(aVoid);
    }

    /**
     * Boilder Plate Java Crap
     */
    private boolean writeResponseBodyToDisk(ResponseBody body, File pdfFile) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(pdfFile.getAbsolutePath());

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("Matex", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}