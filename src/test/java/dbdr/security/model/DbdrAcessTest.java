package dbdr.security.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.entity.Recipient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DbdrAcessTest {
    //인가과정을 담당하는 Model Test

    //관리자는 모든 유형에 대해 접근 권한을 가진다.
    @Test
    @DisplayName("관리자는 모든 유형 접근 가능")
    void admin_has_access_permission() {
        //given
        DbdrAcess dbdrAcess = new DbdrAcess();

        //접근한 사용자는 관리자
        BaseUserDetails userDetails = new BaseUserDetails(1L,"LoginId",Role.ADMIN,"password");

        //접근 테스트 대상
        Institution institution = Institution.builder().build();
        Institution institution1 = Institution.builder().institutionName("test").institutionNumber(3L).build();
        Careworker careworker = Careworker.builder().build();
        Guardian guardian = Guardian.builder().build();
        //when
        boolean institutionResult = dbdrAcess.hasAccessPermission(Role.INSTITUTION, userDetails, institution);
        boolean institutionResult1 = dbdrAcess.hasAccessPermission(Role.INSTITUTION, userDetails, institution1);
        boolean careworkerResult = dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, careworker);
        boolean guardianResult = dbdrAcess.hasAccessPermission(Role.GUARDIAN, userDetails, guardian);

        //then
        assertThat(institutionResult).isTrue();
        assertThat(institutionResult1).isTrue();
        assertThat(careworkerResult).isTrue();
        assertThat(guardianResult).isTrue();
    }

    @Test
    @DisplayName("요양원은 자신의 소속 요양보호사만 접근할 수 있다.")
    void institution_has_access_permission() {
        //given
        DbdrAcess dbdrAcess = new DbdrAcess();

        //접근한 사용자는 요양원
        BaseUserDetails userDetails = new BaseUserDetails(1L,"LoginId",Role.INSTITUTION,"password",1L);

        //접근 테스트 대상

        Institution institutionOne = Institution.builder().build(); //본인의 요양원
        Institution institutionSecond = Institution.builder().institutionName("test").institutionNumber(3L).build(); //타 요양원

        Institution spyInstitutionOne = Mockito.spy(institutionOne);
        Institution spyInstitutionSecond = Mockito.spy(institutionSecond);

        doReturn(1L).when(spyInstitutionOne).getId();
        doReturn(3L).when(spyInstitutionSecond).getId();

        Careworker careworker = Careworker.builder().institution(spyInstitutionOne).build(); //같은 요양원 소속 요양보호사
        Careworker careworker2 = Careworker.builder().institution(spyInstitutionSecond).build(); //타 기관 소속 요양보호사

        //when
        boolean careworkerResult = dbdrAcess.hasAccessPermission(Role.INSTITUTION, userDetails, careworker);
        boolean careworkerResult2 = dbdrAcess.hasAccessPermission(Role.INSTITUTION, userDetails, careworker2);

        //then
        assertThat(careworkerResult).isTrue();
        assertThat(careworkerResult2).isFalse();
    }

    @Test
    @DisplayName("보호자와 요양보호사는 요양원의 Role에 접근할 수 없다.")
    void careworker_has_no_access_permission() {
        //given
        DbdrAcess dbdrAcess = new DbdrAcess();

        //접근한 사용자는 요양보호사
        BaseUserDetails userDetails = new BaseUserDetails(123L,"LoginId",Role.CAREWORKER,"password",1L);

        //접근하는 사용자가 소속된 요양원
        Institution institution = Mockito.spy(Institution.builder().build());
        doReturn(1L).when(institution).getId();
        doReturn(123L).when(institution).getInstitutionNumber();
        doReturn("testinstitution").when(institution).getInstitutionName();

        //접근 테스트 대상
        // 1. 요양보호사 본인
        Careworker careworker = Mockito.spy(Careworker.builder().institution(institution).build());
        doReturn(123L).when(careworker).getId();

        // 1-1 같은 요양원 소속 요양보호사
        Careworker coworker1 = Mockito.spy(Careworker.builder().institution(institution).build());
        doReturn(1234L).when(coworker1).getId();

        // 2. 타 요양원의 소속 요양보호사
        Institution institution2 = Mockito.spy(Institution.builder().institutionNumber(1234L).build());
        doReturn(2L).when(institution2).getId();

        Careworker careworker2 = Mockito.spy(Careworker.builder().institution(institution2).build());
        doReturn(124L).when(careworker2).getId();

        // 3. 같은 요양원 소속의 보호자
        Guardian guardian = Mockito.spy(Guardian.builder().build());
        doReturn(12322L).when(guardian).getId();

        // 4. 본인과 관련없는 환자
        Recipient recipient = Mockito.spy(Recipient.builder().build());
        doReturn(careworker2).when(recipient).getCareworker();

        // 5. 본인이 담당하는 환자
        Recipient recipient2 = Mockito.spy(Recipient.builder().build());
        doReturn(careworker).when(recipient2).getCareworker();

        // 6. 같은 요양원에 소속하지만 본인이 담당하지 않는 환자
        Recipient recipient3 = Mockito.spy(Recipient.builder().build());
        doReturn(coworker1).when(recipient3).getCareworker();


        //when
        //요양보호사는 요양원의 권한에 접근할 수 없다.
        boolean institutionResult = dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, institution);

        //요양보호사는 자신의 정보에 접근할 수 있다.
        boolean careworkerResult = dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, careworker);

        //요양보호사는 보호자의 정보에 접근할 수 없다.
        boolean guardianResult = dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, guardian);

        //요양보호사는 자신이 담당하지 않는 돌봄대상자의 정보에 접근할 수 없다.
        boolean recipientResult = dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, recipient);

        //요양보호사는 자신이 담당하는 돌봄대상자의 정보에 접근할 수 있다.
        boolean recipientResult2 = dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, recipient2);

        //요양보호사는 같은 요양원에 소속되어 있지만 본인이 담당하지 않는 환자의 정보에 접근할 수 없다.
        boolean recipientResult3 = dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, recipient3);

        //then
        assertThat(institutionResult).as("요양보호사의 -> 요양원 접근 거부").isFalse();
        assertThat(careworkerResult).as("요양보호사의 -> 본인 요양보호사 접근 가능").isTrue();
        assertThat(recipientResult).as("요양보호사의 -> 타인 요양보호사 담당 환자 접근 거부").isFalse();
        assertThat(guardianResult).as("요양보호사의 -> 보호자 접근 거부").isFalse();
        assertThat(recipientResult2).as("요양보호사의 -> 본인 담당 환자 접근 가능").isTrue();
        assertThat(recipientResult3).as("요양보호사의 -> 본인이 담당하지 않는 환자 접근 거부").isFalse();
    }

    @Test
    @DisplayName("보호자는 요양원 Role에 접근할 수 없다.")
    void guardian_has_no_access_permission() {
        //given
        DbdrAcess dbdrAcess = new DbdrAcess();

        //접근한 사용자는 보호자
        BaseUserDetails userDetails = new BaseUserDetails(123L, "LoginId", Role.GUARDIAN,
            "password", 1L);

        //접근하는 사용자가 소속된 요양원
        Institution institution = Mockito.spy(Institution.builder().build());
        doReturn(1L).when(institution).getId();
        doReturn(123L).when(institution).getInstitutionNumber();
        doReturn("testinstitution").when(institution).getInstitutionName();

        //접근 테스트 대상
        // 1. 같은 요양원의 소속 타 요양보호사
        Careworker careworker = Mockito.spy(Careworker.builder().institution(institution).build());
        doReturn(123L).when(careworker).getId();

        // 2. 타 요양원의 소속 요양보호사
        Institution institution2 = Mockito.spy(
            Institution.builder().institutionNumber(1234L).build());
        doReturn(2L).when(institution2).getId();

        Careworker careworker2 = Mockito.spy(
            Careworker.builder().institution(institution2).build());
        doReturn(124L).when(careworker2).getId();

        // 3. 같은 요양원 소속의 보호자
        Guardian guardian = Mockito.spy(Guardian.builder().build());
        doReturn(12322L).when(guardian).getId();

        // 4. 본인과 관련없는 환자
        Recipient recipient = Mockito.spy(Recipient.builder().build());
        Guardian guardian1 = Mockito.spy(Guardian.builder().build());
        doReturn(careworker2).when(recipient).getCareworker();
        doReturn(guardian1).when(recipient).getGuardian();
        doReturn(2323232L).when(guardian1).getId();


        // 5. 본인의 환자
        Recipient recipient2 = Mockito.spy(Recipient.builder().build());
        doReturn(careworker).when(recipient2).getCareworker();
        doReturn(guardian).when(recipient2).getGuardian();
        doReturn(123L).when(guardian).getId();


        //when
        boolean institutionResult = dbdrAcess.hasAccessPermission(Role.GUARDIAN, userDetails,
            institution);
        boolean careworkerResult = dbdrAcess.hasAccessPermission(Role.GUARDIAN, userDetails,
            careworker);
        boolean guardianResult = dbdrAcess.hasAccessPermission(Role.GUARDIAN, userDetails,
            guardian);
        boolean recipientResult = dbdrAcess.hasAccessPermission(Role.GUARDIAN, userDetails,
            recipient);
        boolean recipientResult2 = dbdrAcess.hasAccessPermission(Role.GUARDIAN, userDetails,
            recipient2);


        //then
        assertThat(institutionResult).as("보호자의 -> 요양원 접근 거부").isFalse();
        assertThat(careworkerResult).as("보호자의 -> 요양보호사 접근 거부").isFalse();
        assertThat(recipientResult).as("보호자의 -> 타인 요양보호사 접근 거부").isFalse();
        assertThat(guardianResult).as("보호자의 -> 본인 접근 가능 ").isTrue();
        assertThat(recipientResult2).as("보호자의 -> 본인 환자 접근 가능").isTrue();


    }

    @Test
    @DisplayName("요양보호사는 자신과 연관된 돌봄대상자에게만 접근할 수 있다.")
    void careworker_has_access_permission() {
        //init
        DbdrAcess dbdrAcess = new DbdrAcess();

        //접근한 사용자는 요양보호사
        BaseUserDetails userDetails = new BaseUserDetails(123L, "LoginId", Role.CAREWORKER,
            "password", 1L);

        //접근하는 사용자가 소속된 요양원
        Institution institution = Mockito.spy(Institution.builder().build());
        doReturn(1L).when(institution).getId();
        doReturn(123L).when(institution).getInstitutionNumber();
        doReturn("testinstitution").when(institution).getInstitutionName();

        //접근 테스트 대상
        // 1. 본인
        Careworker careworker = Mockito.spy(Careworker.builder().institution(institution).build());
        doReturn(123L).when(careworker).getId();

        // 2. 타 요양원의 소속 요양보호사
        Institution institution2 = Mockito.spy(
            Institution.builder().institutionNumber(1234L).build());
        doReturn(2L).when(institution2).getId();

        Careworker careworker2 = Mockito.spy(
            Careworker.builder().institution(institution2).build());
        doReturn(124L).when(careworker2).getId();

        // 3. 같은 요양원 소속의 보호자
        Guardian guardian = Mockito.spy(Guardian.builder().build());
        doReturn(12322L).when(guardian).getId();
        doReturn(institution).when(guardian).getInstitution();

        // 4. 본인과 관련없는 돌봄대상자
        Recipient recipient = Mockito.spy(Recipient.builder().build());
        Guardian guardian1 = Mockito.spy(Guardian.builder().build());
        doReturn(careworker2).when(recipient).getCareworker();
        doReturn(guardian1).when(recipient).getGuardian();
        doReturn(2323232L).when(guardian1).getId();

        // 5. 본인의 돌봄대상자
        Recipient recipient2 = Mockito.spy(Recipient.builder().build());
        doReturn(careworker).when(recipient2).getCareworker();
        doReturn(guardian).when(recipient2).getGuardian();
        doReturn(123L).when(guardian).getId();

        assertThat(
            dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, institution)).as("요양보호사 -> 자신의 요양원 권한 접근").isFalse();
        assertThat(
            dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, careworker)).as("요양보호사 -> 자신의 정보 권한 접근").isTrue();
        assertThat(
            dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, careworker2)).as("요양보호사 -> 타 요양보호사 정보 권한 접근").isFalse();
        assertThat(dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, guardian)).as("요양보호사 -> 보호자 정보 권한 접근").isFalse();
        assertThat(
            dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, recipient)).as("요양보호사 -> 본인과 연관없는 돌봄대상자 정보 권한 접근").isFalse();
        assertThat(
            dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, recipient2)).as("요양보호사 -> 본인과 연관있는 돌봄대상자 정보 권한 접근").isTrue();


    }
}

