package build.dream.auth.controllers;

import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.UrlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/sso")
public class SSOController {
    private static final List<String> REDIRECT_SSO_LOGIN_URLS = new ArrayList<String>();
    private static final List<String> AJAX_SSO_LOGIN_URLS = new ArrayList<String>();
    private static final String SSO_LOGIN_URL = "http://localhost:8888/sso/login";

    static {
//        URLS.add(CommonUtils.getOutsideUrl(Constants.SERVICE_NAME_PLATFORM, "sso", "login"));
//        URLS.add(CommonUtils.getOutsideUrl(Constants.SERVICE_NAME_APPAPI, "sso", "login"));

//        URLS.add("http://localhost:8889/sso/login");
        REDIRECT_SSO_LOGIN_URLS.add("http://localhost:41011/sso/login");
        AJAX_SSO_LOGIN_URLS.add("http://localhost:41011/sso/ajaxLogin");
    }

    /**
     * 重定向单点登录
     *
     * @return
     */
    @RequestMapping(value = "/login")
    public String login() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String accessToken = requestParameters.get("accessToken");
        String redirectUrl = requestParameters.get("redirectUrl");
        if (CollectionUtils.isEmpty(REDIRECT_SSO_LOGIN_URLS)) {
            return "redirect:" + redirectUrl;
        }

        String indexStr = requestParameters.get("index");
        if (StringUtils.isBlank(indexStr)) {
            indexStr = "-1";
        }

        int index = Integer.parseInt(indexStr);
        index += 1;
        if (index < REDIRECT_SSO_LOGIN_URLS.size()) {
            return "redirect:" + REDIRECT_SSO_LOGIN_URLS.get(index) + "?accessToken=" + accessToken + "&index=" + index + "&redirectUrl=" + SSO_LOGIN_URL + "&originalRedirectUrl=" + UrlUtils.encode(redirectUrl);
        } else {
            return "redirect:" + redirectUrl;
        }
    }

    /**
     * ajax单点登录javascript脚本
     *
     * @return
     */
    @RequestMapping(value = "/ssoJavaScript", produces = "application/javascript; charset=utf-8")
    @ResponseBody
    public String ssoJavaScript() {
        return "function sso(accessToken) {var urls = [" + AJAX_SSO_LOGIN_URLS.stream().map(s -> "'" + s + "'").collect(Collectors.joining(", ")) + "];for (var index in urls) {var url = urls[index];$.get(url, {accessToken: accessToken}, function (result) {console.log(result)}, \"text\");}}";
    }
}
