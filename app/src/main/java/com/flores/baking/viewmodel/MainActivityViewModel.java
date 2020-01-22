package com.flores.baking.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.flores.baking.data.RecipeRepository;
import com.flores.baking.data.model.Recipe;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final RecipeRepository mRepository;
    private final LiveData<List<Recipe>> mRecipes;

    MainActivityViewModel(RecipeRepository repository) {
        mRepository = repository;
        fetchRecipes();
        mRecipes = mRepository.getRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public void fetchRecipes() {
        mRepository.fetchRecipes();
    }
}
