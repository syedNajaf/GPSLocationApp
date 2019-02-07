package com.example.nzaidi.gpslocationapp;

import android.location.Location;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TaxiManager {

    private Location destinationLocation;

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public float returnTheDistanceToDestinationLocationInMeters(Location currentLocation)
    {
        if (currentLocation != null && destinationLocation != null)
        {
           return currentLocation.distanceTo(destinationLocation);
        }
        else
        {
            return -100.0f;
        }

    }

    public String returnTheMilesBetweenCurrentLocationAndDestinationLocation(Location currentLocation,int metersPerMile)
    {
       int miles = (int) (returnTheDistanceToDestinationLocationInMeters(currentLocation) / metersPerMile);

       if(miles == 1)
       {
           return "1 mile";
       }
       else if(miles > 1)
       {
           return miles + "miles";
       }

       else
       {
           return "no miles";
       }
    }

    public String returnTheTimeLeftToGetToDestinationLocation(Location currentLocation, float milesPerHour,int metersPerMile)
    {
       float distanceInMeters = returnTheDistanceToDestinationLocationInMeters(currentLocation);
       float timeLeft = distanceInMeters / (milesPerHour * metersPerMile);
       String timeResult = "";
       int timeLeftInHours = (int) timeLeft;

       if (timeLeftInHours == 1)
       {
           timeResult = timeResult + "1 Hour ";
       }
       else if (timeLeftInHours > 1)
       {
           timeResult += timeLeftInHours + "Hours ";
       }

       int minutesLeft = (int) ((timeLeft - timeLeftInHours) * 60);

       if (minutesLeft == 1)
       {
           timeResult = timeResult + "1 Minute ";
       }
       else if (minutesLeft > 1)
       {
           timeResult = timeResult + minutesLeft + "Minutes";
       }

       if (timeLeftInHours <= 0 && minutesLeft <= 0)
       {
           timeResult = "Less than a minute is left to get to the destination";
       }

       return timeResult;
    }
}
