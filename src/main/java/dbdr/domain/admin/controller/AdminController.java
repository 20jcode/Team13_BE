package dbdr.domain.admin.controller;

import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dbdr.domain.admin.service.AdminService;

@Tag(name = "[관리자] 서버관리자 (Admin)", description = "서버관리자 정보 조회, 수정")
@RestController
@RequestMapping("/${spring.app.version}/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "서버관리자 정보 조회",security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    @DbdrAuth(targetRole = Role.ADMIN, authParam = AuthParam.ADMIN_ID)
    public ResponseEntity<List<String>> getAdminList(){
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @Operation(summary = "특정 서버관리자 정보 조회",security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{id}")
    @DbdrAuth(targetRole = Role.ADMIN, authParam = AuthParam.ADMIN_ID,id = "#id")
    public ResponseEntity<String> getAdmin(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(adminService.getAdminById(id));
    }
}
