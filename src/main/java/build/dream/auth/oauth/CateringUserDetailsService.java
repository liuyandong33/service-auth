package build.dream.auth.oauth;

import build.dream.auth.constants.Constants;
import build.dream.auth.services.PrivilegeService;
import build.dream.auth.services.SystemUserService;
import build.dream.auth.services.TenantService;
import build.dream.common.api.ApiRest;
import build.dream.common.auth.TenantUserDetails;
import build.dream.common.catering.domains.Branch;
import build.dream.common.saas.domains.PosPrivilege;
import build.dream.common.saas.domains.SystemUser;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.saas.domains.TenantSecretKey;
import build.dream.common.utils.CommonUtils;
import build.dream.common.utils.JacksonUtils;
import build.dream.common.utils.ProxyUtils;
import build.dream.common.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;

@Component
public class CateringUserDetailsService implements UserDetailsService {
    @Autowired
    private SystemUserService systemUserService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private PrivilegeService privilegeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = systemUserService.findByLoginNameOrEmailOrMobile(username);
        ValidateUtils.notNull(systemUser, "用户不存在！");

        int userType = systemUser.getUserType();
        ValidateUtils.isTrue(userType == Constants.USER_TYPE_TENANT || userType == Constants.USER_TYPE_TENANT_EMPLOYEE, "用户类型不正确！");

        ValidateUtils.isTrue(systemUser.isAccountNonExpired(), "账号已过期！");
        ValidateUtils.isTrue(systemUser.isAccountNonLocked(), "账号已锁定！");
        ValidateUtils.isTrue(systemUser.isCredentialsNonExpired(), "凭证已过期！");
        ValidateUtils.isTrue(systemUser.isEnabled(), "账号已禁用！");

        BigInteger userId = systemUser.getId();
        List<PosPrivilege> posPrivileges = privilegeService.obtainUserPosPrivileges(userId);
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (PosPrivilege posPrivilege : posPrivileges) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(posPrivilege.getPrivilegeCode());
            authorities.add(simpleGrantedAuthority);
        }

        BigInteger tenantId = systemUser.getTenantId();
        Tenant tenant = tenantService.obtainTenant(tenantId);
        ValidateUtils.notNull(tenant, "商户不存在！");

        TenantSecretKey tenantSecretKey = tenantService.obtainTenantSecretKey(tenantId);
        ValidateUtils.notNull(tenantSecretKey, "商户秘钥不存在！");

        String partitionCode = tenant.getPartitionCode();
        Map<String, Object> obtainBranchInfoParams = new HashMap<String, Object>();
        obtainBranchInfoParams.put("tenantId", tenantId);
        obtainBranchInfoParams.put("userId", userId);
        ApiRest apiRest = ProxyUtils.doPostWithJsonRequestBody(partitionCode, CommonUtils.getServiceName(tenant.getBusiness()), "user", "obtainBranchInfo", JacksonUtils.writeValueAsString(obtainBranchInfoParams));
        ValidateUtils.isTrue(apiRest.isSuccessful(), apiRest.getError());
        Branch branch = (Branch) apiRest.getData();

        TenantUserDetails tenantUserDetails = new TenantUserDetails();
        tenantUserDetails.setUsername(username);
        tenantUserDetails.setPassword(systemUser.getPassword());
        tenantUserDetails.setAuthorities(authorities);
        tenantUserDetails.setUserId(userId);
        tenantUserDetails.setTenantId(tenantId);
        tenantUserDetails.setTenantCode(tenant.getCode());
        tenantUserDetails.setBranchId(branch.getId());
        tenantUserDetails.setBranchCode(branch.getCode());
        tenantUserDetails.setPartitionCode(partitionCode);
        tenantUserDetails.setPublicKey(tenantSecretKey.getPublicKey());
        tenantUserDetails.setPrivateKey(tenantSecretKey.getPrivateKey());
        return tenantUserDetails;
    }
}
