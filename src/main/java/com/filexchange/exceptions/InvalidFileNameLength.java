package com.filexchange.exceptions;

public class InvalidFileNameLength extends RuntimeException {
    public InvalidFileNameLength() {
        super("File name length is invalid.");
    }
}
