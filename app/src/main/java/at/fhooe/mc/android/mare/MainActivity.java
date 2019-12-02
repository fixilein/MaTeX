package at.fhooe.mc.android.mare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import at.fhooe.mc.android.mare.document.Document;
import at.fhooe.mc.android.mare.document.DocumentAdapter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createFAB();
        closeKeyboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "TODO, hehe :p", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateNewDocDialog().show(getSupportFragmentManager(), "dialog");
                // showKeyboard();
                //launchTextEditorActivity(title);
            }
        });
    }

    void launchTextEditorActivity(String title) {
        Intent i = new Intent(MainActivity.this, EditorActivity.class);
        i.putExtra("DocumentTitle", title);
        startActivity(i);
    }


    private void fillList() {
        LinkedList<Document> list = new LinkedList<>();


        File[] files = new File("/data/data/at.fhooe.mc.android.mare").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) { // search for directories starting with "app_"
                return pathname.getName().startsWith("app_");
            }
        });


        Arrays.parallelSort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) { // sort by last modified date
                FileFilter ff = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.toString().endsWith(".md");
                    }
                };

                if (o1.listFiles(ff)[0].lastModified() < o2.listFiles(ff)[0].lastModified())
                    return 0;
                return -1;
            }
        });

        Log.i("MaRe", "Files size = " + files.length);

        for (File f : files)
            list.add(new Document(f.getName().replace("app_", "")));


        final DocumentAdapter adapter = new DocumentAdapter(this, list);
        adapter.addAll(list);
        Log.i("MaRe", "adapter count = " + adapter.getCount());

        ListView listView = findViewById(R.id.activity_main_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Document d = adapter.getItem(position);
                launchTextEditorActivity(d.toString());
            }
        });
    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
