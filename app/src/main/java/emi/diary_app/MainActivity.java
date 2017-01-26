package emi.diary_app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ActionBar.OnNavigationListener {

    final static int EDIT_ENTRY = 1;
    final static int ADD_ENTRY = 2;
    final static int RESULT_OK_NO_LOCATION = 10;


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


            final Note note = (Note) data.getSerializableExtra("note");

            /* - - - - - - - Set Location of Entry - - - - - - - - - - - - - - - - - - - - - - - - - - */
            try {
                // Acquire a reference to the system Location Manager
                final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // Define a listener that responds to location updates
                final LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.


                        note.setCity(getCityfromLocation(location));
                        locationManager.removeUpdates(this);
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };

                // Register the listener with the Location Manager to receive location updates
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


                final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.scheduleAtFixedRate(new Runnable() {

                    int tryFindingLocation = 0;

                    @Override
                    public void run() {

                        if (note.getCity() != null) {

                            tableManager.notifyAdapter();
                            executorService.shutdown();
                        }

                        if (tryFindingLocation == 10) {

                            note.setCity("NO_LOCATION");
                            locationManager.removeUpdates(locationListener);
                            executorService.shutdown();
                        }

                        tryFindingLocation++;
                    }
                }, 0, 1, TimeUnit.SECONDS);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Fehler beim ermitteln des Standortes!", Toast.LENGTH_SHORT).show();
            }
            /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */



            /*if (note.getCity().equals("NO_LOCATION")) {

                Toast.makeText(MainActivity.this, "Could not locate City, GPS disabled?", Toast.LENGTH_SHORT).show();
            }*/

            if (requestCode == EDIT_ENTRY) {

                /* Decode Image from path */
                Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
                note.setBitmap(image);


                if (tableManager.updateEntry(note)) {

                    Toast.makeText(this, "Edit Confirmed!", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Edit Failed!", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == ADD_ENTRY) {


                /* Decode Image from path */
                Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
                note.setBitmap(image);

                tableManager.addEntry(note);

                Toast.makeText(this, "Neuer Eintrag hinzugefügt!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private String getCityfromLocation(Location location) {

        Geocoder geocoder = new Geocoder(this, Locale.GERMAN);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }

                return returnedAddress.getLocality();
            }
            else{
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ort konnte nicht lokalisiert werden!", Toast.LENGTH_SHORT).show();
        }

        return "";
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        return false;
    }
}
