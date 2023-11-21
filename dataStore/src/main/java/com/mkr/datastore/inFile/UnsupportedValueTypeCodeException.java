package com.mkr.datastore.inFile;

public class UnsupportedValueTypeCodeException extends RuntimeException {
    public UnsupportedValueTypeCodeException() {}

    public UnsupportedValueTypeCodeException(String message) {
        super(message);
    }
}
