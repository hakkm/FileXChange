package com.filexchange.exceptions;

public class FileSizeExceedsLimit extends RuntimeException {
    public FileSizeExceedsLimit() {
        super("File size exceeds the allowed limit, or the size is a negative value.");
    }
}
