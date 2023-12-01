package com.mkr.server.dto;

public record UpdateAccountInfo(
    String password,
    String username,
    String aboutMe,
    String firstName,
    String lastName,
    String telNumber
) {
}
