package com.mkr.server.services;

import com.mkr.server.domain.Comment;
import com.mkr.server.dto.CommentInfo;
import com.mkr.server.dto.ConciseUserInfo;
import com.mkr.server.repositories.CommentRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.function.Function;

@Service
public class CommentService {
    @Autowired
    private CommentRepository repo;

    public int addComment(int authorId, int rating, @NotNull String text) {
        var comment = new Comment(
                repo.getNewID(),
                authorId,
                rating,
                text,
                LocalDateTime.now(Clock.systemUTC()).toEpochSecond(ZoneOffset.UTC)
        );

        repo.addComment(comment);

        return comment.getId();
    }

    public CommentInfo getCommentInfo(int id, Function<Integer, Optional<ConciseUserInfo>> getConciseUserInfo) {
        var comment = repo.getCommentById(id).orElseThrow();

        return new CommentInfo(
                comment.getId(),
                getConciseUserInfo.apply(comment.getAuthorId()).orElseThrow(),
                comment.getRating(),
                comment.getText(),
                LocalDateTime.ofEpochSecond(comment.getPostEpochSeconds(), 0, ZoneOffset.UTC)
        );
    }

    public void deleteComment(int id) {
        repo.deleteComment(id);
    }
}
