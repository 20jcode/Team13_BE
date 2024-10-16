package dbdr.security.model;


import java.util.ArrayList;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
@Builder
@Getter
public class BaseUserDetails implements UserDetails {

    @NonNull
    private final Long id;
    @NonNull
    private final String userLoginId;
    @NonNull
    private final Role role;
    @NonNull
    private final Long institutionId;

    private final String password;


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

    public boolean isAdmin(){
        return role.equals(Role.ADMIN);
    }

    public boolean isInstitution(){
        return role.equals(Role.INSTITUTION);
    }

    public boolean isCareworker(){
        return role.equals(Role.CAREWORKER);
    }

    public boolean isGuardian(){
        return role.equals(Role.GUARDIAN);
    }

}
