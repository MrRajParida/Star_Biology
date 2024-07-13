package com.antech.starbiology.ModelClass;

public class VideoModelClass {
    private String thumbnail;
    private String title;
    private String description;
    private String link;

    public VideoModelClass() {
    }

    public VideoModelClass(String thumbnail, String title, String description, String link) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
