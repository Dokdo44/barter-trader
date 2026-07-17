package com.example.a3130_grp8_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a3130_grp8_app.FirebaseConnectionError;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.util.FormValidator;
import com.example.a3130_grp8_app.util.SimpleTextWatcher;
import com.example.a3130_grp8_app.data.SupabaseStorageManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.IOException;
import java.io.InputStream;

/**
 * Activity responsible for creating and submitting new product listings.
 *
 * Handles user input validation, photo selection, image uploading,
 * and saving product data to Firebase.
 */
public class AddProductActivity extends AppCompatActivity {

    private static final String[] CATEGORY_OPTIONS = {"Select a Category", "Decoration", "Sports", "Technology", "Personal Care", "Other"};
    private static final int MAX_PHOTO_SIZE_PIXELS = 1080;

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText locationInput;
    private EditText dateInput;
    private EditText marketValueInput;
    private EditText preferredItemsInput;
    private Spinner categorySpinner;
    private Button createListingButton;
    private View addPhotoPlaceholder;
    private ImageView selectedPhotoPreview;
    private TextView addPhotoPrompt;
    private Uri selectedPhotoUri;
    private ActivityResultLauncher<String[]> photoPickerLauncher;
    private boolean isSubmitting;

    private final ExecutorService photoUploadExecutor = Executors.newSingleThreadExecutor();

