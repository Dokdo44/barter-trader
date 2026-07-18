package com.example.a3130_grp8_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProductListActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;

    private SupportMapFragment mapFragment;
    private FragmentManager fragmentManager;
    private GoogleMap googleMap;

    private Button mapViewButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        initGms();
        initMapViewButton();
    }

    private void initGms() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        geocoder = new Geocoder(this, Locale.getDefault());

        fragmentManager = getSupportFragmentManager();
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapFragment);
        fragmentManager.beginTransaction().hide(mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    private void initMapViewButton() {
        mapViewButton = findViewById(R.id.mapViewButton);
        mapViewButton.setOnClickListener(onMapViewButtonClick);
    }

    private final View.OnClickListener onMapViewButtonClick = view -> {
        if(mapFragment.isHidden()) {
            showMapOrGetPermissions();
        } else {
            hideMap();
        }
    };

    private void showMap() {
        fragmentManager.beginTransaction().show(mapFragment).commit();
        updateCameraLocation();
    }

    private void hideMap() {
        fragmentManager.beginTransaction().hide(mapFragment).commit();
    }

    private LatLng getLatLngForAddressString(String addressString)
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

    private String createProductSnippet(Product product) {
        return String.format(
                "Price: %s, Categories: %s, Address: %s\n",
                product.getPrice(),
                String.join(", ", product.getCategories()),
                product.getAddress()
        );
    }

    FirebaseManager.ProductChildEventListener productChildEventListener =
            new FirebaseManager.ProductChildEventListener() {
        @Override
        public void onGetProduct(Product product) {
            try {
                LatLng position = getLatLngForAddressString(product.getAddress());
                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                        .position(position)
                        .title(product.getName())
                        .snippet(createProductSnippet(product))
                );
                if(marker != null) {
                    marker.setTag(product);
                }
            } catch (IOException | IllegalArgumentException e) {
                Log.e("error", e.getMessage());
            }
        }
    };

    private void onMarkerInfoWindowClick(Marker marker) {
        Product product = (Product) marker.getTag();
        if(product == null) return;

        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("PRODUCT_NAME", product.getName());
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        updateCameraLocation();

        FirebaseManager
                .getInstance()
                .addProductChildEventListener("address", productChildEventListener);

        googleMap.setOnInfoWindowClickListener(this::onMarkerInfoWindowClick);
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocationIfPermitted() {
        googleMap.setMyLocationEnabled(true);
    }

    private boolean haveLocationPermissions() {
        int fineLocationPermissions = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int coarseLocationPermissions = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        );

        return fineLocationPermissions == PackageManager.PERMISSION_GRANTED
                || coarseLocationPermissions == PackageManager.PERMISSION_GRANTED;
    }

    private void handlePermissionsResultOnViewMapButtonClick(boolean isGranted) {
        if (isGranted) {
            enableMyLocationIfPermitted();
            showMap();
        } else {
            Log.d("debug", "User rejected permissions");
        }
    }

    private final ActivityResultLauncher<String> requestPermissionsOnViewMapButtonClick =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    this::handlePermissionsResultOnViewMapButtonClick);

    private void showMapOrGetPermissions() {
        if(haveLocationPermissions()) {
            if(!googleMap.isMyLocationEnabled()) {
                enableMyLocationIfPermitted();
            }
            showMap();
            return;
        }

        requestPermissionsOnViewMapButtonClick.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void updateCameraByLocation(Location location) {
        if(location != null) {
            LatLng targetLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(targetLatLng, 10.0f);
            googleMap.animateCamera(cameraUpdate);
            return;
        }
        throw new RuntimeException("Could not get location");
    }

    @SuppressLint("MissingPermission")
    private void updateCameraLocation() {
        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(this, this::updateCameraByLocation);
    }
}
