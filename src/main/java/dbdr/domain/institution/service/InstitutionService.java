package dbdr.domain.institution.service;

import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.global.util.mapper.EntityMapper;
import java.util.List;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstitutionService {

    private final EntityMapper<Institution> institutionMapper;
    private final PasswordEncoder passwordEncoder;
    private final InstitutionRepository institutionRepository;

    public InstitutionResponse getInstitutionById(Long institutionId) {
        Institution institution = getInstitution(institutionId);
        log.info("요양원 조회 : {}", institutionId);
        return institutionMapper.toResponse(institution);
    }

    public InstitutionResponse updateInstitution(Long institutionId, InstitutionRequest institutionRequest) {
        ensureUniqueInstitutionNumber(institutionId,institutionRequest.institutionNumber());

        Institution institution = getInstitution(institutionId);
        institution.updateInstitution(institutionRequest.institutionNumber(), institutionRequest.institutionName());
        institutionRepository.save(institution);
        return institutionMapper.toResponse(institution);
    }

    public List<InstitutionResponse> getAllInstitution() {
        List<Institution> institutionList = institutionRepository.findAll();
        return institutionList.stream().map(
            institution -> (InstitutionResponse)institutionMapper.toResponse(institution)).toList();
    }

    public InstitutionResponse addInstitution(InstitutionRequest institutionRequest) {
        ensureUniqueInstitutionNumber(institutionRequest.institutionNumber());
        ensureUniqueInstitutionName(institutionRequest.institutionName());
        ensureUniqueInstitutionLoginId(institutionRequest.institutionLoginId());

        Institution institution = institutionMapper.toEntity(institutionRequest);

        institution.updatePassword(passwordEncoder.encode(institutionRequest.institutionLoginPassword()));
        institution = institutionRepository.save(institution);
        log.info("신규 요양원이 등록되었습니다. institutionId : {}", institution.getId());
        return institutionMapper.toResponse(institution);
    }

    public void deleteInstitutionById(Long institutionId) {
        Institution institution = getInstitution(institutionId);
        institution.deactivate();
        institutionRepository.delete(institution);
    }

    private void ensureUniqueInstitutionNumber(Long institutionNumber) {
        if (institutionRepository.existsByInstitutionNumber(institutionNumber)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_INSTITUTION_NUMBER);
        }
    }

    //본인 요양원이 아니면서 이미 존재하는 번호인지 확인 필요
    private void ensureUniqueInstitutionNumber(Long institutionId,Long institutionNumber) {
        if (institutionRepository.existsByInstitutionNumber(institutionNumber)) {
            if(!Objects.equals(
                institutionRepository.findByInstitutionNumber(institutionNumber).getId(), institutionId)){
                //변경하려는 요양원 번호가 자신이 pk가 아닌 경우
                throw new ApplicationException(ApplicationError.DUPLICATE_INSTITUTION_NUMBER);
            }
        }
    }

    private void ensureUniqueInstitutionName(String institutionName) {
        if (institutionRepository.existsByInstitutionName(institutionName)) {
            throw new ApplicationException(ApplicationError.DUPILCATE_INSTITUTION_NAME);
        }
    }

    private void ensureUniqueInstitutionLoginId(String loginId) {
        if (institutionRepository.existsByLoginId(loginId)) {
            throw new ApplicationException(ApplicationError.DUPILCATE_INSTITUTION_LOGIN_ID);
        }
    }

    private Institution getInstitution(Long institutionId) {
        return institutionRepository.findById(institutionId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));
    }
}
