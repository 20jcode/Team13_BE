package dbdr.e2etest.doAdmin;

import static org.assertj.core.api.Assertions.assertThat;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.service.AdminService;
import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.global.util.api.ApiUtils.ApiResult;
import dbdr.security.model.Role;
import dbdr.testhelper.TestHelper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InstitutionCrudTest {

    @Autowired
    AdminService adminService;

    @LocalServerPort
    private int port;

    /*
    기본적인 통합 테스트

    각 테스트에서 영어+숫자 는 1000의 자리와 100의 자리를 의미하게 됩니다.
    ex) b3 테스트의 경우, 내부에서 사용되는 Long,int값등은 2300~2399 사이의 값을 사용합니다.

    사용 Number : a1~a4
     */

    private void addAdmin(String adminId, String adminpassword) {
        Admin admin = Admin.builder()
                .loginId(adminId)
                .loginPassword(adminpassword)
                .build();
        adminService.addAdmin(admin);
    }

    @Test
    @DisplayName("서버관리자가 요양원을 추가한다.")
    void test_a1(){
        //init
        String adminId = "admin_a1";
        String adminpassword = "admin_a1";
        addAdmin(adminId, adminpassword);

        TestHelper testHelper = new TestHelper(port);

        //given

        String testUri = "/admin/institution";

        Long institutionNumber = 1100L;
        String institutionName = "institution_a1";
        String institutionLoginId = "institution_a1";
        String institutionLoginPassword = "institution_a1";

        InstitutionRequest institutionRequest =
            new InstitutionRequest(institutionNumber,institutionName,institutionLoginId,institutionLoginPassword);

        //when

        var response = testHelper.user(Role.ADMIN,adminId,adminpassword)
                .uri(testUri)
                .requestBody(institutionRequest)
                .post()
                .toEntity(new ParameterizedTypeReference<ApiResult<InstitutionResponse>>() {})
                .getBody();

        //then
        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.response().institutionNumber()).isEqualTo(institutionNumber);
    }

    @Test
    @DisplayName("서버 관리자가 요양원을 수정한다.")
    void test_a2(){
        //init
        String adminId = "admin_a2";
        String adminpassword = "admin_a2";
        addAdmin(adminId, adminpassword);

        TestHelper testHelper = new TestHelper(port);

        String institutionCreateUri = "/admin/institution";

        Long institutionNumber = 1200L;
        String institutionName = "institution_a2";
        String institutionLoginId = "institution_a2";
        String institutionLoginPassword = "institution_a2";

        InstitutionRequest institutionRequest =
            new InstitutionRequest(institutionNumber,institutionName,institutionLoginId,institutionLoginPassword);

        var initResponse = testHelper.user(Role.ADMIN,adminId,adminpassword)
                .uri(institutionCreateUri)
                .requestBody(institutionRequest)
                .post()
                .toEntity(new ParameterizedTypeReference<ApiResult<InstitutionResponse>>() {})
                .getBody();

        //given

        Long institutionId = initResponse.response().id();
        String testUri = "/admin/institution/" + institutionId;

        Long institutionNumber2 = 1201L;
        String institutionName2 = "institution_a2_2";
        String institutionLoginId2 = "institution_a2_2";
        String institutionLoginPassword2 = "institution_a2_2";


        InstitutionRequest institutionRequest2 =
            new InstitutionRequest(institutionNumber2,institutionName2,institutionLoginId2,institutionLoginPassword2);

        //when

        var response = testHelper.user(Role.ADMIN,adminId,adminpassword)
                .uri(testUri)
                .requestBody(institutionRequest2)
                .put()
                .toEntity(new ParameterizedTypeReference<ApiResult<InstitutionResponse>>() {})
                .getBody();

        //then
        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.response().institutionName()).as("관리자가 요양원이름 변경").isEqualTo(institutionName2);
        assertThat(response.response().institutionLoginId()).as("관리자가 요양원 로그인 ID 변경").isEqualTo(institutionLoginId2);
        assertThat(response.response().institutionNumber()).as("관리자가 요양원 번호 변경").isEqualTo(institutionNumber2);
    }

    @Test
    @DisplayName("서버 관리자가 요양원을 삭제한다.")
    void test_a3(){
        //init
        String adminId = "admin_a3";
        String adminpassword = "admin_a3";
        addAdmin(adminId, adminpassword);

        TestHelper testHelper = new TestHelper(port);

        String institutionCreateUri = "/admin/institution";

        Long institutionNumber = 1300L;
        String institutionName = "institution_a3";
        String institutionLoginId = "institution_a3";
        String institutionLoginPassword = "institution_a3";

        InstitutionRequest institutionRequest =
            new InstitutionRequest(institutionNumber,institutionName,institutionLoginId,institutionLoginPassword);

        var initResponse = testHelper.user(Role.ADMIN,adminId,adminpassword)
                .uri(institutionCreateUri)
                .requestBody(institutionRequest)
                .post()
                .toEntity(new ParameterizedTypeReference<ApiResult<InstitutionResponse>>() {})
                .getBody();

        //given

        Long institutionId = initResponse.response().id();
        String testUri = "/admin/institution/" + institutionId;

        //when

        var response = testHelper.user(Role.ADMIN,adminId,adminpassword)
                .uri(testUri)
                .delete()
                .toEntity(new ParameterizedTypeReference<ApiResult<String>>() {})
                    .getStatusCode();

        //then
        assertThat(response.is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("서버 관리자가 요양원을 조회한다.")
    void test_a4(){
        //init
        String adminId = "admin_a4";
        String adminpassword = "admin_a4";
        addAdmin(adminId, adminpassword);

        TestHelper testHelper = new TestHelper(port);

        String institutionCreateUri = "/admin/institution";

        Long institutionNumber = 1400L;
        String institutionName = "institution_a4";
        String institutionLoginId = "institution_a4";
        String institutionLoginPassword = "institution_a4";

        InstitutionRequest institutionRequest =
            new InstitutionRequest(institutionNumber,institutionName,institutionLoginId,institutionLoginPassword);

        var initResponse = testHelper.user(Role.ADMIN,adminId,adminpassword)
                .uri(institutionCreateUri)
                .requestBody(institutionRequest)
                .post()
                .toEntity(new ParameterizedTypeReference<ApiResult<InstitutionResponse>>() {})
                .getBody();

        //given

        Long institutionId = initResponse.response().id();
        String testUri1 = "/admin/institution/" + institutionId; //요양원 단일 조회
        String testUri2 = "/admin/institution"; //요양원 전체 조회

        //when

        var response1 = testHelper.user(Role.ADMIN,adminId,adminpassword)
                .uri(testUri1)
                .get()
                .toEntity(new ParameterizedTypeReference<ApiResult<InstitutionResponse>>() {})
                .getBody();

        var response2 = testHelper.user(Role.ADMIN,adminId,adminpassword)
                .uri(testUri2)
                .get()
                .toEntity(new ParameterizedTypeReference<ApiResult<List<InstitutionResponse>>>() {})
                .getBody();

        //then
        assertThat(response1).isNotNull();
        assertThat(response1.success()).isTrue();
        assertThat(response1.response().institutionName()).as("관리자가 요양원 조회").isEqualTo(institutionName);
        assertThat(response1.response().institutionLoginId()).as("관리자가 요양원 로그인 ID 조회").isEqualTo(institutionLoginId);
        assertThat(response1.response().institutionNumber()).as("관리자가 요양원 번호 조회").isEqualTo(institutionNumber);

        assertThat(response2).isNotNull();
        assertThat(response2.success()).isTrue();
        assertThat(response2.response().size()).as("관리자가 요양원 전체 조회").isGreaterThan(0);
        assertThat(response2.response().stream().anyMatch
            (institutionResponse -> institutionResponse.institutionName().equals(institutionName)))
            .as("관리자가 요양원 전체 조회").isTrue();
    }

    

}
