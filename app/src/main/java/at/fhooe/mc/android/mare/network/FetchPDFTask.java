package at.fhooe.mc.android.mare.network;

import android.os.AsyncTask;
import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import at.fhooe.mc.android.mare.document.Document;
import at.fhooe.mc.android.mare.ui.pdfpreview.PDFPreviewFragment;
import local.org.apache.http.HttpStatus;

public class FetchPDFTask extends AsyncTask<Void, Void, Void> {

    private static final String server = "http://192.168.1.145:8080";

    private PDFPreviewFragment pdfFragment;
    private Document mDocument;
    private boolean mSuccess = true;

    public FetchPDFTask(PDFPreviewFragment _pdfFragment, Document _document) {
        pdfFragment = _pdfFragment;
        mDocument = _document;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        mDocument.getPDFFile().delete();
        startConnection();

        return null;
    }

    private void startConnection() {
        Unirest.setTimeouts(0, 0);
        Unirest.get(server + "/upload/new")
                .asStringAsync(new Callback<String>() {
                    @Override
                    public void completed(HttpResponse<String> httpResponse) {
                        String id = httpResponse.getBody();
                        Log.i("MaRe", "ID: " + id);
                        sendMarkdown(id);
                        sendImages(id);
                        downloadPdf(id);
                    }

                    @Override
                    public void failed(UnirestException e) {
                        mSuccess = false;
                        pdfFragment.setError("Couldn't establish connection to Server.");
                    }

                    @Override
                    public void cancelled() {

                    }
                });

    }

    private void sendMarkdown(final String id) {
        File file = mDocument.getFile();

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post(server + "/upload/" + id + "/md")
                    .field("upload", new File(file.getAbsolutePath()))
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        // Log.i("MaRe", "markdown response: " + response.getBody());
        if (response.getCode() == HttpStatus.SC_CREATED) {
            // SUCCESS
        } else {
            mSuccess = false;
            pdfFragment.setError("Error uploading .md File to Server.");
        }

    }


    private void sendImages(String id) {
        mDocument.deleteUnusedImages();
        File[] images = mDocument.getImageDir().listFiles();

        for (File img : images) {
            uploadSingleImage(id, img);
        }

    }

    private void uploadSingleImage(String id, File file) {
        HttpResponse<String> response = null;
        try {
            response = Unirest.post(server + "/upload/" + id + "/img")
                    .field("upload", new File(file.getAbsolutePath()))
                    .asString();

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        // Log.i("MaRe", "image " + file.getName() + " uploaded. resp:  " + response.getBody());
        if (response.getCode() == HttpStatus.SC_CREATED) {
            // SUCCESS
        } else {
            mSuccess = false;
            pdfFragment.setError("Error uploading image to Server.");
        }
    }

    private void downloadPdf(String id) {
        int count;
        try {
            Log.i("MaRe", "Starting pdf download");
            URL url = new URL(server + "/pdf/" + id);
            File pdf = mDocument.getPDFFile();

            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conection.getContentLength();
            long total = 0;

            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            OutputStream output = new FileOutputStream(pdf.getAbsolutePath());

            byte data[] = new byte[1024];


            while ((count = input.read(data)) != -1) {
                total += count;

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
            Log.i("MaRe", "finished download");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i("MaRe", "onPostExecute, Success: " + mSuccess +
                " file exists: " + mDocument.getPDFFile().exists());

        File pdf = mDocument.getPDFFile();
        pdfFragment.mPDFView.fromFile(pdf).load();
        pdfFragment.setLoading(false);
        if (!mDocument.getPDFFile().exists()) {
            pdfFragment.setError("Error generating PDF.");
        }
        super.onPostExecute(aVoid);
    }
}
