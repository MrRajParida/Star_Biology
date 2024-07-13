package com.antech.starbiology.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class QuizModel implements Parcelable {
    private String id;
    private String title;
    private String subtitle;
    private String time;
    private String img;
    private List<QuestionModel> questionList;

    // Default constructor
    public QuizModel() {
        this("", "", "", "", "", new ArrayList<>());
    }

    // Parameterized constructor
    public QuizModel(String id, String title, String subtitle, String time, String img, List<QuestionModel> questionList) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.img = img;
        this.questionList = questionList;
    }

    // Parcelable constructor
    protected QuizModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        subtitle = in.readString();
        time = in.readString();
        img = in.readString();
        questionList = in.createTypedArrayList(QuestionModel.CREATOR);
    }

    // Parcelable Creator
    public static final Creator<QuizModel> CREATOR = new Creator<QuizModel>() {
        @Override
        public QuizModel createFromParcel(Parcel in) {
            return new QuizModel(in);
        }

        @Override
        public QuizModel[] newArray(int size) {
            return new QuizModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(time);
        dest.writeString(img);
        dest.writeTypedList(questionList);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<QuestionModel> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuestionModel> questionList) {
        this.questionList = questionList;
    }

    // QuestionModel class
    public static class QuestionModel implements Parcelable {
        private String question;
        private List<String> options;
        private String correct;
        private boolean isBookmarked;

        // Default constructor
        public QuestionModel() {
            this("", new ArrayList<>(), "", false);
        }

        // Parameterized constructor
        public QuestionModel(String question, List<String> options, String correct, Boolean isBookmarked) {
            this.question = question;
            this.options = options;
            this.correct = correct;
            this.isBookmarked = isBookmarked;
        }

        // Parcelable constructor
        protected QuestionModel(Parcel in) {
            question = in.readString();
            options = in.createStringArrayList();
            correct = in.readString();
            isBookmarked = in.readByte() != 0;
        }

        // Parcelable Creator
        public static final Creator<QuestionModel> CREATOR = new Creator<QuestionModel>() {
            @Override
            public QuestionModel createFromParcel(Parcel in) {
                return new QuestionModel(in);
            }

            @Override
            public QuestionModel[] newArray(int size) {
                return new QuestionModel[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(question);
            dest.writeStringList(options);
            dest.writeString(correct);
            dest.writeByte((byte) (isBookmarked ? 1 : 0));
        }

        // Getters and Setters
        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getCorrect() {
            return correct;
        }

        public void setCorrect(String correct) {
            this.correct = correct;
        }

        public boolean isBookmarked() {
            return isBookmarked;
        }

        public void setBookmarked(boolean bookmarked) {
            isBookmarked = bookmarked;
        }
    }
}
