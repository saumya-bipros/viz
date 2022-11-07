package com.vizzionnaire.server.service.security.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.security.authentication.CredentialsExpiredException;

import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.Authority;
import com.vizzionnaire.server.common.data.security.UserCredentials;
import com.vizzionnaire.server.common.data.security.event.UserAuthDataChangedEvent;
import com.vizzionnaire.server.common.data.security.model.JwtToken;
import com.vizzionnaire.server.config.JwtSettings;
import com.vizzionnaire.server.dao.customer.CustomerService;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.service.security.auth.JwtAuthenticationToken;
import com.vizzionnaire.server.service.security.auth.RefreshAuthenticationToken;
import com.vizzionnaire.server.service.security.auth.TokenOutdatingService;
import com.vizzionnaire.server.service.security.auth.jwt.JwtAuthenticationProvider;
import com.vizzionnaire.server.service.security.auth.jwt.RefreshTokenAuthenticationProvider;
import com.vizzionnaire.server.service.security.exception.JwtExpiredTokenException;
import com.vizzionnaire.server.service.security.model.SecurityUser;
import com.vizzionnaire.server.service.security.model.UserPrincipal;
import com.vizzionnaire.server.service.security.model.token.JwtTokenFactory;
import com.vizzionnaire.server.service.security.model.token.RawAccessJwtToken;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenOutdatingTest {
    private JwtAuthenticationProvider accessTokenAuthenticationProvider;
    private RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider;

    private TokenOutdatingService tokenOutdatingService;
    private ConcurrentMapCacheManager cacheManager;
    private JwtTokenFactory tokenFactory;
    private JwtSettings jwtSettings;

    private UserId userId;

    @BeforeEach
    public void setUp() {
        jwtSettings = new JwtSettings();
        jwtSettings.setTokenIssuer("test.io");
        jwtSettings.setTokenExpirationTime((int) MINUTES.toSeconds(10));
        jwtSettings.setRefreshTokenExpTime((int) DAYS.toSeconds(7));
        jwtSettings.setTokenSigningKey("secret");
        tokenFactory = new JwtTokenFactory(jwtSettings);

        cacheManager = new ConcurrentMapCacheManager();
        tokenOutdatingService = new TokenOutdatingService(cacheManager, tokenFactory, jwtSettings);
        tokenOutdatingService.initCache();

        userId = new UserId(UUID.randomUUID());

        UserService userService = mock(UserService.class);

        User user = new User();
        user.setId(userId);
        user.setAuthority(Authority.TENANT_ADMIN);
        user.setEmail("email");
        when(userService.findUserById(any(), eq(userId))).thenReturn(user);

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setEnabled(true);
        when(userService.findUserCredentialsByUserId(any(), eq(userId))).thenReturn(userCredentials);

        accessTokenAuthenticationProvider = new JwtAuthenticationProvider(tokenFactory, tokenOutdatingService);
        refreshTokenAuthenticationProvider = new RefreshTokenAuthenticationProvider(tokenFactory, userService, mock(CustomerService.class), tokenOutdatingService);
    }

    @Test
    public void testOutdateOldUserTokens() throws Exception {
        JwtToken jwtToken = createAccessJwtToken(userId);

        SECONDS.sleep(1); // need to wait before outdating so that outdatage time is strictly after token issue time
        tokenOutdatingService.onUserAuthDataChanged(new UserAuthDataChangedEvent(userId));
        assertTrue(tokenOutdatingService.isOutdated(jwtToken, userId));

        SECONDS.sleep(1);

        JwtToken newJwtToken = tokenFactory.createAccessJwtToken(createMockSecurityUser(userId));
        assertFalse(tokenOutdatingService.isOutdated(newJwtToken, userId));
    }

    @Test
    public void testAuthenticateWithOutdatedAccessToken() throws InterruptedException {
        RawAccessJwtToken accessJwtToken = getRawJwtToken(createAccessJwtToken(userId));

        assertDoesNotThrow(() -> {
            accessTokenAuthenticationProvider.authenticate(new JwtAuthenticationToken(accessJwtToken));
        });

        SECONDS.sleep(1);
        tokenOutdatingService.onUserAuthDataChanged(new UserAuthDataChangedEvent(userId));

        assertThrows(JwtExpiredTokenException.class, () -> {
            accessTokenAuthenticationProvider.authenticate(new JwtAuthenticationToken(accessJwtToken));
        });
    }

    @Test
    public void testAuthenticateWithOutdatedRefreshToken() throws InterruptedException {
        RawAccessJwtToken refreshJwtToken = getRawJwtToken(createRefreshJwtToken(userId));

        assertDoesNotThrow(() -> {
            refreshTokenAuthenticationProvider.authenticate(new RefreshAuthenticationToken(refreshJwtToken));
        });

        SECONDS.sleep(1);
        tokenOutdatingService.onUserAuthDataChanged(new UserAuthDataChangedEvent(userId));

        assertThrows(CredentialsExpiredException.class, () -> {
            refreshTokenAuthenticationProvider.authenticate(new RefreshAuthenticationToken(refreshJwtToken));
        });
    }

    @Test
    public void testTokensOutdatageTimeRemovalFromCache() throws Exception {
        JwtToken jwtToken = createAccessJwtToken(userId);

        SECONDS.sleep(1);
        tokenOutdatingService.onUserAuthDataChanged(new UserAuthDataChangedEvent(userId));

        int refreshTokenExpirationTime = 3;
        jwtSettings.setRefreshTokenExpTime(refreshTokenExpirationTime);

        SECONDS.sleep(refreshTokenExpirationTime - 2);

        assertTrue(tokenOutdatingService.isOutdated(jwtToken, userId));
        assertNotNull(cacheManager.getCache(CacheConstants.USERS_UPDATE_TIME_CACHE).get(userId.getId().toString()));

        SECONDS.sleep(3);

        assertFalse(tokenOutdatingService.isOutdated(jwtToken, userId));
        assertNull(cacheManager.getCache(CacheConstants.USERS_UPDATE_TIME_CACHE).get(userId.getId().toString()));
    }

    private JwtToken createAccessJwtToken(UserId userId) {
        return tokenFactory.createAccessJwtToken(createMockSecurityUser(userId));
    }

    private JwtToken createRefreshJwtToken(UserId userId) {
        return tokenFactory.createRefreshToken(createMockSecurityUser(userId));
    }

    private RawAccessJwtToken getRawJwtToken(JwtToken token) {
        return new RawAccessJwtToken(token.getToken());
    }

    private SecurityUser createMockSecurityUser(UserId userId) {
        SecurityUser securityUser = new SecurityUser();
        securityUser.setEmail("email");
        securityUser.setUserPrincipal(new UserPrincipal(UserPrincipal.Type.USER_NAME, securityUser.getEmail()));
        securityUser.setAuthority(Authority.CUSTOMER_USER);
        securityUser.setId(userId);
        return securityUser;
    }
}
