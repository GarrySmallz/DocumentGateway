package de.documentgateway.security;

import de.documentgateway.audit.AuditEventType;
import de.documentgateway.audit.AuditOutcome;
import de.documentgateway.audit.AuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    private static final String CORRELATION_ID = "7b8f0d6f-8d90-4f7a-8bf3-3bc4d8b0f6d9";
    private static final String PARTNER_ID = "partner-a";

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private AuditService auditService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    @Test
    @DisplayName("should log rejected authentication when bad credentials are provided")
    void shouldLogRejectedAuth_whenBadCredentials() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Partner-Id", PARTNER_ID);
        request.addHeader("X-Correlation-Id", CORRELATION_ID);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authenticationService
                .getAuthentication(request))
                .thenThrow(new BadCredentialsException("Invalid API Key"));

        authenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        verify(auditService).logFailure(
                CORRELATION_ID,
                PARTNER_ID,
                AuditEventType.REJECTED_AUTH,
                AuditOutcome.FAILURE,
                "Invalid API Key"
        );
        verify(filterChain, never()).doFilter(any(), any());

    }
}
