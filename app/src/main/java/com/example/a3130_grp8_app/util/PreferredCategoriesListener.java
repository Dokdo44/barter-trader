package com.example.a3130_grp8_app.util;
import java.util.List;
public interface PreferredCategoriesListener
{
    void onCategoriesLoaded(List<String> categories);

    void onError(Exception e);
}
