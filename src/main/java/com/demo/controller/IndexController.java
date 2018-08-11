package com.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Franky on 2018/08/10
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public ModelAndView index(){
        return new ModelAndView("/JobManager.html");
    }
}
