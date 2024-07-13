package com.antech.starbiology.ModelClass;

public class QuestionModelClass {
    private String qImg;
    private String qTopic;
    private String qSubject;
    private String qUrl;

    public QuestionModelClass() {
    }

    public QuestionModelClass(String qImg, String qTopic, String qSubject, String qUrl) {
        this.qImg = qImg;
        this.qTopic = qTopic;
        this.qSubject = qSubject;
        this.qUrl = qUrl;
    }

    public String getqImg() {
        return qImg;
    }

    public void setqImg(String qImg) {
        this.qImg = qImg;
    }

    public String getqTopic() {
        return qTopic;
    }

    public void setqTopic(String qTopic) {
        this.qTopic = qTopic;
    }

    public String getqSubject() {
        return qSubject;
    }

    public void setqSubject(String qSubject) {
        this.qSubject = qSubject;
    }

    public String getqUrl() {
        return qUrl;
    }

    public void setqUrl(String qUrl) {
        this.qUrl = qUrl;
    }
}
