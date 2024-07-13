package com.kprandro.newshow;

public class NewsItemClass {
    private int userImg;
    private String userId;
    private Boolean verified;
    private int newsImg;
    private String newsTitle;
    private String newsDescription;

    public NewsItemClass() {
    }

    public NewsItemClass(int userImg, String userId, Boolean verified, int newsImg, String newsTitle, String newsDescription) {
        this.userImg = userImg;
        this.userId = userId;
        this.verified = verified;
        this.newsImg = newsImg;
        this.newsTitle = newsTitle;
        this.newsDescription = newsDescription;
    }

    public int getUserImg() {
        return userImg;
    }

    public void setUserImg(int userImg) {
        this.userImg = userImg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public int getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(int newsImg) {
        this.newsImg = newsImg;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsDescription() {
        return newsDescription;
    }

    public void setNewsDescription(String newsDescription) {
        this.newsDescription = newsDescription;
    }
}
