package com.vizzionnaire.server.exception;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpStatus;

import com.vizzionnaire.server.common.data.exception.VizzionnaireErrorCode;

@ApiModel
public class VizzionnaireCredentialsExpiredResponse extends VizzionnaireErrorResponse {

    private final String resetToken;

    protected VizzionnaireCredentialsExpiredResponse(String message, String resetToken) {
        super(message, VizzionnaireErrorCode.CREDENTIALS_EXPIRED, HttpStatus.UNAUTHORIZED);
        this.resetToken = resetToken;
    }

    public static VizzionnaireCredentialsExpiredResponse of(final String message, final String resetToken) {
        return new VizzionnaireCredentialsExpiredResponse(message, resetToken);
    }

    @ApiModelProperty(position = 5, value = "Password reset token", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public String getResetToken() {
        return resetToken;
    }
}
