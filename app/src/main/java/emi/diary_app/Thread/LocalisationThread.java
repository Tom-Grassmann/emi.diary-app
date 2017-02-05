package emi.diary_app.Thread;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import emi.diary_app.Activity.MainActivity;

public class LocalisationThread extends Thread {

    private static final String TAG = "LocalisationThread";
    private static final int DELAY = 1000;

    private static final int LOCATION_FOUND = 20;
    private static final int LOCATION_ERROR = 21;


    private MainActivity currentActivity;
    private Handler locHandler;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private int tryCounter = 0;

    public LocalisationThread(MainActivity currentActivity, final Handler locHandler) {

        this.currentActivity = currentActivity;
        this.locHandler = locHandler;


        try {
            /* Getting the System LocationMaanger */
            locationManager = (LocationManager) currentActivity.getSystemService(Context.LOCATION_SERVICE);

            /* Set Listener that reacts on LocationUpdate */
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {

                    /* If Location was found -> Get City from it and send Message to Handler */
                    Bundle bundle = new Bundle();
                    bundle.putString("location", getCityfromLocation(location));
                    bundle.putInt("resultCode", LOCATION_FOUND);

                    Message message = new Message();
                    message.setData(bundle);

                    locHandler.sendMessage(message);
                    locationManager.removeUpdates(this);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while(true) {

            Log.d(TAG, "Locating...");

            if (tryCounter == 10) {

                Bundle bundle = new Bundle();
                bundle.putInt("resultCode", LOCATION_ERROR);

                Message message = new Message();
                message.setData(bundle);
                locHandler.sendMessage(message);

                locationManager.removeUpdates(locationListener);

                break;
            }

            tryCounter++;

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                Log.d(TAG, "Interrupting and stopping the Localisation Thread");
                return;
            }
        }
    }

    private String getCityfromLocation(Location location) {

        Geocoder geocoder = new Geocoder(currentActivity, Locale.GERMAN);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }

                return returnedAddress.getLocality() + " (" + returnedAddress.getCountryName() + ")";
            }
            else{
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}

