package com.example.gpsapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener
{
    TextView textLon, textLat, textAddress, textDistance, textTime;
    LocationManager location;
    Location oldLocation;
    ArrayList<LocTime> locationTimes;
    Geocoder geocoder;
    double lon = 0;
    double lat = 0;
    List<Address> addressList;
    float distance_travelled = 0f;
    double oldTime;
    double initTime;
    public static final String save_lon = "lon";
    public static final String save_lat = "lat";
    public static final String save_address = "address";
    public static final String save_distance = "distance";
    public static final String save_time = "time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTime = SystemClock.elapsedRealtime();
        textLon = findViewById(R.id.textLon);
        textLat = findViewById(R.id.textLat);
        textAddress = findViewById(R.id.textAddress);
        textDistance = findViewById(R.id.textDistance);
        textTime = findViewById(R.id.textTime);
        locationTimes = new ArrayList<LocTime>();

        location = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if(savedInstanceState != null){
            lon = savedInstanceState.getDouble(save_lon);
            lat = savedInstanceState.getDouble(save_lat);
            distance_travelled = savedInstanceState.getFloat(save_distance);
            oldTime = savedInstanceState.getDouble(save_time);
        }

        textTime.setText("Time: 0.0 seconds");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,};
            ActivityCompat.requestPermissions(this, permissions, 2);
            return;
        }
        else{location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, .1f, this);}
        geocoder = new Geocoder(this, Locale.getDefault());
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        boolean isThere = false;
        if(oldLocation != null) {
            for (LocTime i : locationTimes) {
                if (i.getLocation().equals(oldLocation)) {
                    isThere = true;
                }
            }
            if (!isThere) {
                locationTimes.add(new LocTime(oldLocation, 0.0));
            }
            for (LocTime i : locationTimes) {
                if (i.getLocation().equals(oldLocation)) {
                    i.addTime(SystemClock.elapsedRealtime() - oldTime - initTime);
                    oldTime = SystemClock.elapsedRealtime() - oldTime - initTime;
                    textTime.setText("Time: " + (oldTime/1000) + " seconds");
                    //oldTime = 0;
                }
            }
        }

        lat = location.getLatitude();
        lon = location.getLongitude();
        textLon.setText("Lon: " + String.valueOf(lon));
        textLat.setText("Lat: " + String.valueOf(lat));
        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);
            textAddress.setText("Address: " + addressList.get(0).getAddressLine(0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(oldLocation != null){
            distance_travelled += location.distanceTo(oldLocation);
        }
        oldLocation = location;
        if(SystemClock.elapsedRealtime() >= 25000)
        {
            textDistance.setText("Distance: " + String.valueOf(distance_travelled / 1600) + " miles");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, .1f, this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(save_lon, lon);
        outState.putDouble(save_lat, lat);
        outState.putFloat(save_distance, distance_travelled);
        outState.putDouble(save_time, oldTime);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
