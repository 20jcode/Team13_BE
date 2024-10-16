package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${spring.app.version}/institution/guardian")
@RequiredArgsConstructor
public class GuardianInstitutionController {

    private final GuardianService guardianService;

    @GetMapping
    public ResponseEntity<List<GuardianResponse>> showAllGuardian() {
        List<GuardianResponse> guardianResponseList = guardianService.getAllGuardian();
        return ResponseEntity.ok(guardianResponseList);
    }
    @DbdrAuth(targetRole = Role.INSTITUTION,type= AuthParam.GUARDIAN_ID,id="#guardianId")
    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> showOneGuardian(
        @PathVariable("guardianId") Long guardianId) {
        GuardianResponse guardianResponse = guardianService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardianResponse);
    }

    @PostMapping
    public ResponseEntity<GuardianResponse> addGuardian(
        @Valid @RequestBody GuardianRequest guardianRequest) {
        GuardianResponse guardianResponse = guardianService.addGuardian(guardianRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardianResponse);
    }

    @PutMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> updateGuardian(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardianRequest guardianRequest) {
        GuardianResponse guardianResponse = guardianService.updateGuardianById(guardianId,
            guardianRequest);
        return ResponseEntity.ok(guardianResponse);
    }

    @DeleteMapping("/{guardianId}")
    public ResponseEntity<Void> deleteGuardian(@PathVariable("guardianId") Long guardianId) {
        guardianService.deleteGuardianById(guardianId);
        return ResponseEntity.noContent().build();
    }
}
