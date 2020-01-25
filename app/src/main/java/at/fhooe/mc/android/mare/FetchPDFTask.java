package at.fhooe.mc.android.mare;

import android.os.AsyncTask;
import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
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

public class FetchPDFTask extends AsyncTask<Void, Void, Void> {

    private static final String server = "http://192.168.1.145:8080";

    private String id, error;
    private PDFPreviewFragment pdfFragment;
    private Document mDocument;


    public FetchPDFTask(PDFPreviewFragment _pdfFragment, Document _document) {
        pdfFragment = _pdfFragment;
        mDocument = _document;
    }


    @Override
    protected Void doInBackground(Void... voids) {

        getId();

        if (id != null) {
            sendMarkdown();

            sendImages();

            downloadPdf();
        } else {
            error = "Couldn't establish connection to server.";
            pdfFragment.setError(error);
        }
        return null;
    }


    private void sendImages() {
        mDocument.deleteUnusedImages();
        File[] images = mDocument.getImageDir().listFiles();

        for (File img : images) {
            uploadSingleImage(img);
        }

    }

    private void uploadSingleImage(File file) {
        try {
            HttpResponse<String> response =
                    Unirest.post(server + "/upload/" + id + "/img")
                            .field("upload", new File(file.getAbsolutePath()))
                            .asString();

            Log.i("MaRe", "image " + file.getName() + " uploaded. resp:  " + response.getBody());

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private void downloadPdf() {
        int count;
        try {
            Log.i("MaRe", "Starting pdf download");
            URL url = new URL(server + "/pdf/" + id);
            File pdf = mDocument.getPDFFile();

            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            OutputStream output = new FileOutputStream(pdf.getAbsolutePath());

            byte data[] = new byte[1024];

            long total = 0;

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


    private void sendMarkdown() {
        File file = mDocument.getFile();
        try {
            HttpResponse<String> response =
                    Unirest.post(server + "/upload/" + id + "/md")
                            .field("upload", new File(file.getAbsolutePath()))
                            .asString();

        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }


    private void getId() {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.get(server + "/upload/new")
                    .asString();
            id = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("MaRe", "ID: " + id);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i("MaRe", "onPostExecute");

        if (error != null) {
            File pdf = mDocument.getPDFFile();
            pdfFragment.mPDFView.fromFile(pdf).load();
            pdfFragment.setLoading(false);
        } else {
            pdfFragment.setError(error);
        }
        super.onPostExecute(aVoid);
    }
}
