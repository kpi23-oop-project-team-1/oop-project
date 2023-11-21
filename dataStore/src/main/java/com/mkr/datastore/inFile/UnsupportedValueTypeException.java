package com.mkr.datastore.inFile;

public class UnsupportedValueTypeException extends RuntimeException {
    public UnsupportedValueTypeException() {}

    public UnsupportedValueTypeException(String message) {
        super(message);
    }
}