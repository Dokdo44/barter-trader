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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.a3130_grp8_app.data.FirebaseManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductSearchActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;

    private SupportMapFragment mapFragment;
    private FragmentManager fragmentManager;
    private GoogleMap googleMap;

    private Button mapViewButton;
    private Button searchButton;

    private List<Marker> markers = new ArrayList<>();

    FirebaseDatabase db;
    DatabaseReference productReference;
    private List<Product> products;

    EditText searchField;

    RecyclerView productRecyclerView;
    ProductSearchResultAdapter productSearchResultAdapter;

    FirebaseManager manager = FirebaseManager.getInstance();

    private LatLng currPosition;

    Spinner proximitySpinner;
    private String[] proximitySpinnerOptions = new String[] {"Select Proximity", "5km", "10km", "25km"};

    private FirebaseManager.ProductValueEventListener getProductValueEventListener(String query) {
        return new FirebaseManager.ProductValueEventListener() {
            @Override
            public void onGetProducts(List<Product> products) {
                if(productRecyclerView == null || productSearchResultAdapter == null) {
                    return;
                }
                List<Product> filteredProducts = filterSearchResults(query, products);
                productSearchResultAdapter.resetData(filteredProducts);

                // Adjust map markers to new products
                for(Marker marker : markers) {
                    marker.remove();
                }
                markers.clear();
                for(Product product : filteredProducts) {
                    addMapMarkerForProduct(product);
                }
            }
        };
    }

    private View.OnTouchListener onProximityCheckedStateChangeListener =
    new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return getCurrPositionOnProximityChipClickOrGetPermissions();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_product_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productSearchConstraintLayout),
                (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        searchButton = findViewById(R.id.searchButton);
        searchButton.setEnabled(false);
        searchButton.setOnClickListener(v -> onSearch());
        searchField = findViewById(R.id.searchEditText);

        createProximitySpinner();
        proximitySpinner.setOnTouchListener(onProximityCheckedStateChangeListener);

        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productSearchResultAdapter = new ProductSearchResultAdapter(new ArrayList<>());
        productRecyclerView.setAdapter(productSearchResultAdapter);

        initGms();
        initMapViewButton();


    }

    private void createProximitySpinner() {
        proximitySpinner = findViewById(R.id.proximitySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                proximitySpinnerOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        proximitySpinner.setAdapter(adapter);
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

    private void addMapMarkerForProduct(Product product) {
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
                markers.add(marker);
            }
        } catch (IOException | IllegalArgumentException e) {
            Log.e("error", e.getMessage());
        }
    }

    private void onMarkerInfoWindowClick(Marker marker) {
        Product product = (Product) marker.getTag();
        if(product == null) return;

        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("PRODUCT", product);
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        updateCameraLocation();

        googleMap.setOnInfoWindowClickListener(this::onMarkerInfoWindowClick);
        beforeSearchPage();
        searchButton.setEnabled(true);
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

    private final ActivityResultLauncher<String> requestPermissionsOnProximityChipClick =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    this::handlePermissionResultOnProximityChipClick);

    private void handlePermissionResultOnProximityChipClick(boolean isGranted) {
        if(isGranted) {
            setCurrPosition();
        }
    }

    private boolean getCurrPositionOnProximityChipClickOrGetPermissions() {
        if(haveLocationPermissions()) {
            setCurrPosition();
            return false;
        } else {
            requestPermissionsOnProximityChipClick.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    private void setCurrPosition() {
        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(this, this::setCurrPositionByLocation);
    }

    private void setCurrPositionByLocation(Location location) {
        currPosition = new LatLng(location.getLatitude(), location.getLongitude());
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

    private void beforeSearchPage()
    {
        FirebaseManager.ProductValueEventListener productValueEventListener =
                getProductValueEventListener("");
        manager.getProducts(productValueEventListener, 100);
    }

    private void onSearch()
    {
        String query = searchField.getText().toString();
        FirebaseManager.ProductValueEventListener productValueEventListener =
                getProductValueEventListener(query);
        manager.getProducts(productValueEventListener, 100);
    }

    private List<Product> filterSearchResults(String query, List<Product> products)
    {
        ChipGroup categoryChipGroup = findViewById(R.id.categoryChipGroup);
        List<String> selectedCategories = getSelectedChipTexts(categoryChipGroup);

        List<Product> ret = new ArrayList<>();
        for (Product product : products)
        {
            boolean nameContainsQuery = product.getName().contains(query);
            boolean categoriesMatch = matchesAtLeastOne(product.getCategories(), new ArrayList<>(selectedCategories));
            if(!proximitySpinner.getSelectedItem().toString().equals("Select Proximity")) {
                if(!isAddressWithinProximitySpinnerRange(product.getAddress())) {
                    continue;
                }
            }
            if (nameContainsQuery && categoriesMatch)
            {
                ret.add(product);
            }
        }
        return ret;
    }

    private boolean isAddressWithinProximitySpinnerRange(String address) {
        assert currPosition != null;
        int range = switch(proximitySpinner.getSelectedItem().toString()) {
            case "5km" -> 5000;
            case "10km" -> 10000;
            case "25km" -> 25000;
            default -> 100000;
        };
        try {
            LatLng addressLatLng = getLatLngForAddressString(address);

            float[] results = new float[1];
            Location.distanceBetween(
                    currPosition.latitude, currPosition.longitude,
                    addressLatLng.latitude, addressLatLng.longitude,
                    results);
            return results[0] <= range;
        } catch (IOException | IllegalArgumentException e) {
            Log.e("error", "Could not find address");
            return false;
        }

    }

    private void  sortProductsByDate()
    {
        products.sort((p1, p2) -> {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            return p2.getTimeUploaded().compareTo(p1.getTimeUploaded());
        });
    }

    private List<String> getSelectedChipTexts(ChipGroup chipGroup) {
        List<String> selectedTexts = new ArrayList<>();

        for (Integer id : chipGroup.getCheckedChipIds()) {
            Chip chip = chipGroup.findViewById(id);

            if (chip != null) {
                selectedTexts.add(chip.getText().toString());
            }
        }

        return selectedTexts;
    }

    private boolean matchesAtLeastOne(List<String> itemCategories, List<String> selectedCategories)
    {
        if (selectedCategories.isEmpty()) return true;

        for (String category : itemCategories)
        {
            if (selectedCategories.contains(category))
            {
                return true;
            }
        }

        return false;
    }

    private void loadProductsWithListener()
    {
        products.clear();
        manager.addProductChildEventListener("timeUploaded", new FirebaseManager.ProductChildEventListener()
        {
            @Override
            public void onGetProduct (Product product)
            {
                if (product == null) return;
                products.add(product);
                sortProductsByDate();


            }


        });
    }
}
