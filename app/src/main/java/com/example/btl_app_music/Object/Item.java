package com.example.btl_app_music.Object;

public class Item {
    private String itemName, itemImg;

    private int userPlaylistType;

    public String getItemImg() {
        return itemImg;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    public Item() {
    }

    public Item(String itemName, int userPlaylistType, String itemImg) {
        this.itemName = itemName;
        this.userPlaylistType = userPlaylistType;
        this.itemImg = itemImg;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getUserPlaylistType() {
        return userPlaylistType;
    }

    public void setUserPlaylistType(int userPlaylistType) {
        this.userPlaylistType = userPlaylistType;
    }
}
