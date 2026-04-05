package io.fusion.air.microservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prototype unit test for JsonWebToken after Java 21 / Spring Boot 3.2 / JJWT 0.12.x upgrade.
 * Validates token generation, parsing, claims extraction, and expiry using the secret-key path.
 */
class JsonWebTokenTest {

    private JsonWebToken jwt;
    private final String subject = "test.user";
    private final String issuer = "metarivu";

    @BeforeEach
    void setUp() {
        jwt = new JsonWebToken();
        jwt.init(JsonWebToken.SECRET_KEY);
        jwt.setSubject(subject);
        jwt.setIssuer(issuer);
    }

    @Test
    @DisplayName("Generate and validate a token with secret key")
    void generateAndValidateToken() {
        String token = jwt.generateToken(subject, JsonWebToken.EXPIRE_IN_FIVE_MINS);

        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        // JWT format: header.payload.signature
        assertEquals(3, token.split("\\.").length, "Token should have 3 parts (header.payload.signature)");
    }

    @Test
    @DisplayName("Extract subject from token")
    void extractSubjectFromToken() {
        String token = jwt.generateToken(subject, JsonWebToken.EXPIRE_IN_FIVE_MINS);

        String extracted = jwt.getSubjectFromToken(token);
        assertEquals(subject, extracted, "Subject should match");
    }

    @Test
    @DisplayName("Extract issuer from token")
    void extractIssuerFromToken() {
        String token = jwt.generateToken(subject, JsonWebToken.EXPIRE_IN_FIVE_MINS);

        String extracted = jwt.getIssuerFromToken(token);
        assertEquals(issuer, extracted, "Issuer should match");
    }

    @Test
    @DisplayName("Token should not be expired when just created")
    void tokenNotExpiredWhenFresh() {
        String token = jwt.generateToken(subject, JsonWebToken.EXPIRE_IN_FIVE_MINS);

        assertFalse(jwt.isTokenExpired(token), "Fresh token should not be expired");
    }

    @Test
    @DisplayName("Validate token with correct user returns true")
    void validateTokenWithCorrectUser() {
        String token = jwt.generateToken(subject, JsonWebToken.EXPIRE_IN_FIVE_MINS);

        assertTrue(jwt.validateToken(subject, token), "Token should be valid for the correct user");
    }

    @Test
    @DisplayName("Validate token with wrong user returns false")
    void validateTokenWithWrongUser() {
        String token = jwt.generateToken(subject, JsonWebToken.EXPIRE_IN_FIVE_MINS);

        assertFalse(jwt.validateToken("wrong.user", token), "Token should be invalid for wrong user");
    }

    @Test
    @DisplayName("Generate auth and refresh token pair")
    void generateTokenPair() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", "test-service");
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("rol", "Admin");

        HashMap<String, String> tokens = jwt
                .setTokenAuthExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
                .setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
                .addAllTokenClaims(claims)
                .addAllRefreshTokenClaims(claims)
                .generateTokens();

        assertNotNull(tokens.get("token"), "Auth token should not be null");
        assertNotNull(tokens.get("refresh"), "Refresh token should not be null");
        assertNotEquals(tokens.get("token"), tokens.get("refresh"), "Auth and refresh tokens should differ");
    }

    @Test
    @DisplayName("Extract custom claims from token")
    void extractCustomClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "Admin");
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("aud", "test-service");

        jwt.addAllTokenClaims(claims);
        HashMap<String, String> tokens = jwt.generateTokens();
        String token = tokens.get("token");

        String role = jwt.getUserRoleFromToken(token);
        assertEquals("Admin", role, "Role claim should be 'Admin'");
    }

    @Test
    @DisplayName("getJws returns valid signed claims object")
    void getJwsReturnsSignedClaims() {
        String token = jwt.generateToken(subject, JsonWebToken.EXPIRE_IN_FIVE_MINS);

        Jws<Claims> jws = jwt.getJws(token);
        assertNotNull(jws, "Jws should not be null");
        assertNotNull(jws.getPayload(), "Payload should not be null");
        assertNotNull(jws.getHeader(), "Header should not be null");
    }

    @Test
    @DisplayName("getPayload returns JSON string")
    void getPayloadReturnsJson() {
        String token = jwt.generateToken(subject, JsonWebToken.EXPIRE_IN_FIVE_MINS);

        String payload = jwt.getPayload(token);
        assertNotNull(payload, "Payload JSON should not be null");
        assertTrue(payload.startsWith("{"), "Payload should start with {");
        assertTrue(payload.endsWith("}"), "Payload should end with }");
    }

    @Test
    @DisplayName("printExpiryTime formats correctly")
    void printExpiryTimeFormats() {
        String result = JsonWebToken.printExpiryTime(JsonWebToken.EXPIRE_IN_FIVE_MINS);
        assertEquals("00:00:05", result, "5 minutes should format as 00:00:05");

        result = JsonWebToken.printExpiryTime(JsonWebToken.EXPIRE_IN_ONE_HOUR);
        assertEquals("00:01:00", result, "1 hour should format as 00:01:00");

        result = JsonWebToken.printExpiryTime(JsonWebToken.EXPIRE_IN_ONE_DAY);
        assertEquals("01:00:00", result, "1 day should format as 01:00:00");
    }
}
