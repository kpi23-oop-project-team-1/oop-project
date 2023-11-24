package com.mkr.datastore.inFile;

public class CollectionFileIsClosedException extends RuntimeException {
    public CollectionFileIsClosedException() {}

    public CollectionFileIsClosedException(String message) {
        super(message);
    }
}
