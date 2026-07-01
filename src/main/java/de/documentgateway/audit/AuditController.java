package de.documentgateway.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditEventRepository auditEventRepository;

    @GetMapping
    public ResponseEntity<List<AuditEventDto>> getAuditEvents() {
        return ResponseEntity.ok(
                auditEventRepository.findAll()
                        .stream()
                        .map(AuditEventDto::from)
                        .toList()
        );
    }

}
