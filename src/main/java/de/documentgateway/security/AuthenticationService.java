package de.documentgateway.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    private final GatewayApiKeyProperties gatewayApiKeyProperties;


    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        String partnerId = request.getHeader("X-Partner-Id");


        if (partnerId == null || partnerId.isBlank()) {
            throw new BadCredentialsException("Invalid credentials");
        }
        if (apiKey == null || apiKey.isBlank() || !gatewayApiKeyProperties.getApiKeys().containsKey(partnerId)) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String expectedApiKey = gatewayApiKeyProperties.getApiKeys().get(partnerId);
        if (!MessageDigest.isEqual(expectedApiKey.getBytes(), apiKey.getBytes())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
