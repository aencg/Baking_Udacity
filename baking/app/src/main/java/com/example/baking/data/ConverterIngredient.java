package com.example.baking.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;

public class ConverterIngredient implements Serializable {


    @TypeConverter
    public static List<Ingredient> fromString(String value) {
        if(value==null)  return Collections.emptyList();

        Type listType = new TypeToken<List<Ingredient>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromIngredientsArrayList(List<Ingredient> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

}
