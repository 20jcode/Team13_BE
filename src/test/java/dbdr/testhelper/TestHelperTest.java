package dbdr.testhelper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class TestHelperTest {
    @Autowired
    TestHelperFactory testHelperFactory;

    TestHelper testHelper;

    @Test
    void test(){

        //given
        Guardian guardian = Guardian.builder().build();
        Institution institution = Institution.builder().build();

        testHelper = testHelperFactory.addGuardian(guardian).addInstitution(institution).create();

        ResponseEntity<GuardianResponse> res = testHelper.user(guardian).uri("/guardian").queryParam("name","value")
            .get().toEntity(GuardianResponse.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}
