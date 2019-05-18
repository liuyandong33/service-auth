package build.dream.auth.controllers;

import build.dream.common.utils.ApplicationHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping(value = "/oauth")
public class OAuthController {
    @RequestMapping(value = "/confirmAccess")
    public ModelAndView confirmAccess() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addAllObjects(requestParameters);
        modelAndView.setViewName("oauth/confirmAccess");
        return modelAndView;
    }
}
