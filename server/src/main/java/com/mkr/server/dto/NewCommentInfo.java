package com.mkr.server.dto;

public record NewCommentInfo(int targetId, int rating, String text) {
}
