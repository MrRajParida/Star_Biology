package com.antech.starbiology.ModelClass;

public class QuizCatagoryModelClass {

    private String title;
    private String subject;
    private String img;
    private String quizId;

    public QuizCatagoryModelClass() {
    }

    public QuizCatagoryModelClass(String title, String subject, String img, String quizId) {
        this.title = title;
        this.subject = subject;
        this.img = img;
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
}
