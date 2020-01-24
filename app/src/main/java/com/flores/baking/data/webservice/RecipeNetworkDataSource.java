package com.flores.baking.data.webservice;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.flores.baking.R;
import com.flores.baking.data.model.Recipe;
import com.flores.baking.widget.BakingAppWidget;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Provides an API for doing all operations with the server data
 */
public class RecipeNetworkDataSource {
    private static final String LOG_TAG = RecipeNetworkDataSource.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static RecipeNetworkDataSource sInstance;
    private final AppWidgetManager appWidgetManager;
    private final int[] appWidgetIds;

    private final MutableLiveData<List<Recipe>> mRecipeList;

    private RecipeNetworkDataSource(Context context) {
        appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingAppWidget.class));
        mRecipeList = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static RecipeNetworkDataSource getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new RecipeNetworkDataSource(context);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipeList;
    }

    public List<Recipe> getRecipesWidget() {
        return mRecipeList.getValue();
    }

    public void fetchRecipes() {
        Log.d(LOG_TAG, "Fetch recipes started");
        Call<List<Recipe>> call = WebserviceClient.getWebservice().getRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                Log.d(LOG_TAG, "response code = " + response.code());
                if (response.isSuccessful()) {
                    mRecipeList.postValue(response.body());
                    //Trigger data update to handle the widgets and force a data refresh
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_recipe_widget);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.d(LOG_TAG, t.getMessage() != null ? t.getMessage() : "onFailure");
            }

        });
    }
}