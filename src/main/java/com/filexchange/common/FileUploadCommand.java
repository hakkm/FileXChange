package com.filexchange.common;

public class FileUploadCommand {
    private final String filename;
    private final long filesize;

    public FileUploadCommand(String filename, long filesize) {
        this.filename = filename;
        this.filesize = filesize;
    }

    public String getFilename() {
        return filename;
    }

    public long getFilesize() {
        return filesize;
    }

    @Override
    public String toString() {
        return "FileUploadCommand{" +
                "filename='" + filename + '\'' +
                ", filesize=" + filesize +
                '}';
    }
}
