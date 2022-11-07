package com.vizzionnaire.server.common.data.exception;

public class VizzionnaireException extends Exception {

    private static final long serialVersionUID = 1L;

    private VizzionnaireErrorCode errorCode;

    public VizzionnaireException() {
        super();
    }

    public VizzionnaireException(VizzionnaireErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public VizzionnaireException(String message, VizzionnaireErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public VizzionnaireException(String message, Throwable cause, VizzionnaireErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public VizzionnaireException(Throwable cause, VizzionnaireErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public VizzionnaireErrorCode getErrorCode() {
        return errorCode;
    }

}
