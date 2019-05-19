package build.dream.auth.controllers;

import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ValidateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping(value = "/auth")
public class AuthController {
    @RequestMapping(value = "/index")
    public ModelAndView index() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ModelAndView modelAndView = new ModelAndView();
        try {
            String responseType = requestParameters.get("response_type");
            ValidateUtils.notBlank(responseType, "response_type 不能为空！");

            String clientId = requestParameters.get("client_id");
            ValidateUtils.notBlank(clientId, "clientId 不能为空！");

            String redirectUri = requestParameters.get("redirect_uri");
            ValidateUtils.notBlank(redirectUri, "redirectUri 不能为空！");

            String scope = requestParameters.get("scope");
            ValidateUtils.notBlank(scope, "scope 不能为空！");

            modelAndView.setViewName("auth/index");
            modelAndView.addObject("response_type", responseType);
            modelAndView.addObject("client_id", clientId);
            modelAndView.addObject("redirect_uri", redirectUri);
            modelAndView.addObject("scope", scope);
        } catch (Exception e) {
            modelAndView.setViewName("auth/error");
            modelAndView.addObject("message", e.getMessage());
        }
        return modelAndView;
    }
}
