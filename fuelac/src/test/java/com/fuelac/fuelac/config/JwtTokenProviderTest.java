package com.fuelac.fuelac.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        org.springframework.test.util.ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "testSecretKeyForJwtTokenProviderTestingOnly12345");
        org.springframework.test.util.ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 86400000L);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@fuelac.ru", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@fuelac.ru", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        String token = jwtTokenProvider.generateToken(authentication);

        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("test@fuelac.ru", username);
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@fuelac.ru", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        String token = jwtTokenProvider.generateToken(authentication);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidToken() {
        boolean isValid = jwtTokenProvider.validateToken("invalid.token.here");

        assertFalse(isValid);
    }
}
