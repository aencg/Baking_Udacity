package com.example.baking.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.baking.R;
import com.example.baking.data.AppDatabase;
import com.example.baking.data.Ingredient;
import com.example.baking.data.Recipe;
import com.example.baking.data.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeDescriptionActivity extends AppCompatActivity implements  StepsAdapter.StepAdapterOnClickHandler,
        StepFragment.OnStepSelectedChangeListener {
    public static final String EXTRA_RECIPE = "com.example.android.baking.extra.RECIPE";
    public static final String EXTRA_RECIPE_ID = "com.example.android.baking.extra.RECIPE_ID";
    public static final String EXTRA_STEP_ID = "com.example.android.baking.extra.STEP_ID";

    public static final String SAVED_POSITION = "com.example.android.baking.saved.SAVED_POSITION";

    TextView titleTv;
    TextView ingredientsTv;
    RecyclerView mRecyclerView;
    StepsAdapter mStepsAdapter;
    Recipe mRecipe;
    ImageView mRecipeImageView;
    private boolean mTwoPane;
    int mStepPosition = 0;
    AppDatabase mDb;

    NestedScrollView mNestedScrollView;

    int mRecipeId;

    boolean mCreateFragent = true;
    boolean mLaunchStep = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_description);

        initViews();
        mDb = AppDatabase.getInstance(this);

        mNestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollview);
        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_RECIPE_ID)){
           // mRecipe = (Recipe) intent.getParcelableExtra("recipe");

           // Log.e(getClass().getName(),"extra "+intent.getExtras());
            mRecipeId = intent.getIntExtra(EXTRA_RECIPE_ID,0);

            if(intent.hasExtra(EXTRA_STEP_ID)) {
                mStepPosition = intent.getIntExtra(EXTRA_STEP_ID, 0);
                mLaunchStep = true;
            }

            if(savedInstanceState != null) {
                //position not empty
                try{
                    mStepPosition = savedInstanceState.getInt(SAVED_POSITION);
                    mLaunchStep = false;
                    //   updateSelectedStep();
                }   catch(Exception e){
                    //
                }
            }

            loadRecipe();
        }

        if(findViewById(R.id.step_container) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;

            if(savedInstanceState == null) {
                mCreateFragent = true;
                //create the fragment
            //    createFragment();
            }
        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            mTwoPane = false;
        }
    }

    private void loadRecipe() {
        RecipeViewModelFactory factory = new RecipeViewModelFactory(mDb, mRecipeId);
        //  Declare a AddTaskViewModel variable and initialize it by calling ViewModelProviders.of
        // for that use the factory created above AddTaskViewModel
        final RecipeViewModel viewModel
                = ViewModelProviders.of(this, factory).get(RecipeViewModel.class);

        //  Observe the LiveData object in the ViewModel. Use it also when removing the observer
        viewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                viewModel.getRecipe().removeObserver(this);
                mRecipe = recipe;
                populateRecipe();
                if(mTwoPane){
                    if(mCreateFragent)
                        createFragment();
                    updateSelectedStep();
                }   else{
                    if(mLaunchStep){
                        mLaunchStep = false;
                        startStepActivity(mStepPosition);
                    }
                }
            }
        });
    }

    void createFragment(){
        StepFragment fragment = new StepFragment();
        fragment.setRecipe(mRecipe);
        fragment.setmStepNumber(mStepPosition);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.step_container,fragment)
                .commit();
    }


    void populateRecipe() {
        /*String texto =mRecipe.getName();
        List<Step> steps2 = mRecipe.getSteps();
        for(int i = 0; i<steps2.size(); i++)
            texto+=" - "+steps2.get(i).getId()+"  \n";
        Log.e("recipe-comp",texto);*/

        if (mRecipe.getImage() != null && !mRecipe.getImage().trim().equals("")) {
            mRecipeImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            mRecipeImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(mRecipe.getImage()).into(mRecipeImageView);
        } else {
            mRecipeImageView.setVisibility(View.GONE);
        }

        //   Log.e("secundario",recipe.toString());
        titleTv.setText(mRecipe.getName());
        List<Ingredient> ingredients = mRecipe.getIngredients();
        StringBuilder sBuilderIngredients = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            sBuilderIngredients.append(" " + ingredient.getQuantity() + " "
                    + ingredient.getMeasure() + " " + ingredient.getName() + "\n");
        }

        ingredientsTv.setText(sBuilderIngredients.toString());
        StringBuilder sBuilderSteps = new StringBuilder();
        List<Step> steps = mRecipe.getSteps();
        mStepsAdapter = new StepsAdapter(this, this);

        //Log.e("stepadapter", "tamaño" + steps.size());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mStepsAdapter);
        mStepsAdapter.setStepsData(steps);
    }

    void initViews(){
        titleTv = (TextView) findViewById(R.id.title_recipe);
        ingredientsTv = (TextView) findViewById(R.id.ingredients);
        mRecipeImageView = (ImageView) findViewById(R.id.recipe_image);
        mRecyclerView = (RecyclerView) findViewById(R.id.steps_recyclerview);
    }

    @Override
    public void onClick(Step stepClicked, View view) {
        if(mTwoPane) {
            //Log.e("step-filter",stepClicked.toString());
            int position = mStepsAdapter.getPositionStep(stepClicked.getId());
            replaceFragment(position);
            /*
            mRecyclerView.get
            StepFragment fragment = new StepFragment();
            fragment.setRecipe(mRecipe);
            fragment.setmStepNumber(stepClicked.getId());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.step_container,fragment)
                    .commit();*/
        }   else{
            //Toast.makeText(this, String.valueOf(stepClicked.getId()+1),Toast.LENGTH_SHORT).show();
            startStepActivity(stepClicked.getId());
        }
    }

    void startStepActivity(int step){
        Intent intent = new Intent(this, StepActivity.class);
        intent.putExtra(EXTRA_STEP_ID,step);
        intent.putExtra(EXTRA_RECIPE,mRecipe);
        startActivity(intent);
    }

    void replaceFragment(int position){
        if(mStepPosition!=position){
            mStepPosition = position;
            updateSelectedStep();
            StepFragment fragment = new StepFragment();
            fragment.setRecipe(mRecipe);
            fragment.setmStepNumber(position);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.step_container,fragment)
                    .commit();
        }
    }



    void updateSelectedStep(){
       mStepsAdapter.setmItemSelected(mStepPosition);
    }

    @Override
    public void onStepSelectedChange(int position) {
        replaceFragment(position);
        final int y = (int) mRecyclerView.getChildAt(position).getY();

        mNestedScrollView.smoothScrollTo(0, y);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_POSITION, mStepPosition);
    }
}
