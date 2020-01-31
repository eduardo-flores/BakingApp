package com.flores.baking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.flores.baking.data.RecipeRepository;
import com.flores.baking.data.model.Recipe;
import com.flores.baking.data.webservice.RecipeNetworkDataSource;
import com.flores.baking.idlingResource.SimpleIdlingResource;
import com.flores.baking.viewmodel.MainActivityViewModel;
import com.flores.baking.viewmodel.MainViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.flores.baking.ItemListActivity.ARG_RECIPE;

public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SimpleItemRecyclerViewAdapter mAdapter;
    private List<Recipe> mRecipes;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;


    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mRecyclerView = findViewById(R.id.card_list_item);
        mAdapter = new SimpleItemRecyclerViewAdapter(new ArrayList<>(0));
        mRecyclerView.setAdapter(mAdapter);

        MainViewModelFactory factory = new MainViewModelFactory(RecipeRepository.getInstance(RecipeNetworkDataSource.getInstance(getApplicationContext())));
        MainActivityViewModel mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        getIdlingResource().setIdleState(false);

        mViewModel.getRecipes().observe(this, recipes -> {
            mRecipes = recipes;
            showData();
            getIdlingResource().setIdleState(true);
        });
    }

    private void showData() {
        mAdapter.updateValues(mRecipes);
        if (mRecipes != null && mRecipes.size() != 0) showDataView();
        else showLoading();
    }

    private void showDataView() {
        Log.d(LOG_TAG, "showMovieDataView");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        Log.d(LOG_TAG, "Loading");
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final View.OnClickListener mOnClickListener = view -> {
            Recipe item = (Recipe) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, ItemListActivity.class);
            intent.putExtra(ARG_RECIPE, item);

            context.startActivity(intent);
        };
        private List<Recipe> mValues;

        SimpleItemRecyclerViewAdapter(List<Recipe> items) {
            mValues = items;
        }

        void updateValues(List<Recipe> recipes) {
            if (recipes != null) {
                mValues = recipes;
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_list_content, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mContentView.setText(mValues.get(position).getName());

            // Load image if it exists
            if (!TextUtils.isEmpty(mValues.get(position).getImage())) {
                Picasso.get()
                        .load(mValues.get(position).getImage())
                        .placeholder(R.drawable.ic_book)
                        .into(holder.mImageView);
            }
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mContentView;
            final ImageView mImageView;

            ViewHolder(View view) {
                super(view);
                mContentView = view.findViewById(R.id.recipe_name);
                mImageView = view.findViewById(R.id.iv_place_holder);
            }
        }
    }

    @NonNull
    public SimpleIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
