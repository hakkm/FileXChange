package com.filexchange.exceptions;

public class InvalidAttributes extends RuntimeException {
    public InvalidAttributes() {
        super("Invalid filename or file size.");
    }
}
