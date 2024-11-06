package dbdr.testhelper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.repository.AdminRepository;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.domain.institution.entity.Institution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestHelperTest {

    /**
     * 기본적인 TestHelper의 CRUD 테스트와 동시에 Service 계층의 테스트를 진행하여 서비스 안정성을 높이고자 합니다.
     */

    @LocalServerPort
    private int port;

    @Autowired
    TestHelperFactory testHelperFactory;

    TestHelper testHelper;

    DefaultEntity defaultEntity = new DefaultEntity();

    @Autowired
    AdminRepository adminRepository;

    @BeforeEach
    public void setUp() {
    }

    @Test
    @DisplayName("Admin 계정 생성 테스트")
    public void createAdminTest() {
        //given
        Admin emadmin = defaultEntity.createAdmin();
        testHelperFactory.addAdmin(emadmin);

        Institution institution = defaultEntity.createInstitution();
        testHelperFactory.addInstitution(institution);

        testHelper = testHelperFactory.create(port);

        Admin admin = defaultEntity.createAdmin();
        //when
        //var response = testHelper.user(admin).uri("/admin/institution/1").get().toEntity(InstitutionResponse.class);

        //then
        //assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
