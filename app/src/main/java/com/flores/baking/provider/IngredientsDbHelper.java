package com.flores.baking.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IngredientsDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "baking.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    IngredientsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TABLE = "CREATE TABLE " + BakingContract.IngredientEntry.TABLE_NAME + " (" +
                BakingContract.IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BakingContract.IngredientEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                BakingContract.IngredientEntry.COLUMN_INGREDIENTS + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BakingContract.IngredientEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
