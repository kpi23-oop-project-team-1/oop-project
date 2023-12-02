package com.mkr.server.domain;

public class Comment {
    private Integer id;
    private Integer targetId;
    private Integer userId;
    private Integer rating;
    private String text;
    private Long postEpochSeconds;

    public Comment() {
    }

    public Comment(Integer id, Integer targetId, Integer userId, Integer rating, String text, Long postEpochSeconds) {
        this.id = id;
        this.targetId = targetId;
        this.userId = userId;
        this.rating = rating;
        this.text = text;
        this.postEpochSeconds = postEpochSeconds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Long getPostEpochSeconds() {
        return postEpochSeconds;
    }

    public void setPostEpochSeconds(Long postEpochSeconds) {
        this.postEpochSeconds = postEpochSeconds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
