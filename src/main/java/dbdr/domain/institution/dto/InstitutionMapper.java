package dbdr.domain.institution.dto;

import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.domain.institution.entity.Institution;
import dbdr.global.util.mapper.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InstitutionMapper implements EntityMapper<Institution> {

    @Override
    public InstitutionRequest toRequest(Institution entity) {
        return new InstitutionRequest(entity.getInstitutionNumber(), entity.getInstitutionName(),
            entity.getLoginId(), entity.getLoginPassword());
    }

    @Override
    public InstitutionResponse toResponse(Institution entity) {
        return new InstitutionResponse(entity.getId(), entity.getInstitutionNumber(), entity.getInstitutionName(),
            entity.getLoginId());
    }

    @Override
    public <D> Institution toEntity(D dto) {
        if(dto instanceof InstitutionRequest institutionRequest) {
            return Institution.builder()
                .institutionNumber(institutionRequest.institutionNumber())
                .institutionName(institutionRequest.institutionName())
                .loginId(institutionRequest.institutionLoginId())
                .loginPassword(institutionRequest.institutionLoginPassword())
                .build();
        } else if (dto instanceof InstitutionResponse institutionResponse) {
            log.info("Response를 Entity로 변환 - test에서만 사용해주세요");
            return Institution.builder()
                .institutionNumber(institutionResponse.institutionNumber())
                .institutionName(institutionResponse.institutionName())
                .institutionNumber(institutionResponse.institutionNumber())
                .loginPassword(null)
                .build();
        } else {
            throw new IllegalArgumentException("InstitutionMapper를 사용할 수 없는 타입입니다.");
        }
    }

    @Override
    public boolean isSupports(Class<?> object) {
        return object.equals(Institution.class);
    }
}
