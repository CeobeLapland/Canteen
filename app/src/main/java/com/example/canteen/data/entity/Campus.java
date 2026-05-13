package com.example.canteen.data.entity;

import java.util.ArrayList;
import java.util.List;

public class Campus {
    private final String campusName;
    private List<Canteen> canteenList = new ArrayList<>(); // 子食堂

    public Campus(String campusName) {
        this.campusName = campusName;
    }

    //添加食堂（自动绑定双向关系）
    public void addCanteen(Canteen canteen) {
        canteenList.add(canteen);
        canteen.setParentCampus(this);
    }

    // getter & setter
    public String getCampusName() {return campusName;}
    public List<Canteen> getCanteenList() {return canteenList;}
}