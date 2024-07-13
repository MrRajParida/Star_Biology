package com.antech.starbiologyedu.ModelClass;

public class QuestionBookmarkModelClass {
    private String qBookedImg;
    private String qBookedTopic;
    private String qBookedSubject;
    private String qBookedUrl;

    public QuestionBookmarkModelClass() {
    }

    public QuestionBookmarkModelClass(String qBookedImg, String qBookedTopic, String qBookedUrl, String qBookedSubject) {
        this.qBookedImg = qBookedImg;
        this.qBookedTopic = qBookedTopic;
        this.qBookedUrl = qBookedUrl;
        this.qBookedSubject = qBookedSubject;
    }

    public String getqBookedImg() {
        return qBookedImg;
    }

    public void setqBookedImg(String qBookedImg) {
        this.qBookedImg = qBookedImg;
    }

    public String getqBookedTopic() {
        return qBookedTopic;
    }

    public void setqBookedTopic(String qBookedTopic) {
        this.qBookedTopic = qBookedTopic;
    }

    public String getqBookedUrl() {
        return qBookedUrl;
    }

    public void setqBookedUrl(String qBookedUrl) {
        this.qBookedUrl = qBookedUrl;
    }

    public String getqBookedSubject() {
        return qBookedSubject;
    }

    public void setqBookedSubject(String qBookedSubject) {
        this.qBookedSubject = qBookedSubject;
    }
}
