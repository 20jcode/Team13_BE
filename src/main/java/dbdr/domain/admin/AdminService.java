package dbdr.domain.admin;

import dbdr.global.exception.ApplicationException;
import dbdr.global.exception.ApplicationError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    @Transactional
    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public Admin getAdminById(Long adminId) {

        return adminRepository.findById(adminId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.DATABASE_ERROR));
    }

}
