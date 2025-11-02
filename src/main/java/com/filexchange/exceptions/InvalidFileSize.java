package com.filexchange.exceptions;

public class InvalidFileSize extends RuntimeException {
    public InvalidFileSize() {
        super("Invalid file size.");
    }
}
