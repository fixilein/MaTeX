package at.fhooe.mc.android.mare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.List;

import at.fhooe.mc.android.mare.document.DocHeader;
import at.fhooe.mc.android.mare.document.Document;
import at.fhooe.mc.android.mare.document.DocumentAdapter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Document.setmContext(getApplicationContext());
        createFAB();
        // createWelcomeDocumentOnFirstStart();
        //closeKeyboard();
    }

    private void createWelcomeDocumentOnFirstStart() { // TODO redo
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean firstLaunch = sharedPref.getBoolean(getString(R.string.preference_first_launch), true);
        if (firstLaunch) {

            Document d = Document.createDocument(getString(R.string.welcome_doc_title));
            DocHeader header = d.getHeader();
            header.setAuthor("Felix");
            header.setDate("Decmber 8th 2019");
            d.saveFile(header.toString(), getString(R.string.welcome_doc_content));
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.preference_first_launch), false);
        editor.apply();

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
        List<Document> list = Document.getDocumentList();

        final DocumentAdapter adapter = new DocumentAdapter(this, list);
        adapter.addAll(list);

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
