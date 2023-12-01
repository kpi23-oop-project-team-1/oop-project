package com.mkr.server.domain;

public class Comment {
    private int id;
    private int targetId;
    private int userId;
    private int rating;
    private String text;
    private long postEpochSeconds;

    public Comment() {
    }

    public Comment(int id, int targetId, int userId, int rating, String text, long postEpochSeconds) {
        this.targetId = targetId;
        this.userId = userId;
        this.rating = rating;
        this.text = text;
        this.postEpochSeconds = postEpochSeconds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getPostEpochSeconds() {
        return postEpochSeconds;
    }

    public void setPostEpochSeconds(long postEpochSeconds) {
        this.postEpochSeconds = postEpochSeconds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
