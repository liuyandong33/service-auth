package build.dream.auth.exceptions;

import build.dream.auth.oauth.CustomOAuth2ExceptionSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomOAuth2Exception extends OAuth2Exception {
    public CustomOAuth2Exception(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public CustomOAuth2Exception(String msg) {
        super(msg);
    }
}
