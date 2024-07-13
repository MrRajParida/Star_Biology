package com.antech.starbiology.ModelClass;

public class SaveQuizModelClass {
    private String question;
    private String correctAnswer;
    private String documentId;

    public SaveQuizModelClass() {
    }

    public SaveQuizModelClass(String question, String correctAnswer, String documentId) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.documentId = documentId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
