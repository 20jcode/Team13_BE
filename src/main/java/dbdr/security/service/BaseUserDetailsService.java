package dbdr.security.service;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.repository.AdminRepository;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.model.BaseUserDetails;
import dbdr.security.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseUserDetailsService {

    private final GuardianRepository guardianRepository;
    private final CareworkerRepository careWorkerRepository;
    private final InstitutionRepository institutionRepository;
    private final AdminRepository adminRepository;

    public BaseUserDetails loadUserByUsernameAndRole(String username, Role role) {

        return switch (role) {
            case GUARDIAN -> getGuadianDetails(username);
            case CAREWORKER -> getCareWorkerDetails(username);
            case INSTITUTION -> getInstitutionDetails(username);
            case ADMIN -> getAdminDetails(username);
            default -> throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
        };

    }

    private BaseUserDetails getInstitutionDetails(String userId) {
        if (!institutionRepository.existsByInstitutionNumber(Long.parseLong(userId))) {
            throw new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND);
        }

        Institution institution = institutionRepository.findByInstitutionNumber(Long.parseLong(userId));

        return securityRegister(institution.getId(), institution.getInstitutionNumber().toString(),
            institution.getLoginPassword(), Role.INSTITUTION);
    }

    private BaseUserDetails getCareWorkerDetails(String userId) {

        Careworker careWorker = careWorkerRepository.findByPhone(userId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
        return securityRegister(careWorker.getId(), careWorker.getPhone(),
            careWorker.getLoginPassword(), Role.CAREWORKER);
    }

    private BaseUserDetails getAdminDetails(String username) {
        Admin admin = adminRepository.findByLoginId(username)
            .orElseThrow(() -> new ApplicationException(ApplicationError.ADMIN_NOT_FOUND));
        return securityRegister(admin.getId(), admin.getLoginId(), admin.getLoginPassword(), Role.ADMIN);
    }

    private BaseUserDetails getGuadianDetails(String userId) {
        log.debug("보호자 userId : {}", userId);
        Guardian guardian = guardianRepository.findByPhone(userId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));

        return securityRegister(guardian.getId(), guardian.getPhone(), guardian.getLoginPassword(),
            Role.GUARDIAN);
    }

    private BaseUserDetails securityRegister(Long id, String username, String password, Role role) {
        BaseUserDetails userDetails = BaseUserDetails.builder().id(id).userLoginId(username)
            .password(password).role(role).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
            userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("security register : {}", userDetails.getUserLoginId());
        return userDetails;
    }

}
