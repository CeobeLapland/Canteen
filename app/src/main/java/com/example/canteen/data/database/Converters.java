package com.example.canteen.data.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.room.TypeConverter;
import java.util.List;

public class Converters {

    // List<String> 转 JSON 字符串
    @TypeConverter
    public static String fromStringList(List<String> list) {
        if (list == null)
            return null;
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    // JSON 字符串 转回 List<String>
    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null)
            return null;
        Gson gson = new Gson();
        TypeToken<List<String>> token = new TypeToken<List<String>>() {};
        return gson.fromJson(value, token.getType());
    }
    /*
    @TypeConverter
    public static String fromList(List<String> list) {
        return list == null ? null : String.join(",", list);
    }

    @TypeConverter
    public static List<String> toList(String data) {
        return data == null ? null : Arrays.asList(data.split(","));
    }*/

    // List<Integer> 转 JSON 字符串
    @TypeConverter
    public static String fromIntegerList(List<Integer> list) {
        if (list == null)
            return null;
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    // JSON 字符串 转回 List<Integer>
    @TypeConverter
    public static List<Integer> toIntegerList(String value) {
        if (value == null)
            return null;
        Gson gson = new Gson();
        TypeToken<List<Integer>> token = new TypeToken<List<Integer>>() {};
        return gson.fromJson(value, token.getType());
    }
}