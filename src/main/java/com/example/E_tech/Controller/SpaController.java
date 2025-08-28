package com.example.E_tech.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        // Forward all non-file requests to index.html
        return "forward:/index.html";
    }
}
