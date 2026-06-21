package com.example.canteen.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**食品实体类*/
//@AllArgsConstructor
@Getter
@Setter
@Entity(tableName = "foods")
//@NoArgsConstructor
public class Food implements Parcelable
{

    @PrimaryKey(autoGenerate = true)
    private long id;

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
    @ColumnInfo(name = "window_sell")
    private String window;

    // ── 基本信息 ──────────────────────────────────────────
    /** 食品名称 */
    @ColumnInfo(name = "name")
    private String name;

    /** 食品介绍 / 描述 */
    @ColumnInfo(name = "description")
    private String description;

    /** 价格（元），单位为分 */
    @ColumnInfo(name = "price")
    private Integer price;

    /**
     * 售卖时间，存储为字符串，例如："07:00-09:30, 11:00-13:00"
     * 简单起见用字符串，后续可拆分为独立表或者枚举
     */
    @ColumnInfo(name = "sell_time")
    private String sellTime;


    // 标签已经改成带中间表的多对多关系了

    // ── 评分信息 ──────────────────────────────────────────
    /** 全局综合评分（0.0 ~ 5.0） */
    @ColumnInfo(name = "average_rating")
    private Float averageRating;

    /** 参与评分的总人数 */
    @ColumnInfo(name = "rating_count")
    private Integer ratingCount;


    /** 辅助方法：返回完整位置字符串，如 "东校区 > 第一食堂 > 2楼 > A03" */
    public String getFullLocation() {
        return campus + " > " + canteen + " > " + floor + " > " + window;
    }

    public Food() {
        // 默认构造函数，Room 需要
    }

    @Ignore
    public Food(long id,
                String campus,
                String canteen,
                String floor,
                String window,
                String name,
                String description,
                Integer price,
                String sellTime,
                float averageRating,
                int ratingCount) {
        this.id = id;
        this.campus = campus;
        this.canteen = canteen;
        this.floor = floor;
        this.window = window;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellTime = sellTime;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }

    @Ignore
    protected Food(Parcel in) {
        id = in.readInt();
        campus = in.readString();
        canteen = in.readString();
        floor = in.readString();
        window = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readInt();
        sellTime = in.readString();

        averageRating = in.readFloat();
        ratingCount = in.readInt();
    }

    public static final Parcelable.Creator<Food> CREATOR = new Parcelable.Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(campus);
        dest.writeString(canteen);
        dest.writeString(floor);
        dest.writeString(window);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeString(sellTime);
        dest.writeFloat(averageRating);
        dest.writeInt(ratingCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
