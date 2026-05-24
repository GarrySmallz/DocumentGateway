package de.documentgateway.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "gateway.api.keys")
public class GatewayApiKeyProperties {


    private Map<String, String> apiKeys = new HashMap<>();
    public Map<String, String> getApiKeys() {
        return apiKeys;
    }
}
