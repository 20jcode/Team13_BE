package dbdr.e2etest.Admin;

import static org.assertj.core.api.Assertions.assertThat;

import dbdr.domain.admin.entity.Admin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminTest {

    @LocalServerPort
    private int port;


    @Test
    @DisplayName("기존 관리자 계정 전체 조회")
    public void getAdminTest(){
        //given
        Admin admin = Admin.builder().loginId("ad").loginPassword("123").build();
        Admin admin2 = Admin.builder().loginId("ad2").loginPassword("123").build();

    }


}
