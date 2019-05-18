package build.dream.auth.controllers;

import build.dream.common.utils.ApplicationHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/auth")
public class AuthController {
    private HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();

    @RequestMapping(value = "/index")
    public String index() {
        HttpServletRequest httpServletRequest = ApplicationHandler.getHttpServletRequest();
        HttpServletResponse httpServletResponse = ApplicationHandler.getHttpServletResponse();
        SavedRequest savedRequest = httpSessionRequestCache.getRequest(httpServletRequest, httpServletResponse);
        return "auth/index";
    }
}
