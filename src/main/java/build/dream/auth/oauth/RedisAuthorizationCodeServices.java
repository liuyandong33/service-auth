package build.dream.auth.oauth;

import build.dream.auth.constants.Constants;
import build.dream.common.utils.CommonRedisUtils;
import build.dream.common.utils.ObjectUtils;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.stereotype.Component;

@Component
public class RedisAuthorizationCodeServices implements AuthorizationCodeServices {
    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = RandomStringUtils.randomAlphanumeric(50);
        CommonRedisUtils.setex(code.getBytes(Constants.CHARSET_UTF_8), 300, ObjectUtils.serialize(authentication));
        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        byte[] key = code.getBytes(Constants.CHARSET_UTF_8);
        byte[] value = CommonRedisUtils.get(key);
        ValidateUtils.isTrue(ArrayUtils.isNotEmpty(value), "Invalid authorization code: " + code);
        CommonRedisUtils.del(key);
        return ObjectUtils.deserialize(value);
    }
}
