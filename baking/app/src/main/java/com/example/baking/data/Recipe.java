package com.example.baking.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "recipes")
public class Recipe implements Parcelable {

    @TypeConverters(ConverterIngredient.class)
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    @TypeConverters(ConverterIngredient.class)
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
    @TypeConverters(ConverterStep.class)
    public List<Step> getSteps() {
        return steps;
    }
    @TypeConverters(ConverterStep.class)
    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

   @TypeConverters(ConverterIngredient.class)
 @ColumnInfo(name = "ingredients_info")
 List<Ingredient> ingredients;
    @TypeConverters(ConverterStep.class)
   @ColumnInfo(name = "steps_info")
List<Step> steps;
    @PrimaryKey
    @NonNull
    int id;
    int servings;
    String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public Recipe(List<Ingredient> ingredients, List<Step> steps, @NonNull int id, int servings, String image, String name) {
        this.ingredients = ingredients;
        this.steps = steps;
        this.id = id;
        this.servings = servings;
        this.image = image;
        this.name = name;
    }
    @Ignore
    public Recipe() {
        this.ingredients = null;
        this.steps = null;
        this.id = -1;
        this.servings = -1;
        this.image = "";
        this.name = null;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name=" + name +"\n"+
                "ingredients=" + ingredients+"\n"+
                " steps=" + steps +"\n"+
                "servings=" + servings +
                ", image='" + image + '\'' +
                '}';
    }
    @Ignore
    protected Recipe(Parcel in) {
        if (in.readByte() == 0x01) {
            ingredients = new ArrayList<Ingredient>();
            in.readList(ingredients, Ingredient.class.getClassLoader());
        } else {
            ingredients = null;
        }
        if (in.readByte() == 0x01) {
            steps = new ArrayList<Step>();
            in.readList(steps, Step.class.getClassLoader());
        } else {
            steps = null;
        }
        id = in.readInt();
        servings = in.readInt();
        image = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (ingredients == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ingredients);
        }
        if (steps == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(steps);
        }
        dest.writeInt(id);
        dest.writeInt(servings);
        dest.writeString(image);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}