package dbdr.e2etest.Institution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.domain.institution.entity.Institution;
import dbdr.global.exception.ApplicationError;
import dbdr.security.model.Role;
import dbdr.testhelper.TestHelper;
import dbdr.testhelper.TestHelperFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //테스트 간에 독립성 유지
public class InstitutionTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestHelperFactory testHelperFactory;

    TestHelper testHelper;

    @BeforeEach
    public void setUp() {
        Admin admin = Admin.builder().loginId("testadmin").loginPassword("adminpassword").build();
        testHelperFactory.addAdmin(admin);
        testHelper = testHelperFactory.create(port);
    }

    @Test
    @DisplayName("신규 요양원 등록")
    public void addInstitutionTest() {
        //given

        Institution institution = Institution.builder().institutionName("김치덮밥요양원")
            .institutionNumber(123123L).build();
        InstitutionRequest institutionRequest = new InstitutionRequest(
            institution.getInstitutionNumber(), institution.getInstitutionName());

        //when

        //서버 관리자가 신규 요양원을 등록한다.
        var response = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
            .uri("/admin/institution").requestBody(institutionRequest).post()
            .toEntity(InstitutionResponse.class);

        //then

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().institutionName()).isEqualTo(
            institution.getInstitutionName());
        assertThat(response.getBody().institutionNumber()).isEqualTo(
            institution.getInstitutionNumber());
    }

    @Test
    @DisplayName("요양원 번호 중복 등록 방지")
    public void addInstitutionTest2() {
        //given

        Institution institution = Institution.builder().institutionName("김치덮밥요양원")
            .institutionNumber(123123L).build();
        InstitutionRequest institutionRequest = new InstitutionRequest(
            institution.getInstitutionNumber(), institution.getInstitutionName());

        //요양원 번호 중복됨
        InstitutionRequest institutionRequest2 = new InstitutionRequest(
            institution.getInstitutionNumber(), "김치덮밥요양원아닙니다.");

        //요양원 이름 중복됨
        InstitutionRequest institutionRequest3 = new InstitutionRequest(123124L,
            institution.getInstitutionName());

        //when

        //요양원 등록
        var response = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
            .uri("/admin/institution").requestBody(institutionRequest).post()
            .toEntity(InstitutionResponse.class);

        //then

        //중복없는 요양원 등록 성공
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //요양원 번호가 중복되는 경우
        assertThatThrownBy(() -> {
            testHelper.user(Role.ADMIN, "testadmin", "adminpassword").uri("/admin/institution")
                .requestBody(institutionRequest2).post().toEntity(InstitutionResponse.class);
        }).hasMessageContaining(ApplicationError.DUPLICATE_INSTITUTION_NUMBER.getMessage());

        //요양원 이름이 중복되는 경우
        assertThatThrownBy(() -> {
            testHelper.user(Role.ADMIN, "testadmin", "adminpassword").uri("/admin/institution")
                .requestBody(institutionRequest3).post().toEntity(InstitutionResponse.class);
        }).hasMessageContaining(ApplicationError.DUPLICATE_INSTITUTION_NUMBER.getMessage());

    }

    @Test
    @DisplayName("요양원 정보 수정")
    public void updateInstitutionTest() {
        //given

        Institution institution = Institution.builder()
            .institutionName("김치덮밥요양원")
            .institutionNumber(123123L).build();

        InstitutionRequest institutionRequest =
            new InstitutionRequest(institution.getInstitutionNumber(), institution.getInstitutionName());

            //요양원 등록
        var response = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
            .uri("/admin/institution")
            .requestBody(institutionRequest)
            .post()
            .toEntity(InstitutionResponse.class);

            //요양원 생성 response에서 id를 추출
        long id = response.getBody().id();

            //수정할 요양원 정보 : 요양원 이름과 번호 변경
        InstitutionRequest updateRequest =
            new InstitutionRequest(1233L, "김치찌개요양원");

        //when

            //요양원 정보 수정
         var updateResponse = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
             .uri("/admin/institution/"+id) //pathVariable로 id를 넘겨줌.
             .requestBody(updateRequest)
             .put()
             .toEntity(InstitutionResponse.class);

        //then

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().institutionName()).isEqualTo("김치찌개요양원");
        assertThat(updateResponse.getBody().institutionNumber()).isEqualTo(1233L);
    }

}
