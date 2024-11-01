package dbdr.testhelper;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.chart.service.ChartService;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.service.RecipientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TestHelperFactory {

    @LocalServerPort
    private int port;

    @Autowired
    GuardianService guardianService;

    @Autowired
    CareworkerService careworkerService;

    @Autowired
    InstitutionService institutionService;

    @Autowired
    ChartService chartService;

    @Autowired
    RecipientService recipientService;

    @Autowired
    InstitutionRepository institutionRepository;

    private TestHelper testHelper;

    //TODO : 서비스들 순서 맞춰서 등록해줘야 오류 없음. (요양원이 있어야 요양보호사 가능 처럼_

    public TestHelperFactory addGuardian(Guardian guardian) {
        GuardianRequest guardianRequest = convertGuardian(guardian);
        guardianService.addGuardian(guardianRequest);
        return this;
    }

    public TestHelperFactory addCareworker(Careworker careworker) {
        CareworkerRequestDTO careworkerRequestDTO = convertCareworker(careworker);
        careworkerService.createCareworker(careworkerRequestDTO);
        return this;
    }

    public TestHelperFactory addInstitution(Institution institution) {
        InstitutionRequest institutionRequest = convertInstitution(institution);
        institutionService.addInstitution(institutionRequest);
        return this;
    }

    public TestHelperFactory addRecipient(Recipient recipient) {
        RecipientRequestDTO recipientRequestDTO = convertRecipient(recipient);
        recipientService.createRecipient(recipientRequestDTO);
        return this;
    }

    public TestHelperFactory addAdmin(){
        Institution admin = Institution.builder().institutionName("admin").institutionNumber(0L).build();
        //TODO admin
        return this;
    }

    public TestHelper create() {
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port+"/v1")
            .defaultHeaders(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
            .build();
        testHelper = new TestHelper(port,restClient);
        return testHelper;
    }

     private GuardianRequest convertGuardian(Guardian guardian){
        return new GuardianRequest(guardian.getPhone(), guardian.getName(),guardian.getLoginPassword());
    }

    private CareworkerRequestDTO convertCareworker(Careworker careworker){
        return new CareworkerRequestDTO(careworker.getInstitutionId(),careworker.getName(),careworker.getEmail(),careworker.getPhone());
    }

    private InstitutionRequest convertInstitution(Institution institution){
        return new InstitutionRequest(institution.getInstitutionNumber(),institution.getInstitutionName());
    }

    private RecipientRequestDTO convertRecipient(Recipient recipient) {
        return new RecipientRequestDTO();
    }
}
