package com.ecom.webapp.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticController {

    @GetMapping("/admin/statis")
    public String getStatisticPage() {
        return "admin/statis/statis";
    }
}
