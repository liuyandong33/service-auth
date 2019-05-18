package build.dream.auth.oauth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;

@Component
public class WebResponseExceptionTranslator implements org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator<OAuth2Exception> {
    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        System.out.println("");
        OAuth2Exception oAuth2Exception = new OAuth2Exception(e.getMessage());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        ResponseEntity<OAuth2Exception> responseEntity = new ResponseEntity<OAuth2Exception>(oAuth2Exception, httpHeaders, HttpStatus.OK);
        return responseEntity;
    }
}
