package com.example.btl_app_music.Object;

import java.util.List;

public class ListItem {

    private int type;
    private int listType;
    private String itemCategory;
    private List<Item> itemForYou, itemArtist;

    public ListItem() {
    }

    public ListItem(int type, String itemCategory, List<Item> itemForYou, List<Item> itemArtist, int listType) {
        this.type = type;
        this.itemCategory = itemCategory;
        this.itemForYou = itemForYou;
        this.itemArtist = itemArtist;
        this.listType = listType;
    }



    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public List<Item> getItemForYou() {
        return itemForYou;
    }

    public void setItemForYou(List<Item> itemForYou) {
        this.itemForYou = itemForYou;
    }

    public List<Item> getItemArtist() {
        return itemArtist;
    }

    public void setItemArtist(List<Item> itemArtist) {
        this.itemArtist = itemArtist;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getListType() {
        return listType;
    }

    public void setListType(int listType) {
        this.listType = listType;
    }
}
