package dbdr.security.model;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.entity.Recipient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DbdrAcess {

    public boolean hasAccessPermission(Role role,BaseUserDetails authLoginUser, BaseEntity accessTarget) {
        log.info("권한확인 메소드 동작 시작 : role : {}, userDetails : {}, baseEntity : {}", role, authLoginUser, accessTarget);

        if(authLoginUser.isAdmin()){
            return true;
        }

        if(!hasRequiredRole(role, authLoginUser)){
            return false;
        }
        if (accessTarget instanceof Institution) {
            return hasAccessPermission(authLoginUser, (Institution) accessTarget);
        }
        if (accessTarget instanceof Careworker) {
            return hasAccessPermission(authLoginUser, (Careworker) accessTarget);
        }
        if (accessTarget instanceof Guardian) {
            return hasAccessPermission(authLoginUser, (Guardian) accessTarget);
        }
        if (accessTarget instanceof Chart) {
            return hasAccessPermission(authLoginUser, (Chart) accessTarget);
        }
        if (accessTarget instanceof Recipient) {
            return hasAccessPermission(authLoginUser, (Recipient) accessTarget);
        }
        return false;
    }


    private boolean hasRequiredRole(Role role, BaseUserDetails userDetails) {
        if (role.equals(Role.INSTITUTION) && (userDetails.isAdmin() || userDetails.isInstitution())){
            return true;
        }
        if (role.equals(Role.CAREWORKER) && (!userDetails.isGuardian())){
            return true;
        }
        if (role.equals(Role.GUARDIAN) && (!userDetails.isCareworker())){
            return true;
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails loginUser, Institution targetInstitution) {
        if(loginUser.isInstitution()){
            return loginUser.getInstitutionId().equals(targetInstitution.getId());
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails loginUser, Careworker careworker) {
        if (loginUser.isInstitution()) {
            return loginUser.getInstitutionId().equals(careworker.getInstitution().getId());
        }
        if (loginUser.isCareworker()) {
            return loginUser.getId().equals(careworker.getId());
        }
        return false;
    }


    private boolean hasAccessPermission(BaseUserDetails loginUser, Guardian guardian) {
        if (loginUser.isInstitution()) {
            return loginUser.getInstitutionId().equals(guardian.getInstitution().getId());
        }
        if(loginUser.isGuardian()){
            return loginUser.getId().equals(guardian.getId());
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails loginUser, Chart chart) {
        if(loginUser.isInstitution()){
            return loginUser.getInstitutionId().equals(chart.getRecipient().getInstitutionNumber());
        }
        if(loginUser.isCareworker()){
            return loginUser.getId().equals(chart.getRecipient().getCareworker().getId());
        }
        if(loginUser.isGuardian()){
            return loginUser.getId().equals(chart.getRecipient().getGuardian().getId());
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails loginUser, Recipient recipient) {
        if(loginUser.isAdmin()){
            return loginUser.getInstitutionId().equals(recipient.getInstitutionNumber());
        }
        if(loginUser.isCareworker()){
            return loginUser.getId().equals(recipient.getCareworker().getId());
        }
        if(loginUser.isGuardian()){
            return loginUser.getId().equals(recipient.getGuardian().getId());
        }
        return false;
    }

    //특정 소속 확인없이 단순한 권한 검사만을 위해 존재
    public boolean hasRole(Role role, BaseUserDetails baseUserDetails) {
        if(baseUserDetails.isAdmin()){
            return true;
        } //관리자는 무조건 통과
        if(role.equals(Role.INSTITUTION) && baseUserDetails.isInstitution()){
            return true;
        }
        if(role.equals(Role.CAREWORKER) && (baseUserDetails.isInstitution() ||baseUserDetails.isCareworker())){
            return true;
        }
        if(role.equals(Role.GUARDIAN) && (baseUserDetails.isInstitution() || baseUserDetails.isGuardian())){
            return true;
        }
        log.info("권한 부족으로 인한 거부 : role : {}, userDetails : {}", role, baseUserDetails);
        return false;
    }
}
