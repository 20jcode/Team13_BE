package dbdr.testhelper;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import dbdr.security.dto.LoginRequest;
import dbdr.security.model.Role;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

public class TestHelper {

    private RestClient restClient;
    private Integer port;
    private Object user;
    private String authHeader;
    private Map<String,String> queryParam;
    private Object requestBody;
    private String uri;

    private Role userRole;
    private String loginId;
    private String password;

    //TestHelper.port(port).user(Object obj).get//post//put//delete -> return ResponseEntity

    public TestHelper(int port,RestClient restClient){
        this.port = port;
        this.restClient = restClient;
    }

    public TestHelper user(Guardian guardian){
        user = guardian;
        userRole = Role.GUARDIAN;
        loginId = guardian.getLoginId();
        password = guardian.getLoginPassword();
        return this;
    }

    public TestHelper user(Careworker careworker){
        user = careworker;
        userRole = Role.CAREWORKER;
        loginId = careworker.getLoginId();
        password = careworker.getLoginPassword();
        return this;
    }

    public TestHelper user(Institution institution){
        user = institution;
        userRole = Role.INSTITUTION;
        loginId = institution.getLoginId();
        password = institution.getLoginPassword();
        return this;
    }
    /* TODO : admin
    public TestHelper admin(){
        user = Institution.builder().institutionName("admin").institutionNumber(0L).build();
        userRole = Role.ADMIN;
    }

     */

    public TestHelper uri(String uri){
        this.uri = uri;
        return this;
    }

    public TestHelper queryParam(String key, String value){
        queryParam.put(key,value);
        return this;
    }
    public TestHelper requestBody(Object requestBody){
        this.requestBody = requestBody;
        return this;
    }

    public ResponseSpec get(){
        authHeader = userLogin();
        return restClient.get().uri(uriBuilder -> {
            var ans = uriBuilder.path(uri);
            queryParam.forEach(ans::queryParam);
            return ans.build();
        }).header("Authorization",authHeader).retrieve();
    }

    public ResponseSpec post(){
        authHeader = userLogin();
        return restClient.post().uri(uri).body(requestBody).header("Authorization",authHeader).retrieve();
    }

    public ResponseSpec put(){
        authHeader = userLogin();
        return restClient.put().uri(uri).body(requestBody).header("Authorization",authHeader).retrieve();
    }

    public ResponseSpec delete(){
        authHeader = userLogin();
        return restClient.delete().uri(uri).header("Authorization",authHeader).retrieve();
    }


    private String userLogin() {
        return restClient.post().uri("/login").body(convertUserToLoginRequest()).retrieve().toEntity(String.class).getBody();
    }

    private LoginRequest convertUserToLoginRequest(){
        return new LoginRequest(loginId,password);
    }


}
