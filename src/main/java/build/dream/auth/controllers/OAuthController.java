package build.dream.auth.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping(value = "/oauth")
public class OAuthController {
    @Autowired
    ConsumerTokenServices consumerTokenServices;

    @RequestMapping(value = "/confirmAccess")
    public ModelAndView confirmAccess() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addAllObjects(requestParameters);
        modelAndView.setViewName("oauth/confirmAccess");
        return modelAndView;
    }

    @RequestMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String logout() {
        String accessToken = ApplicationHandler.getRequestParameter("access_token");
        if (consumerTokenServices.revokeToken(accessToken)) {
            return GsonUtils.toJson(ApiRest.builder().message("注销成功！").successful(true).build());
        } else {
            return GsonUtils.toJson(ApiRest.builder().message("注销失败！").successful(true).build());
        }
    }
}
