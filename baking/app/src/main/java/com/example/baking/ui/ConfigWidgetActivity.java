package com.example.baking.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.baking.R;
import com.example.baking.data.AppDatabase;
import com.example.baking.data.Recipe;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ConfigWidgetActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Recipe>> {
    private static final int RECIPE_LIST_LOADER_CONFIG_WIDGET = 569;
    private static final String RECIPE_PREF_ID = "RECIPE_PREF_ID";

    private static final String PREFS_NAME
            = "com.example.baking";
    private static final String PREF_PREFIX_KEY = "prefix_";


    RecyclerView recyclerView;
    RecipesAdapter mRecipeAdapter;
    List<Recipe> mRecipes;
    int appWidgetId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);
        setResult(RESULT_CANCELED);


        recyclerView = (RecyclerView) findViewById(R.id.list_config_activity);
        mRecipeAdapter = new RecipesAdapter(new RecipesAdapter.RecipeAdapterOnClickHandler() {
            @Override
            public void onClick(Recipe recipeClicked, View view) {
                Log.e("config","recipe id"+recipeClicked.getId());
                showAppWidget(recipeClicked.getId());

            }
        }, this);
        RecyclerView.LayoutManager layoutManager;
        if (getResources().getBoolean(R.bool.sw600dp)) {
            layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        } else {
            layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mRecipeAdapter);
        mRecipeAdapter.setRecipeData(mRecipes);

        Bundle bundle = new Bundle();

        LoaderManager.getInstance(this).initLoader(RECIPE_LIST_LOADER_CONFIG_WIDGET, bundle, this);

    }

    private void showAppWidget(int recipeId) {
        appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

        //Retrieve the App Widget ID from the Intent that launched the Activity//

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            //If the intent doesn’t have a widget ID, then call finish()//
            if(appWidgetId==AppWidgetManager.INVALID_APPWIDGET_ID){
                finish();
            }

            final Context context = ConfigWidgetActivity.this;
            saveRecipeIdPref(context, appWidgetId, recipeId);

            Log.e("onClick", RECIPE_PREF_ID + appWidgetId + "  recipe" + recipeId);

            Log.e("onClick", "load "+ RECIPE_PREF_ID + appWidgetId + "  recipe" + loadRecipeIdPref(context, appWidgetId));

            //Create the return intent//
            Intent resultValue = new Intent();

            //Pass the original appWidgetId//
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            Log.e("configActivity", "appWidgetId" + appWidgetId);
            //Set the results from the configuration Activity//

            setResult(RESULT_OK, resultValue);

            //Finish the Activity//
            finish();
        }
    }


    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Recipe>>(this) {
            List<Recipe> mRecipeList;

            @Override
            protected void onStartLoading() {
                if (mRecipeList != null) {
                    deliverResult(mRecipeList);
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<Recipe> loadInBackground() {
                List<Recipe> recipes = null;
                try {
                    //load recipes from memory
                    recipes = AppDatabase.getInstance(getContext()).recipeDao().loadAllRecipes();
                    return recipes;
                } catch (Exception e) {
                    //Log.e("e", e.toString());
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(List<Recipe> results) {
                mRecipeList = results;
                super.deliverResult(mRecipeList);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, List<Recipe> data) {
        mRecipes = data;
        mRecipeAdapter.setRecipeData(mRecipes);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) {

    }

    // Write the prefix to the SharedPreferences object for this widget
    public static void saveRecipeIdPref(Context context, int appWidgetId, int recipeId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, recipeId);
        prefs.commit();
    }
    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static int loadRecipeIdPref(Context context, int appWidgetId) {

        Log.e("load","appWidgetId "+appWidgetId);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int recipeId = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, -1);

        return recipeId;
    }





}
