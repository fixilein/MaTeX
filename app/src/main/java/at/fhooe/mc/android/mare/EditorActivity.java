package at.fhooe.mc.android.mare;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import at.fhooe.mc.android.mare.document.Document;

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, _menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.menu_editor_delete: {
                new AlertDialog.Builder(EditorActivity.this)
                        .setTitle(String.format(getApplicationContext().getString(R.string.delete_question), mTitle))
                        .setMessage(getApplicationContext().getString(R.string.delete_question_long))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDocument.deleteFiles();
                                finish(); // close editor
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;
            }

            case R.id.menu_editor_share_md: {
                File f = mDocument.getZipOrMdFile();
                shareFile(f, "string or md idk");
                return true;

            }

            case R.id.menu_editor_share_pdf: {
                File pdf = mDocument.getPDFFile();
                if (!pdf.exists()) {
                    new AlertDialog.Builder(EditorActivity.this)
                            .setTitle(getString(R.string.dialog_create_pdf_first))
                            .setMessage(getString(R.string.dialog_create_pdf_first_message))
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                } else {
                    shareFile(pdf, "application/pdf");
                    return true;
                }
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }


    }

    // TODO
    private void shareFile(File _file, String mimeType) {
        try {
            File shareFolder = new File(getFilesDir(), "share");
            shareFolder.mkdirs();
            Path copy = Files.copy(_file.toPath(), shareFolder.toPath());
            String pkg = "com.mare.fileprovider";
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), pkg, copy.toFile());

            Intent shareIntent = new Intent();

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType(mimeType);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)));
        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(EditorActivity.this)
                    .setTitle("Error sharing file.")
                    .setMessage(e.getMessage()) // TODO remove message
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

}
