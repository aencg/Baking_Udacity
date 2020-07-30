package com.example.baking.ui;

import android.app.Application;
import android.util.Log;

import com.example.baking.data.AppDatabase;
import com.example.baking.data.Recipe;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<Recipe>> recipes;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        //Log.e(TAG, "Actively retrieving the tasks from the DataBase");
        recipes = database.recipeDao().loadAllLiveRecipes();
        if(recipes==null){
            //Log.e("viewModel", "recipes null");
        }   else{
            if(recipes.getValue()==null){
               // Log.e("viewModel", "recipes value null");
            }   else{
              //  Log.e("viewModel", "recipes size "+recipes.getValue().size());

            }
        }
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }
}
