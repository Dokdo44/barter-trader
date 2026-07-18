package com.example.a3130_grp8_app.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoLocator {
    private final Geocoder geocoder;

    public GeoLocator(Context context) {
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    public LatLng getLatLngForAddressString(String addressString)
            throws
            IOException,
            IllegalArgumentException{
        List<Address> addressResults;
        addressResults = geocoder.getFromLocationName(addressString, 1);
        if(addressResults == null || addressResults.isEmpty()) {
            throw new IllegalArgumentException(
                    "Address had no geolocation results: " + addressString
            );
        }
        Address address = addressResults.get(0);
        return new LatLng(address.getLatitude(), address.getLongitude());
    }
}
