package com.mkr.server.dto;

public record DetailedUserInfo(String pfpSource, String displayName, String description, CommentInfo[] comments) {
}
