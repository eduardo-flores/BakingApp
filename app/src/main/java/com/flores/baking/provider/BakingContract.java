package com.flores.baking.provider;


import android.net.Uri;
import android.provider.BaseColumns;

public class BakingContract {

    // The authority, which is how your code knows which Content Provider to access
    static final String AUTHORITY = "com.flores.baking.provider.baking_app";
    // Define the possible paths for accessing data in this contract
    // This is the path for the "ingredients" directory
    static final String PATH_INGREDIENTS = "ingredients";
    // The base content URI = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class IngredientEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();
        public static final String COLUMN_RECIPE_NAME = "recipe_name";
        public static final String COLUMN_INGREDIENTS = "ingredients";
        static final String TABLE_NAME = "ingredients";
    }
}
