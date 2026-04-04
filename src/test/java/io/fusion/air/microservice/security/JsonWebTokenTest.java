/**
 * (C) Copyright 2024 Araf Karsh Hamid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JsonWebToken - validates JJWT 0.12.5 API
 * with Java 21 and Spring Boot 3.2.4 upgrade.
 */
class JsonWebTokenTest {

    private JsonWebToken jwt;
    private String subject;
    private String issuer;
    private Map<String, Object> claims;

    @BeforeEach
    void setUp() {
        jwt = new JsonWebToken();
        subject = "test.user";
        issuer = "test-issuer.com";

        claims = new HashMap<>();
        claims.put("aud", "test-service");
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("rol", "Admin");
        claims.put("iss", issuer);
        claims.put("sub", subject);
    }

    @Test
    @DisplayName("Should generate auth and refresh tokens using secret key")
    void shouldGenerateTokensWithSecretKey() {
        HashMap<String, String> tokens = jwt
                .init(JsonWebToken.SECRET_KEY)
                .setSubject(subject)
                .setIssuer(issuer)
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        assertNotNull(tokens, "Tokens map should not be null");
        assertNotNull(tokens.get("token"), "Auth token should not be null");
        assertNotNull(tokens.get("refresh"), "Refresh token should not be null");
        assertNotEquals(tokens.get("token"), tokens.get("refresh"),
                "Auth and refresh tokens should be different");
    }

    @Test
    @DisplayName("Should extract subject from token")
    void shouldExtractSubjectFromToken() {
        HashMap<String, String> tokens = jwt
                .init(JsonWebToken.SECRET_KEY)
                .setSubject(subject)
                .setIssuer(issuer)
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        String extractedSubject = jwt.getSubjectFromToken(tokens.get("token"));
        assertEquals(subject, extractedSubject);
    }

    @Test
    @DisplayName("Should extract issuer from token")
    void shouldExtractIssuerFromToken() {
        HashMap<String, String> tokens = jwt
                .init(JsonWebToken.SECRET_KEY)
                .setSubject(subject)
                .setIssuer(issuer)
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        String extractedIssuer = jwt.getIssuerFromToken(tokens.get("token"));
        assertEquals(issuer, extractedIssuer);
    }

    @Test
    @DisplayName("Should extract audience from token (Set<String> API)")
    void shouldExtractAudienceFromToken() {
        HashMap<String, String> tokens = jwt
                .init(JsonWebToken.SECRET_KEY)
                .setSubject(subject)
                .setIssuer(issuer)
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        String audience = jwt.getAudienceFromToken(tokens.get("token"));
        assertEquals("test-service", audience);
    }

    @Test
    @DisplayName("Should extract custom role claim from token")
    void shouldExtractRoleFromToken() {
        HashMap<String, String> tokens = jwt
                .init(JsonWebToken.SECRET_KEY)
                .setSubject(subject)
                .setIssuer(issuer)
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        String role = jwt.getUserRoleFromToken(tokens.get("token"));
        assertEquals("Admin", role);
    }

    @Test
    @DisplayName("Should validate token is not expired")
    void shouldValidateTokenNotExpired() {
        HashMap<String, String> tokens = jwt
                .init(JsonWebToken.SECRET_KEY)
                .setSubject(subject)
                .setIssuer(issuer)
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        assertFalse(jwt.isTokenExpired(tokens.get("token")),
                "Freshly generated token should not be expired");
    }

    @Test
    @DisplayName("Should validate token with correct subject")
    void shouldValidateTokenWithCorrectSubject() {
        HashMap<String, String> tokens = jwt
                .init(JsonWebToken.SECRET_KEY)
                .setSubject(subject)
                .setIssuer(issuer)
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        assertTrue(jwt.validateToken(subject, tokens.get("token")),
                "Token should be valid for the correct subject");
    }

    @Test
    @DisplayName("Should reject token with wrong subject")
    void shouldRejectTokenWithWrongSubject() {
        HashMap<String, String> tokens = jwt
                .init(JsonWebToken.SECRET_KEY)
                .setSubject(subject)
                .setIssuer(issuer)
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        assertFalse(jwt.validateToken("wrong.user", tokens.get("token")),
                "Token should be invalid for a different subject");
    }

    @Test
    @DisplayName("Should get all claims from token via new JJWT 0.12.5 API")
    void shouldGetAllClaimsFromToken() {
        HashMap<String, String> tokens = jwt
                .init(JsonWebToken.SECRET_KEY)
                .setSubject(subject)
                .setIssuer(issuer)
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        Claims allClaims = jwt.getAllClaims(tokens.get("token"));
        assertNotNull(allClaims);
        assertEquals(subject, allClaims.getSubject());
        assertEquals(issuer, allClaims.getIssuer());
        assertNotNull(allClaims.getIssuedAt());
        assertNotNull(allClaims.getExpiration());
    }

    @Test
    @DisplayName("Should format expiry time correctly")
    void shouldFormatExpiryTime() {
        assertEquals("00:00:05", JsonWebToken.printExpiryTime(JsonWebToken.EXPIRE_IN_FIVE_MINS));
        assertEquals("00:00:30", JsonWebToken.printExpiryTime(JsonWebToken.EXPIRE_IN_THIRTY_MINS));
        assertEquals("00:01:00", JsonWebToken.printExpiryTime(JsonWebToken.EXPIRE_IN_ONE_HOUR));
        assertEquals("01:00:00", JsonWebToken.printExpiryTime(JsonWebToken.EXPIRE_IN_ONE_DAY));
    }

    @Test
    @DisplayName("Should use HS512 algorithm for secret key type")
    void shouldUseHS512ForSecretKey() {
        jwt.init(JsonWebToken.SECRET_KEY);
        assertNotNull(jwt.getAlgorithm());
    }
}
