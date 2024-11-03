package dbdr.domain.recipient.service;

import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.domain.recipient.dto.request.RecipientRequest;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final CareworkerRepository careworkerRepository;
    private final InstitutionRepository institutionRepository;

    @Transactional(readOnly = true)
    public List<RecipientResponseDTO> getAllRecipients() {
        return recipientRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipientResponseDTO getRecipientById(Long id) {
        Recipient recipient = findRecipientById(id);
        return toResponseDTO(recipient);
    }

    @Transactional
    public RecipientResponseDTO createRecipient(RecipientRequest recipientRequest) {
        ensureUniqueCareNumber(recipientRequest.getCareNumber());
        Institution institution = institutionRepository.findById(recipientRequest.getInstitutionId())
                .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));
        Recipient recipient = new Recipient(
                recipientRequest.getName(),
                recipientRequest.getBirth(),
                recipientRequest.getGender(),
                recipientRequest.getCareLevel(),
                recipientRequest.getCareNumber(),
                recipientRequest.getStartDate(),
                institution,
                recipientRequest.getInstitutionNumber(),
                careworkerRepository.findById(recipientRequest.getCareworkerId())
                        .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND))
        );
        recipientRepository.save(recipient);
        return toResponseDTO(recipient);
    }

    @Transactional
    public RecipientResponseDTO updateRecipient(Long id, RecipientRequest recipientRequest) {
        ensureUniqueCareNumber(recipientRequest.getCareNumber());

        Recipient recipient = findRecipientById(id);

        recipient.updateRecipient(recipientRequest);
        recipientRepository.save(recipient);

        return toResponseDTO(recipient);
    }

    @Transactional
    public void deleteRecipient(Long id) {
        Recipient recipient = findRecipientById(id);
        recipient.deactivate();
        recipientRepository.delete(recipient);
    }

    public Recipient findRecipientById(Long id) {
        return recipientRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.RECIPIENT_NOT_FOUND));
    }

    private void ensureUniqueCareNumber(String careNumber) {
        if (recipientRepository.existsByCareNumber(careNumber)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_CARE_NUMBER);
        }
    }

    private RecipientResponseDTO toResponseDTO(Recipient recipient) {
        return new RecipientResponseDTO(
                recipient.getId(),
                recipient.getName(),
                recipient.getBirth(),
                recipient.getGender(),
                recipient.getCareLevel(),
                recipient.getCareNumber(),
                recipient.getStartDate(),
                recipient.getInstitution().getInstitutionName(),
                recipient.getInstitutionNumber(),
                recipient.getInstitution().getId(),
                recipient.getCareworker().getId()
        );
    }
}

