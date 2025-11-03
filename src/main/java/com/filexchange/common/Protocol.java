package com.filexchange.common;

public final class Protocol {
    private Protocol() {}

    public static final String CMD_LIST = "LIST";
    public static final String CMD_DOWNLOAD = "DOWNLOAD";
    public static final String CMD_UPLOAD = "UPLOAD";
    public static final String CMD_EXIT = "QUIT";

    public static final String RESP_OK = "OK";
    public static final String RESP_READY = "READY";
    public static final String RESP_BYE = "BYE";
    public static final String RESP_ERROR = "ERROR";
    public static final String RESP_LIST_PREFIX = "FILES";
    public static final String RESP_SUCCESS = "SUCCESS";
}
