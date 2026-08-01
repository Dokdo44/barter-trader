package com.example.a3130_grp8_app.data;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {

    Context context;

    public StorageManager(Context context) {
        this.context = context;
    }

    public void saveObject(String filename, Object object) {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(object);
        } catch (IOException e) {
            Log.e("e", e.getMessage());
        }
    }

    public Object loadObject(String filename) {
        try (FileInputStream fis = context.openFileInput(filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.e("e", e.getMessage());
            return null;
        }
    }
}
