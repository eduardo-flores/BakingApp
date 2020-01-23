package com.flores.baking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.flores.baking.data.model.Ingredient;
import com.flores.baking.data.model.Recipe;
import com.flores.baking.data.model.Step;

import java.text.DecimalFormat;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    /**
     * The activity argument representing the recipe that this fragment
     * represents.
     */
    public static final String ARG_RECIPE = "recipe_object";

    private StepRecyclerViewAdapter mAdapterStep;
    private RecyclerView mRecyclerViewStep;
    private IngredientRecyclerViewAdapter mAdapterIngredient;
    private RecyclerView mRecyclerViewIngredient;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mRecyclerViewStep = findViewById(R.id.item_list);

        mRecyclerViewIngredient = findViewById(R.id.item_list_ingredient);

        mRecipe = (Recipe) getIntent().getSerializableExtra(ARG_RECIPE);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(ARG_RECIPE, mRecipe);
        }
        setTitle(mRecipe.getName());

        mAdapterStep = new StepRecyclerViewAdapter(this, mRecipe, mRecipe.getSteps(), mTwoPane);
        mRecyclerViewStep.setAdapter(mAdapterStep);

        mAdapterIngredient = new IngredientRecyclerViewAdapter(mRecipe.getIngredients());
        mRecyclerViewIngredient.setAdapter(mAdapterIngredient);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

    }

    private static class IngredientRecyclerViewAdapter
            extends RecyclerView.Adapter<IngredientRecyclerViewAdapter.ViewHolder> {

        private List<Ingredient> mValues;

        IngredientRecyclerViewAdapter(List<Ingredient> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_ingredient, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance();
            formatter.applyPattern("#,###,##0.#");
            holder.mQuantity.setText(formatter.format(mValues.get(position).getQuantity()));
            holder.mMeasure.setText(mValues.get(position).getMeasure());
            holder.mIngredient.setText(mValues.get(position).getIngredient());

            holder.itemView.setTag(mValues.get(position));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mQuantity;
            final TextView mMeasure;
            final TextView mIngredient;

            ViewHolder(View view) {
                super(view);
                mQuantity = view.findViewById(R.id.tv_quantity);
                mMeasure = view.findViewById(R.id.tv_measure);
                mIngredient = view.findViewById(R.id.tv_ingredient);
            }
        }
    }


    private static class StepRecyclerViewAdapter
            extends RecyclerView.Adapter<StepRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private Recipe mRecipe;
        private final boolean mTwoPane;
        private List<Step> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Step item = (Step) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putSerializable(ItemDetailFragment.ARG_ITEM, item);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM, item);
                    intent.putExtra(ARG_RECIPE, mRecipe);

                    context.startActivity(intent);
                }
            }
        };

        StepRecyclerViewAdapter(ItemListActivity parent,
                                Recipe recipe,
                                List<Step> items,
                                boolean twoPane) {
            mRecipe = recipe;
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        public void updateValues(List<Step> steps) {
            if (steps != null) {
                mValues = steps;
                notifyDataSetChanged();
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_step, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mContentView.setText(mValues.get(position).getShortDescription());

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
                mContentView = view.findViewById(R.id.content);
            }
        }
    }
}
