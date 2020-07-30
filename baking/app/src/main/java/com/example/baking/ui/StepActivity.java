package com.example.baking.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.baking.R;
import com.example.baking.data.Recipe;

public class StepActivity extends AppCompatActivity implements StepFragment.OnStepSelectedChangeListener {

    public static final String EXTRA_RECIPE = "com.example.android.baking.extra.RECIPE";
    public static final String EXTRA_STEP_ID = "com.example.android.baking.extra.STEP_ID";


    Recipe mRecipe;
    int mStepNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);


        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_STEP_ID) && intent.hasExtra(EXTRA_RECIPE)) {
            mRecipe = (Recipe) intent.getParcelableExtra(EXTRA_RECIPE);

            mStepNumber = intent.getIntExtra(EXTRA_STEP_ID,0);
        }


        FragmentManager fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null){
            //Log.e("saved to fragment",mRecipe.getName()+"     "+mStepNumber);

            StepFragment fragment = new StepFragment();
            fragment.setRecipe(mRecipe);
            fragment.setmStepNumber(mStepNumber);
            fragmentManager.beginTransaction().add(R.id.step_container,fragment,"step")
                    .commit();
        }

    }

    @Override
    public void onStepSelectedChange(int position) {
        if(mStepNumber!=position){
            mStepNumber = position;

            StepFragment fragment = new StepFragment();
            fragment.setRecipe(mRecipe);
            fragment.setmStepNumber(position);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.step_container,fragment)
                    .commit();
        }

    }


}
