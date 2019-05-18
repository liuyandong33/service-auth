package build.dream.auth.oauth;

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
        Collection<? extends GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        CateringUserDetails cateringUserDetails = new CateringUserDetails();
        cateringUserDetails.setUsername(username);
        cateringUserDetails.setPassword(passwordEncoder.encode("123456"));
        cateringUserDetails.setAuthorities(authorities);
        cateringUserDetails.setAccountNonExpired(true);
        cateringUserDetails.setAccountNonLocked(true);
        cateringUserDetails.setCredentialsNonExpired(true);
        cateringUserDetails.setEnabled(true);
        return cateringUserDetails;
    }
}
