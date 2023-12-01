package com.mkr.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record CommentInfo(
    int id,
    ConciseUserInfo user,
    int rating,
    String text,
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty(value = "dateString")
    LocalDateTime postDateTime
) {
}