    /**
     * Initializes the activity, sets up UI components, listeners, and form validation.
     *
     * @param savedInstanceState the previously saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registers the system image picker before the click handler tries to launch it.
        photoPickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                this::handleSelectedPhoto);

        EdgeToEdge.enable(this);
        setContentView(R.layout.add_product_activity);

        titleInput = findViewById(R.id.InputTitle);
        descriptionInput = findViewById(R.id.InputDescription);
        locationInput = findViewById(R.id.InputLocation);
        dateInput = findViewById(R.id.InputDate);
        marketValueInput = findViewById(R.id.InputMarketValue);
        preferredItemsInput = findViewById(R.id.InputPreferredItems);
        categorySpinner = findViewById(R.id.CategorySpinner);
        createListingButton = findViewById(R.id.createListing);
        addPhotoPlaceholder = findViewById(R.id.addPhotoPlaceholder);
        selectedPhotoPreview = findViewById(R.id.selectedPhotoPreview);
        addPhotoPrompt = findViewById(R.id.addPhotoPrompt);

        categorySpinner.setAdapter(createCategoryAdapter());

        SimpleTextWatcher requiredFieldsWatcher = createRequiredFieldsWatcher();
        titleInput.addTextChangedListener(requiredFieldsWatcher);
        descriptionInput.addTextChangedListener(requiredFieldsWatcher);
        locationInput.addTextChangedListener(requiredFieldsWatcher);
        dateInput.addTextChangedListener(requiredFieldsWatcher);
        marketValueInput.addTextChangedListener(requiredFieldsWatcher);

        categorySpinner.setOnItemSelectedListener(createCategorySelectinListener());
        addPhotoPlaceholder.setOnClickListener(v -> onAddPhotoClick());
        createListingButton.setOnClickListener(v -> onCreateListingButtonClick());
        checkCreateListingButtonState();
    }

    /**
     * Cleans up resources used by the activity.
     *
     * Shuts down the photo upload executor when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        photoUploadExecutor.shutdownNow();
    }


    private ArrayAdapter<String> createCategoryAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CATEGORY_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
    private void checkCreateListingButtonState() {
        createListingButton.setEnabled(FormValidator.isListingComplete(
                titleInput.getText(),
                descriptionInput.getText(),
                locationInput.getText(),
                dateInput.getText(),
                marketValueInput.getText(),
                categorySpinner.getSelectedItemPosition()
        ) && !isSubmitting);
    }
    private SimpleTextWatcher createRequiredFieldsWatcher() {
        return new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkCreateListingButtonState();
            }
        };
    }
    private AdapterView.OnItemSelectedListener createCategorySelectinListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkCreateListingButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }
    private void onAddPhotoClick() {
        // Opens the device document picker so providers can attach an existing image.
        photoPickerLauncher.launch(new String[]{"image/*"});
    }

    void handleSelectedPhoto(Uri photoUri) {
        if (photoUri == null) {
            return;
        }

        selectedPhotoUri = photoUri;
        // Keep read access to the selected photo after the picker returns to this Activity.
        try {
            getContentResolver().takePersistableUriPermission(photoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {
            // Some test or provider URIs are preview-only and do not grant persistable access.
        }
        selectedPhotoPreview.setImageURI(photoUri);
        selectedPhotoPreview.setVisibility(View.VISIBLE);
        addPhotoPrompt.setText("Change Photo");
        Toast.makeText(this, "Photo attached", Toast.LENGTH_SHORT).show();
    }

    Uri getSelectedPhotoUri() {
        return selectedPhotoUri;
    }

    private void onCreateListingButtonClick() {
        setSubmitting(true);

        String listingId = FirebaseManager.reserveProductId();
        if(listingId == null) {
            setSubmitting(false);
            Toast.makeText(this, "Could not create listing", Toast.LENGTH_SHORT).show();
            return;
        }
        Product product = createProductFromForm();

        if (selectedPhotoUri == null) {
            saveProduct(listingId, product);
            return;
        }
        photoUploadExecutor.execute(() -> {
            try {
                Bitmap scaledPhoto = decodeAndScaleSelectedPhoto();
                String photoUrl = SupabaseStorageManager.uploadListingPhoto(scaledPhoto, listingId);
                runOnUiThread(() -> {
                    product.setImageUrl(photoUrl);
                    saveProduct(listingId, product);
                        });
            } catch (IOException error) {

                runOnUiThread(() -> {
                    setSubmitting(false);
                    Toast.makeText(this, "Could not upload photo", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private void saveProduct(String listingId, Product product) {
        try {
            FirebaseManager.saveProduct(listingId, product)
                    .addOnCompleteListener(this, task -> {
                        setSubmitting(false);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Listing posted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Could not post listing", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (FirebaseConnectionError error) {
            setSubmitting(false);
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Product createProductFromForm() {

        String name = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String address = locationInput.getText().toString().trim();
        String dateAvailable = dateInput.getText().toString().trim();
        float price = parsePrice(marketValueInput.getText().toString().trim());
        List<String> preferredItems = Arrays.asList(
                preferredItemsInput.getText().toString().split("\\s*,\\s*")
        );
        List<String> categories = new ArrayList<>(
                Collections.singletonList((String) categorySpinner.getSelectedItem())
        );
        String timeUploaded = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA).format(new Date());

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setAddress(address);
        product.setPrice(price);
        product.setCategories(categories);
        product.setAvailable(true);
        product.setTimeUploaded(timeUploaded);
        product.setDateAvailable(dateAvailable);
        product.setPreferredItems(preferredItems);

        return product;




    }

    private float parsePrice(String rawValue) {
        try {
            return Float.parseFloat(rawValue);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }
    private Bitmap decodeAndScaleSelectedPhoto() throws IOException {
        try (InputStream photoStream = getContentResolver().openInputStream(selectedPhotoUri)) {
            if (photoStream == null) {
                throw new IOException("Could not open selected photo");
            }
            Bitmap sourceBitmap = BitmapFactory.decodeStream(photoStream);
            if (sourceBitmap == null) {
                throw new IOException("Could not decode selected photo");
            }
            return scaleBitmap(sourceBitmap);
        }
    }
    private Bitmap scaleBitmap(Bitmap sourceBitmap) {
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        int largestSide = Math.max(width, height);
        if (largestSide <= MAX_PHOTO_SIZE_PIXELS) {
            return sourceBitmap;
        }

        float scale = (float) MAX_PHOTO_SIZE_PIXELS / largestSide;
        int scaledWidth = Math.round(width * scale);
        int scaledHeight = Math.round(height * scale);
        return Bitmap.createScaledBitmap(sourceBitmap, scaledWidth, scaledHeight, true);
    }
    private void setSubmitting(boolean submitting) {
        isSubmitting = submitting;
        createListingButton.setText(submitting ? "Posting..." : "Post Listing");
        checkCreateListingButtonState();
    }
}
