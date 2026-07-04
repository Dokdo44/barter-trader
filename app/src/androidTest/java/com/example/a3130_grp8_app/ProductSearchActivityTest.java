package com.example.a3130_grp8_app;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

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
    final int LAUNCH_TIMEOUT = 5000;
    private UiDevice device;
    final String launcherPackage = "com.example.a3130_grp8_app";

    @Before
    public void setUp() throws IOException {
        FirebaseManager.setAuth(FirebaseEmulator.getAuth());
        FirebaseManager.setDb(FirebaseEmulator.getDatabase());

        FirebaseEmulator.sendBulkDataToDatabase("ProductSearchActivityTest/database_test_data.json");
        FirebaseEmulator.sendBulkDataToAuth("ProductSearchActivityTest/auth_test_data.json");

        FirebaseManager.signIn("receiver@example.com", "password123");

        device = UiDevice.getInstance(getInstrumentation());
        Context context = ApplicationProvider.getApplicationContext();

        final Intent launchIntent = new Intent(context, ProductSearchActivity.class);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(launchIntent);

        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
    }

    @After
    public void tearDown() throws IOException {
        FirebaseEmulator.resetAuth();
        FirebaseEmulator.resetDatabase();
    }

    @Test
    public void productsOrderedByUploadTimeOnInitialEntry() throws IOException, UiObjectNotFoundException {
        assert FirebaseManager.getCurrentUser().getUid().equals("user1");

        UiObject product1 = device.findObject(new UiSelector().textContains("Toyota Something"));
        UiObject product2 = device.findObject(new UiSelector().textContains("Ford Something"));
        UiObject product3 = device.findObject(new UiSelector().textContains("Honda Something"));

        assertTrue(product1.exists() && product2.exists() && product3.exists());

        Rect product1Rect = product1.getBounds();
        Rect product2Rect = product2.getBounds();
        Rect product3Rect = product3.getBounds();

        assertTrue(product1Rect.centerY() < product2Rect.centerY());
        assertTrue(product2Rect.centerY() < product3Rect.centerY());
    }

}
