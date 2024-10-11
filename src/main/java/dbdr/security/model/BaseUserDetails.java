package dbdr.security.model;


import java.util.ArrayList;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
@Builder
@Getter
public class BaseUserDetails implements UserDetails {

    private final Long id; //db pk
    private final String userLoginId; //로그인 시 사용할 id
    private final String password;
    private final String role; //TODO : role에 대해서 String으로 Role 클래스로 처리할 수 있도록?
    private final Long institutionId; //TODO : 회원식별에 요양원ID이외의 값이 필요없는지?

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> role);
        return collection;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userLoginId;
    }

    public Role getRole() {
        return Role.valueOf(role);
    }
}
