package build.dream.auth.oauth;

import build.dream.auth.constants.Constants;
import build.dream.auth.services.AgentService;
import build.dream.auth.services.PrivilegeService;
import build.dream.auth.services.SystemUserService;
import build.dream.auth.services.TenantService;
import build.dream.common.api.ApiRest;
import build.dream.common.auth.AgentUserDetails;
import build.dream.common.auth.CateringUserDetails;
import build.dream.common.auth.IotUserDetails;
import build.dream.common.saas.domains.*;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.*;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private SystemUserService systemUserService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private AgentService agentService;

    private static final String PUBLIC_KEY = "Public-Key";
    private static final String PRIVATE_KEY = "Private-Key";
    private static final String PLATFORM_PUBLIC_KEY = "Platform-Public-Key";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = systemUserService.findByLoginNameOrEmailOrMobile(username);
        ValidateUtils.notNull(systemUser, "用户不存在！");

        ValidateUtils.isTrue(systemUser.isAccountNonExpired(), "账号已过期！");
        ValidateUtils.isTrue(systemUser.isAccountNonLocked(), "账号已锁定！");
        ValidateUtils.isTrue(systemUser.isCredentialsNonExpired(), "凭证已过期！");
        ValidateUtils.isTrue(systemUser.isEnabled(), "账号已禁用！");

        int userType = systemUser.getUserType();
        if (userType == Constants.USER_TYPE_AGENT) {
            return buildAgentUserDetails(username, systemUser);
        }

        if (userType == Constants.USER_TYPE_TENANT || userType == Constants.USER_TYPE_TENANT_EMPLOYEE) {
            return buildTenantUserDetails(username, systemUser);
        }
        return null;
    }

    private UserDetails buildAgentUserDetails(String username, SystemUser systemUser) {
        BigInteger agentId = systemUser.getAgentId();
        Agent agent = agentService.obtainAgent(agentId);
        ValidateUtils.notNull(agent, "代理商不存在！");

        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        AgentUserDetails agentUserDetails = new AgentUserDetails();
        agentUserDetails.setAuthorities(authorities);
        agentUserDetails.setUsername(username);
        agentUserDetails.setPassword(systemUser.getPassword());
        agentUserDetails.setAccountNonExpired(systemUser.isAccountNonExpired());
        agentUserDetails.setAccountNonLocked(systemUser.isAccountNonLocked());
        agentUserDetails.setCredentialsNonExpired(systemUser.isCredentialsNonExpired());
        agentUserDetails.setEnabled(systemUser.isEnabled());
        agentUserDetails.setUserId(systemUser.getId());
        agentUserDetails.setAgentId(agentId);
        agentUserDetails.setAgentCode(agent.getCode());
        return agentUserDetails;
    }

    private UserDetails buildTenantUserDetails(String username, SystemUser systemUser) {
        BigInteger tenantId = systemUser.getTenantId();
        Tenant tenant = tenantService.obtainTenant(tenantId);
        ValidateUtils.notNull(tenant, "商户不存在！");

        String business = tenant.getBusiness();
        if (Constants.BUSINESS_CATERING.equals(business)) {
            return buildCateringUserDetails(username, systemUser, tenant);
        }

        if (Constants.BUSINESS_IOT.equals(business)) {
            return buildIotUserDetails(username, systemUser, tenant);
        }

        return null;
    }

    private UserDetails buildIotUserDetails(String username, SystemUser systemUser, Tenant tenant) {
        BigInteger userId = systemUser.getId();
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        BigInteger tenantId = systemUser.getTenantId();

        TenantSecretKey tenantSecretKey = tenantService.obtainTenantSecretKey(tenantId);
        ValidateUtils.notNull(tenantSecretKey, "商户秘钥不存在！");

        String partitionCode = tenant.getPartitionCode();
        Map<String, Object> obtainBranchInfoParams = new HashMap<String, Object>();
        obtainBranchInfoParams.put("tenantId", tenantId);
        obtainBranchInfoParams.put("userId", userId);
        ApiRest apiRest = ProxyUtils.doPostWithJsonRequestBody(partitionCode, CommonUtils.getServiceName(tenant.getBusiness()), "user", "obtainBranchInfo", JacksonUtils.writeValueAsString(obtainBranchInfoParams));
        ValidateUtils.isTrue(apiRest.isSuccessful(), apiRest.getError());

        Map<String, Object> branchInfo = (Map<String, Object>) apiRest.getData();
        BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(branchInfo, "id"));
        String branchCode = MapUtils.getString(branchInfo, "code");

        IotUserDetails iotUserDetails = new IotUserDetails();
        iotUserDetails.setUsername(username);
        iotUserDetails.setPassword(systemUser.getPassword());
        iotUserDetails.setAuthorities(authorities);
        iotUserDetails.setUserId(userId);
        iotUserDetails.setTenantId(tenantId);
        iotUserDetails.setTenantCode(tenant.getCode());
        iotUserDetails.setBranchId(branchId);
        iotUserDetails.setBranchCode(branchCode);
        iotUserDetails.setPartitionCode(partitionCode);
        iotUserDetails.setPublicKey(tenantSecretKey.getPublicKey());
        iotUserDetails.setPrivateKey(tenantSecretKey.getPrivateKey());

        HttpServletResponse httpServletResponse = ApplicationHandler.getHttpServletResponse();
        httpServletResponse.addHeader(PUBLIC_KEY, tenantSecretKey.getPublicKey());
        httpServletResponse.addHeader(PRIVATE_KEY, tenantSecretKey.getPrivateKey());
        httpServletResponse.addHeader(PLATFORM_PUBLIC_KEY, tenantSecretKey.getPlatformPublicKey());
        return iotUserDetails;
    }

    private UserDetails buildCateringUserDetails(String username, SystemUser systemUser, Tenant tenant) {
        BigInteger userId = systemUser.getId();
        List<PosPrivilege> posPrivileges = privilegeService.obtainUserPosPrivileges(userId);
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (PosPrivilege posPrivilege : posPrivileges) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(posPrivilege.getPrivilegeCode());
            authorities.add(simpleGrantedAuthority);
        }

        BigInteger tenantId = systemUser.getTenantId();

        TenantSecretKey tenantSecretKey = tenantService.obtainTenantSecretKey(tenantId);
        ValidateUtils.notNull(tenantSecretKey, "商户秘钥不存在！");

        String partitionCode = tenant.getPartitionCode();
        Map<String, Object> obtainBranchInfoParams = new HashMap<String, Object>();
        obtainBranchInfoParams.put("tenantId", tenantId);
        obtainBranchInfoParams.put("userId", userId);
        ApiRest apiRest = ProxyUtils.doPostWithJsonRequestBody(partitionCode, CommonUtils.getServiceName(tenant.getBusiness()), "user", "obtainBranchInfo", JacksonUtils.writeValueAsString(obtainBranchInfoParams));
        ValidateUtils.isTrue(apiRest.isSuccessful(), apiRest.getError());

        Map<String, Object> branchInfo = (Map<String, Object>) apiRest.getData();
        BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(branchInfo, "id"));
        String branchCode = MapUtils.getString(branchInfo, "code");

        CateringUserDetails cateringUserDetails = new CateringUserDetails();
        cateringUserDetails.setUsername(username);
        cateringUserDetails.setPassword(systemUser.getPassword());
        cateringUserDetails.setAuthorities(authorities);
        cateringUserDetails.setUserId(userId);
        cateringUserDetails.setTenantId(tenantId);
        cateringUserDetails.setTenantCode(tenant.getCode());
        cateringUserDetails.setBranchId(branchId);
        cateringUserDetails.setBranchCode(branchCode);
        cateringUserDetails.setPartitionCode(partitionCode);
        cateringUserDetails.setPublicKey(tenantSecretKey.getPublicKey());
        cateringUserDetails.setPrivateKey(tenantSecretKey.getPrivateKey());

        HttpServletResponse httpServletResponse = ApplicationHandler.getHttpServletResponse();
        httpServletResponse.addHeader(PUBLIC_KEY, tenantSecretKey.getPublicKey());
        httpServletResponse.addHeader(PRIVATE_KEY, tenantSecretKey.getPrivateKey());
        httpServletResponse.addHeader(PLATFORM_PUBLIC_KEY, tenantSecretKey.getPlatformPublicKey());
        return cateringUserDetails;
    }
}
