package build.dream.auth.oauth;

import build.dream.auth.services.OauthClientDetailService;
import build.dream.common.saas.domains.OauthClientDetail;
import build.dream.common.utils.JacksonUtils;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CustomClientDetailsService implements ClientDetailsService {
    @Autowired
    private OauthClientDetailService oauthClientDetailService;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return obtainClientDetailsSafe(clientId);
    }

    private ClientDetails obtainClientDetailsSafe(String clientId) {
        try {
            return obtainClientDetails(clientId);
        } catch (Exception e) {
            throw new OAuth2Exception(e.getMessage());
        }
    }

    private ClientDetails obtainClientDetails(String clientId) {
        OauthClientDetail oauthClientDetail = oauthClientDetailService.obtainOauthClientDetail(clientId);
        ValidateUtils.notNull(oauthClientDetail, "客户端不存在！");

        List<String> resourceIds = Arrays.asList(oauthClientDetail.getResourceIds().split(","));
        List<String> scope = Arrays.asList(oauthClientDetail.getScope().split(","));
        List<String> authorizedGrantTypes = Arrays.asList(oauthClientDetail.getAuthorizedGrantTypes().split(","));
        Set<String> registeredRedirectUris = new HashSet<String>(Arrays.asList(oauthClientDetail.getWebServerRedirectUri().split(",")));

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (StringUtils.isNotEmpty(oauthClientDetail.getAuthorities())) {
            String[] authorityCodes = oauthClientDetail.getAuthorities().split(",");
            for (String authorityCode : authorityCodes) {
                authorities.add(new SimpleGrantedAuthority(authorityCode));
            }
        }

        String additionalInformation = oauthClientDetail.getAdditionalInformation();
        Map<String, Object> additionalInformationMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(additionalInformation)) {
            additionalInformationMap = JacksonUtils.readValueAsMap(additionalInformation, String.class, Object.class);
        }

        List<String> autoApproveScopes = new ArrayList<String>();
        String autoApproveScope = oauthClientDetail.getAutoApproveScope();
        if (StringUtils.isNotBlank(autoApproveScope)) {
            autoApproveScopes = Arrays.asList(autoApproveScope.split(","));
        }

        BaseClientDetails baseClientDetails = new BaseClientDetails();
        baseClientDetails.setClientId(clientId);
        baseClientDetails.setClientSecret(oauthClientDetail.getClientSecret());
        baseClientDetails.setResourceIds(resourceIds);
        baseClientDetails.setScope(scope);
        baseClientDetails.setAuthorizedGrantTypes(authorizedGrantTypes);
        baseClientDetails.setRegisteredRedirectUri(registeredRedirectUris);
        baseClientDetails.setAuthorities(authorities);
        baseClientDetails.setAccessTokenValiditySeconds(oauthClientDetail.getAccessTokenValidity());
        baseClientDetails.setRefreshTokenValiditySeconds(oauthClientDetail.getRefreshTokenValidity());
        baseClientDetails.setAdditionalInformation(additionalInformationMap);
        baseClientDetails.setAutoApproveScopes(autoApproveScopes);
        return baseClientDetails;
    }
}
