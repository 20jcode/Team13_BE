package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.security.LoginGuardian;
import dbdr.security.dto.BaseUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${spring.app.version}/guardian")
@RequiredArgsConstructor
@Slf4j
public class GuardianController {

    private final GuardianService guardianService;

    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> showGuardianInfo(
        @PathVariable("guardianId") Long guardianId, @LoginGuardian Guardian gaurdian) {
        log.info("guardianId: {}", gaurdian.getName());
        GuardianResponse guardianResponse = guardianService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardianResponse);
    }

    @PutMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> updateGuardianInfo(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardianRequest guardianRequest) {
        GuardianResponse guardianResponse = guardianService.updateGuardianById(guardianId,
            guardianRequest);
        return ResponseEntity.ok(guardianResponse);
    }
}
