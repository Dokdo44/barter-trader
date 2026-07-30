package com.example.a3130_grp8_app.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.Rating;
import com.example.a3130_grp8_app.model.Request;
import com.example.a3130_grp8_app.model.User;
import com.example.a3130_grp8_app.util.PreferredCategoriesListener;
import com.example.a3130_grp8_app.util.ProductListenerRegistration;
import com.example.a3130_grp8_app.util.ProductPostedListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Centralizes Firebase Auth and Realtime Database calls so Activities do not hold business logic.
 */
public class FirebaseManager {

    // Singleton
    private static FirebaseManager instance;

    private static final String USERS_PATH = "users";
    private static final String PRODUCTS_PATH = "product";
    private static final String REQUESTS_PATH = "requests";
    static final String FIREBASE_URL = "https://group8-19144-default-rtdb.firebaseio.com/";

    private FirebaseAuth auth;
    private FirebaseDatabase db;

    protected FirebaseManager() {}

    /**
     * Returns the singleton instance of the Firebase manager.
     *
     * @return the shared FirebaseManager instance
     */
    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    /**
     * Installs the singleton instance for the application.
     *
     * @param firebaseManager the FirebaseManager instance to install
     */
    public static void install(FirebaseManager firebaseManager) {
        instance = firebaseManager;
    }

    /**
     * Returns the currently authenticated Firebase user.
     *
     * @return the signed-in Firebase user, or {@code null} if no user is signed in
     */
    public FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    /**
     * Returns the Firebase Realtime Database instance.
     *
     * @return the database instance
     */
    public FirebaseDatabase getDb() {
        if (db == null) {
            db = FirebaseDatabase.getInstance(FIREBASE_URL);
        }
        return db;
    }

    /**
     * Returns the Firebase Authentication instance.
     *
     * @return the authentication instance
     */
    public FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    /**
     * Sets the Firebase Realtime Database instance.
     *
     * @param db the database instance to use
     */
    public void setDb(FirebaseDatabase db) {
        this.db = db;
    }

    /**
     * Sets the Firebase Authentication instance.
     *
     * @param auth the authentication instance to use
     */
    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    /**
     * Creates a new user account and stores the user's profile information.
     *
     * @param email the user's email address
     * @param password the user's password
     * @param role the user's role
     * @return a Task that completes when the account and profile have been created
     */
    public Task<Void> createUser(String email, String password, String role) {
        return getAuth()
                .createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    AuthResult authResult = task.getResult();
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        throw new FirebaseConnectionError();
                    }

