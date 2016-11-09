package com.bosch.myfootprint;

/**
 * Created by BHY1MTP on 10/31/2016.
 */

public class CompanionCard {
    private String name;

    public String getDescription() {
        return description;
    }

    public CompanionCard(String name, String description, int thumbnail, String key) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.key=key;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String description;
    private int thumbnail;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;

}
