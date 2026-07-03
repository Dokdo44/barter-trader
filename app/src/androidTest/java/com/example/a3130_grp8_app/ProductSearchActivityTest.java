package com.example.a3130_grp8_app;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.util.FirebaseEmulator;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class ProductSearchActivityTest {

    @Rule
    public ActivityScenarioRule<ProductSearchActivity> activityTestRule =
            new ActivityScenarioRule<>(ProductSearchActivity.class);

    @Before
    public void setUp() {
        FirebaseManager.setAuth(FirebaseEmulator.getAuth());
        FirebaseManager.setDb(FirebaseEmulator.getDatabase());
    }

    @After
    public void tearDown() throws IOException {
        FirebaseEmulator.resetAuth();
        FirebaseEmulator.resetDatabase();
    }

    @Test
    public void

}
