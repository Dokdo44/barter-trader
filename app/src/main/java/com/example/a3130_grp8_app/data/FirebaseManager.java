package com.example.a3130_grp8_app.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3130_grp8_app.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.Request;
import com.example.a3130_grp8_app.model.User;
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

import java.util.List;

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

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public static void install(FirebaseManager firebaseManager) {
        if (instance != null) {
            throw new IllegalStateException("FirebaseManager singleton already initialized");
        }
        instance = firebaseManager;
    }

    public FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public FirebaseDatabase getDb() {
        if (db == null) {
            db = FirebaseDatabase.getInstance(FIREBASE_URL);
        }
        return db;
    }

    public FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public void setDb(FirebaseDatabase db) {
        this.db = db;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

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

    public Task<AuthResult> signIn(String email, String password) {
        return getAuth().signInWithEmailAndPassword(email, password);
    }

    public Task<User> getCurrentUserProfile() throws FirebaseConnectionError {
        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser == null) {
            throw new FirebaseConnectionError();
        }

        return getUserReference(firebaseUser.getUid()).get()
                .continueWith(task -> task.getResult().getValue(User.class));
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
    public static Task<Product> getProduct(String productId) {
        return getProductsReference().child(productId).get()
                .continueWith(task -> task.getResult().getValue(Product.class));
    }

    public static String reserveProductId() {
        return getProductsReference().push().getKey();
    }

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

    public void addProductChildEventListener(String orderBy,
                                             ProductChildEventListener productChildEventListener) {
        getDb().getReference(PRODUCTS_PATH)
                .orderByChild(orderBy)
                .addChildEventListener(productChildEventListener);
    }

    /**
     * Submits a request for a product if the product is available and not already requested.
     *
     * @param productId the ID of the product being requested
     * @return a Task that resolves on success
     * @throws FirebaseConnectionError when no Auth user is signed in
     */
    public static Task<Void> submitRequest(String productId, String requesterName,
                                           String requesterLocation, List<String> offeredItems) throws FirebaseConnectionError {
        FirebaseUser firebaseUser = getInstance().getCurrentUser();
        if (firebaseUser == null) {
            throw new FirebaseConnectionError();
        }

        String receiverUid = firebaseUser.getUid();
        DatabaseReference requestsRef = getInstance().getDb().getReference(REQUESTS_PATH);
        DatabaseReference productRef = getInstance().getDb()
                .getReference(PRODUCTS_PATH).child(productId);

        return productRef.get().continueWithTask(productTask -> {
            if (!productTask.isSuccessful() || productTask.getResult() == null) {
                throw new Exception("Could not retrieve product");
            }

            Boolean available = productTask.getResult().child("available").getValue(Boolean.class);
            if (available == null || !available) {
                throw new Exception("Product is no longer available");
            }

            return requestsRef.orderByChild("productId").equalTo(productId).get();

        }).continueWithTask(existingRequestsTask -> {
            if (!existingRequestsTask.isSuccessful()) {
                throw new Exception("Could not check existing requests");
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
                    new Request(productId, receiverUid, requesterName,
                            requesterLocation, offeredItems));
        });
    }

    /**
     * Updates the status of an existing request (e.g. "accepted" or "declined").
     *
     * @param requestId the Firebase key of the request
     * @param status the new status to set
     * @return a Task that resolves on success
     */
    public static Task<Void> updateRequestStatus(String requestId, String status) {
        return getInstance().getDb().getReference(REQUESTS_PATH)
                .child(requestId)
                .child("status")
                .setValue(status);
    }
}