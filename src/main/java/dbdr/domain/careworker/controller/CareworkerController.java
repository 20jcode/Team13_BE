package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[관리자] 요양보호사 (Careworker)", description = "요양보호사 정보 조회, 수정, 삭제, 추가")
@RestController
@RequestMapping("/${spring.app.version}/careworker")
@RequiredArgsConstructor
public class CareworkerController {

    private final CareworkerService careworkerService;

    @Operation(summary = "특정 요양원아이디로 전체 요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.INSTITUTION_ID, id = "#institutionId")
    public ResponseEntity<List<CareworkerResponseDTO>> getAllCareworkers(
            @RequestParam("institutionId") @NotNull Long institutionId) {
        List<CareworkerResponseDTO> careworkerList = careworkerService.getCareworkersByInstitution(institutionId);
        return ResponseEntity.ok(careworkerList);
    }

    @Operation(summary = "요양보호사 한 사람의 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{careworkerId}")
    @DbdrAuth(targetRole = Role.CAREWORKER,authParam = AuthParam.CAREWORKER_ID, id = "#careworkerId")
    public ResponseEntity<CareworkerResponseDTO> getCareworkerById(
            @PathVariable("careworkerId") Long careworkerId) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerResponseById(careworkerId);
        return ResponseEntity.ok(careworker);
    }

    @Operation(summary = "요양보호사 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/{institutionId}")
    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.INSTITUTION_ID, id = "#institutionId")
    public ResponseEntity<CareworkerResponseDTO> createCareworker(
            @PathVariable Long institutionId,
            @Valid @RequestBody CareworkerRequest careworkerDTO) {
        CareworkerResponseDTO newCareworker = careworkerService.addCareworker(careworkerDTO);
        return ResponseEntity.created(
                        URI.create("/" + institutionId + "/careworker/" + newCareworker.getId()))
                .body(newCareworker);
    }

    @Operation(summary = "요양보호사 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{careworkerId}")
    @DbdrAuth(targetRole = Role.CAREWORKER, authParam = AuthParam.CAREWORKER_ID, id = "#careworkerId")
    public ResponseEntity<CareworkerResponseDTO> updateCareworker(
            @PathVariable Long careworkerId,
            @RequestParam("institutionId") @NotNull Long institutionId,
            @RequestBody CareworkerRequest careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworker(careworkerId, careworkerDTO, institutionId);
        return ResponseEntity.ok(updatedCareworker);
    }

    @Operation(summary = "요양보호사 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{careworkerId}")
    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.CAREWORKER_ID, id = "#careworkerId")
    public ResponseEntity<Void> deleteCareworker(
            @PathVariable Long careworkerId,
            @RequestParam("institutionId") @NotNull Long institutionId) {
        careworkerService.deleteCareworker(careworkerId, institutionId);
        return ResponseEntity.noContent().build();
    }
}