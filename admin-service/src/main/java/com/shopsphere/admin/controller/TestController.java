package com.shopsphere.admin.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Admin Service Working";
    }
}
