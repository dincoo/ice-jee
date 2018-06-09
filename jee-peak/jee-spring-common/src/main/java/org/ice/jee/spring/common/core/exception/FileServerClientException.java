package org.ice.jee.spring.common.core.exception;

public class FileServerClientException extends RuntimeException {
    private static final long serialVersionUID = -4231866871430421954L;
    private String errorCode;
    private String errorMsg;

    public FileServerClientException(String message) {
        super(message);
    }

    public FileServerClientException(Throwable cause) {
        super(cause);
    }

    public FileServerClientException(String errorCode, String errorMsg, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public FileServerClientException(String errorCode, String message) {
        this(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }
}
