package com.example.canteen.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.List;

/**食品实体类*/
@Entity(tableName = "foods")
public class Food {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // ── 位置信息 ──────────────────────────────────────────
    /** 校区*/
    @ColumnInfo(name = "campus")
    private String campus;

    /** 食堂名称*/
    @ColumnInfo(name = "canteen")
    private String canteen;

    /** 楼层*/
    @ColumnInfo(name = "floor")
    private String floor;

    /** 窗口编号或名称*/
    @ColumnInfo(name = "window")
    private String window;

    // ── 基本信息 ──────────────────────────────────────────
    /** 食品名称 */
    @ColumnInfo(name = "name")
    private String name;

    /** 食品介绍 / 描述 */
    @ColumnInfo(name = "description")
    private String description;

    /** 价格（元），保留两位小数 */
    @ColumnInfo(name = "price")
    private double price;

    /**
     * 售卖时间，存储为字符串，例如："07:00-09:30, 11:00-13:00"
     * 简单起见用字符串，后续可拆分为独立表或者枚举
     */
    @ColumnInfo(name = "sell_time")
    private String sellTime;

    /**
     * 标签列表，以逗号分隔存储，例如："辣,套餐,热食"
     * 查询时可用 LIKE '%辣%' 过滤；后续可改为关联表
     * 已经改成 List<String>，需要 TypeConverter 转换为 String 存储
     */
    @ColumnInfo(name = "tags")
    private List<String> tags;

    // ── 评分信息 ──────────────────────────────────────────
    /** 全局综合评分（0.0 ~ 5.0） */
    @ColumnInfo(name = "average_rating")
    private float averageRating;

    /** 参与评分的总人数 */
    @ColumnInfo(name = "rating_count")
    private int ratingCount;


    public Food(String campus, String canteen, String floor, String window,
                String name, String description, double price,
                String sellTime, List<String> tags) {
        this.campus       = campus;
        this.canteen      = canteen;
        this.floor        = floor;
        this.window       = window;
        this.name         = name;
        this.description  = description;
        this.price        = price;
        this.sellTime     = sellTime;
        this.tags         = tags;
        this.averageRating = 0f;
        this.ratingCount  = 0;
    }

    //region ── Getters & Setters ─────────────────────────────────
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getCampus()               { return campus; }
    public void setCampus(String campus)    { this.campus = campus; }

    public String getCanteen()              { return canteen; }
    public void setCanteen(String c)        { this.canteen = c; }

    public String getFloor()                { return floor; }
    public void setFloor(String floor)      { this.floor = floor; }

    public String getWindow()               { return window; }
    public void setWindow(String window)    { this.window = window; }

    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }

    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }

    public double getPrice()                { return price; }
    public void setPrice(double price)      { this.price = price; }

    public String getSellTime()             { return sellTime; }
    public void setSellTime(String t)       { this.sellTime = t; }

    public List<String> getTags()                 { return tags; }
    public void setTags(List<String> tags)        { this.tags = tags; }

    public float getAverageRating()         { return averageRating; }
    public void setAverageRating(float r)   { this.averageRating = r; }

    public int getRatingCount()             { return ratingCount; }
    public void setRatingCount(int c)       { this.ratingCount = c; }

    //endregion

    /** 辅助方法：返回完整位置字符串，如 "东校区 > 第一食堂 > 2楼 > A03" */
    public String getFullLocation() {
        return campus + " > " + canteen + " > " + floor + " > " + window;
    }
}
