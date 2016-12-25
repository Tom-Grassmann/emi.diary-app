package emi.diary_app;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


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
        //database.removeAllData();
        final TableManager tableManager = new TableManager(MainActivity.this, table, database);





        btnNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Note note = new Note(database.getNextFreeID() - 1, "Image Entry");
                note.setTextNote("mit Bild...");

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                note.setImageNote(((BitmapDrawable)imageView.getDrawable()).getBitmap());
                System.out.println(tableManager.updateEntry(note));

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int x = database.getNextFreeID();
                System.out.println(Integer.toString(x));

                Note note = new Note(database.getNextFreeID(), "new Entry");
                note.setTextNote("Das ist ein Tagebucheintrag...\n\n\nEnde.");

                tableManager.addEntry(note);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, database.getTableAsString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
