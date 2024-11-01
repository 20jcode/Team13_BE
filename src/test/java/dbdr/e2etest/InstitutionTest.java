package dbdr.e2etest;

import dbdr.domain.institution.entity.Institution;
import dbdr.testhelper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InstitutionTest {

    @LocalServerPort
    Long port;

    //@Autowired
    //TestHelper testHelper;

    @BeforeEach
    void setUp() {
        //testHelper.port(port);
    }

    @Test
    @DisplayName("Admin이 Institution을 등록한다.")
    void test1() {
        // given
        String adminName = "나는어드민";
        Long adminNumber = 111L;

        Institution admin = Institution.builder().institutionName(adminName).institutionNumber(
            adminNumber).build();

        //testHelper.addAdmin(admin);

        // when
        //ResponseEntity<?> response = testHelper.user(admin).post("/admin/institution");
    }
}
