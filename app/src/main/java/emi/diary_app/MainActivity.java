package emi.diary_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

    private ListView table;
    private Button
            btnShare,
            btnNewEntry,
            btnDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeApp();
    }


    public void InitializeApp() {

        setContentView(R.layout.activity_main);

        table = (ListView) findViewById(R.id.tableDisplayEntry);

        btnShare = (Button) findViewById(R.id.btnShare);
        btnNewEntry = (Button) findViewById(R.id.btnNewEntry);
        btnDelete = (Button) findViewById(R.id.btnDelete);


        final Database database = new Database(this);
        final TableManager tableManager = new TableManager(getApplicationContext(), table, database);



        btnNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Note note = new Note(1, "Datum", "new Entry");
                note.setTextNote("nochmal anders");
                tableManager.updateEntry(note);

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Note note = new Note(tableManager.getNextFreeID(), "Datum", "new Entry");
                note.setTextNote("Das ist ein Tagebucheintrag...\n\n\nEnde.");
                note.addToDatabase(database);

                tableManager.addEntry(note);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Note note = new Note(1, "Datum", "new Entry");
                tableManager.removeEntry(note);
            }
        });

    }

}
