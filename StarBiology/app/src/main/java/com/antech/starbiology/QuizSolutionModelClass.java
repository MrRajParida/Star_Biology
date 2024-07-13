package com.antech.starbiology;

import android.os.Parcel;
import android.os.Parcelable;

public class QuizSolutionModelClass implements Parcelable {
    private String question;
    private String answer;

    public QuizSolutionModelClass(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    protected QuizSolutionModelClass(Parcel in) {
        question = in.readString();
        answer = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(answer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuizSolutionModelClass> CREATOR = new Creator<QuizSolutionModelClass>() {
        @Override
        public QuizSolutionModelClass createFromParcel(Parcel in) {
            return new QuizSolutionModelClass(in);
        }

        @Override
        public QuizSolutionModelClass[] newArray(int size) {
            return new QuizSolutionModelClass[size];
        }
    };
}
