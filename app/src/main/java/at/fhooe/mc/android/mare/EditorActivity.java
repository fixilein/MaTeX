package at.fhooe.mc.android.mare;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

import at.fhooe.mc.android.mare.document.DocumentContent;
import at.fhooe.mc.android.mare.document.DocumentContent.Document;
import at.fhooe.mc.android.mare.ui.editor.EditorFragment;

public class EditorActivity extends AppCompatActivity {

    public static String mTitle;
    public static String mFilename;
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
        mDirectory = Document.getDirectoryFromName(mTitle);}


    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, _menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_editor_delete) {
            new AlertDialog.Builder(EditorActivity.this)
                    .setTitle(String.format(getApplicationContext().getString(R.string.delete_question), mTitle))
                    .setMessage(getApplicationContext().getString(R.string.delete_question_long))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFile();
                            finish(); // close editor
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean deleteFile() {
        return mFile.delete() && mDirectory.delete();
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


}
