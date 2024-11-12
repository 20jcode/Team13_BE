package dbdr.testhelper;

import dbdr.security.dto.LoginRequest;
import dbdr.security.dto.TokenDTO;
import dbdr.security.model.Role;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

public class TestHelper {

    private RestClient restClient;
    private Integer port;
    private String authHeader;
    private Object requestBody;
    private String uri;

    private Role userRole;
    private String loginId;
    private String password;

    public TestHelper(int port){
        this.port = port;
        this.restClient = RestClient.builder().baseUrl("http://localhost:" + port + "/v1")
                .defaultHeaders(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .build();
    }

    public TestHelper user(Role userRole,String loginId,String password){
        this.userRole = userRole;
        this.loginId = loginId;
        this.password = password;
        return this;
    }
    public TestHelper uri(String uri){
        this.uri = uri;
        return this;
    }

    public TestHelper requestBody(Object requestBody){
        this.requestBody = requestBody;
        return this;
    }

    public ResponseSpec get(){
        authHeader = userLogin();
        return restClient.get().uri(uri).header("Authorization",authHeader).retrieve();
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
        TokenDTO tokenDTO = restClient.post().uri("/auth/login/" + userRole.toString()).body(convertUserToLoginRequest()).retrieve().toEntity(
            TokenDTO.class).getBody();
        return "Bearer " + tokenDTO.accessToken();
    }

    private LoginRequest convertUserToLoginRequest(){
        return new LoginRequest(loginId,password);
    }


}
