package com.filexchange.server;

import com.filexchange.common.FileUploadCommand;
import com.filexchange.common.Protocol;
import com.filexchange.exceptions.FileSizeExceedsLimit;
import com.filexchange.exceptions.InvalidAttributes;
import com.filexchange.exceptions.InvalidFileNameLength;
import com.filexchange.exceptions.InvalidFileSize;

import java.util.logging.Logger;

public class CommandParser {
    static final public int MAX_FILENAME_LENGTH = 255;
    static final public int MAX_FILESIZE = 10 * 1024 * 1024; // 10 MB
    private static final Logger logger = Logger.getLogger(CommandParser.class.getName());

    public FileUploadCommand parseUpdate(String[] parts) throws FileSizeExceedsLimit, InvalidAttributes, InvalidFileSize, InvalidFileNameLength {
        // parse input: UPLOAD <filename> <filesize>
        if (parts.length < 3) {
            String response = Protocol.RESP_ERROR + " Missing filename or filesize";
            logger.info(response);
            throw new InvalidAttributes();
        }

        String filesizeStr = parts[1].trim();
        long filesize;
        try {
            filesize = Long.parseLong(filesizeStr);
        } catch (NumberFormatException e) {
            String response = Protocol.RESP_ERROR + " Invalid filesize";
            logger.info(response);
            throw new InvalidFileSize();
        }

        // check for file size
        if (filesize < 0 || filesize > MAX_FILESIZE) {
            String response = Protocol.RESP_ERROR + " Filesize exceeds limit";
            logger.info(response);
            throw new FileSizeExceedsLimit();
        }

        // filename is the concat from 2 to end
        String filename = String.join(" ", java.util.Arrays.copyOfRange(parts, 2, parts.length)).trim();

        // check for filename length
        if (filename.isEmpty() || filename.length() > MAX_FILENAME_LENGTH) {
            String response = Protocol.RESP_ERROR + " Invalid filename length";
            logger.info(response);
            throw new InvalidFileNameLength();
        }
        return new FileUploadCommand(filename, filesize);
    }
}
