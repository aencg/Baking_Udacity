package com.example.baking.data;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes")
    LiveData<List<Recipe>> loadAllLiveRecipes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(Recipe recipe);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Query("SELECT * FROM recipes WHERE id = :id")
    LiveData<Recipe> loadRecipeById(int id);

    @Query("SELECT * FROM recipes")
    List<Recipe> loadAllRecipes();


    @Query("DELETE FROM recipes")
    public void deleteAll();

    @Query("SELECT * FROM recipes WHERE id = :id")
    Recipe loadRecipeByIdNumber(int id);
}
