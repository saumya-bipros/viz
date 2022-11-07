package com.vizzionnaire.server.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.server.common.data.exception.VizzionnaireErrorCode;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.msg.tools.TbRateLimitsException;
import com.vizzionnaire.server.service.security.exception.AuthMethodNotSupportedException;
import com.vizzionnaire.server.service.security.exception.JwtExpiredTokenException;
import com.vizzionnaire.server.service.security.exception.UserPasswordExpiredException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class VizzionnaireErrorResponseHandler extends ResponseEntityExceptionHandler implements AccessDeniedHandler {

    private static final Map<HttpStatus, VizzionnaireErrorCode> statusToErrorCodeMap = new HashMap<>();
    static {
        statusToErrorCodeMap.put(HttpStatus.BAD_REQUEST, VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
        statusToErrorCodeMap.put(HttpStatus.UNAUTHORIZED, VizzionnaireErrorCode.AUTHENTICATION);
        statusToErrorCodeMap.put(HttpStatus.FORBIDDEN, VizzionnaireErrorCode.PERMISSION_DENIED);
        statusToErrorCodeMap.put(HttpStatus.NOT_FOUND, VizzionnaireErrorCode.ITEM_NOT_FOUND);
        statusToErrorCodeMap.put(HttpStatus.METHOD_NOT_ALLOWED, VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
        statusToErrorCodeMap.put(HttpStatus.NOT_ACCEPTABLE, VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
        statusToErrorCodeMap.put(HttpStatus.UNSUPPORTED_MEDIA_TYPE, VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
        statusToErrorCodeMap.put(HttpStatus.TOO_MANY_REQUESTS, VizzionnaireErrorCode.TOO_MANY_REQUESTS);
        statusToErrorCodeMap.put(HttpStatus.INTERNAL_SERVER_ERROR, VizzionnaireErrorCode.GENERAL);
        statusToErrorCodeMap.put(HttpStatus.SERVICE_UNAVAILABLE, VizzionnaireErrorCode.GENERAL);
    }
    private static final Map<VizzionnaireErrorCode, HttpStatus> errorCodeToStatusMap = new HashMap<>();
    static {
        errorCodeToStatusMap.put(VizzionnaireErrorCode.GENERAL, HttpStatus.INTERNAL_SERVER_ERROR);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.CREDENTIALS_EXPIRED, HttpStatus.UNAUTHORIZED);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.INVALID_ARGUMENTS, HttpStatus.BAD_REQUEST);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.BAD_REQUEST_PARAMS, HttpStatus.BAD_REQUEST);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.TOO_MANY_UPDATES, HttpStatus.TOO_MANY_REQUESTS);
        errorCodeToStatusMap.put(VizzionnaireErrorCode.SUBSCRIPTION_VIOLATION, HttpStatus.FORBIDDEN);
    }

    private static VizzionnaireErrorCode statusToErrorCode(HttpStatus status) {
        return statusToErrorCodeMap.getOrDefault(status, VizzionnaireErrorCode.GENERAL);
    }

    private static HttpStatus errorCodeToStatus(VizzionnaireErrorCode errorCode) {
        return errorCodeToStatusMap.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Autowired
    private ObjectMapper mapper;

    @Override
    @ExceptionHandler(AccessDeniedException.class)
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException,
            ServletException {
        if (!response.isCommitted()) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            mapper.writeValue(response.getWriter(),
                    VizzionnaireErrorResponse.of("You don't have permission to perform this operation!",
                            VizzionnaireErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN));
        }
    }

    @ExceptionHandler(Exception.class)
    public void handle(Exception exception, HttpServletResponse response) {
        log.debug("Processing exception {}", exception.getMessage(), exception);
        if (!response.isCommitted()) {
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                if (exception instanceof VizzionnaireException) {
                    VizzionnaireException vizzionnaireException = (VizzionnaireException) exception;
                    if (vizzionnaireException.getErrorCode() == VizzionnaireErrorCode.SUBSCRIPTION_VIOLATION) {
                        handleSubscriptionException((VizzionnaireException) exception, response);
                    } else {
                        handleVizzionnaireException((VizzionnaireException) exception, response);
                    }
                } else if (exception instanceof TbRateLimitsException) {
                    handleRateLimitException(response, (TbRateLimitsException) exception);
                } else if (exception instanceof AccessDeniedException) {
                    handleAccessDeniedException(response);
                } else if (exception instanceof AuthenticationException) {
                    handleAuthenticationException((AuthenticationException) exception, response);
                } else {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    mapper.writeValue(response.getWriter(), VizzionnaireErrorResponse.of(exception.getMessage(),
                            VizzionnaireErrorCode.GENERAL, HttpStatus.INTERNAL_SERVER_ERROR));
                }
            } catch (IOException e) {
                log.error("Can't handle exception", e);
            }
        }
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body,
            HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        VizzionnaireErrorCode errorCode = statusToErrorCode(status);
        return new ResponseEntity<>(VizzionnaireErrorResponse.of(ex.getMessage(), errorCode, status), headers, status);
    }

    private void handleVizzionnaireException(VizzionnaireException vizzionnaireException, HttpServletResponse response) throws IOException {
        VizzionnaireErrorCode errorCode = vizzionnaireException.getErrorCode();
        HttpStatus status = errorCodeToStatus(errorCode);
        response.setStatus(status.value());
        mapper.writeValue(response.getWriter(), VizzionnaireErrorResponse.of(vizzionnaireException.getMessage(), errorCode, status));
    }

    private void handleRateLimitException(HttpServletResponse response, TbRateLimitsException exception) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        String message = "Too many requests for current " + exception.getEntityType().name().toLowerCase() + "!";
        mapper.writeValue(response.getWriter(),
                VizzionnaireErrorResponse.of(message,
                        VizzionnaireErrorCode.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS));
    }

    private void handleSubscriptionException(VizzionnaireException subscriptionException, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        mapper.writeValue(response.getWriter(),
                (new ObjectMapper()).readValue(((HttpClientErrorException) subscriptionException.getCause()).getResponseBodyAsByteArray(), Object.class));
    }

    private void handleAccessDeniedException(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        mapper.writeValue(response.getWriter(),
                VizzionnaireErrorResponse.of("You don't have permission to perform this operation!",
                        VizzionnaireErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN));

    }

    private void handleAuthenticationException(AuthenticationException authenticationException, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        if (authenticationException instanceof BadCredentialsException || authenticationException instanceof UsernameNotFoundException) {
            mapper.writeValue(response.getWriter(), VizzionnaireErrorResponse.of("Invalid username or password", VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof DisabledException) {
            mapper.writeValue(response.getWriter(), VizzionnaireErrorResponse.of("User account is not active", VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof LockedException) {
            mapper.writeValue(response.getWriter(), VizzionnaireErrorResponse.of("User account is locked due to security policy", VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof JwtExpiredTokenException) {
            mapper.writeValue(response.getWriter(), VizzionnaireErrorResponse.of("Token has expired", VizzionnaireErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof AuthMethodNotSupportedException) {
            mapper.writeValue(response.getWriter(), VizzionnaireErrorResponse.of(authenticationException.getMessage(), VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof UserPasswordExpiredException) {
            UserPasswordExpiredException expiredException = (UserPasswordExpiredException) authenticationException;
            String resetToken = expiredException.getResetToken();
            mapper.writeValue(response.getWriter(), VizzionnaireCredentialsExpiredResponse.of(expiredException.getMessage(), resetToken));
        } else {
            mapper.writeValue(response.getWriter(), VizzionnaireErrorResponse.of("Authentication failed", VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        }
    }

}
