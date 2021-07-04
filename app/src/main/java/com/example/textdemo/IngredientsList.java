package com.example.textdemo;

public class IngredientsList {
    int ingredientId;
    String ingredientName;
    int ingredientType;
    String ingredientDescription;

    public IngredientsList(int id, String name, int type, String description) {
        //initialise object variables
        this.ingredientId = id;
        this.ingredientName = name;
        this.ingredientType = type;
        this.ingredientDescription = description;
    }

    public void setIngredientId(int id) {
        ingredientId = id;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientName(String name) {
        ingredientName = name;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientType(int type) {
        ingredientType = type;
    }

    public int getIngredientType() {
        return ingredientType;
    }

    public void setIngredientDescription(String description) {
        ingredientDescription = description;
    }

    public String getIngredientDescription() {
        return ingredientDescription;
    }

}
