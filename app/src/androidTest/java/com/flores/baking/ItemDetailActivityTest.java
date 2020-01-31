package com.flores.baking;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ItemDetailActivityTest {

    private static final String RECIPE_INTRODUCTION = "Recipe Introduction";
    private IdlingResource mIdlingResource;


    @Before
    public void registerIdlingResource() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity((ActivityScenario.ActivityAction<MainActivity>) activity -> {
            mIdlingResource = activity.getIdlingResource();
            // To prove that the test fails, omit this call:
            IdlingRegistry.getInstance().register(mIdlingResource);
        });
        onView(withId(R.id.card_list_item)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void checkStepIsDisplayed() {
        onView(withId(R.id.item_list)).check(matches(isDisplayed()));
        onView(withId(R.id.item_list)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withText(RECIPE_INTRODUCTION)).check(matches(isDisplayed()));
    }

    @Test
    public void onClickRecyclerView() {
        onView(withId(R.id.item_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.item_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.item_detail)).check(matches(withText(RECIPE_INTRODUCTION)));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
