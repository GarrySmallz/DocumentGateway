package de.documentgateway.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;


class AuthenticationServiceTest {

    GatewayApiKeyProperties gatewayApiKeyProperties = new GatewayApiKeyProperties();

    AuthenticationService authenticationService = new AuthenticationService(gatewayApiKeyProperties);
    @Test
    @DisplayName("Authentication should fail with wrong API key")
    void authenticationShouldFailWithFalseKey() {
        //arrange
        String apiKey = "falseKey";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", apiKey);


        assertThrows(BadCredentialsException.class, () -> authenticationService.getAuthentication(request));
    }

    @Test
    @DisplayName("Authentication should fail with missing API key")
    void authenticationShouldFailWithMissingKey() {
        //arrange

        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(BadCredentialsException.class, () -> authenticationService.getAuthentication(request));
    }


    @Test
    @DisplayName("Authentication should succeed with correct API key")
    void shouldSucceedWithCorrectKey() {
        //arrange
        ReflectionTestUtils.setField(authenticationService, "expectedApiKey", "test-key");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", "test-key");


        Authentication auth = authenticationService.getAuthentication(request);

        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals("test-key", auth.getPrincipal());

    }

}