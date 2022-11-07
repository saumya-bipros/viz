package com.vizzionnaire.server.install;

import org.springframework.boot.ExitCodeGenerator;

public class VizzionnaireInstallException extends RuntimeException implements ExitCodeGenerator {

    public VizzionnaireInstallException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getExitCode() {
        return 1;
    }

}