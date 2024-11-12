package dbdr.e2etest.doAdmin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.service.AdminService;
import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.response.CareworkerResponse;
import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.global.util.api.ApiUtils.ApiResult;
import dbdr.security.model.Role;
import dbdr.testhelper.TestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CareworkerCrudTest {

    @Autowired
    AdminService adminService;

    @LocalServerPort
    private int port;

    /*
    사용Nubber : b1~
     */

    private void addAdmin(String adminId, String adminpassword) {
        Admin admin = Admin.builder()
            .loginId(adminId)
            .loginPassword(adminpassword)
            .build();
        adminService.addAdmin(admin);
    }

    /**
     * 요양원 추가를 위해 사용되어지며, 영어문자를 뺀 뒤의 숫자값을 사용
     *
     * @param testNumber a3일 경우 -> 3을 넣음
     * @return institutionId
     */
    private Long addInstitutionRequestLogic(String adminId, String adminpassword,
        String testNumber) {
        Long institutionNumber = 1000L + Long.parseLong(testNumber) * 100;
        String institutionName = "institution_b" + testNumber;
        String institutionLoginId = "institution_b" + testNumber;
        String institutionLoginPassword = "institution_b" + testNumber;

        InstitutionRequest institutionRequest = new InstitutionRequest(institutionNumber,
            institutionName, institutionLoginId, institutionLoginPassword);

        TestHelper testHelper = new TestHelper(port);
        return testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/institution")
            .requestBody(institutionRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<InstitutionResponse>>() {
            })
            .getBody().response().id();
    }

    @Test
    @DisplayName("서버관리자가 요양보호사를 추가한다.")
    void test_b1() {
        //init
        String adminId = "admin_b1";
        String adminpassword = "admin_b1";
        addAdmin(adminId, adminpassword);

        String testNumber = "1";
        Long institutionId = addInstitutionRequestLogic(adminId, adminpassword, testNumber);

        TestHelper testHelper = new TestHelper(port);

        //given
        String careworkerPhone = "01076837162";
        String careworkerName = "careworker_b1";
        String careworkerEmail = "b1@b1.com";
        String careworkerLoginPassword = "careworker_b1";

        CareworkerRequest careworkerRequest = new CareworkerRequest(institutionId, careworkerName,
            careworkerEmail, careworkerPhone, careworkerLoginPassword);

        //when
        var response = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/careworker")
            .requestBody(careworkerRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<CareworkerResponse>>() {
            })
            .getBody();

        //then

        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.response().getEmail()).isEqualTo(careworkerEmail);
        assertThat(response.response().getName()).isEqualTo(careworkerName);
        assertThat(response.response().getPhone()).isEqualTo(careworkerPhone);
        assertThat(response.response().getInstitutionId()).isEqualTo(institutionId);

    }

    @Test
    @DisplayName("서버관리자가 요양보호사를 수정한다.")
    void test_b2() {
        //init
        String adminId = "admin_b2";
        String adminpassword = "admin_b2";
        addAdmin(adminId, adminpassword);

        String testNumber = "2";
        Long institutionId = addInstitutionRequestLogic(adminId, adminpassword, testNumber);

        TestHelper testHelper = new TestHelper(port);

        //given : 수정 전 요양보호사 정보
        String beforeUri = "/admin/careworker";

        String careworkerPhone = "01032121162";
        String careworkerName = "careworker_b2";
        String careworkerEmail = "b2@b2.com";
        String careworkerLoginPassword = "careworker_b2";

        CareworkerRequest careworkerRequest = new CareworkerRequest(institutionId, careworkerName,
            careworkerEmail, careworkerPhone, careworkerLoginPassword);

        //when
        var response = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri(beforeUri)
            .requestBody(careworkerRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<CareworkerResponse>>() {
            })
            .getBody();

        //then

        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.response().getEmail()).isEqualTo(careworkerEmail);
        assertThat(response.response().getName()).isEqualTo(careworkerName);
        assertThat(response.response().getPhone()).isEqualTo(careworkerPhone);
        assertThat(response.response().getInstitutionId()).isEqualTo(institutionId);

        //given : 수정 후 요양보호사 정보
        String careworkerPhone2 = "01032121163";
        String careworkerName2 = "careworker_b2_2";
        String careworkerEmail2 = "b22@b22.com";
        String careworkerLoginPassword2 = "careworker_b2_2";

        //이름 변경
        CareworkerRequest careworkerChangeNameRequest = new CareworkerRequest(institutionId,
            careworkerName2, careworkerEmail, careworkerPhone, careworkerLoginPassword);
        //이름 + 전화번호 변경
        CareworkerRequest careworkerChangePhoneRequest = new CareworkerRequest(institutionId,
            careworkerName2, careworkerEmail, careworkerPhone2, careworkerLoginPassword);
        //이름 + 전화번호 + 이메일 변경
        CareworkerRequest careworkerChangeEmailRequest = new CareworkerRequest(institutionId,
            careworkerName2, careworkerEmail2, careworkerPhone2, careworkerLoginPassword);
        //이름 + 전화번호 + 이메일 + 비밀번호 변경
        CareworkerRequest careworkerChangePasswordRequest = new CareworkerRequest(institutionId,
            careworkerName2, careworkerEmail2, careworkerPhone2, careworkerLoginPassword2);

        String testUri = "/admin/careworker/" + response.response().getId();

        //when : 요양보호사의 이름만을 수정한다.
        var responseName = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri(testUri)
            .requestBody(careworkerChangeNameRequest)
            .put()
            .toEntity(new ParameterizedTypeReference<ApiResult<CareworkerResponse>>() {
            })
            .getBody();

        //then : 요양보호사의 이름만을 수정한 경우
        assertThat(responseName).isNotNull();
        assertThat(responseName.success()).isTrue().as("요양보호사 이름 변경 성공");
        assertThat(responseName.response().getEmail()).isEqualTo(careworkerEmail);
        assertThat(responseName.response().getName()).isEqualTo(careworkerName2);
        assertThat(responseName.response().getPhone()).isEqualTo(careworkerPhone);
        assertThat(responseName.response().getInstitutionId()).isEqualTo(institutionId);

        //when : 요양보호사의 전화번호만을 수정한다.
        var responsePhone = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri(testUri)
            .requestBody(careworkerChangePhoneRequest)
            .put()
            .toEntity(new ParameterizedTypeReference<ApiResult<CareworkerResponse>>() {
            })
            .getBody();

        //then : <이전수정내역>요양보호사의 이름 + 전화번호만을 수정한 경우
        assertThat(responsePhone).isNotNull();
        assertThat(responsePhone.success()).isTrue().as("요양보호사 전화번호 변경 성공");
        assertThat(responsePhone.response().getEmail()).isEqualTo(careworkerEmail);
        assertThat(responsePhone.response().getName()).isEqualTo(
            careworkerName2); //위의 이름 수정이 그대로 반영되어있음
        assertThat(responsePhone.response().getPhone()).isEqualTo(careworkerPhone2);
        assertThat(responsePhone.response().getInstitutionId()).isEqualTo(institutionId);

        //when : 요양보호사의 이메일만을 수정한다.
        var responseEmail = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri(testUri)
            .requestBody(careworkerChangeEmailRequest)
            .put()
            .toEntity(new ParameterizedTypeReference<ApiResult<CareworkerResponse>>() {
            })
            .getBody();

        //then : <이전수정내역>요양보호사의 이름,전화번호 + 이메일만을 수정한 경우
        assertThat(responseEmail).isNotNull();
        assertThat(responseEmail.success()).isTrue().as("요양보호사 이메일 변경 성공");
        assertThat(responseEmail.response().getEmail()).isEqualTo(careworkerEmail2);
        assertThat(responseEmail.response().getName()).isEqualTo(
            careworkerName2); //위의 이름 수정이 그대로 반영되어있음
        assertThat(responseEmail.response().getPhone()).isEqualTo(
            careworkerPhone2); //위의 전화번호 수정이 그대로 반영되어있음
        assertThat(responseEmail.response().getInstitutionId()).isEqualTo(institutionId);

        /*

        비밀번호 변경은 지원하지 않는 정책

        //when : 요양보호사의 비밀번호만을 수정한다.
        var responsePassword = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri(testUri)
            .requestBody(careworkerChangePasswordRequest)
            .put()
            .toEntity(new ParameterizedTypeReference<ApiResult<CareworkerResponse>>() {
            })
            .getBody();

        //then : <이전수정내역>요양보호사의 이름,전화번호,이메일 + 비밀번호만을 수정한 경우
        assertThat(responsePassword).isNotNull();
        assertThat(responsePassword.success()).isTrue().as("요양보호사 비밀번호 변경 성공");
        assertThat(responsePassword.response().getEmail()).isEqualTo(
            careworkerEmail2); //위의 이메일 수정이 그대로 반영되어있음
        assertThat(responsePassword.response().getName()).isEqualTo(
            careworkerName2); //위의 이름 수정이 그대로 반영되어있음
        assertThat(responsePassword.response().getPhone()).isEqualTo(
            careworkerPhone2); //위의 전화번호 수정이 그대로 반영되어있음
        assertThat(responsePassword.response().getInstitutionId()).isEqualTo(institutionId);

        //변경된 비밀번호로 로그인 확인
        assertThatNoException().as("변경된 비밀번호로 로그인 성공").isThrownBy(() -> {
            testHelper.user(Role.CAREWORKER, careworkerChangePasswordRequest.getPhone(),
                    careworkerChangePasswordRequest.getLoginPassword())
                .uri("/careworker")
                .get()
                .toEntity(new ParameterizedTypeReference<ApiResult<CareworkerResponse>>() {
                })
                .getBody();
        });

         */

    }
}
