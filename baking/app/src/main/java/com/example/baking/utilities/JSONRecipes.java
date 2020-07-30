package com.example.baking.utilities;

import android.content.Context;

import com.example.baking.data.Ingredient;
import com.example.baking.data.Recipe;
import com.example.baking.data.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class JSONRecipes {

    public static List<Recipe> getRecipesFromJson(String recipeJsonStr)
            throws JSONException {



        final String THUMBNAILURL = "thumbnailURL";
        final String VIDEOURL = "videoURL";
        final String DESCRIPTION = "description";
        final String SHORTDESCRIPTION = "shortDescription";

        final String INGREDIENTS = "ingredients";
        final String INGREDIENT = "ingredient";
        final String MEASURE = "measure";
        final String QUANTITY= "quantity";

        final String STEPS = "steps";
        final String IMAGE = "image";
        final String SERVINGS = "servings";
        final String NAME = "name";
        final String ID = "id";

        ArrayList<Recipe> recipes = new ArrayList<Recipe>();

        JSONArray recipesJson = new JSONArray(recipeJsonStr);

        final String HTTP_STATUS_CODE ="status_code";

       /* if (recipesJson.has(HTTP_STATUS_CODE)) {
            int errorCode = recipesJson.getInt(HTTP_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }*/

        for(int i = 0; i<recipesJson.length(); i++){
            Recipe recipe = new Recipe();
            JSONObject recipeJsonObject = recipesJson.getJSONObject(i);
            int id = recipeJsonObject.getInt(ID);
            recipe.setId(id);
            String name = recipeJsonObject.getString(NAME);
            recipe.setName(name);
            int servings = recipeJsonObject.getInt(SERVINGS);
            recipe.setServings(servings);
            String image = recipesJson.getJSONObject(i).getString(IMAGE);
            recipe.setImage(image);

            JSONArray ingredientsJson = recipeJsonObject.getJSONArray(INGREDIENTS);
            ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
            for(int x = 0; x<ingredientsJson.length(); x++){
                Ingredient ingredient = new Ingredient();
                JSONObject ingredientJSON = ingredientsJson.getJSONObject(x);
                int quantity = ingredientJSON.getInt(QUANTITY);
                String measure = ingredientJSON.getString(MEASURE);
                String ingredientName = ingredientJSON.getString(INGREDIENT);

                ingredient.setQuantity(quantity);
                ingredient.setMeasure(measure);
                ingredient.setName(ingredientName);
                ingredients.add(ingredient);
            }
            recipe.setIngredients(ingredients);

            ArrayList<Step> steps = new ArrayList<Step>();
            JSONArray stepsJSON = recipeJsonObject.getJSONArray(STEPS);
            for(int z = 0; z<stepsJSON.length(); z++){
                Step step = new Step();
                JSONObject stepJSON = stepsJSON.getJSONObject(z);
                int stepId = stepJSON.getInt(ID);
                String shortDescription = stepJSON.getString(SHORTDESCRIPTION);
                String description = stepJSON.getString(DESCRIPTION);
                String videoURL = stepJSON.getString(VIDEOURL);
                String thumbnailURL = stepJSON.getString(THUMBNAILURL);
                step.setDescription(description);
                step.setId(id);
                step.setShortDescription(shortDescription);
                step.setThumbnailUrl(thumbnailURL);
                step.setVideoUrl(videoURL);
                step.setId(stepId);
                steps.add(step);
            }

            recipe.setSteps(steps);


           // Log.e("m",movie.toString());
            recipes.add(recipe);
        }
      //  for(int i = 0; i<recipes.size(); i++)
        //Log.e("hola",recipes.get(i).toString());
        return recipes;
    }


}
