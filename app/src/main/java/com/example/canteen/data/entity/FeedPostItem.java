package com.example.canteen.data.entity;


public class FeedPostItem {
    private final Post post;
    private final String browseTimeText;

    public FeedPostItem(Post post, String browseTimeText) {
        this.post = post;
        this.browseTimeText = browseTimeText;
    }

    public Post getPost() {
        return post;
    }

    public String getBrowseTimeText() {
        return browseTimeText;
    }
}