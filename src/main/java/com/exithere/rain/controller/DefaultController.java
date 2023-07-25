package com.exithere.rain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String redirectNotion(){
        return "redirect:https://www.notion.so/exit-here/d598e38889a248c59318f3c431833eae?pvs=4";
    }
}
