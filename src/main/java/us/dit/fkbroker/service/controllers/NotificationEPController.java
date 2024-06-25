package us.dit.fkbroker.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import us.dit.fkbroker.service.entities.NotificationEP;
import us.dit.fkbroker.service.services.kie.NotificationEPService;

/**
 * Controlador para gestionar las operaciones sobre las entidades NotificationEP.
 */
@Controller
@RequestMapping("/notificationEPs")
public class NotificationEPController {

    @Autowired
    private NotificationEPService notificationEPService;

    /**
     * Maneja las solicitudes GET para obtener la lista de entidades NotificationEP.
     * 
     * @param model el modelo de Spring para añadir atributos.
     * @return el nombre de la vista "notificationEPs".
     */
    @GetMapping
    public String getNotificationEPs(Model model) {
        model.addAttribute("notificationEPs", notificationEPService.getAllNotificationEPs());
        model.addAttribute("newNotificationEP", new NotificationEP());
        return "notificationEPs";
    }

    /**
     * Maneja las solicitudes POST para añadir una nueva entidad NotificationEP.
     * 
     * @param notificationEP el objeto NotificationEP a añadir.
     * @return una redirección a la página de entidades NotificationEP.
     */
    @PostMapping("/add")
    public String addNotificationEP(@ModelAttribute NotificationEP notificationEP) {
        notificationEPService.saveNotificationEP(notificationEP);
        return "redirect:/notificationEPs";
    }

    /**
     * Maneja las solicitudes POST para eliminar una entidad NotificationEP.
     * 
     * @param id el ID de la entidad NotificationEP a eliminar.
     * @return una redirección a la página de entidades NotificationEP.
     */
    @PostMapping("/delete")
    public String deleteNotificationEP(@RequestParam Long id) {
        notificationEPService.deleteNotificationEP(id);
        return "redirect:/notificationEPs";
    }
}
