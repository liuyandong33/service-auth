package build.dream.auth.controllers;

import build.dream.auth.constants.Constants;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.CommonUtils;
import build.dream.common.utils.UrlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/sso")
public class SSOController {
    private static final List<String> URLS = new ArrayList<String>();
    private static final String SSO_LOGIN_URL = "http://localhost:8888/sso/login";

    static {
//        URLS.add(CommonUtils.getOutsideUrl(Constants.SERVICE_NAME_PLATFORM, "sso", "login"));
//        URLS.add(CommonUtils.getOutsideUrl(Constants.SERVICE_NAME_APPAPI, "sso", "login"));

        URLS.add("http://localhost:8889/sso/login");
        URLS.add("http://localhost:41011/sso/login");
    }

    @RequestMapping(value = "/login")
    public String login() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String accessToken = requestParameters.get("accessToken");
        String redirectUrl = requestParameters.get("redirectUrl");
        if (CollectionUtils.isEmpty(URLS)) {
            return "redirect:" + redirectUrl;
        }

        String indexStr = requestParameters.get("index");
        if (StringUtils.isBlank(indexStr)) {
            indexStr = "-1";
        }

        int index = Integer.parseInt(indexStr);
        index += 1;
        if (index < URLS.size()) {
            return "redirect:" + URLS.get(index) + "?accessToken=" + accessToken + "&index=" + index + "&redirectUrl=" + SSO_LOGIN_URL + "&originalRedirectUrl=" + UrlUtils.encode(redirectUrl);
        } else {
            return "redirect:" + redirectUrl;
        }
    }
}
