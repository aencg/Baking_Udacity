package com.example.baking.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.baking.IdlingResource.SimpleIdlingResource;
import com.example.baking.R;
import com.example.baking.data.AppDatabase;
import com.example.baking.data.Recipe;
import com.example.baking.data.RecipeDao;


import java.net.URL;
import java.util.List;

import static com.example.baking.utilities.JSONRecipes.*;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.RecipeAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Recipe>>{

    private static String bakeURL ="https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    public static final String EXTRA_RECIPE_ID = "com.example.android.baking.extra.RECIPE_ID";
    public static final String EXTRA_STEP_ID = "com.example.android.baking.extra.STEP_ID";

    private static final int RECIPE_LIST_LOADER = 100;

    RecyclerView recyclerView;
    RecipesAdapter mRecipeAdapter;
    List<Recipe> mRecipes;
    TextView textViewEmpty;
    ProgressBar loadingIndicator;


    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        textViewEmpty = (TextView) findViewById(R.id.textview_empty_recipes);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_recipes);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        // Get the IdlingResource instance
        getIdlingResource();
    }


    @Override
    protected void onStart() {
        super.onStart();

        /*test*/
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        mRecipeAdapter = new RecipesAdapter(this, this);
        RecyclerView.LayoutManager layoutManager;
        if(getResources().getBoolean(R.bool.sw600dp)){
            layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        }   else{
            layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mRecipeAdapter);
        mRecipeAdapter.setRecipeData(mRecipes);

        //Log.e("tamaño on create", "antes view model"+String.valueOf(mRecipeAdapter.getItemCount()));
        loadingIndicator.setVisibility(View.VISIBLE);
        textViewEmpty.setVisibility(View.GONE);
        setupViewModel();

    }

    private void setupViewModel() {
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
               // Log.e("tag", "Updating list of movies from LiveData in ViewModel");
                 mRecipes= recipes;
                 if(recipes!=null) {
                     //Log.e("onChanged","lectura desde bd not null, size "+recipes.size());
                     loadingIndicator.setVisibility(View.GONE);
                     textViewEmpty.setVisibility(View.GONE);
                     loadUI();
                     if (mIdlingResource != null) {
                         mIdlingResource.setIdleState(true);
                     }
                 } else{
                     //Log.e("onChanged","lectura desde bd null");
                 }

                // if no data from the db, we use the loader to read the recipes from the web
                if(mRecipes== null || mRecipes.size()==0)
                    loadRecipesFromWeb();
            }
        });
    }

    void loadUI(){
        mRecipeAdapter.setRecipeData(mRecipes);
        if(mRecipes!=null && mRecipes.size()!=0){
            //Log.e("recipes",mRecipes.toString());
        }
    }

    public void loadRecipesFromWeb(){
       // Log.e("load recipes", "inicio");
            loadingIndicator.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            LoaderManager.getInstance(this).restartLoader( RECIPE_LIST_LOADER, bundle, this);
    }

    @Override
    public void onClick(Recipe recipeClicked, View view) {
            Intent intent = new Intent(this, RecipeDescriptionActivity.class);
            intent.putExtra(EXTRA_RECIPE_ID,recipeClicked.getId());
            startActivity(intent);
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
                URL recipesRequestUrl = com.example.baking.utilities.NetworkUtils.buildUrl(bakeURL);
                try {
                    AppDatabase.getInstance(getContext()).recipeDao().deleteAll();
                    //Thread.sleep(2000);
                    String jsonRecipesResponse = com.example.baking.utilities.NetworkUtils.getResponseFromHttpUrl(recipesRequestUrl);


                    recipes = getRecipesFromJson(jsonRecipesResponse);
                    if(recipes!=null){
                        RecipeDao dao = AppDatabase.getInstance(getContext()).recipeDao();
                        for(Recipe aux : recipes){
                            dao.insertRecipe(aux);
                        }
                    }
                } catch (Exception e) {
                    Log.e("e", e.toString());
                    e.printStackTrace();
                    return null;
                }
                return recipes;
            }

            @Override
            public void deliverResult(List<Recipe> results) {
                mRecipeList = results;
                if(results!=null && results.size()>0)
                    textViewEmpty.setVisibility(View.GONE);
                else
                    textViewEmpty.setVisibility(View.VISIBLE);
                super.deliverResult(mRecipeList);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, List<Recipe> data) {
        loadingIndicator.setVisibility(View.GONE);
      if(data!=null){
          textViewEmpty.setVisibility(View.GONE);
      } else{
          textViewEmpty.setVisibility(View.VISIBLE);
      }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) {

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_menu_refresh:
                //Log.e("switch menu", "item");
                loadRecipesFromWeb();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
