package dbdr.domain.admin.service;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.repository.AdminRepository;
import dbdr.global.exception.ApplicationException;
import dbdr.global.exception.ApplicationError;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void addAdmin(Admin admin) {
        admin.changePassword(passwordEncoder.encode(admin.getLoginPassword()));
        adminRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public String getAdminById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.DATABASE_ERROR));

        return admin.getLoginId();
    }

    @Transactional(readOnly = true)
    public List<String> getAllAdmins() {
        return adminRepository.findAll().stream().map(Admin::getLoginId).toList();
    }
}
