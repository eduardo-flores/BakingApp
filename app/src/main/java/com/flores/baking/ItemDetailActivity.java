package com.flores.baking;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.flores.baking.data.model.Recipe;

import java.util.Objects;

import static com.flores.baking.ItemListActivity.ARG_RECIPE;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = ItemDetailActivity.class.getSimpleName();

    public static final String ARG_ITEM_POSITION = "item_position";

    private Recipe mRecipe;
    private int mItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        if (getIntent().hasExtra(ARG_RECIPE)) {
            mRecipe = (Recipe) getIntent().getSerializableExtra(ARG_RECIPE);
            assert mRecipe != null;
            setTitle(mRecipe.getName());
        } else return;

        mItemPosition = getIntent().getIntExtra(ARG_ITEM_POSITION, 0);


        if (findViewById(R.id.item_detail_container) != null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(ItemDetailFragment.ARG_ITEM, mRecipe.getSteps().get(mItemPosition));
            arguments.putSerializable(ARG_RECIPE, mRecipe);
            if (mItemPosition > 0) {
                arguments.putInt(ItemDetailFragment.ARG_ITEM_POSITION_PREVIOUS, mItemPosition - 1);
            }
            if (mItemPosition < mRecipe.getSteps().size() - 1) {
                arguments.putInt(ItemDetailFragment.ARG_ITEM_POSITION_NEXT, mItemPosition + 1);
            }
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }


        if (getResources().getBoolean(R.bool.is_landscape)) {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra(ARG_RECIPE, mRecipe);

            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
