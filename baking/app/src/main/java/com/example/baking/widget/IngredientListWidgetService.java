package com.example.baking.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.baking.R;
import com.example.baking.data.AppDatabase;
import com.example.baking.data.Ingredient;
import com.example.baking.data.Recipe;
import com.example.baking.ui.ConfigWidgetActivity;
import com.example.baking.ui.RecipeDescriptionActivity;

import java.util.ArrayList;
import java.util.List;

public class IngredientListWidgetService extends RemoteViewsService {

    private static final String RECIPE_ID = "RECIPE_ID";


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {


        Log.e(getClass().getName(), "onGetViewFactory");


        Bundle extras = intent.getExtras();
        int recipeId = 0;
        int widgetId =-1;
        if (extras != null) {
            Log.e("onGetViewFactory","extras"+extras.toString());
            Context context = this.getApplicationContext();

            widgetId  = extras.getInt(RECIPE_ID, 0);
            recipeId = ConfigWidgetActivity.loadRecipeIdPref(context, widgetId);


            Log.e("IngredListWidgetService", "recipeId " + recipeId+" "+RECIPE_ID+widgetId);
            Log.e("extras", extras.toString());
        } else {
            Log.e("IngredListWidgetService", "no extras");
        }

        ListRemoteViewsFactory factory = new ListRemoteViewsFactory(this.getApplicationContext(), recipeId);

        factory.setmWidgetId(widgetId);

        return factory;
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Recipe mRecipe;
    private List<Ingredient> mIngredients = new ArrayList<Ingredient>();
    private Context mContext;
    int mWidgetId;


    protected void setmWidgetId(int recipeId) {
        Log.e("set","recipeId"+recipeId);
        mWidgetId = recipeId;
       // onDataSetChanged();
    }

    public ListRemoteViewsFactory(Context context, int recipeId) {
        Log.e(getClass().getName(), "constructor");
        mContext = context;


        mWidgetId = recipeId;
    }

    public String getRecipeName(){
        if(mRecipe!=null){
            return mRecipe.getName();
        }
        return  null;
    }

    @Override
    public void onCreate() {
        Log.e(getClass().getName(), "onCreate");


/*
            mSteps.add(new Step(0,"short","long","dasd ","dada"));
            mSteps.add(new Step(1,"short","long","dasd ","dada"));
            mSteps.add(new Step(2,"short","long","dasd ","dada"));
            mSteps.add(new Step(3,"short","long","dasd ","dada"));
            mSteps.add(new Step(4,"short","long","dasd ","dada"));
            mSteps.add(new Step(5,"short","long","dasd ","dada"));*/

    }

    @Override
    public void onDestroy() {
        Log.e(getClass().getName(), "onDestroy");
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mIngredients.clear();
    }

    @Override
    public int getCount() {
        Log.e(getClass().getName(),"getCount");
        if (mIngredients == null) {
            Log.e(getClass().getName(),"getCount 0");
            return 0;
        }
        Log.e(getClass().getName(),"getCount "+mIngredients.size());
        return mIngredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
         Log.e(getClass().getName(),"getViewAt");
        if (mRecipe == null) return null;
        if (mIngredients == null || mIngredients.size() == 0) return null;

        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        Ingredient ingredient = mIngredients.get(position);
        //String text = (ingredient.getId()+1)+" "+ingredient.getDescription();
        String text = ingredient.getQuantity() + " " + ingredient.getMeasure() + " " + ingredient.getName();
        rv.setTextViewText(R.id.appwidget_item_text, text); //mSteps.get(position).text);
        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(RecipeDescriptionActivity.EXTRA_RECIPE_ID, mWidgetId);
        //extras.putInt(RecipeDescriptionActivity.EXTRA_STEP_ID, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.appwidget_item_text, fillInIntent);
        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.


        // Return the remote views object.
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        Log.e(getClass().getName(), "getLoadingView");
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {


        Log.e(getClass().getName(), "onDataSetChanged mWidgetId" + mWidgetId);
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.

        int recipeId = ConfigWidgetActivity.loadRecipeIdPref(mContext, mWidgetId);
        mRecipe = AppDatabase.getInstance(mContext).recipeDao().loadRecipeByIdNumber(recipeId);


        Log.e("widgetservice","onDataSetChanged");
        if (mRecipe != null) {

            mIngredients = mRecipe.getIngredients();
            Log.e("widgetservice","onDataSetChanged ingredients " + mIngredients.toString());
        }

    }
}