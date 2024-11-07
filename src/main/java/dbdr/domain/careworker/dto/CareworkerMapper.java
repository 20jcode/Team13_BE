package dbdr.domain.careworker.dto;

import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.global.util.mapper.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CareworkerMapper implements EntityMapper<Careworker> {

    @Override
    public CareworkerRequest toRequest(Careworker entity) {
        return new CareworkerRequest(entity.getLoginPassword(), entity.getInstitution().getId(),
            entity.getName(), entity.getEmail(), entity.getPhone());
    }

    @Override
    public CareworkerResponseDTO toResponse(Careworker entity) {
        return new CareworkerResponseDTO(entity.getId(), entity.getInstitution().getId(),
            entity.getName(), entity.getEmail(), entity.getPhone());
    }

    @Override
    public <D> Careworker toEntity(D dto) {
        log.info("toEntity 변환은 사용될 수 없습니다. : CareworkerMapper");
        throw new IllegalArgumentException("CareworkerMapper를 사용할 수 없는 타입입니다.");
    }

    @Override
    public boolean isSupports(Class<?> object) {
        return object.equals(Careworker.class);
    }
}
