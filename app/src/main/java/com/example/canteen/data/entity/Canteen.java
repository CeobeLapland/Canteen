package com.example.canteen.data.entity;

import java.util.ArrayList;
import java.util.List;

public class Canteen {
    private final String canteenName;
    private Campus parentCampus; // 父校园
    private List<Floor> floorList = new ArrayList<>(); // 子楼层

    public Canteen(String canteenName) {
        this.canteenName = canteenName;
    }

    // 添加楼层（自动绑定双向关系）
    public void addFloor(Floor floor) {
        floorList.add(floor);
        floor.setParentCanteen(this);
    }

    // getter & setter
    public String getCanteenName() {return canteenName;}
    public Campus getParentCampus() {return parentCampus;}
    public void setParentCampus(Campus parentCampus) {this.parentCampus = parentCampus;}
    public List<Floor> getFloorList() {return floorList;}
}