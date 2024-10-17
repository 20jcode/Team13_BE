package dbdr.security.service;

import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAcess;
import dbdr.security.model.Role;
import dbdr.security.model.BaseUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class DbdrSeucrityService {

    private final InstitutionRepository institutionRepository;
    private final CareworkerRepository careworkerRepository;
    private final GuardianRepository guardianRepository;
    private final ChartRepository chartRepository;
    private final RecipientRepository recipientRepository;

    private final DbdrAcess dbdrAcess;

    public boolean hasAcesssPermission(@NotNull Role role, @NotNull AuthParam type,@NotNull String id) {

        BaseUserDetails baseUserDetails = (BaseUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        Long paramId = parseId(id);

        return dbdrAcess.hasAccessPermission(role,baseUserDetails, findEntity(type, paramId));

    }

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ApplicationException(ApplicationError.ILLIGAL_ARGUMENT);
        }
    }

    private BaseEntity findEntity(AuthParam type, long id) {
        return switch (type) {
            case INSTITUTION_ID -> institutionRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));
            case CAREWORKER_ID -> careworkerRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
            case GUARDIAN_ID -> guardianRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));
            case CHART_ID -> chartRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.CHART_NOT_FOUND));
            case RECIPIENT_ID -> recipientRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.RECIPIENT_NOT_FOUND));
            default -> throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
        };
    }


}
