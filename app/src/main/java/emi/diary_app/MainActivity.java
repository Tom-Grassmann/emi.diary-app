package emi.diary_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    final static int EDIT_ENTRY = 1;
    final static int ADD_ENTRY = 2;

    private ListView table;
    private Button
            btnAdd,
            btnNewEntry,
            btnDelete,
            btnEdit;

    private TableManager tableManager;
    private Database database;

    private static Note noteToEidt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeApp();
    }

    public void InitializeApp() {

        setContentView(R.layout.activity_main);

        table = (ListView) findViewById(R.id.tableDisplayEntry);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnEdit = (Button) findViewById(R.id.btnEdit);


        database = new Database(this);
        //database.removeAllData();
        tableManager = new TableManager(this, table, database);

        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Note note = new Note(database.getNextFreeID(), "New Entry");

                Intent i = new Intent(MainActivity.this, EditEntryActivity.class);
                i.putExtra("note", note);
                i.putExtra("requestCode", ADD_ENTRY);

                startActivityForResult(i, ADD_ENTRY);


            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ArrayList<Note> arrayList = new ArrayList<>();

                Cursor data = database.getAllData();

                String picturePath;
                while (data.moveToNext()) {

                    Note note = new Note(data.getInt(0), data.getString(1));
                    note.setTimestamp(data.getLong(2));
                    note.setTextNote(data.getString(3));
                    note.setVoiceNote(data.getString(4));
                    note.setImageNote(data.getString(5));

                    arrayList.add(note);
                }

                Collections.sort(arrayList);

                /* Start Edit Activity for Result */
                Intent i = new Intent(MainActivity.this, EditEntryActivity.class);
                i.putExtra("note", arrayList.get(0));

                if (arrayList.get(0).getImageNote() != null) {

                    File internalStorage = getDir("Pictures", Context.MODE_PRIVATE);
                    File FilePath = new File(internalStorage, arrayList.get(0).getID() + ".png");
                    picturePath = FilePath.toString();

                } else {

                    picturePath = "";
                }
                i.putExtra("imagePath", picturePath);
                i.putExtra("requestCode", EDIT_ENTRY);


                startActivityForResult(i, EDIT_ENTRY);
            }
        });



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == EDIT_ENTRY) {

                Note note = (Note) data.getSerializableExtra("note");

                if (tableManager.updateEntry(note)) {

                    Toast.makeText(this, "Edit Confirmed!", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Edit Failed!", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == ADD_ENTRY) {

                Note note = (Note) data.getSerializableExtra("note");
                tableManager.addEntry(note);

                Toast.makeText(this, "Neuer Eintrag hinzugefügt!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
