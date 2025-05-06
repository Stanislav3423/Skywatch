package org.meteoinit.skywatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class MainController {
    @GetMapping("/user")
    public String userAccess(Principal principal){
        if (principal == null) {
            //System.out.println(principal.getName()+" Null");
            return null;
        }
        return principal.getName();
    }

   @GetMapping("/")
    public String showLoginPage() {
        return "dashboard";
    }

    @GetMapping("/signin")
    public String signin() {
        return "signin"; // signin.html
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup"; // signup.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // dashboard.html (твоя головна сторінка)
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }

    @GetMapping("/alerts")
    public String alerts() {
        return "alerts";
    }

    @GetMapping("/analytics")
    public String analytics() {
        return "analytics";
    }

    @GetMapping("/reports")
    public String reports() {
        return "reports";
    }

    @GetMapping("/users")
    public String users() {
        return "users";
    }
}
