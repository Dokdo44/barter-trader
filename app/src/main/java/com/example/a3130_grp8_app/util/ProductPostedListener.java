package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.model.Product;

public interface ProductPostedListener {

    void onProductPosted(Product product); //these two for u Evan

    void onError(Exception e);
}
