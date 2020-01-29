package at.fhooe.mc.android.mare;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import at.fhooe.mc.android.mare.document.Document;

public class MyFileProvider {
    private static final String PKG = "com.matex.fileprovider";
    public static final String MIMETYPE_ZIP = "application/zip";
    public static final String MIMETYPE_PDF = "application/pdf";
    public static final String MIMETYPE_MD = "text/markdown";

    private Context mContext;
    private Document mDocument;

    public MyFileProvider(Context _context, Document _document) {
        mContext = _context;
        mDocument = _document;
    }

    public void shareFile(File _file, String mimeType) {
        try {
            Uri uri;
            if (mimeType.equals(MIMETYPE_PDF))
                uri = movePDFToShareFolder(_file);
            else
                uri = moveFileToShareFolder(_file);
            createIntent(uri, mimeType);
        } catch (Exception e) {
            showError(e);
        }


    }

    private Uri moveFileToShareFolder(File _file) throws IOException {

        Path source = _file.toPath();
        Path newdir = getShareFolder().toPath();
        Path newPath = newdir.resolve(source.getFileName());
        Files.copy(source, newPath, StandardCopyOption.REPLACE_EXISTING);

        return FileProvider.getUriForFile(mContext, PKG, newPath.toFile());
    }

    private Uri movePDFToShareFolder(File _file) throws IOException {
        File shareFolder = getShareFolder();
        File newFile = new File(shareFolder, mDocument.getTitle() + ".pdf");
        Files.copy(_file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return FileProvider.getUriForFile(mContext, PKG, newFile);
    }

    private File getShareFolder() {
        File shareFolder = new File(mContext.getFilesDir(), "share/");
        shareFolder.mkdirs();
        return shareFolder;
    }


    private void createIntent(Uri _uri, String _mimeType) {
        Intent shareIntent = new Intent();
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, _uri);
        shareIntent.setType(_mimeType);
        mContext.startActivity(Intent.createChooser(shareIntent, null));
    }

    private void showError(Exception _e) {
        _e.printStackTrace();
        new AlertDialog.Builder(mContext)
                .setTitle("Error sharing file.")
                // .setMessage(_e.getMessage() + "\n\n" + _e.toString())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


}
