package com.example.canteen.data.repository;

import com.example.canteen.data.entity.Ingredient;
import com.example.canteen.data.entity.IngredientType;
import com.example.canteen.data.entity.Recipe;
import android.app.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecipeRepository {

    private volatile static RecipeRepository instance;
    public static RecipeRepository getInstance() {
        return instance;
    }

    public RecipeRepository(Application application) {
        // 初始化数据库、网络等资源
    }

    public Single<List<String>> getAllTags(){
        // 返回示例
        return Single.just(List.of("家常菜", "快手菜", "素食", "肉食", "甜点"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Recipe>> searchRecipes(String keyword, String tag, int page, int pageSize){
        return Single.just(List.of(
                new Recipe("番茄炒蛋", "简单又美味的番茄炒蛋", "番茄 鸡蛋 盐 油", "1. 番茄切块；2. 鸡蛋打散；3. 热锅加油，先炒鸡蛋，再炒番茄，最后调味。", List.of("家常菜","快手菜"), 100, 10)
        ))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Recipe> getRecipeDetail(long recipeId){
        return Single.just(
                        new Recipe("番茄炒蛋", "简单又美味的番茄炒蛋", "番茄 鸡蛋 盐 油", "1. 番茄切块；2. 鸡蛋打散；3. 热锅加油，先炒鸡蛋，再炒番茄，最后调味。", List.of("家常菜","快手菜"), 100, 10)
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Long> submitRecipe(Recipe recipe){
        // 模拟提交成功，返回新菜谱ID
        return Single.just(123L)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> likeRecipe(long recipeId){
        return Single.just(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> dislikeRecipe(long recipeId){
        return Single.just(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }








    private final List<Ingredient> storage = new ArrayList<>(Arrays.asList(
            new Ingredient(IngredientType.VEGETABLE, "土豆"),
            new Ingredient(IngredientType.VEGETABLE, "胡萝卜"),
            new Ingredient(IngredientType.VEGETABLE, "洋葱"),
            new Ingredient(IngredientType.MEAT, "鸡肉"),
            new Ingredient(IngredientType.MEAT, "牛肉"),
            new Ingredient(IngredientType.SEAFOOD, "虾仁"),
            new Ingredient(IngredientType.DAIRY, "牛奶"),
            new Ingredient(IngredientType.GRAIN, "米饭"),
            new Ingredient(IngredientType.FRUIT, "番茄"),
            new Ingredient(IngredientType.OTHER, "鸡蛋"),
            new Ingredient(IngredientType.OTHER, "盐"),
            new Ingredient(IngredientType.OTHER, "粽子"),
            new Ingredient(IngredientType.OTHER, "书"),
            new Ingredient(IngredientType.OTHER, "水")
    ));


    public Single<List<Ingredient>> loadAllIngredients() {
        return Single.just(new ArrayList<>(storage));
    }


    public Single<Ingredient> addCustomIngredient(String name) {
        Ingredient custom = new Ingredient(IngredientType.CUSTOM, name);
        storage.add(custom);
        return Single.just(custom);
    }


    public Single<String> generateRecipe(List<Ingredient> ingredients) {
        StringBuilder sb = new StringBuilder();
        sb.append("【AI食谱占位】\n\n");
        sb.append("当前锅里食材：");
        if (ingredients == null || ingredients.isEmpty()) {
            sb.append("无\n\n");
            sb.append("建议：先拖入 2~4 种食材再生成。");
            return Single.just(sb.toString());
        }

        for (int i = 0; i < ingredients.size(); i++) {
            sb.append(i == 0 ? "" : "、").append(ingredients.get(i).name);
        }
        sb.append("\n\n");

        sb.append("示例步骤：\n");
        sb.append("1. 热锅少油，先下").append(ingredients.get(0).name).append("翻炒。\n");
        if (ingredients.size() > 1) {
            sb.append("2. 加入").append(ingredients.get(1).name).append("继续翻炒。\n");
        }
        sb.append("3. 适量加盐和调味，出锅即可。\n");

        return Single.just(sb.toString());
    }

}
