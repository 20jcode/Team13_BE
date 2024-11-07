package dbdr.domain.careworker.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareworkerService {

    private final CareworkerRepository careworkerRepository;
    private final InstitutionRepository institutionRepository;

    @Transactional(readOnly = true)
    public List<CareworkerResponseDTO> getCareworkersByInstitution(Long institutionId) {
        return careworkerRepository.findByInstitutionId(institutionId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Careworker getCareworkerById(Long careworkerId) {
        return careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
    }


    @Transactional(readOnly = true)
    public CareworkerResponseDTO getCareworkerResponseById(Long careworkerId) {
        Careworker careworker = findCareworkerById(careworkerId);
        return toResponseDTO(careworker);
    }

    @Transactional
    public CareworkerResponseDTO addCareworker(CareworkerRequest careworkerRequest) {
        ensureUniqueEmail(careworkerRequest.getEmail());
        ensureUniquePhone(careworkerRequest.getPhone());
        Long institutionId = careworkerRequest.getInstitutionId();
        Institution institution = institutionRepository.findById(institutionId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));
        Careworker careworker = new Careworker(institution, careworkerRequest.getName(),
                careworkerRequest.getEmail(), careworkerRequest.getPhone());

        careworkerRepository.save(careworker);
        return toResponseDTO(careworker);
    }

    @Transactional
    public CareworkerResponseDTO updateCareworker(Long careworkerId, CareworkerRequest careworkerDTO, Long institutionId) {
        Careworker careworker = findCareworkerById(careworkerId);
        careworker.updateCareworker(careworkerDTO);
        return toResponseDTO(careworker);
    }

    @Transactional
    public void deleteCareworker(Long careworkerId, Long institutionId) {
        Careworker careworker = findCareworkerById(careworkerId);

        if (!careworker.getInstitution().getId().equals(institutionId)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        careworker.deactivate();
        careworkerRepository.delete(careworker);
    }

    private Careworker findCareworkerById(Long careworkerId) {
        return careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
    }

    private void ensureUniqueEmail(String email) {
        if (careworkerRepository.existsByEmail(email)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_EMAIL);
        }
    }

    private void ensureUniquePhone(String phone) {
        if (careworkerRepository.findByPhone(phone).isPresent()) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    private CareworkerResponseDTO toResponseDTO(Careworker careworker) {
        return new CareworkerResponseDTO(careworker.getId(), careworker.getInstitution().getId(),
                careworker.getName(), careworker.getEmail(), careworker.getPhone());
    }

    public Careworker findByLineUserId(String userId) {
        return careworkerRepository.findByLineUserId(userId).orElse(null);
    }

    public Careworker findByPhone(String phoneNumber) {
        return careworkerRepository.findByPhone(phoneNumber).orElse(null);
    }
}
