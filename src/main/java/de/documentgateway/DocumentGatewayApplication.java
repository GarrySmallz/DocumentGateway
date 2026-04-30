package de.documentgateway;

import de.documentgateway.message.routing.RoutingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RoutingProperties.class)
@SpringBootApplication
public class DocumentGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentGatewayApplication.class, args);
    }

}
