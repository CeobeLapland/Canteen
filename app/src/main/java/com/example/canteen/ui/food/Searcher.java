package com.example.canteen.ui.food;

import com.example.canteen.data.entity.Campus;
import com.example.canteen.data.entity.Canteen;
import com.example.canteen.data.entity.Floor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 树形结构查询管理器
public class Searcher {
    // 全局ID索引（O(1)快速查询，比遍历树快很多）
    private final Map<String, Campus> campusMap = new HashMap<>();
    private final Map<String, Canteen> canteenMap = new HashMap<>();
    private final Map<String, Floor> floorMap = new HashMap<>();

    // 1. 添加校园（自动维护索引）
    public void addCampus(Campus campus) {
        campusMap.put(campus.getCampusName(), campus);
    }

    // 2. 添加食堂（必须归属某个校园）
    public void addCanteenToCampus(String campusName, Canteen canteen) {
        Campus campus = campusMap.get(campusName);
        if (campus != null) {
            campus.addCanteen(canteen);
            canteenMap.put(canteen.getCanteenName(), canteen);
        }
    }

    // 3. 添加楼层（必须归属某个食堂）
    public void addFloorToCanteen(String canteenName, Floor floor) {
        Canteen canteen = canteenMap.get(canteenName);
        if (canteen != null) {
            canteen.addFloor(floor);
            floorMap.put(floor.getFloorName(), floor);
        }
    }

    // ==================== 核心：双向查询方法 ====================
    // 【父查子】根据校园ID查所有食堂
    public List<Canteen> findCanteensByCampusName(String campusName) {
        Campus campus = campusMap.get(campusName);
        return campus == null ? new ArrayList<>() : campus.getCanteenList();
    }

    // 【父查子】根据食堂ID查所有楼层
    public List<Floor> findFloorsByCanteenName(String canteenName) {
        Canteen canteen = canteenMap.get(canteenName);
        return canteen == null ? new ArrayList<>() : canteen.getFloorList();
    }

    // 【子查父】根据食堂ID查所属校园
    public Campus findCampusByCanteenName(String canteenName) {
        Canteen canteen = canteenMap.get(canteenName);
        return canteen == null ? null : canteen.getParentCampus();
    }

    // 【子查父】根据楼层ID查所属食堂
    public Canteen findCanteenByFloorName(String floorName) {
        Floor floor = floorMap.get(floorName);
        return floor == null ? null : floor.getParentCanteen();
    }

    // 根据ID获取单个对象
    public Campus getCampus(String name) {
        return campusMap.get(name);
    }

    public Canteen getCanteen(String name) {
        return canteenMap.get(name);
    }

    public Floor getFloor(String name) {
        return floorMap.get(name);
    }

    public List<Campus> getAllCampuses() {
        return new ArrayList<>(campusMap.values());
    }

    public List<Canteen> getAllCanteens() {
        return new ArrayList<>(canteenMap.values());
    }

    public List<Floor> getAllFloors() {
        return new ArrayList<>(floorMap.values());
    }

    public List<String> getAllCampusNames() {
        return new ArrayList<>(campusMap.keySet());
    }

    public List<String> getAllCanteenNames() {
        return new ArrayList<>(canteenMap.keySet());
    }

    public List<String> getAllFloorNames() {
        return new ArrayList<>(floorMap.keySet());
    }



    private String selectedCampusName;
    private String selectedCanteenName;
    private String selectedFloorName;
    /*
    // setter and getter
    public String getSelectedCampusName() {
        return selectedCampusName;
    }
    public void setSelectedCampusName(String selectedCampusName) {
        this.selectedCampusName = selectedCampusName;
    }
    public String getSelectedCanteenName() {
        return selectedCanteenName;
    }
    public void setSelectedCanteenName(String selectedCanteenName) {
        this.selectedCanteenName = selectedCanteenName;
    }
    public String getSelectedFloorName() {
        return selectedFloorName;
    }
    public void setSelectedFloorName(String selectedFloorName) {
        this.selectedFloorName = selectedFloorName;
    }
     */

    //public List<String> getCanteenNamesBySelectedCampus()
    public List<String> getCanteenNamesByCampus(String campusName)
    {
        //if (selectedCampusName == null) return new ArrayList<>();
        selectedCampusName = campusName;
        Campus campus = campusMap.get(selectedCampusName);
        //if (campus == null) return new ArrayList<>();
        List<String> canteenNames = new ArrayList<>();
        for (Canteen c : campus.getCanteenList()) {
            canteenNames.add(c.getCanteenName());
        }
        return canteenNames;
    }
    public List<String> getFloorNamesByCanteens(List<String> canteenNames)
    {
        List<String> floorNames = new ArrayList<>();
        for (String canteenName : canteenNames) {
            Canteen canteen = canteenMap.get(canteenName);
            if (canteen != null) {
                for (Floor f : canteen.getFloorList()) {
                    floorNames.add(f.getFloorName());
                }
            }
        }
        return floorNames;
    }
    public List<String> getFloorNamesByCanteen(String canteenName)
    {
        //if (selectedCanteenName == null) return new ArrayList<>();
        selectedCanteenName = canteenName;
        Canteen canteen = canteenMap.get(selectedCanteenName);
        //if (canteen == null) return new ArrayList<>();
        List<String> floorNames = new ArrayList<>();
        for (Floor f : canteen.getFloorList()) {
            floorNames.add(f.getFloorName());
        }
        return floorNames;
    }
    public String getCampusNameByCanteen(String canteenName)
    {
        //if (selectedCanteenName == null) return null;
        selectedCanteenName = canteenName;
        Canteen canteen = canteenMap.get(selectedCanteenName);
        //if (canteen == null) return null;
        Campus campus = canteen.getParentCampus();
        return campus == null ? null : campus.getCampusName();
    }
    public String getCanteenNameByFloor(String floorName)
    {
        //if (selectedFloorName == null) return null;
        selectedFloorName = floorName;
        Floor floor = floorMap.get(selectedFloorName);
        //if (floor == null) return null;
        Canteen canteen = floor.getParentCanteen();
        return canteen == null ? null : canteen.getCanteenName();
    }
}