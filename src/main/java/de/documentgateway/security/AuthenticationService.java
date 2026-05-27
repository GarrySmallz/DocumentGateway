package de.documentgateway.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    private final GatewayApiKeyProperties gatewayApiKeyProperties;


    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        String partnerId = request.getHeader("X-Partner-Id");
        if (apiKey == null) {
            throw new BadCredentialsException("Invalid API Key");
        } else if (!gatewayApiKeyProperties.getApiKeys().containsKey(partnerId)) {
            throw new BadCredentialsException("Invalid Partner Id");
        } else if (!gatewayApiKeyProperties.getApiKeys().get(partnerId).equals(apiKey)) {
            throw new BadCredentialsException("Invalid API Key for Partner Id");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
