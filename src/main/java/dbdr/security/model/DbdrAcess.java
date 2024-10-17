package dbdr.security.model;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.entity.Recipient;
import org.springframework.stereotype.Component;

@Component
public class DbdrAcess {

    public boolean hasAccessPermission(Role role,BaseUserDetails userDetails, BaseEntity baseEntity) {

        if(!hasRequiredRole(role, userDetails)){
            return false;
        }
        if (baseEntity instanceof Institution) {
            return hasAccessPermission(userDetails, (Institution) baseEntity);
        }
        if (baseEntity instanceof Careworker) {
            return hasAccessPermission(userDetails, (Careworker) baseEntity);
        }
        if (baseEntity instanceof Guardian) {
            return hasAccessPermission(userDetails, (Guardian) baseEntity);
        }
        if (baseEntity instanceof Chart) {
            return hasAccessPermission(userDetails, (Chart) baseEntity);
        }
        if (baseEntity instanceof Recipient) {
            return hasAccessPermission(userDetails, (Recipient) baseEntity);
        }
        return false;
    }


    private boolean hasRequiredRole(Role role, BaseUserDetails userDetails) {
        if (role.equals(Role.ADMIN)){
            return userDetails.isAdmin();
        }
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
        return userDetails.getInstitutionId().equals(institution.getId());
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Careworker careworker) {
        if (userDetails.isInstitution()) {
            return userDetails.getInstitutionId().equals(careworker.getInstitutionId());
        }
        if (userDetails.isCareworker()) {
            return userDetails.getId().equals(careworker.getId());
        }
        return false;
    }


    private boolean hasAccessPermission(BaseUserDetails userDetails, Guardian guardian) {
        if (userDetails.isInstitution()) {
            return userDetails.getInstitutionId().equals(guardian.getInstitutionId());
        }
        if(userDetails.isGuardian()){
            return userDetails.getId().equals(guardian.getId());
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Chart chart) {
        if(userDetails.isInstitution()){
            return userDetails.getInstitutionId().equals(chart.getInstitutionId());
        }
        if(userDetails.isCareworker()){
            return userDetails.getId().equals(chart.getCareworker().getId());
        }
        return false;
    }

    private boolean hasAccessPermission(BaseUserDetails userDetails, Recipient recipient) {
        //TODO : chart와 recipient사이 관계가 있으므로 리팩토링 가능
        if(userDetails.isAdmin()){
            return userDetails.getInstitutionId().equals(recipient.getInstitutionNumber());
        }
        if(userDetails.isCareworker()){
            return userDetails.getId().equals(recipient.getCareworker().getId());
        }
        if(userDetails.isGuardian()){
            return false;
            //TODO : 환자와 보호자 간의 mapping 필요
            //return userDetails.getId().equals(recipient.getGuardian().getId());
        }
        return false;
    }
}
