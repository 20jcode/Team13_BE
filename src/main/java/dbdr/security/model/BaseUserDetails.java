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

    private final Long id;
    private final String userLoginId;
    private final String password;
    private final Role role;
    private final Long institutionId;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> String.valueOf(role));
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

}