                    return getUserReference(firebaseUser.getUid()).setValue(new User(email, role));
                });
    }

    /**
     * Signs a user into Firebase Authentication.
     *
     * @param email the user's email address
     * @param password the user's password
     * @return a Task that resolves to the authentication result
     */
    public Task<AuthResult> signIn(String email, String password) {
        return getAuth().signInWithEmailAndPassword(email, password);
    }

    /**
     * Retrieves the profile of the currently signed-in user.
     *
     * @return a Task that resolves to the user's profile
     * @throws FirebaseConnectionError if no user is currently signed in
     */
    public Task<User> getCurrentUserProfile() throws FirebaseConnectionError {
        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser == null) {
            throw new FirebaseConnectionError();
        }

        return getUserReference(firebaseUser.getUid()).get()
                .continueWith(task -> task.getResult().getValue(User.class));
    }

    /**
     * Retrieves the profile of a user by their Firebase UID.
     *
     * @param uid the UID of the user to retrieve
     * @return a Task that resolves to the user's profile
     */
    public Task<User> getUserById(String uid) {
        return getUserReference(uid)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return task.getResult().getValue(User.class);
                });
    }

    /**
     * Saves new product listing under /product/{productId}.
     *
     * @param product product to save
     * @return task to resolve generated product id
     */
    public static Task<String> createProduct(Product product) {
        DatabaseReference newRef = getProductsReference().push();
        String productId = newRef.getKey();
        if (productId == null) {
            return Tasks.forException(new FirebaseConnectionError());
        }
        return newRef.setValue(product).continueWith(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return productId;
        });
    }

    /**
     * Loads a single product by ID.
     *
     * @param productId Id of the product to load
     * @return task that resolves to the product
     */
    public Task<Product> getProduct(String productId) {
        return getProductsReference().child(productId).get()
                .continueWith(task -> task.getResult().getValue(Product.class));
    }

    /**
     * Loads a single product by ID and returns it through a callback.
     *
     * @param productId ID of the product to load
     * @param callback callback that receives the product
     */
    public void getProductById(String productId, ProductCallback callback) {
        getProductsReference()
                .child(productId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Product product = snapshot.getValue(Product.class);
                    callback.onProductLoaded(product);
                })
                .addOnFailureListener(e -> {
                    callback.onProductLoaded(null);
                });
    }

    /**
     * Callback interface for loading a single product.
     */
    public interface ProductCallback {
        void onProductLoaded(Product product);
    }

    /**
     * Reserves and returns a unique product ID.
     *
     * @return a newly generated product ID
     */
    public static String reserveProductId() {
        return getProductsReference().push().getKey();
    }

    /**
     * Saves a product under the specified product ID.
     *
     * @param productId the ID of the product
     * @param product the product to save
     * @return a Task that completes when the product has been saved
     * @throws FirebaseConnectionError if no user is signed in or the product ID is invalid
     */
    public static Task<Void> saveProduct(String productId, Product product) throws FirebaseConnectionError {
        FirebaseUser firebaseUser = getInstance().getCurrentUser();
        if (firebaseUser == null || productId == null) {
            throw new FirebaseConnectionError();
        }

        product.setProviderUid(firebaseUser.getUid());
        return getProductsReference().child(productId).setValue(product);
    }

    private static DatabaseReference getProductsReference() {
        return getInstance().getDb().getReference(PRODUCTS_PATH);
    }

    /**
     * Sends a Firebase Auth password reset email.
     *
     * @param email account email that should receive the reset message
     * @return Firebase Auth password reset task
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return getAuth().sendPasswordResetEmail(email);
    }

    /**
     * Clears the current Firebase Auth session.
     */
    public void signOut() {
        getAuth().signOut();
    }

    private DatabaseReference getUserReference(String uid) {
        return getDb().getReference(USERS_PATH).child(uid);
    }

    public static abstract class ProductChildEventListener implements ChildEventListener {
        @Override
        public final void onChildAdded(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {
            Product product = snapshot.getValue(Product.class);
            onGetProduct(product);
        }

        @Override
        public final void onChildChanged(@NonNull DataSnapshot snapshot,
                                         @Nullable String previousChildName) {}

        @Override
        public final void onChildRemoved(@NonNull DataSnapshot snapshot) {}

        @Override
        public final void onChildMoved(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {}

        @Override
        public final void onCancelled(@NonNull DatabaseError error) {}

        public abstract void onGetProduct(Product product);
    }

    public abstract static class ProductValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<Product> products = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Product product = snapshot.getValue(Product.class);
                assert product != null;
                product.setId(snapshot.getKey());
                products.add(product);
            }
            onGetProducts(products);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {}

        public abstract void onGetProducts(List<Product> products);
    }

    /**
     * Registers a listener for product changes ordered by the specified field.
     *
     * @param orderBy the child field used to order products
     * @param productChildEventListener the listener that receives product events
     */
    public void addProductChildEventListener(String orderBy,
                                             ProductChildEventListener productChildEventListener) {
        getDb().getReference(PRODUCTS_PATH)
                .orderByChild(orderBy)
                .addChildEventListener(productChildEventListener);
    }

    public void getProducts(ProductValueEventListener productValueEventListener,
                            int limitToFirst) {
        getProductReference()
                .orderByChild("timeUploaded")
                .limitToLast(limitToFirst)
                .addListenerForSingleValueEvent(productValueEventListener);
    }

    public void getProductsByUserId(String userId,
                                    ProductValueEventListener productValueEventListener) {
        getProductReference()
                .orderByChild("providerUid")
                .equalTo(userId)
                .addListenerForSingleValueEvent(productValueEventListener);
    }

    public DatabaseReference getProductReference() {
        return getDb().getReference(PRODUCTS_PATH);
    }

    public Task<Void> addPreferredProductForUser(String userId, String preferredProductId) {
        return getUserReference(userId)
                .child("preferredProductIds")
                .push()
                .setValue(preferredProductId);
    }

    public Task<Void> deletePreferredProductForUser(String userId, String preferenceId) {
        return getUserReference(userId)
                .child("preferredProductIds")
                .child(preferenceId)
                .removeValue();
    }

    public void addChildEventListenerForUserPreferredProductIds(
            String userId, ChildEventListener listener) {
        getUserReference(userId)
                .child("preferredProductIds")
                .addChildEventListener(listener);
    }

    public Task<Void> addPreferredCategoryForUser(String userId, String preferredCategoryName) {
        return getUserReference(userId)
                .child("preferredCategoryNames")
                .child(preferredCategoryName)
                .setValue(preferredCategoryName);
    }

    public Task<Void> deletePreferredCategoriesForUser(
            String userId, String preferredCategoryName) {
        return getUserReference(userId)
                .child("preferredCategoryNames")
                .child(preferredCategoryName)
                .removeValue();
    }

    public void addChildEventListenerForUserPreferredCategoryNames(
            String userId, ChildEventListener listener) {
        getUserReference(userId)
                .child("preferredCategoryNames")
                .addChildEventListener(listener);
    }

    /**
     * Submits a request for a product if the product is available and not already requested.
     *
     * @param productId the ID of the product being requested
     * @return a Task that resolves on success
     * @throws FirebaseConnectionError when no Auth user is signed in
     */
    public Task<Void> submitRequest(String productId, String providerUid, String requesterName,
                                    String requesterLocation, List<String> offeredItems) throws FirebaseConnectionError {
        FirebaseUser firebaseUser = getInstance().getCurrentUser();
        if (firebaseUser == null) {
            throw new FirebaseConnectionError();
        }

        String receiverUid = firebaseUser.getUid();
        String receiverEmail = firebaseUser.getEmail();
        DatabaseReference requestsRef = getInstance().getDb().getReference(REQUESTS_PATH);
        DatabaseReference productRef = getInstance().getDb()
                .getReference(PRODUCTS_PATH).child(productId);

        return productRef.get()
                .continueWithTask(checkAvailabilityAndFetchExisting(productId, requestsRef))
                .continueWithTask(checkDuplicateAndSubmit(productId, providerUid, receiverUid,
                        requesterName, requesterLocation, offeredItems, requestsRef, receiverEmail));
    }

    private Continuation<DataSnapshot, Task<DataSnapshot>> checkAvailabilityAndFetchExisting(
            String productId, DatabaseReference requestsRef) {
        return productTask -> {
            if (!productTask.isSuccessful() || productTask.getResult() == null) {
                throw new IOException("Could not retrieve product");
            }

            Boolean available = productTask.getResult().child("available").getValue(Boolean.class);
            if (available == null || !available) {
                throw new IOException("Product is no longer available");
            }

            return requestsRef.orderByChild("productId").equalTo(productId).get();
        };
    }

    private Continuation<DataSnapshot, Task<Void>> checkDuplicateAndSubmit(
            String productId, String providerUid, String receiverUid, String requesterName,
            String requesterLocation, List<String> offeredItems, DatabaseReference requestsRef,
            String receiverEmail) {
        return existingRequestsTask -> {
            if (!existingRequestsTask.isSuccessful()) {
                Exception e = existingRequestsTask.getException();
                if (e != null) throw e;
                throw new IOException("Could not check existing requests");
            }

            for (DataSnapshot snap : existingRequestsTask.getResult().getChildren()) {
                String existingReceiver = snap.child("receiverUid").getValue(String.class);
                if (receiverUid.equals(existingReceiver)) {
                    throw new Exception("You have already requested this product");
                }
            }

            String requestId = requestsRef.push().getKey();
            if (requestId == null) {
                throw new FirebaseConnectionError();
            }

            return requestsRef.child(requestId).setValue(
                    new Request(requestId, productId, receiverEmail, receiverUid,
                            requesterName, requesterLocation, offeredItems, providerUid));
        };
    }

    /**
     * Updates the status of an existing request (e.g. "accepted" or "declined").
     *
     * @param requestId the Firebase key of the request
     * @param status the new status to set
     * @return a Task that resolves on success
     */
    public Task<Void> updateRequestStatus(String requestId, String status) {
        return getInstance().getDb().getReference(REQUESTS_PATH)
                .child(requestId)
                .child("status")
                .setValue(status);
    }

    public abstract static class RequestValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<Request> requests = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Request request = snapshot.getValue(Request.class);
                assert request != null;
                request.setRequestId(snapshot.getKey());
                requests.add(request);
            }
            onGetRequests(requests);
        }

        @Override
        public void onCancelled(DatabaseError error) { }

        public abstract void onGetRequests(List<Request> requests);
    }

    public void getRequests(String productId,
                            RequestValueEventListener requestValueEventListener) {
        getDb()
                .getReference(REQUESTS_PATH)
                .orderByChild("productId")
                .equalTo(productId)
                .addListenerForSingleValueEvent(requestValueEventListener);
    }

    /**
     * Retrieves all requests made by a specific receiver.
     *
     * @param receiverUid the UID of the receiver
     * @param requestValueEventListener the listener that receives requests
     */
    public void getRequestsByReceiver(String receiverUid,
                                      RequestValueEventListener requestValueEventListener) {
        getDb()
                .getReference(REQUESTS_PATH)
                .orderByChild("receiverUid")
                .equalTo(receiverUid)
                .addListenerForSingleValueEvent(requestValueEventListener);
    }

    /**
     * Retrieves all requests for products provided by a specific provider.
     *
     * @param providerUid the UID of the provider
     * @param requestValueEventListener the listener that receives requests
     */
    public void getRequestsByProvider(String providerUid,
                                      RequestValueEventListener requestValueEventListener) {
        getDb()
                .getReference(REQUESTS_PATH)
                .orderByChild("providerUid")
                .equalTo(providerUid)
                .addListenerForSingleValueEvent(requestValueEventListener);
    }

    /**
     * Retrieves all ratings for a specific provider.
     *
     * @param providerId the ID of the provider
     * @param listener callback to handle the ratings data
     */
    public void getRatings(String providerId, RatingValueEventListener listener) {
        throw new UnsupportedOperationException();
    }

    /**
     * Callback interface for rating data retrieval.
     */
    public interface RatingValueEventListener {
        void onGetRatings(ArrayList<Rating> ratings);
    }

    /**
     * Loads the preferred category names for a given user.
     *
     * @param userId the UID of the user
     * @param listener callback invoked with the loaded categories or an error
     */
    public void loadPreferredCategories(String userId, PreferredCategoriesListener listener) {
        getDb().getReference(USERS_PATH)
                .child(userId)
                .child("preferredCategoryNames")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> categories = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String category = child.getValue(String.class);
                            if (category != null) {
                                categories.add(category);
                            }
                        }
                        listener.onCategoriesLoaded(categories);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError(error.toException());
                    }
                });
    }

    public ProductListenerRegistration listenForNewProduct(ProductPostedListener listener) {
        long registrationTime = System.currentTimeMillis();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    product.setId(snapshot.getKey());
                    if (isProductNewSince(product, registrationTime)) {
                        listener.onProductPosted(product);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.toException());
            }
        };

        DatabaseReference productsRef = getProductReference();
        productsRef.addChildEventListener(childEventListener);

        return () -> productsRef.removeEventListener(childEventListener);
    }

    private boolean isProductNewSince(Product product, long registrationTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            Date uploadedDate = format.parse(product.getTimeUploaded());
            if (uploadedDate == null) return false;
            return uploadedDate.getTime() >= registrationTime;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Returns the UID of the currently signed-in user.
     *
     * @return the current user's UID, or {@code null} if no user is signed in
     */
    @Nullable
    public String getCurrentUserID() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

}
