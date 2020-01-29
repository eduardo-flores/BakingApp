package com.flores.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.flores.baking.R;
import com.flores.baking.provider.BakingContract;


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
        private Cursor mCursor;

        ListRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        public void onCreate() {
        }


        @Override
        public void onDataSetChanged() {
            if (mCursor != null) mCursor.close();

            mCursor = getContentResolver().query(BakingContract.IngredientEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    BakingContract.IngredientEntry._ID);
            Log.d(LOG_TAG, "onDataSetChanged: mCursor " + mCursor);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor != null ? mCursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            Log.d(LOG_TAG, "getViewAt: " + i);

            if (mCursor == null || mCursor.getCount() == 0) {
                return null;
            }
            mCursor.moveToPosition(i);
            int recipeNameIndex = mCursor.getColumnIndex(BakingContract.IngredientEntry.COLUMN_RECIPE_NAME);
            int ingredientsIndex = mCursor.getColumnIndex(BakingContract.IngredientEntry.COLUMN_INGREDIENTS);

            String recipeName = mCursor.getString(recipeNameIndex);
            String ingredients = mCursor.getString(ingredientsIndex);

            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.baking_app_widget_item);

            remoteViews.setTextViewText(R.id.tv_recipe_name_widget, recipeName);
            remoteViews.setTextViewText(R.id.tv_ingredients_widget, ingredients);

            Log.d(LOG_TAG, "getViewAt: recipeName" + recipeName);

            // Fill in the onClick PendingIntent Template using the specific recipe for each item individually
//            Bundle extras = new Bundle();
//            extras.putSerializable(ARG_RECIPE, recipe);
//            Intent fillInIntent = new Intent();
//            fillInIntent.putExtras(extras);
//            views.setOnClickFillInIntent(R.id.cv_baking_card, fillInIntent);
            return remoteViews;
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

