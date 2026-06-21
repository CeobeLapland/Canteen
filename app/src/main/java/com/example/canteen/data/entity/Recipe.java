package com.example.canteen.data.entity;

import androidx.room.Ignore;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Recipe {
    private long id;
    private String name;
    private String description;
    private String ingredients; // 以逗号分隔的原料列表
    private String steps;       // 以换行分隔的步骤列表

    private List<String> tags;
    private Integer likes;
    private Integer dislikes;

    @Ignore
    public Recipe(String name, String description, String ingredients, String steps, List<String> tags, Integer likes, Integer dislikes) {
        //this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.tags = tags;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public Recipe() {
    }
}