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
import build.dream.common.auth.VipUserDetails;
import build.dream.common.domains.catering.Vip;
import build.dream.common.domains.saas.*;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.stream.Collectors;

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
    private static final String CLIENT_ID = "client_id";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = ApplicationHandler.getRequestParameter(CLIENT_ID);
        if (Constants.O2O.equals(clientId)) {
            return buildVipUserDetails(username);
        }
        return buildUserDetails(username, clientId);
    }

    private UserDetails buildVipUserDetails(String username) {
        String tenantId = ApplicationHandler.getRequestParameter("tenantId");
        ValidateUtils.notBlank(tenantId, "商户ID不能为空！");

        String mainOpenId = ApplicationHandler.getRequestParameter("mainOpenId");
        String alipayUserId = ApplicationHandler.getRequestParameter("alipayUserId");
        String phoneNumber = ApplicationHandler.getRequestParameter("phoneNumber");

        ValidateUtils.isTrue(StringUtils.isNotBlank(mainOpenId) || StringUtils.isNotBlank(alipayUserId) || StringUtils.isNotBlank(phoneNumber), "mainOpenId、alipayUserId、phoneNumber不能同时为空！");

        Tenant tenant = TenantUtils.obtainTenantInfo(BigInteger.valueOf(Long.valueOf(tenantId)));

        Map<String, String> obtainVipInfoRequestParameters = new HashMap<String, String>();
        obtainVipInfoRequestParameters.put("tenantId", tenantId);
        if (StringUtils.isNotBlank(mainOpenId)) {
            obtainVipInfoRequestParameters.put("mainOpenId", mainOpenId);
        }
        if (StringUtils.isNotBlank(alipayUserId)) {
            obtainVipInfoRequestParameters.put("alipayUserId", alipayUserId);
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            obtainVipInfoRequestParameters.put("phoneNumber", phoneNumber);
        }
        ApiRest obtainVipInfoResult = ProxyUtils.doGetWithRequestParameters(tenant.getPartitionCode(), CommonUtils.getServiceName(tenant.getBusiness()), "o2o", "obtainVipInfo", obtainVipInfoRequestParameters);
        ValidateUtils.isTrue(obtainVipInfoResult.isSuccessful(), obtainVipInfoResult.getError());

        Vip vip = (Vip) obtainVipInfoResult.getData();

        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        VipUserDetails vipUserDetails = new VipUserDetails();
        vipUserDetails.setAuthorities(authorities);
        vipUserDetails.setUsername(username);
        vipUserDetails.setPassword(BCryptUtils.encode("123456"));
        vipUserDetails.setTenantId(tenant.getId());
        vipUserDetails.setTenantCode(tenant.getCode());
        vipUserDetails.setVipId(vip.getId());
        vipUserDetails.setClientType(Constants.CLIENT_TYPE_O2O);
        return vipUserDetails;
    }

    private UserDetails buildUserDetails(String username, String clientId) {
        String clientType = null;
        if (Constants.APP.equals(clientId)) {
            clientType = Constants.CLIENT_TYPE_APP;
        } else if (Constants.POS.equals(clientId)) {
            clientType = Constants.CLIENT_TYPE_POS;
        } else if (Constants.WEB.equals(clientId)) {
            clientType = Constants.CLIENT_TYPE_WEB;
        }
        SystemUser systemUser = systemUserService.findByLoginNameOrEmailOrMobile(username);
        ValidateUtils.notNull(systemUser, "用户不存在！");

        ValidateUtils.isTrue(systemUser.isAccountNonExpired(), "账号已过期！");
        ValidateUtils.isTrue(systemUser.isAccountNonLocked(), "账号已锁定！");
        ValidateUtils.isTrue(systemUser.isCredentialsNonExpired(), "凭证已过期！");
        ValidateUtils.isTrue(systemUser.isEnabled(), "账号已禁用！");

        int userType = systemUser.getUserType();
        if (userType == Constants.USER_TYPE_AGENT) {
            return buildAgentUserDetails(username, systemUser, clientType);
        }

        if (userType == Constants.USER_TYPE_TENANT || userType == Constants.USER_TYPE_TENANT_EMPLOYEE) {
            return buildTenantUserDetails(username, systemUser, clientType);
        }
        return null;
    }

    private UserDetails buildAgentUserDetails(String username, SystemUser systemUser, String clientType) {
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
        agentUserDetails.setClientType(clientType);
        return agentUserDetails;
    }

    private UserDetails buildTenantUserDetails(String username, SystemUser systemUser, String clientType) {
        BigInteger tenantId = systemUser.getTenantId();
        Tenant tenant = tenantService.obtainTenant(tenantId);
        ValidateUtils.notNull(tenant, "商户不存在！");

        String business = tenant.getBusiness();
        if (Constants.BUSINESS_CATERING.equals(business)) {
            return buildCateringUserDetails(username, systemUser, tenant, clientType);
        }

        if (Constants.BUSINESS_IOT.equals(business)) {
            return buildIotUserDetails(username, systemUser, tenant, clientType);
        }

        return null;
    }

    private UserDetails buildIotUserDetails(String username, SystemUser systemUser, Tenant tenant, String clientType) {
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
        iotUserDetails.setClientType(clientType);

        HttpServletResponse httpServletResponse = ApplicationHandler.getHttpServletResponse();
        httpServletResponse.addHeader(PUBLIC_KEY, tenantSecretKey.getPublicKey());
        httpServletResponse.addHeader(PRIVATE_KEY, tenantSecretKey.getPrivateKey());
        httpServletResponse.addHeader(PLATFORM_PUBLIC_KEY, tenantSecretKey.getPlatformPublicKey());
        return iotUserDetails;
    }

    private UserDetails buildCateringUserDetails(String username, SystemUser systemUser, Tenant tenant, String clientType) {
        BigInteger userId = systemUser.getId();
        Collection<GrantedAuthority> authorities = obtainAuthorities(userId, clientType);
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
        cateringUserDetails.setClientType(clientType);

        HttpServletResponse httpServletResponse = ApplicationHandler.getHttpServletResponse();
        httpServletResponse.addHeader(PUBLIC_KEY, tenantSecretKey.getPublicKey());
        httpServletResponse.addHeader(PRIVATE_KEY, tenantSecretKey.getPrivateKey());
        httpServletResponse.addHeader(PLATFORM_PUBLIC_KEY, tenantSecretKey.getPlatformPublicKey());
        return cateringUserDetails;
    }

    private Collection<GrantedAuthority> obtainAuthorities(BigInteger userId, String clientType) {
        if (Constants.APP.equals(clientType)) {
            List<AppPrivilege> appPrivileges = privilegeService.obtainUserAppPrivileges(userId);
            return appPrivileges.stream().map(appPrivilege -> new SimpleGrantedAuthority(appPrivilege.getPrivilegeCode())).collect(Collectors.toSet());
        }

        if (Constants.POS.equals(clientType)) {
            List<PosPrivilege> posPrivileges = privilegeService.obtainUserPosPrivileges(userId);
            return posPrivileges.stream().map(posPrivilege -> new SimpleGrantedAuthority(posPrivilege.getPrivilegeCode())).collect(Collectors.toSet());
        }

        if (Constants.WEB.equals(clientType)) {
            List<BackgroundPrivilege> backgroundPrivileges = privilegeService.obtainUserBackgroundPrivileges(userId);
            return backgroundPrivileges.stream().map(backgroundPrivilege -> new SimpleGrantedAuthority(backgroundPrivilege.getPrivilegeCode())).collect(Collectors.toSet());
        }
        return null;
    }
}
