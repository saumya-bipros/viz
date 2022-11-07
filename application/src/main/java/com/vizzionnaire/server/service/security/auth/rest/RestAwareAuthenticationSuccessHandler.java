package com.vizzionnaire.server.service.security.auth.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.server.common.data.security.Authority;
import com.vizzionnaire.server.service.security.auth.MfaAuthenticationToken;
import com.vizzionnaire.server.service.security.auth.jwt.RefreshTokenRepository;
import com.vizzionnaire.server.service.security.auth.mfa.config.TwoFaConfigManager;
import com.vizzionnaire.server.service.security.model.JwtTokenPair;
import com.vizzionnaire.server.service.security.model.SecurityUser;
import com.vizzionnaire.server.service.security.model.token.JwtTokenFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component(value = "defaultAuthenticationSuccessHandler")
@RequiredArgsConstructor
public class RestAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper mapper;
    private final JwtTokenFactory tokenFactory;
    private final TwoFaConfigManager twoFaConfigManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        JwtTokenPair tokenPair = new JwtTokenPair();

        if (authentication instanceof MfaAuthenticationToken) {
            int preVerificationTokenLifetime = twoFaConfigManager.getPlatformTwoFaSettings(securityUser.getTenantId(), true)
                    .flatMap(settings -> Optional.ofNullable(settings.getTotalAllowedTimeForVerification())
                            .filter(time -> time > 0))
                    .orElse((int) TimeUnit.MINUTES.toSeconds(30));
            tokenPair.setToken(tokenFactory.createPreVerificationToken(securityUser, preVerificationTokenLifetime).getToken());
            tokenPair.setRefreshToken(null);
            tokenPair.setScope(Authority.PRE_VERIFICATION_TOKEN);
        } else {
            tokenPair.setToken(tokenFactory.createAccessJwtToken(securityUser).getToken());
            tokenPair.setRefreshToken(refreshTokenRepository.requestRefreshToken(securityUser).getToken());
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), tokenPair);

        clearAuthenticationAttributes(request);
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process..
     *
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
