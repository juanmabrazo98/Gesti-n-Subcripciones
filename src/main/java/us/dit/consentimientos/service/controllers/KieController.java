package us.dit.consentimientos.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import us.dit.consentimientos.service.entities.KieServer;
import us.dit.consentimientos.service.services.kie.KieServerService;

@Controller
@RequestMapping("/kieServers")
public class KieController {

    private final KieServerService kieServerService;

    @Autowired
    public KieController(KieServerService kieServerService) {
        this.kieServerService = kieServerService;
    }

    @GetMapping
    public String getKieServers(Model model) {
        model.addAttribute("kieServers", kieServerService.getAllKieServers());
        model.addAttribute("newKieServer", new KieServer());
        return "kieServers";
    }

    @PostMapping("/add")
    public String addKieServer(@ModelAttribute KieServer kieServer) {
        kieServerService.saveKieServer(kieServer);
        return "redirect:/kieServers";
    }

    @PostMapping("/delete")
    public String deleteKieServer(@RequestParam String url) {
        kieServerService.deleteKieServer(url);
        return "redirect:/kieServers";
    }
}
