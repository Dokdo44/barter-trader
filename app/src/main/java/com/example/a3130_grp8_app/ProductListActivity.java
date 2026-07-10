package com.example.a3130_grp8_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ProductListActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private FragmentManager fragmentManager;
    private Button mapViewButton;

    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private final CountDownLatch mapInitializedLatch = new CountDownLatch(1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        fragmentManager = getSupportFragmentManager();

        mapViewButton = findViewById(R.id.mapViewButton);
        mapViewButton.setOnClickListener(onMapViewButtonClick);
    }

    private final View.OnClickListener onMapViewButtonClick = view -> {
        if(mapFragment.isHidden()) {
            fragmentManager.beginTransaction().show(mapFragment).commit();
        } else {
            fragmentManager.beginTransaction().hide(mapFragment).commit();
        }
    };

    FirebaseManager.ProductChildEventListener productChildEventListener =
            new FirebaseManager.ProductChildEventListener() {
        @Override
        public void onGetProduct(Product product) {
            List<Address> addressResults;
            try {
                addressResults = geocoder.getFromLocationName(product.getAddress(), 1);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return;
            }
            if(addressResults == null || addressResults.isEmpty()) {
                System.err.println("Was not able to get address for product: " + product.getAddress());
                return;
            }
            Address address = addressResults.get(0);
            LatLng position = new LatLng(address.getLatitude(), address.getLongitude());

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(product.getName())
                    .snippet(Float.toString(product.getPrice())));
            if(marker != null) {
                marker.setTag(product);
            }
        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        enableMyLocation();
        updateCameraLocation();
        mapInitializedLatch.countDown();

        FirebaseManager.getInstance().addProductChildEventListener("address", productChildEventListener);

        googleMap.setOnInfoWindowClickListener(marker -> {
            Product product = (Product) marker.getTag();
            if(product == null) return;

            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_NAME", product.getName());
            startActivity(intent);
        });
    }

    private final ActivityResultLauncher<String> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    enableMyLocation();
                } else {
                    throw new RuntimeException("This is a temporary exception showing that location must be enabled");
                }
            });
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            return;
        }
        requestPermissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @SuppressLint("MissingPermission")
    private void updateCameraLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if(location != null) {
                        LatLng targetLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(targetLatLng, 10.0f);
                        googleMap.animateCamera(cameraUpdate);
                        return;
                    }
                    throw new RuntimeException("Could not get location");
                });
    }
}
