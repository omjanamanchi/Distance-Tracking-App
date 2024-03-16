package com.example.gpsapp;

import android.location.Location;

public class LocTime
{
    Location location;
    double time;

    public LocTime(Location location, double time)
    {
        this.location = location;
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }

    public double getTime() {
        return time;
    }

    public double addTime(double t)
    {
        time += t;
        return time;
    }
}
