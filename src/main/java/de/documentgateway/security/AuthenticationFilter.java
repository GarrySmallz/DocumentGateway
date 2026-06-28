package de.documentgateway.security;

import de.documentgateway.audit.AuditEventType;
import de.documentgateway.audit.AuditOutcome;
import de.documentgateway.audit.AuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    private final AuditService auditService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = authenticationService.getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String correlationID = resolveCorrelationId(request);
            auditService.logFailure(correlationID,
                    request.getHeader("X-Partner-Id"),
                    AuditEventType.REJECTED_AUTH,
                    AuditOutcome.FAILURE,
                    e.getMessage());
            PrintWriter writer = response.getWriter();
            writer.print(e.getMessage());
            writer.flush();
            writer.close();
        }
    }

    private String resolveCorrelationId(HttpServletRequest request){
        String header = request.getHeader("X-Correlation-Id");
        if (header == null || header.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return header.trim();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.equals("/openapi.yaml") || path.equals("/actuator/health");
    }
}
