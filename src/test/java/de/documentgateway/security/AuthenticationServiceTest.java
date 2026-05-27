package de.documentgateway.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class AuthenticationServiceTest {



    @Test
    @DisplayName("Authentication should fail with wrong API key")
    void authenticationShouldFailWithFalseKey() {

        //arrange

        GatewayApiKeyProperties gatewayApiKeyProperties = new GatewayApiKeyProperties();
        gatewayApiKeyProperties.setApiKeys(Map.of("partner-a", "test-key"));
        AuthenticationService authenticationService = new AuthenticationService(gatewayApiKeyProperties);
        String apiKey = "falseKey";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", apiKey);
        request.addHeader("X-Partner-Id", "partner-a");


        assertThrows(BadCredentialsException.class, () -> authenticationService.getAuthentication(request));
    }

    @Test
    @DisplayName("Authentication should fail with missing API key")
    void authenticationShouldFailWithMissingKey() {
        //arrange
        GatewayApiKeyProperties gatewayApiKeyProperties = new GatewayApiKeyProperties();
        gatewayApiKeyProperties.setApiKeys(Map.of("partner-a", "test-key"));
        AuthenticationService authenticationService = new AuthenticationService(gatewayApiKeyProperties);

        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(BadCredentialsException.class, () -> authenticationService.getAuthentication(request));
    }


    @Test
    @DisplayName("Authentication should succeed with correct API key")
    void shouldSucceedWithCorrectKey() {
        GatewayApiKeyProperties gatewayApiKeyProperties = new GatewayApiKeyProperties();
        gatewayApiKeyProperties.setApiKeys(Map.of("partner-a", "test-key"));
        AuthenticationService authenticationService = new AuthenticationService(gatewayApiKeyProperties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", "test-key");
        request.addHeader("X-Partner-Id", "partner-a");


        Authentication auth = authenticationService.getAuthentication(request);

        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals("test-key", auth.getPrincipal());

    }

}