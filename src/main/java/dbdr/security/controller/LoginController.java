package dbdr.security.controller;

import dbdr.exception.ApplicationError;
import dbdr.exception.ApplicationException;
import dbdr.security.Role;
import dbdr.security.dto.LoginRequest;
import dbdr.security.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${spring.app.version}/login")
public class LoginController {

    private final LoginService loginService;

    private final String authHeader;

    public LoginController(LoginService loginService, @Value("/${spring.jwt.authheader}") String authHeader) {
        this.loginService = loginService;
        this.authHeader = authHeader;
    }

    //path 변수로 role을 입력받아서 하나의 컨트롤러에서 처리하도록 하는 방법은?
    @PostMapping("/{role}")
    public ResponseEntity<Void> login(@PathVariable("role") String role, @RequestBody LoginRequest loginRequest) {

        Role roleEnum = roleCheck(role);

        String token = loginService.login(roleEnum, loginRequest);

        return ResponseEntity.ok().header(authHeader, token).build();


    }

    private Role roleCheck(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
        }
    }

}
