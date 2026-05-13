package com.example.canteen.data.entity;

public class Floor {
    private final String floorName;  // 楼层名
    private Canteen parentCanteen; // 父食堂（双向查询关键）

    // 构造、get/set
    public Floor(String floorName) {
        this.floorName = floorName;
    }

    // getter & setter
    public String getFloorName() {return floorName;}
    public Canteen getParentCanteen() {return parentCanteen;}
    public void setParentCanteen(Canteen parentCanteen) {this.parentCanteen = parentCanteen;}
}