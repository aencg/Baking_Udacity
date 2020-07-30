package com.example.baking.ui;

import com.example.baking.data.AppDatabase;
import com.example.baking.data.Recipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class RecipeViewModel extends ViewModel {

    // Constant for logging
    private static final String TAG = RecipeViewModel.class.getSimpleName();

    private LiveData<Recipe> recipe;

    public RecipeViewModel(AppDatabase database, int recipeId) {
        recipe = database.recipeDao().loadRecipeById(recipeId);
    }


    public LiveData<Recipe> getRecipe() {
        return recipe;
    }
}
