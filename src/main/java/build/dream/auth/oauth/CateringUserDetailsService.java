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

        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtJ0/E/yt86Z05Ive4bIgRA7zT0RweUmVU8FsZo1Eh/C5ZrCVeZU93Ul6naK+7hCr9311fLlxmp5KEdAdWRukHMTwlBpSNihTBSsxCmfX+dNw3lqXcWVBbf8mDO4GZUdGRlWlWgdTTMoTZccII4oHHNlCVkSCrCys3SMf47nVm2Qrw8y+dwGetU3V0cIUOoGUbS5nWgthV3qEbjjsKASTwk0p10d2tdNrqU08jqwi9ckaDiuz3GY8KFCJcYf4xay3+bZxFjGcZTjP0UEEQ1dNio4sCSbnWzMvkV1ahTW8GqjYj6LuLRKLLBnPqV5pmD554WP0w37sqXWyO0vi7mNUQwIDAQAB";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC0nT8T/K3zpnTki97hsiBEDvNPRHB5SZVTwWxmjUSH8LlmsJV5lT3dSXqdor7uEKv3fXV8uXGankoR0B1ZG6QcxPCUGlI2KFMFKzEKZ9f503DeWpdxZUFt/yYM7gZlR0ZGVaVaB1NMyhNlxwgjigcc2UJWRIKsLKzdIx/judWbZCvDzL53AZ61TdXRwhQ6gZRtLmdaC2FXeoRuOOwoBJPCTSnXR3a102upTTyOrCL1yRoOK7PcZjwoUIlxh/jFrLf5tnEWMZxlOM/RQQRDV02KjiwJJudbMy+RXVqFNbwaqNiPou4tEossGc+pXmmYPnnhY/TDfuypdbI7S+LuY1RDAgMBAAECggEBAKT4C6lh3jDyFUq7RvS3EimXzjzUsjbWMwKxZSW2WqYgBff5yk2nNz5r4wcgo2wm5ivD+A/YAh/L/LOQd33wdVGhwGxGLlkxM8NA1CZA+1BBBJLUBAebCMxDhKnCryzKkTGlAMbecgNtf5Iy3Qg5Bo4fEyxgn0pL9Ah4EKScFLM8nY9JKhzvnyzeJvJHxz/pPAv0rmpQ/lgkrzrVsS6/CYGK7X99EPE32mEF1qdKRnajtknR2XZQzixWQbM65VkJKPPBcOS2lRuC6WQjJ/lKgBVff7ncDBt5Kwgvp6FrU9FcznNQsKZfQshcV1CRQBWNwjtM5VxroOUmnRcbv5hb+qECgYEA6FsVASGnIN11aqXO/C0nyZNSME2GUgzFltRR0aBpJg5Cpv7TekaEE80R0bYHIMP+Dv1LARV24+0cbdVosropxMKClAsZzrXANyBgbKyvMDlhMc0IyuOFL6nYX1DDzimMQJgTywR2rTt7LP+516lYm/0lUoa0+Cfr0eV5rBiFGz8CgYEAxv5JOZ7wRr2h5pgEbvjWf+KHtyoVHIcHWa7AcVOeinT7/eIBMvX4NDk4+PaCOTAxY0/ul4rutambiuqAgUSrxmR5NfRCCCWTKLt/h2ddD+OTWw1iJxweq4MGHqDEs/KUh1bCzKM2TNxRYiq+alHIfuTqYYjkT4s9d/6tN9yB2f0CgYA2Pq1UqkqePZVf5H3CGbikMqSJak/lb93hLIg9bDmgDMw6uFsevL+w77stPlXDrH3veeq8wgoThBOTALOEpjmCGfbqIP1RaNULCZ/5PwdXNs4eFkPrOdqqGfjNp8lOpBx+KALW7p/WKYDELJ9yXbK2GA9SiOhO1uMTCYOyTbcleQKBgBhwiKvEAnyds7Gvf/PlKesA9mwfhgI/7z4rgUUp4PHeVkijJkSNAxPe50lkdS/y5E3vNj10ecbj8s+H54p41aQDGQPwYN/BRaaeCJansj1OVP280g49nOKxr+G5Gw+Cvo00sa4SaYvdDz3ARCyoqLg1AzhBaNEAJ+eB5uJSebw9AoGBAIrwdn2pwk4eM6B8iVgyzXzA0xJZr5Gwqg+5V9eJCg45ml0QciQukrACpa8zS1nmecET3AvzDrJqcovQ+qX7gNslooyiQkUD5rmNSp+61MWSI98W3WO5UeIpP6Kb10t4+b+7XpnGAmbI2GfM68sswaejvmg3tPsxw3Ebr0ZQmfPL";

        TenantUserDetails tenantUserDetails = new TenantUserDetails();
        tenantUserDetails.setUsername(username);
        tenantUserDetails.setPassword(passwordEncoder.encode("123456"));
        tenantUserDetails.setAuthorities(authorities);
        tenantUserDetails.setPublicKey(publicKey);
        tenantUserDetails.setPrivateKey(privateKey);
        return tenantUserDetails;
    }
}
