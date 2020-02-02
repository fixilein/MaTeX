package at.fhooe.mc.android.matex.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

import at.fhooe.mc.android.matex.MyFileProvider;
import at.fhooe.mc.android.matex.R;
import at.fhooe.mc.android.matex.document.Document;

public class EditorActivity extends AppCompatActivity {

    public static String mTitle;
    public static Document mDocument;
    public static File mFile, mDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        mTitle = getIntent().getStringExtra("DocumentTitle");

        mFile = Document.getFileFromName(mTitle);
        mDirectory = Document.getDirectoryFromName(mTitle);
        mDocument = new Document(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        getMenuInflater().inflate(R.menu.menu_editor, _menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_editor_delete: {
                deleteFileDialog();
                return true;
            }

            case R.id.menu_editor_share_md: {
                File f = mDocument.getFile();
                shareFile(f, MyFileProvider.MIMETYPE_MD);
                return true;
            }

            case R.id.menu_editor_share_pdf: {
                File pdf = mDocument.getPDFFile();
                if (!pdf.exists()) {
                    pdfDoesNotExistAlert();
                } else {
                    shareFile(pdf, MyFileProvider.MIMETYPE_PDF);
                }
                return true;
            }

            case R.id.menu_editor_share_zip: {
                File f = mDocument.getZipFile();
                shareFile(f, MyFileProvider.MIMETYPE_ZIP);
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }


    }

    private void deleteFileDialog() {
        new AlertDialog.Builder(EditorActivity.this)
                .setTitle(String.format(getApplicationContext().getString(R.string.delete_question), mTitle))
                .setMessage(getApplicationContext().getString(R.string.delete_question_long))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    mDocument.deleteFiles();
                    finish(); // close editor
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void pdfDoesNotExistAlert() {
        new AlertDialog.Builder(EditorActivity.this)
                .setTitle(getString(R.string.dialog_create_pdf_first))
                .setMessage(getString(R.string.dialog_create_pdf_first_message))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void shareFile(File _file, String mimeType) {
        MyFileProvider fileProvider = new MyFileProvider(this, mDocument);
        fileProvider.shareFile(_file, mimeType);
    }

}
