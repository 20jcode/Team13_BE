package dbdr.testhelper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.linecorp.bot.client.LineMessagingClient;
import dbdr.domain.admin.AdminService;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.chart.dto.ChartMapper;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.domain.chart.service.ChartService;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.global.util.line.LineMessagingUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestHelperTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestHelperFactory testHelperFactory;

    TestHelper testHelper;

    @BeforeEach
    public void setUp(){
    }

    @Test
    void test(){

        //given
        Guardian guardian = Guardian.builder().build();
        Institution institution = Institution.builder().build();

        testHelper = testHelperFactory.addGuardian(guardian).addInstitution(institution).create(port);

        ResponseEntity<GuardianResponse> res = testHelper.user(guardian).uri("/guardian").queryParam("name","value")
            .get().toEntity(GuardianResponse.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}
