package com.mkr.server.repositories;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.DataStoreCollection;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.Comment;
import com.mkr.server.domain.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CommentRepository {
    private final DataStore dataStore;

    @Autowired
    public CommentRepository(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @NotNull
    private DataStoreCollection<Comment> commentCollection() {
        return dataStore.getCollection(DataStoreConfig.comments);
    }

    public Optional<Comment> getCommentById(int id) {
        try (var data = commentCollection().data()) {
            return data.filter(p -> p.getId() == id).findFirst();
        }
    }

    public void addComment(@NotNull Comment comment) {
        commentCollection().insert(comment);
        commentCollection().setLastID(comment.getId());
    }

    public void deleteComment(int id) {
        commentCollection().delete(o -> o.getId() == id);
    }

    public int getNewID() {
        return commentCollection().getLastID() + 1;
    }
}
