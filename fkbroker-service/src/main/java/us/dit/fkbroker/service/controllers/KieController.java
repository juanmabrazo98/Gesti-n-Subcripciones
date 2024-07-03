package us.dit.fkbroker.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import us.dit.fkbroker.service.entities.KieServer;
import us.dit.fkbroker.service.services.kie.KieServerService;

/**
 * Controlador para gestionar las operaciones sobre los servidores KIE.
 */
@Controller
@RequestMapping("/kieServers")
public class KieController {

    private final KieServerService kieServerService;

    /**
     * Constructor que inyecta el servicio KieServerService.
     * 
     * @param kieServerService el servicio para gestionar los servidores KIE.
     */
    @Autowired
    public KieController(KieServerService kieServerService) {
        this.kieServerService = kieServerService;
    }

    /**
     * Maneja las solicitudes GET para obtener la lista de servidores KIE.
     * 
     * @param model el modelo de Spring para añadir atributos.
     * @return el nombre de la vista "kieServers".
     */
    @GetMapping
    public String getKieServers(Model model) {
        model.addAttribute("kieServers", kieServerService.getAllKieServers());
        model.addAttribute("newKieServer", new KieServer());
        return "kieServers";
    }

    /**
     * Maneja las solicitudes POST para añadir un nuevo servidor KIE.
     * 
     * @param kieServer el objeto KieServer a añadir.
     * @return una redirección a la página de servidores KIE.
     */
    @PostMapping("/add")
    public String addKieServer(@ModelAttribute KieServer kieServer) {
        kieServerService.saveKieServer(kieServer);
        return "redirect:/kieServers";
    }

    /**
     * Maneja las solicitudes POST para eliminar un servidor KIE.
     * 
     * @param url la URL del servidor KIE a eliminar.
     * @return una redirección a la página de servidores KIE.
     */
    @PostMapping("/delete")
    public String deleteKieServer(@RequestParam String url) {
        kieServerService.deleteKieServer(url);
        return "redirect:/kieServers";
    }
}
