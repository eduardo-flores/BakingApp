package com.flores.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.flores.baking.R;
import com.flores.baking.data.model.Recipe;
import com.flores.baking.data.webservice.RecipeNetworkDataSource;

import java.util.List;

import static com.flores.baking.ItemListActivity.ARG_RECIPE;


public class ListWidgetService extends RemoteViewsService {

    private static final String LOG_TAG = ListWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(LOG_TAG, "onGetViewFactory");

        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    private class ListRemoteViewsFactory implements RemoteViewsFactory {
        private final String LOG_TAG = "ListRemoteViewsFactory";
        private final Context mContext;
        private List<Recipe> mRecipes;
        private final RecipeNetworkDataSource mNetworkDataSource;

        ListRemoteViewsFactory(Context applicationContext) {
            mNetworkDataSource = RecipeNetworkDataSource.getInstance(applicationContext);
            mNetworkDataSource.fetchRecipes();

            mContext = applicationContext;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            mRecipes = mNetworkDataSource.getRecipesWidget();
            Log.d(LOG_TAG, "onDataSetChanged: mRecipes " + mRecipes);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mRecipes != null ? mRecipes.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            Log.d(LOG_TAG, "getViewAt: " + i);
            Recipe recipe = mRecipes.get(i);
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.baking_app_widget_item);
            views.setTextViewText(R.id.tv_recipe_name_widget, recipe.getName());
            Log.d(LOG_TAG, "getViewAt: recipe.getName()" + recipe.getName());

            // Fill in the onClick PendingIntent Template using the specific recipe for each item individually
            Bundle extras = new Bundle();
            extras.putSerializable(ARG_RECIPE, recipe);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.cv_baking_card, fillInIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}

