package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import androidx.test.ext.junit.runners.AndroidJUnit4;


import com.example.a3130_grp8_app.data.FirebaseManager;

import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.util.NotificationSender;
import com.example.a3130_grp8_app.util.PreferredCategoriesListener;
import com.example.a3130_grp8_app.util.PreferredCategoryMatcher;
import com.example.a3130_grp8_app.util.PreferredProductNotificationManager;
import com.example.a3130_grp8_app.util.ProductListenerRegistration;

import com.example.a3130_grp8_app.util.ProductPostedListener;


import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NotificationTest {
    private FirebaseManager mockFirebaseManager;
    private NotificationSender mockSender;
    private PreferredCategoryMatcher mockMatcher;
    private ProductListenerRegistration mockRegistration;
    private PreferredProductNotificationManager manager;
    private ProductPostedListener registeredProductListener;

    private Product matchingProduct;
    private Product notMatchingProduct;

    private List<String> preferredCategories;

    @Before
    public void setUp()
    {
        mockFirebaseManager = mock(FirebaseManager.class);
        mockSender = mock(NotificationSender.class);
        mockMatcher = mock(PreferredCategoryMatcher.class);
        mockRegistration = mock(ProductListenerRegistration.class);

        preferredCategories = Arrays.asList("Decoration", "Sports", "Personal Care");
        doAnswer(invocation -> {
            PreferredCategoriesListener listener = invocation.getArgument(1);

            listener.onCategoriesLoaded(preferredCategories);

            return null;
        }).when(mockFirebaseManager).loadPreferredCategories(anyString(), any(PreferredCategoriesListener.class));



        when(mockFirebaseManager.getCurrentUserID()).thenReturn("user-1");

        when(mockFirebaseManager.listenForNewProduct(any(ProductPostedListener.class))).thenAnswer(invocation -> {
            registeredProductListener =
                    invocation.getArgument(0);

            return mockRegistration;
        });

        matchingProduct = mock(Product.class);
        notMatchingProduct = mock(Product.class);

        when(mockMatcher.matches(preferredCategories, matchingProduct)).thenReturn(true);
        when(mockMatcher.matches(preferredCategories, notMatchingProduct)).thenReturn(false);



        manager = new PreferredProductNotificationManager(mockFirebaseManager, mockSender, mockMatcher);





    }

    @After
    public void tearDown() {
        registeredProductListener = null;
        manager = null;
    }

    private void simulateProductPosted(Product product)
    {
        assertNotNull("The manager has not registered a product listener", registeredProductListener);

        registeredProductListener.onProductPosted(product);
    }

    private void simulateProductListenerError(Exception exception)
    {
        assertNotNull("The manager has not registered a product listener", registeredProductListener);

        registeredProductListener.onError(exception);
    }

    private void simulatePreferredCategoriesLoadFailure(Exception exception)
    {
        doAnswer(invocation -> {
            PreferredCategoriesListener listener =
                    invocation.getArgument(1);

            listener.onError(exception);

            return null;
        }).when(mockFirebaseManager)
                .loadPreferredCategories(
                        anyString(),
                        any(PreferredCategoriesListener.class));
    }

    private void setCurrentUserId(String userId)
    {
        when(mockFirebaseManager.getCurrentUserID()).thenReturn(userId);
    }

    private void setCategoryMatchResult(Product product, boolean matches)
    {
        when(mockMatcher.matches(preferredCategories, product)).thenReturn(matches);
    }

}
