package emi.diary_app.Activity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import emi.diary_app.Database.Database;
import emi.diary_app.Note;
import emi.diary_app.R;
import emi.diary_app.ListManagement.TableManager;
import emi.diary_app.Thread.LocalisationThread;

public class MainActivity extends AppCompatActivity {

    final static int EDIT_ENTRY = 1;
    final static int ADD_ENTRY = 2;
    final static int RESULT_OK_NO_LOCATION = 10;
    final static int LOCATION_FOUND = 20;
    final static int LOCATION_ERROR = 21;

    final static int REQUEST_READ_EXTERNAL_STORAGE = 30;
    final static int REQUEST_WRITE_EXTERNAL_STORAGE = 31;
    final static int REQUEST_RECORD_AUDIO = 32;
    final static int REQUEST_ACCESS_COARSE_LOCATION = 33;
    final static int REQUEST_ACCESS_FINE_LOCATION = 34;


    private ListView listView;
    private TableManager tableManager;
    private Database database;
    private ActionBar actionBar;

    private Handler locHandler;
    private Thread localisationThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeApp();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            askForPermission(REQUEST_READ_EXTERNAL_STORAGE);
            askForPermission(REQUEST_WRITE_EXTERNAL_STORAGE);

        }




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

    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {


            final Note note = (Note) data.getSerializableExtra("note");
            note.setSelected(false);


            switch (requestCode) {

                case (EDIT_ENTRY): {

                    /* Decode Image from path */
                    Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
                    note.setBitmap(image);


                    if (tableManager.updateEntry(note)) {

                        Toast.makeText(this, "Änderungen gespeichert!", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(this, "Eintrag konnte nicht gespeichert werden!", Toast.LENGTH_SHORT).show();
                    }

                    break;
                }

                case (ADD_ENTRY): {

                    /* - - - - - - - Try to Set Location of Entry - - - - - - - - - - - - - - - - - - - - - - - - - - */
                    /* Check for localisationPermission */
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        askForPermission(REQUEST_ACCESS_COARSE_LOCATION);

                    } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        askForPermission(REQUEST_ACCESS_FINE_LOCATION);

                    } else {

                        locHandler = new Handler() {

                            @Override
                            public void handleMessage(Message message) {

                                int resultCode = message.getData().getInt("resultCode");
                                String resultLocation = message.getData().getString("location");

                                switch (resultCode) {

                                    case (LOCATION_FOUND): {

                                        note.setCity(resultLocation);
                                        tableManager.updateEntry(note);

                                        localisationThread.interrupt();

                                        break;
                                    }
                                    case (LOCATION_ERROR): {

                                        note.setCity("NO_LOCATION");
                                        tableManager.updateEntry(note);

                                        Toast.makeText(MainActivity.this, "Fehler beim ermitteln des Standortes!", Toast.LENGTH_SHORT).show();

                                        localisationThread.interrupt();

                                        break;
                                    }

                                }
                            }
                        };

                        localisationThread = new Thread(new LocalisationThread(MainActivity.this, locHandler));
                        localisationThread.start();
                    }
                    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */


                    /* Decode Image from path */
                    Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
                    note.setBitmap(image);

                    tableManager.addEntry(note);

                    Toast.makeText(this, "Neuer Eintrag hinzugefügt!", Toast.LENGTH_SHORT).show();

                    break;
                }
            }
        }
    }


    private void askForPermission(int REQUEST_CODE) {

        switch (REQUEST_CODE) {

            case (REQUEST_READ_EXTERNAL_STORAGE): {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);

                }
                break;
            }

            case (REQUEST_WRITE_EXTERNAL_STORAGE): {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

                }
                break;
            }

            case (REQUEST_ACCESS_COARSE_LOCATION): {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);

                }
                break;
            }

            case (REQUEST_ACCESS_FINE_LOCATION): {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                }
                break;
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(this, "Please give Read Permission in Order to use the App!\n" +
                            "Please restart App now.", Toast.LENGTH_LONG).show();


                    // TODO: make App unusable when there is no Permission

                    RelativeLayout mainLayout = (RelativeLayout) MainActivity.this.findViewById(R.id.Main_Layout_Main);
                    mainLayout.setClickable(false);
                    mainLayout.setEnabled(false);
                }

                break;
            }
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(this, "Please give Write Permission in Order to use the App!\n" +
                            "Please restart App now.", Toast.LENGTH_LONG).show();

                    listView.setClickable(false);
                    closeOptionsMenu();
                }
                break;
            }
            case REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(this, "Bitte erlauben Sie die Ortung um das ermitteln des Standortes zu ermöglichen!", Toast.LENGTH_LONG).show();
                }

                break;
            }
            case REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(this, "Bitte erlauben Sie die Ortung um das ermitteln des Standortes zu ermöglichen!", Toast.LENGTH_LONG).show();
                }

                break;
            }

        }
    }


}
