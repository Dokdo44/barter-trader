package com.example.a3130_grp8_app.data;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
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
import com.google.firebase.messaging.FirebaseMessaging;

import com.example.a3130_grp8_app.util.PreferredCategoriesListener;
import com.example.a3130_grp8_app.util.ProductPostedListener; // NEW

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private static final String CHAT_PATH = "chats";
    private static final String MESSAGES_PATH = "messages";
    static final String FIREBASE_URL = "https://group8-19144-default-rtdb.firebaseio.com/";

    private FirebaseAuth auth;
    private FirebaseDatabase db;

    protected FirebaseManager() {
    }

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
     * @throws IllegalStateException if the singleton has already been initialized
     */
    public static void install(FirebaseManager firebaseManager) {
        if (instance != null) {
            throw new IllegalStateException("FirebaseManager singleton already initialized");
        }
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

                    FirebaseMessaging
                            .getInstance()
                            .getToken()
                            .continueWithTask(t -> onGetTokenSuccess(t, firebaseUser, email, role));

                    User user = new User(email, role);
                    user.setId(firebaseUser.getUid());
                    return getUserReference(firebaseUser.getUid()).setValue(user);
                });
    }

    private Task<Void> onGetTokenSuccess(Task<String> tokenTask, FirebaseUser firebaseUser, String email, String role) {
        if (!tokenTask.isSuccessful() && tokenTask.getException() != null) {
            return Tasks.forException(tokenTask.getException());
        }

        String token = tokenTask.getResult();
        User user = new User(email, role);
        user.setId(firebaseUser.getUid());
        user.setNotificationToken(token);

        return getDb()
                .getReference(USERS_PATH)
                .child(firebaseUser.getUid())
                .setValue(user);
    }

    /**
     * Signs a user into Firebase Authentication.
     *
     * @param email    the user's email address
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
     * @param product   the product to save
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
                                         @Nullable String previousChildName) {
        }

        @Override
        public final void onChildRemoved(@NonNull DataSnapshot snapshot) {
        }

        @Override
        public final void onChildMoved(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {
        }

        @Override
        public final void onCancelled(@NonNull DatabaseError error) {
        }

        public abstract void onGetProduct(Product product);
    }

    public abstract static class ProductValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<Product> products = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Product product = snapshot.getValue(Product.class);
                assert product != null;
                product.setId(snapshot.getKey());
                products.add(product);
            }
            onGetProducts(products);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }

        public abstract void onGetProducts(List<Product> products);
    }

    /**
     * Registers a listener for product changes ordered by the specified field.
     *
     * @param orderBy                   the child field used to order products
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

    public void addChildEventListenerForUserPreferredProductIds(String userId,
                                                                ChildEventListener preferredProductIdsChildEventListener) {
        DatabaseReference userRef =
                getDb()
                        .getReference(USERS_PATH)
                        .child(userId)
                        .child("preferredProductIds");
        userRef.addChildEventListener(preferredProductIdsChildEventListener);

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
    public Task<Void> submitRequest(String productId, String requesterName,
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
                .continueWithTask(checkDuplicateAndSubmit(productId, receiverUid, requesterName,
                        requesterLocation, offeredItems, requestsRef, receiverEmail));
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
            String productId, String receiverUid, String requesterName,
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
                            requesterName, requesterLocation, offeredItems));
        };
    }

    /**
     * Updates the status of an existing request (e.g. "accepted" or "declined").
     *
     * @param requestId the Firebase key of the request
     * @param status    the new status to set
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
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Request request = snapshot.getValue(Request.class);
                assert request != null;
                request.setRequestId(snapshot.getKey());
                requests.add(request);
            }
            onGetRequests(requests);
        }

        @Override
        public void onCancelled(DatabaseError error) {
        }

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

    /**
     * Adds a Chat item to the database
     *
     * @param providerName The name of the provider of the product in the chat
     * @param receiverName the name of the receiver of the product in the chat
     * @param productName  the name of the product being discussed
     * @param productId    the Id of the product
     * @param messageIds   a map of the message Ids contained in the chat
     * @param providerId   the Id of the provider
     * @param receiverId   the Id of the receiver
     * @return a task that completes to the newly created chat
     */
    public Task<Chat> addChat(String providerName,
                              String receiverName,
                              String productName,
                              String productId,
                              Map<String, String> messageIds,
                              String providerId,
                              String receiverId) {

        DatabaseReference chatReference = getDb()
                .getReference(CHAT_PATH)
                .push();

        String chatId = chatReference.getKey();
        Chat chat = new Chat(
                providerName,
                receiverName,
                productName,
                productId,
                messageIds,
                chatId,
                providerId,
                receiverId);
        chat.setId(chatId);
        return toResult(chatReference.setValue(chat), chat);
    }

    /**
     * Get the chats associated with the given product id
     *
     * @param productId the Id of the product associated with the chats
     * @return a DataSnapshot containing all of the chats associated with the product id
     */
    public Task<DataSnapshot> getChatsForProduct(String productId) {
        return getDb()
                .getReference(CHAT_PATH)
                .orderByChild("productId")
                .equalTo(productId)
                .get();
    }

    /**
     * Adds a child event listener to all chats where the specified user is either
     * the provider or receiver.
     *
     * <p>This registers listeners for both possible relationships because Firebase
     * Realtime Database queries cannot perform an OR operation on multiple fields.</p>
     *
     * @param userId             the unique identifier of the user whose chats should be observed
     * @param childEventListener the listener to receive chat events
     */
    public void addChildEventListenerToUserChats(String userId, ChildEventListener childEventListener) {
        getDb()
                .getReference(CHAT_PATH)
                .orderByChild("providerUid")
                .equalTo(userId)
                .addChildEventListener(childEventListener);

        getDb()
                .getReference(CHAT_PATH)
                .orderByChild("receiverUid")
                .equalTo(userId)
                .addChildEventListener(childEventListener);
    }

    /**
     * Adds a new message to a chat and stores the message ID in the chat's message list.
     *
     * <p>The message is stored under the messages collection, and a reference to the
     * message is added to the specified chat so that the chat can retrieve its messages.</p>
     *
     * @param chatId        the unique identifier of the chat receiving the message
     * @param senderName    the display name of the user sending the message
     * @param timeSent      the time the message was sent, represented as a String
     * @param messageString the text content of the message
     * @return a task that completes with the created Message when both database writes
     * have completed successfully
     */
    public Task<Message> addMessageToChat(String chatId, String senderName, String timeSent, String messageString) {
        DatabaseReference messageRef = getDb()
                .getReference(MESSAGES_PATH)
                .push();

        DatabaseReference chatRef = getDb()
                .getReference(CHAT_PATH)
                .child(chatId)
                .child("messageIds")
                .push();

        Message message = new Message(senderName, timeSent, messageString, chatId, messageRef.getKey());
        Task<Void> addMessage = messageRef.setValue(message);
        Task<Void> addMessageId = chatRef.setValue(messageRef.getKey());

        return toResult(Tasks.whenAll(addMessage, addMessageId), message);
    }

    /**
     * Adds a message ID to a user's inbox.
     *
     * <p>This allows users to be notified of or retrieve messages associated with
     * their account.</p>
     *
     * @param userId    the unique identifier of the user receiving the message reference
     * @param messageId the unique identifier of the message to add
     * @return a task that completes with the message ID when the operation finishes
     */
    public Task<String> addMessageToUserInbox(String userId, String messageId) {
        DatabaseReference userInboxRef = getDb()
                .getReference(USERS_PATH)
                .child(userId)
                .child("messageInbox")
                .child(messageId);

        return toResult(userInboxRef.setValue(true), messageId);
    }

    /**
     * Adds a child event listener to the messages belonging to a specific chat.
     *
     * <p>The listener will receive updates when messages are added, changed,
     * removed, or moved within the chat's message ID list.</p>
     *
     * @param chatId             the unique identifier of the chat to observe
     * @param childEventListener the listener to receive message events
     */
    public void addChildEventListenerForChatMessages(String chatId, ChildEventListener childEventListener) {
        getDb()
                .getReference(CHAT_PATH)
                .child(chatId)
                .child("messageIds")
                .addChildEventListener(childEventListener);
    }

    /**
     * Retrieves a user from the database using their unique identifier.
     *
     * @param uid the unique identifier of the user
     * @return a task that completes with the User object associated with the UID
     */
    public Task<User> getUserByUid(String uid) {
        return getDb()
                .getReference(USERS_PATH)
                .child(uid)
                .get()
                .continueWith(task -> task.getResult().getValue(User.class));
    }

    /**
     * Retrieves a chat from the database using its unique identifier.
     *
     * @param chatId the unique identifier of the chat
     * @return a task that completes with the Chat object associated with the ID
     */
    public Task<Chat> getChatById(String chatId) {
        return getDb()
                .getReference(CHAT_PATH)
                .child(chatId)
                .get()
                .continueWith(task -> task.getResult().getValue(Chat.class));
    }

    /**
     * Adds a child event listener to a user's message inbox.
     *
     * <p>The listener receives events when message references are added, changed,
     * removed, or moved in the user's inbox.</p>
     *
     * @param userId               the user whose message inbox should be observed
     * @param childEventListener the listener to receive inbox events
     */
    public void addChildEventListenerForUserMessageInbox(String userId, ChildEventListener childEventListener) {
        getDb()
                .getReference(USERS_PATH)
                .child(userId)
                .child("messageInbox")
                .addChildEventListener(childEventListener);
    }

    /**
     * Retrieves a message from the database using its unique identifier.
     *
     * @param messageId the unique identifier of the message
     * @return a task that completes with the Message object associated with the ID
     */
    public Task<Message> getMessageById(String messageId) {
        return getDb()
                .getReference(MESSAGES_PATH)
                .child(messageId)
                .get()
                .continueWith(task -> task.getResult().getValue(Message.class));
    }

    /**
     * Add a value event listener that listens for a change in firebase connection status
     * @param valueEventListener the ValueEventListener to add
     */
    public void addValueEventListenerForConnection(ValueEventListener valueEventListener) {
        getDb().getReference(".info/connected").addValueEventListener(valueEventListener);
    }

    /**
     * Sets the valid flag on the message in the user's inbox to false
     * @param messageId the Id to make invalid
     * @param userId the user that has the message in their inbox
     * @return a void task that completes when the message status is changed
     */
    public Task<Void> removeMessageIdFromUseInbox(String messageId, String userId) {
        return getDb()
                .getReference(USERS_PATH)
                .child(userId)
                .child("messageInbox")
                .child(messageId)
                .setValue(false);
    }

    /**
     * Converts a database write task into a task that returns a specified result.
     *
     * <p>If the original task fails, the returned task also fails with the same
     * exception. Otherwise, the returned task completes with the provided value.</p>
     *
     * @param voidTask the database operation task to monitor
     * @param toReturn the value to return after successful completion
     * @param <T>      the type of the result value
     * @return a task that completes with the provided result value
     */
    private <T> Task<T> toResult(Task<Void> voidTask, T toReturn) {
        return voidTask.continueWithTask(task -> {
            if (!task.isSuccessful() && task.getException() != null) {
                return Tasks.forException(task.getException());
            }
            return Tasks.forResult(toReturn);
        });
    }
}