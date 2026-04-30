package de.documentgateway.message.routing;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@ConfigurationProperties(prefix = "routing")
public class RoutingProperties {

    private Map<String, RouteConfig> routes = new HashMap<>();

    @Data
    public static class RouteConfig {
        private String receiverId;
        private String targetUrl;
    }
}
