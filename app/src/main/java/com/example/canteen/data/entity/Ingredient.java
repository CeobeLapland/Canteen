package com.example.canteen.data.entity;

public class Ingredient {

    public final IngredientType type;
    public final String name;

    public Ingredient(String name, IngredientType type) {
        this.name = name;
        this.type = type;
    }

    public Ingredient(IngredientType type, String name) {
        this.type = type;
        this.name = name;
    }
}