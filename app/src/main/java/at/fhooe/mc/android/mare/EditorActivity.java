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
                return true;

            }

            case R.id.menu_editor_share_pdf: {
                File pdf = mDocument.getPDFFile();
                if (!pdf.exists()) {
                    new AlertDialog.Builder(EditorActivity.this)
                            .setTitle(getApplicationContext().getString(R.string.create_pdf_first))
                            .setMessage("In order to share a PDF file, go to the PDF Tab and create one!")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                } else {

                    try {
                        // TODO
                        String pkg = "com.mare.fileprovider";
                        Uri uri = FileProvider.getUriForFile(getApplicationContext(), pkg, pdf);
                        grantUriPermission(pkg, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        Intent shareIntent = new Intent();

                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, pdf);
                        shareIntent.setType("application/pdf");
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(EditorActivity.this)
                                .setTitle("Error sharing file.")
                                // .setMessage(e.getMessage())
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                    return true;
                }
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }


    }

}
