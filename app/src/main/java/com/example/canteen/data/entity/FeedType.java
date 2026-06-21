package com.example.canteen.data.entity;

public enum FeedType {
    MY_POSTS("我发的"),
    HISTORY("历史浏览"),
    FAVORITES("收藏");

    private final String title;

    FeedType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}