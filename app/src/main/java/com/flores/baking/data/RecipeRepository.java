package com.flores.baking.data;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.flores.baking.data.model.Recipe;
import com.flores.baking.data.webservice.RecipeNetworkDataSource;

import java.util.List;

public class RecipeRepository {
    private static final String LOG_TAG = RecipeRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static RecipeRepository sInstance;
    private final RecipeNetworkDataSource mRecipeNetworkDataSource;

    private RecipeRepository(RecipeNetworkDataSource recipeNetworkDataSource) {
        mRecipeNetworkDataSource = recipeNetworkDataSource;
    }

    public synchronized static RecipeRepository getInstance(RecipeNetworkDataSource recipeNetworkDataSource) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new RecipeRepository(recipeNetworkDataSource);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipeNetworkDataSource.getRecipes();
    }

    public void fetchRecipes() {
        mRecipeNetworkDataSource.fetchRecipes();
    }

}