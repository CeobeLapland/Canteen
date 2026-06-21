package com.example.canteen.controller;

import com.example.canteen.data.entity.Food;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class PostDraftStore {

    private static final PostDraftStore INSTANCE = new PostDraftStore();

    private String authorName = "";
    private String title = "";
    private String content = "";

    private final LinkedHashSet<String> selectedTypes = new LinkedHashSet<>();
    private final LinkedHashMap<Long, Food> selectedFoods = new LinkedHashMap<>();

    private PostDraftStore() {
    }

    public static PostDraftStore get() {
        return INSTANCE;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName == null ? "" : authorName.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? "" : content.trim();
    }

    public void toggleType(String type) {
        if (type == null || type.trim().isEmpty()) return;
        if (selectedTypes.contains(type)) {
            selectedTypes.remove(type);
        } else {
            selectedTypes.add(type);
        }
    }

    public boolean isTypeSelected(String type) {
        return selectedTypes.contains(type);
    }

    public List<String> getSelectedTypes() {
        return new ArrayList<>(selectedTypes);
    }

    public void setSelectedTypes(Collection<String> types) {
        selectedTypes.clear();
        if (types != null) {
            for (String t : types) {
                if (t != null && !t.trim().isEmpty()) {
                    selectedTypes.add(t);
                }
            }
        }
    }

    public void toggleFood(Food food) {
        if (food == null) return;
        long id = food.getId();
        if (selectedFoods.containsKey(id)) {
            selectedFoods.remove(id);
        } else {
            selectedFoods.put(id, food);
        }
    }

    public boolean isFoodSelected(long foodId) {
        return selectedFoods.containsKey(foodId);
    }

    public void setSelectedFoods(Collection<Food> foods) {
        selectedFoods.clear();
        if (foods != null) {
            for (Food f : foods) {
                if (f != null) selectedFoods.put(f.getId(), f);
            }
        }
    }

    public List<Food> getSelectedFoods() {
        return new ArrayList<>(selectedFoods.values());
    }

    public List<Long> getSelectedFoodIds() {
        List<Long> ids = new ArrayList<>();
        for (Map.Entry<Long, Food> entry : selectedFoods.entrySet()) {
            ids.add(entry.getKey());
        }
        return ids;
    }

    public void clear() {
        authorName = "";
        title = "";
        content = "";
        selectedTypes.clear();
        selectedFoods.clear();
    }
}