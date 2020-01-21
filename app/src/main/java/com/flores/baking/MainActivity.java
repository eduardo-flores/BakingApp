package com.flores.baking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.flores.baking.model.Recipe;
import com.flores.baking.webservice.WebserviceClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.flores.baking.ItemListActivity.ARG_RECIPE;

public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SimpleItemRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View recyclerView = findViewById(R.id.card_list_item);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimpleItemRecyclerViewAdapter(new ArrayList<>(0));
        getRecipes();
        recyclerView.setAdapter(mAdapter);
    }

    private void getRecipes() {
        Call<List<Recipe>> call = WebserviceClient.getWebservice().getRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                Log.d(LOG_TAG, "response code = " + response.code());
                if (response.isSuccessful()) {
                    mAdapter.updateValues(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.d(LOG_TAG, t.getMessage());
            }

        });
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

        public void updateValues(List<Recipe> recipes) {
            if (recipes != null) {
                mValues = recipes;
                notifyDataSetChanged();
            }
        }

        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_list_content, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mContentView.setText(mValues.get(position).getName());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mContentView = view.findViewById(R.id.recipe_name);
            }
        }
    }
}
