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
        log.info("권한 확인 : 사용 시 요구되는 권한 : {}, 로그인한 사용자 권한 : {}", role,authLoginUser.getRole());
        if(authLoginUser.isAdmin()){ //관리자는 자세한 확인 없이 통과
            log.info("관리자 권한 확인 완료");
            return true;
        }

        if(!hasRequiredRole(role, authLoginUser)){
            return false;
        }
        if (accessTarget instanceof Institution) { //요양원에 대한 접근 권한을 확인
            log.info("사용자 {} : {} 가 요양원에 대한 접근 권한을 확인합니다.",authLoginUser.getRole(), authLoginUser.getId());
            return hasAccessPermission(authLoginUser, (Institution) accessTarget);
        }
        if (accessTarget instanceof Careworker) {
            log.info("사용자 {} : {} 가 요양보호사에 대한 접근 권한을 확인합니다.",authLoginUser.getRole(), authLoginUser.getId());
            return hasAccessPermission(authLoginUser, (Careworker) accessTarget);
        }
        if (accessTarget instanceof Guardian) {
            log.info("사용자 {} : {} 가 보호자에 대한 접근 권한을 확인합니다.",authLoginUser.getRole(), authLoginUser.getId());
            return hasAccessPermission(authLoginUser, (Guardian) accessTarget);
        }
        if (accessTarget instanceof Chart) {
            log.info("사용자 {} : {} 가 차트에 대한 접근 권한을 확인합니다.",authLoginUser.getRole(), authLoginUser.getId());
            return hasAccessPermission(authLoginUser, (Chart) accessTarget);
        }
        if (accessTarget instanceof Recipient) {
            log.info("사용자 {} : {} 가 돌봄대상자에 대한 접근 권한을 확인합니다.",authLoginUser.getRole(), authLoginUser.getId());
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

    private boolean hasAccessPermission(BaseUserDetails userDetails, Institution institution) {
        if(userDetails.isInstitution()){ //요양원이면
            return userDetails.getInstitutionId().equals(institution.getId()); //같은 요양원인지 확인
        }
        return false; //그 이외 불가
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Careworker careworker) {
        if (userDetails.isInstitution()) { //요양원이라면
            return userDetails.getInstitutionId().equals(careworker.getInstitution().getId()); //자신의 소속 요양보호사 접근 가능
        }
        if (userDetails.isCareworker()) { //요양보호사라면
            return userDetails.getId().equals(careworker.getId()); //자신의 정보만 접근 가능
        }
        return false;
    }


    private boolean hasAccessPermission(BaseUserDetails userDetails, Guardian guardian) {
        if (userDetails.isInstitution()) { //요양원이라면
            return userDetails.getInstitutionId().equals(guardian.getInstitution().getId()); //자신의 소속 보호자 접근 가능
        }
        if(userDetails.isGuardian()){ //보호자라면
            return userDetails.getId().equals(guardian.getId()); //자신의 정보만 접근 가능
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Chart chart) {
        if(userDetails.isInstitution()){ //요양원이라면
            return userDetails.getInstitutionId().equals(chart.getRecipient().getInstitutionNumber());

        }
        if(userDetails.isCareworker()){
            return userDetails.getId().equals(chart.getRecipient().getCareworker().getId());
        }
        if(userDetails.isGuardian()){
            return userDetails.getId().equals(chart.getRecipient().getGuardian().getId());
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Recipient recipient) {
        if(userDetails.isInstitution()){
            return userDetails.getInstitutionId().equals(recipient.getInstitution().getId());
        }
        if(userDetails.isCareworker()){
            return userDetails.getId().equals(recipient.getCareworker().getId());
        }
        if(userDetails.isGuardian()){
            return userDetails.getId().equals(recipient.getGuardian().getId());
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
