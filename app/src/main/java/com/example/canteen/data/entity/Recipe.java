package com.example.canteen.data.entity;

public class Recipe {
    private int id;
    private String name;
    private String description;
    private String ingredients; // 以逗号分隔的原料列表
    private String steps;       // 以换行分隔的步骤列表

    public Recipe(int id, String name, String description, String ingredients, String steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
}