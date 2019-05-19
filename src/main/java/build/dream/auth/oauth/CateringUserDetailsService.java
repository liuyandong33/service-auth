package build.dream.auth.oauth;

import build.dream.common.auth.TenantUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

@Component
public class CateringUserDetailsService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        TenantUserDetails tenantUserDetails = new TenantUserDetails();
        tenantUserDetails.setUsername(username);
        tenantUserDetails.setPassword(passwordEncoder.encode("123456"));
        tenantUserDetails.setAuthorities(authorities);
        return tenantUserDetails;
    }
}
