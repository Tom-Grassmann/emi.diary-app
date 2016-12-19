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
            btnNewEntry;

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

        final TableManager tableManager = new TableManager(getApplicationContext(), table);


        btnNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VoiceNote tNote = new VoiceNote(0, "Datum", "new Entry");
                //tNote.setText_note("Content");

                tableManager.addEntry(tNote);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TextNote tNote = new TextNote(0, "Datum", "new Entry");
                tNote.setText_note("Das ist ein Tagebucheintrag...\n\n\nEnde.");

                tableManager.addEntry(tNote);
            }
        });

    }

}
