package com.example.edudy;

public class QuestionsAdapter {

    private String questions, optionA, optionB, optionC, optionD, correctans;

    public QuestionsAdapter(String questions, String optionA, String optionB, String optionC, String optionD, String correctans) {
        this.questions = questions;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctans = correctans;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectans() {
        return correctans;
    }

    public void setCorrectans(String correctans) {
        this.correctans = correctans;
    }
}
