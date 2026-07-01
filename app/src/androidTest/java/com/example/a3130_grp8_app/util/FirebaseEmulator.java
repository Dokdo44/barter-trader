package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class FirebaseEmulator {


    private static final int EMULATOR_AUTH_PORT = BuildConfig.EMULATOR_AUTH_PORT;
    private static final int EMULATOR_DATABASE_PORT = BuildConfig.EMULATOR_DATABASE_PORT;
    private static final String EMULATOR_PROJECT_ID = BuildConfig.PROJECT_ID;

    public static boolean useEmulator() {
        return BuildConfig.DEBUG;
    }

    public static FirebaseAuth getAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.useEmulator("10.0.2.2", EMULATOR_AUTH_PORT);
        return auth;
    }

    public static FirebaseDatabase getDatabase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.useEmulator("10.0.2.2", EMULATOR_DATABASE_PORT);
        return db;
    }

    public static void sendBulkDataToAuth(InputStream is) throws Exception {
        String json;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
        }

        String url = "http://10.0.2.2:" + EMULATOR_AUTH_PORT +
                "/identitytoolkit.googleapis.com/v1/projects/" + EMULATOR_PROJECT_ID +
                "/accounts:batchCreate";

        HttpURLConnection conn = (HttpURLConnection) URI.create(url)
                .toURL()
                .openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer owner");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            String response;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                response = sb.toString();
            }
            throw new Exception(
                    "Failed to upload auth test data — response code: " + responseCode +
                            "\nResponse: " + response
            );
        }
        conn.disconnect();
    }

    public static void sendBulkDataToDatabase(InputStream is) throws Exception {

        // Read fixture file from assets
        String json;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
        }

        // POST each document to the emulator
        String url = "http://10.0.2.2:" + EMULATOR_DATABASE_PORT + "/.json?ns="
                + EMULATOR_PROJECT_ID + "-default-rtdb";

        HttpURLConnection conn = (HttpURLConnection) URI.create(url)
                .toURL()
                .openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            String response;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                response = sb.toString();
            }
            throw new Exception(
                    "Failed to upload test data — response code: " + responseCode +
                            "\nResponse: " + response
            );
        }
        conn.disconnect();
    }

    public static void resetAuth() throws IOException {
        sendDelete(URI.create(
                "http://10.0.2.2:" + EMULATOR_AUTH_PORT +
                        "/emulator/v1/projects/" + EMULATOR_PROJECT_ID +
                        "/accounts"
        ));
    }

    public static void resetDatabase() throws IOException {
        sendDelete(URI.create(
                "http://10.0.2.2:" + EMULATOR_DATABASE_PORT +
                        "/.json?ns=" + EMULATOR_PROJECT_ID + "-default-rtdb"
        ));
    }

    private static void sendDelete(URI uri) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) uri
                .toURL()
                .openConnection();
        conn.setRequestMethod("DELETE");
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException(
                    "Failed to clear Firebase data at " + uri.toURL() +
                            " — response code: " + responseCode
            );
        }
        conn.disconnect();
    }
}
