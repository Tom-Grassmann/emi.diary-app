package emi.diary_app;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActionBar.OnNavigationListener {

    final static int EDIT_ENTRY = 1;
    final static int ADD_ENTRY = 2;

    private ListView listView;
    private TableManager tableManager;
    private Database database;
    private ActionBar actionBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeApp();
    }

    public void InitializeApp() {

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.tableDisplayEntry);

        database = new Database(this);
        tableManager = new TableManager(this, listView, database);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.MenuSelected_AddEntry) {

            Note note = new Note(database.getNextFreeID(), "New Entry");

            Intent i = new Intent(MainActivity.this, EditEntryActivity.class);
            i.putExtra("note", note);
            i.putExtra("requestCode", ADD_ENTRY);

            startActivityForResult(i, ADD_ENTRY);
        }


        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == EDIT_ENTRY) {

                Note note = (Note) data.getSerializableExtra("note");

                /* Decode Image from path */
                Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
                note.setBitmap(image);

                if (tableManager.updateEntry(note)) {

                    Toast.makeText(this, "Edit Confirmed!", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Edit Failed!", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == ADD_ENTRY) {

                Note note = (Note) data.getSerializableExtra("note");

                /* Decode Image from path */
                Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
                note.setBitmap(image);

                tableManager.addEntry(note);

                Toast.makeText(this, "Neuer Eintrag hinzugefügt!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        return false;
    }
}
