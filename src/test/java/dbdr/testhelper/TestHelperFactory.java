package dbdr.testhelper;

import com.linecorp.bot.client.LineMessagingClient;
import dbdr.domain.admin.Admin;
import dbdr.domain.admin.AdminService;
import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.service.CareworkerMessagingService;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.chart.dto.ChartMapper;
import dbdr.domain.chart.dto.request.BodyManagementRequest;
import dbdr.domain.chart.dto.request.ChartDetailRequest;
import dbdr.domain.chart.entity.BodyManagement;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.domain.chart.service.ChartService;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.domain.recipient.dto.request.RecipientRequest;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.global.configuration.LineMessagingClientConfig;
import java.util.ArrayList;
import java.util.List;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

@TestPropertySource("classpath:application.yml")
@Component
public class TestHelperFactory {

    @MockBean
    private LineMessagingClient lineMessagingClient;

    @Autowired
    GuardianService guardianService;

    @Autowired
    CareworkerService careworkerService;

    @Autowired
    InstitutionService institutionService;

    @Autowired
    ChartService chartService;

    @Autowired
    ChartRepository chartRepository;

    @Autowired
    RecipientService recipientService;

    @Autowired
    AdminService adminService;

    @Autowired
    ChartMapper chartMapper;

    private TestHelper testHelper;

    private List<GuardianRequest> guardians = new ArrayList<>();
    private List<CareworkerRequest> careworkers = new ArrayList<>();
    private List<InstitutionRequest> institutions = new ArrayList<>();
    private List<RecipientRequest> recipients = new ArrayList<>();
    private List<Chart> charts = new ArrayList<>();
    private List<Admin> admins = new ArrayList<>();

    public TestHelperFactory addGuardian(Guardian guardian) {
        GuardianRequest guardianRequest = convertGuardian(guardian);
        guardians.add(guardianRequest);
        return this;
    }

    public TestHelperFactory addCareworker(Careworker careworker) {
        CareworkerRequest careworkerRequest = convertCareworker(careworker);
        careworkers.add(careworkerRequest);
        return this;
    }

    public TestHelperFactory addInstitution(Institution institution) {
        InstitutionRequest institutionRequest = convertInstitution(institution);
        institutions.add(institutionRequest);
        return this;
    }

    public TestHelperFactory addRecipient(Recipient recipient) {
        RecipientRequest recipientRequest = convertRecipient(recipient);
        recipients.add(recipientRequest);
        return this;
    }

    /**
     * Request 만들기가 너무 복잡해서 entity로 넣습니다.
     * @param chart
     */
    public TestHelperFactory addChart(Chart chart) {
        charts.add(chart);
        return this;
    }

    public TestHelperFactory addAdmin() {
        Admin admin = new Admin("admin", "adminpassword");
        admins.add(admin);
        return this;
    }

    public TestHelper create(Integer port) {
        serviceInit();
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port + "/v1")
            .defaultHeaders(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
            .build();
        testHelper = new TestHelper(port, restClient);
        return testHelper;
    }

    private void serviceInit() {
        for (Admin admin : admins) {
            adminService.addAdmin(admin);
        }
        for (InstitutionRequest institutionRequest : institutions) {
            institutionService.addInstitution(institutionRequest);
        }

        for (Chart chart : charts) {
            chartRepository.save(chart);
        }

        for (CareworkerRequest careworkerRequest : careworkers) {
            careworkerService.addCareworker(careworkerRequest);
        }

        for (GuardianRequest guardianRequest : guardians) {
            guardianService.addGuardian(guardianRequest);
        }
    }

    private GuardianRequest convertGuardian(Guardian guardian) {
        return new GuardianRequest(guardian.getPhone(), guardian.getName(),
            guardian.getLoginPassword());
    }

    private CareworkerRequest convertCareworker(Careworker careworker) {
        return new CareworkerRequest(careworker.getInstitution().getId(), careworker.getName(),
            careworker.getEmail(), careworker.getPhone());
    }

    private InstitutionRequest convertInstitution(Institution institution) {
        return new InstitutionRequest(institution.getInstitutionNumber(),
            institution.getInstitutionName());
    }

    private RecipientRequest convertRecipient(Recipient recipient) {
        return new RecipientRequest();
    }

}