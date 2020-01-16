package build.dream.auth.controllers;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/sso")
public class SSOController {
    private static final List<String> URLS = new ArrayList<String>();

    static {
        URLS.add("https://www.baidu.com");
        URLS.add("https://www.taobao.com");
        URLS.add("https://www.jd.com");
    }

    @RequestMapping(value = "/login")
    public String login(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getParameter("token");
        String redirectUrl = httpServletRequest.getParameter("redirectUrl");
        if (CollectionUtils.isEmpty(URLS)) {
            return "redirect:" + redirectUrl;
        }

        String indexStr = httpServletRequest.getParameter("index");
        if (StringUtils.isBlank(indexStr)) {
            indexStr = "-1";
        }

        int index = Integer.parseInt(indexStr);
        index += 1;
        if (index > URLS.size()) {
            return "redirect:" + URLS.get(index) + "?token=" + token + "&redirectUrl=https://www.groovy.top/auth/sso/login" + "&originalRedirectUrl=" + redirectUrl;
        } else {
            return "redirect:" + httpServletRequest.getParameter("originalRedirectUrl");
        }
    }
}
