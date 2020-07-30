package com.example.baking.widget;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.baking.R;

import java.util.HashMap;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RecipeWidgetService extends IntentService {

    public static final String ACTION_UPDATE_RECIPE_WIDGETS = "com.example.android.mygarden.action.update_plant_widgets";


    public RecipeWidgetService() {
        super("PlantWateringService");
    }



    /**
     * Starts this service to perform UpdatePlantWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateRecipeWidgets(Context context) {
        Log.e( "RecipeWidgetService", "startActionUpdateRecipeWidgets");
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGETS);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent);
//        } else {
            context.startService(intent);
//        }
    }

    /**
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(getClass().getName(), "onHandleIntent");
        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_WATER_PLANT.equals(action)) {
//                final long plantId = intent.getLongExtra(EXTRA_PLANT_ID,
//                        PlantContract.INVALID_PLANT_ID);
//                handleActionWaterPlant(plantId);
//            } else if (ACTION_UPDATE_PLANT_WIDGETS.equals(action)) {
//                handleActionUpdatePlantWidgets();
//            }
            if(intent.getExtras()!=null)
                Log.e(getClass().getName(), "onHandleIntent extras"+intent.getExtras().toString());
            handleActionUpdateRecipeWidgets();
        }


    }


    /**
     * Handle action UpdatePlantWidgets in the provided background thread
     */

    private void handleActionUpdateRecipeWidgets() {
        Log.e(getClass().getName(), "handleActionUpdateRecipeWidgets");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_widget);
        //Now update all widgets
        RecipeWidgetProvider.updateWidgets(this, appWidgetManager  ,appWidgetIds);
    }
}
