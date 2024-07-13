package com.example.edudy;

public class VideoBookmarkViewModelClass {
    private String bookedThumbnail;
    private String bookedTitle;
    private String bookedLink;

    public VideoBookmarkViewModelClass() {
    }

    public VideoBookmarkViewModelClass(String bookedThumbnail, String bookedTitle, String bookedLink) {
        this.bookedThumbnail = bookedThumbnail;
        this.bookedTitle = bookedTitle;
        this.bookedLink = bookedLink;
    }

    public String getBookedThumbnail() {
        return bookedThumbnail;
    }

    public void setBookedThumbnail(String bookedThumbnail) {
        this.bookedThumbnail = bookedThumbnail;
    }

    public String getBookedTitle() {
        return bookedTitle;
    }

    public void setBookedTitle(String bookedTitle) {
        this.bookedTitle = bookedTitle;
    }

    public String getBookedLink() {
        return bookedLink;
    }

    public void setBookedLink(String bookedLink) {
        this.bookedLink = bookedLink;
    }
}
