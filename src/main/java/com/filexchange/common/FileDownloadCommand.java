package com.filexchange.common;

import java.io.File;

public class FileDownloadCommand {
    private int filesize;
    private String filename;
    private File filepath;

    public FileDownloadCommand(String filename, File filepath, int filesize) {
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public File getFilepath() {
        return filepath;
    }

    public void setFilepath(File filepath) {
        this.filepath = filepath;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }
}
