package io.ankur.coronavirustracker.controllers;

import io.ankur.coronavirustracker.services.CoronavirusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private CoronavirusService coronavirusService;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("locationStats", coronavirusService.getLocationStats());
        model.addAttribute("totalNumberOfCases", coronavirusService.getTotalNumberOfCases());
        return "home";
    }
}
