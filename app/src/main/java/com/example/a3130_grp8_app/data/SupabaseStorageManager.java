package com.example.a3130_grp8_app.data;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class SupabaseStorageManager {

    private static final String SUPABASE_URL = "https://gojfwmzbhhobeqqdgbrt.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdvamZ3bXpiaGhvYmVxcWRnYnJ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODQyMDYzMjksImV4cCI6MjA5OTc4MjMyOX0.tZGdiiZof5zsXlPxCFbagv96qsBMa2S88IDIXJ5xBNQ";
    private static final String BUCKET = "product-photos";
    private static final int JPEG_QUALITY = 85;

    public static String uploadListingPhoto(Bitmap photo, String listingId) throws IOException {
        ByteArrayOutputStream imageBytesStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, imageBytesStream);
        byte[] imageBytes = imageBytesStream.toByteArray();

        String objectPath = listingId + ".jpg";
        URL uploadUrl = new URL(SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/" + objectPath);

        HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY);
            connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON_KEY);
            connection.setRequestProperty("Content-Type", "image/jpeg");
            connection.setRequestProperty("x-upsert", "true");

            try (OutputStream out = connection.getOutputStream()) {
                out.write(imageBytes);
            }
            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                throw new IOException("Supabase upload failed with HTTP " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
        return SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/" + objectPath;
    }

}
