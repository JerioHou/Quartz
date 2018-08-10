package com.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Franky on 2018/08/10
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(){
        return "/templates/JobManager.html";
    }
}
