package com.example.a3130_grp8_app;

import android.content.Intent;
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
import com.example.a3130_grp8_app.model.ProductListing;
import com.example.a3130_grp8_app.util.FormValidator;
import com.example.a3130_grp8_app.util.SimpleTextWatcher;

public class AddProductActivity extends AppCompatActivity {

    private static final String[] CATEGORY_OPTIONS = {"Select a Category", "Decoration", "Sports", "Technology", "Personal Care", "Other"};

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
        try {
            FirebaseManager.createProductListing(createListingFromForm(), selectedPhotoUri)
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
            // A provider must be signed in before Firebase can save the listing under their uid.
            setSubmitting(false);
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private ProductListing createListingFromForm() {
        // FirebaseManager assigns the Firebase id, signed-in provider uid, and uploaded photo URL.
        return new ProductListing(
                null,
                null,
                titleInput.getText().toString().trim(),
                descriptionInput.getText().toString().trim(),
                locationInput.getText().toString().trim(),
                dateInput.getText().toString().trim(),
                marketValueInput.getText().toString().trim(),
                preferredItemsInput.getText().toString().trim(),
                categorySpinner.getSelectedItem().toString(),
                null
        );
    }

    private void setSubmitting(boolean submitting) {
        isSubmitting = submitting;
        createListingButton.setText(submitting ? "Posting..." : "Post Listing");
        checkCreateListingButtonState();
    }
}
