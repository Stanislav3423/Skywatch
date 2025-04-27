package org.meteoinit.skywatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/secured")
public class MainController {
    @GetMapping("/user")
    public String userAccess(Principal principal){
        if (principal == null) {
            //System.out.println(principal.getName()+" Null");
            return null;
        }
        return principal.getName();
    }
   /* @GetMapping("/")
    public String showLoginPage() {
        return "signin";
    }

    @GetMapping("/signin")
    public String signin() {
        return "signin"; // signin.html
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup"; // signup.html
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // index.html (твоя головна сторінка)
    }*/
}
