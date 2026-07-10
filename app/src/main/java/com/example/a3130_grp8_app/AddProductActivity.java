package com.example.a3130_grp8_app;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        ));
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
            Toast.makeText(this, "Upload Photo (Mock for now)", Toast.LENGTH_SHORT).show();
        }
        private void onCreateListingButtonClick() {
            createListingButton.setEnabled(false);

            String name = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String address = locationInput.getText().toString().trim();
            double price = parsePrice(marketValueInput.getText().toString().trim());
            List<String> categories = Collections.singletonList( (String) categorySpinner.getSelectedItem());
            String dateAvailable = dateInput.getText().toString().trim();
            List<String> preferredItems = Arrays.asList(
                    preferredItemsInput.getText().toString().split("\\s*,\\s*"));
            String providerUid = FirebaseManager.getCurrentUser() != null ? FirebaseManager.getCurrentUser().getUid() : null;
            String timeUploaded = new SimpleDateFormat("yyyy-MM-dd' T 'HH:mm:ss", Locale.CANADA)
                    .format(new Date());

            Product product = new Product(name, description, address, price, categories, true, "placeholder", providerUid, timeUploaded, dateAvailable, preferredItems);

            FirebaseManager.createProduct(product).addOnSuccessListener(productId -> onCreateListingSuccess()).addOnFailureListener(this::onCreateListingFailure);

        }

        private double parsePrice(String rawValue) {
            try {
                return Double.parseDouble(rawValue);
            } catch (NumberFormatException e) {
            return 0.0;
            }
        }
        private void onCreateListingSuccess() {
            Toast.makeText(this, "Listing created", Toast.LENGTH_SHORT).show();
            finish();
        }
        private void onCreateListingFailure(Exception error) {
            Toast.makeText(this, "Could not create listing", Toast.LENGTH_SHORT).show();
            createListingButton.setEnabled(true);
        }

}
