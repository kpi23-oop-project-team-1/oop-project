package com.mkr.server.dto;

public record AccountInfo(
    int id,
    String email,
    String username,
    String pfpSource,
    String aboutMe,
    String firstName,
    String lastName,
    String telNumber
) {
}
