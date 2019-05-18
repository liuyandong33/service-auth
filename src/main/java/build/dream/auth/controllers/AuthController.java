package build.dream.auth.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/auth")
public class AuthController {
    @RequestMapping(value = "/index")
    public String index() {
        return "auth/index";
    }
}
