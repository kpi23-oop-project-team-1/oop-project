package com.mkr.server.dto;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

public record PostCommentInfo(int targetId, @Range(min = 0, max = 5) int rating, @NotEmpty String text) {
}
