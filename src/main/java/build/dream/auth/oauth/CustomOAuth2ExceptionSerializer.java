package build.dream.auth.oauth;

import build.dream.auth.constants.Constants;
import build.dream.auth.exceptions.CustomOAuth2Exception;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class CustomOAuth2ExceptionSerializer extends StdSerializer<CustomOAuth2Exception> {
    private static final String[] OLD_ERROR_CODES = {"invalid_client", "unauthorized_client",
            "invalid_grant", "invalid_scope",
            "invalid_token", "invalid_request",
            "redirect_uri_mismatch", "unsupported_grant_type",
            "unsupported_response_type", "insufficient_scope",
            "access_denied"};
    private static final String[] NEW_ERROR_CODES = {Constants.ERROR_CODE_INVALID_CLIENT, Constants.ERROR_CODE_UNAUTHORIZED_CLIENT,
            Constants.ERROR_CODE_INVALID_GRANT, Constants.ERROR_CODE_INVALID_SCOPE,
            Constants.ERROR_CODE_INVALID_TOKEN, Constants.ERROR_CODE_INVALID_REQUEST,
            Constants.ERROR_CODE_REDIRECT_URI_MISMATCH, Constants.ERROR_CODE_UNSUPPORTED_GRANT_TYPE,
            Constants.ERROR_CODE_UNSUPPORTED_RESPONSE_TYPE, Constants.ERROR_CODE_INSUFFICIENT_SCOPE,
            Constants.ERROR_CODE_ACCESS_DENIED};

    protected CustomOAuth2ExceptionSerializer(Class<CustomOAuth2Exception> customOAuth2ExceptionClass) {
        super(customOAuth2ExceptionClass);
    }

    protected CustomOAuth2ExceptionSerializer(JavaType type) {
        super(type);
    }

    protected CustomOAuth2ExceptionSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected CustomOAuth2ExceptionSerializer(StdSerializer<?> stdSerializer) {
        super(stdSerializer);
    }

    @Override
    public void serialize(CustomOAuth2Exception value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeBooleanField("successful", false);
        jsonGenerator.writeStringField("className", null);
        jsonGenerator.writeStringField("message", null);
        jsonGenerator.writeObjectFieldStart("error");

        String code = null;
        int index = ArrayUtils.indexOf(OLD_ERROR_CODES, value.getOAuth2ErrorCode());
        if (index >= 0) {
            code = NEW_ERROR_CODES[index];
        } else {
            code = Constants.ERROR_CODE_UNKNOWN_ERROR;
        }

        jsonGenerator.writeStringField("code", code);
        String errorMessage = value.getMessage();
        if (errorMessage != null) {
            errorMessage = HtmlUtils.htmlEscape(errorMessage);
        }
        jsonGenerator.writeStringField("message", errorMessage);
        jsonGenerator.writeEndObject();
        if (value.getAdditionalInformation() != null) {
            jsonGenerator.writeObjectFieldStart("data");
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                jsonGenerator.writeStringField(key, add);
            }
            jsonGenerator.writeEndObject();
        } else {
            jsonGenerator.writeObjectField("data", null);
        }
        jsonGenerator.writeStringField("id", UUID.randomUUID().toString());
        jsonGenerator.writeStringField("timestamp", new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN).format(new Date()));
        jsonGenerator.writeStringField("signature", null);
        jsonGenerator.writeBooleanField("zipped", false);
        jsonGenerator.writeBooleanField("encrypted", false);
        jsonGenerator.writeEndObject();
    }
}
