package us.dit.consentimientos.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import us.dit.consentimientos.service.entities.NotificationEP;
import us.dit.consentimientos.service.services.kie.NotificationEPService;

@Controller
@RequestMapping("/notificationEPs")
public class NotificationEPController {
    @Autowired
    private NotificationEPService notificationEPService;

    @GetMapping
    public String getNotificationEPs(Model model) {
        model.addAttribute("notificationEPs", notificationEPService.getAllNotificationEPs());
        model.addAttribute("newNotificationEP", new NotificationEP());
        return "notificationEPs";
    }

    @PostMapping("/add")
    public String addNotificationEP(@ModelAttribute NotificationEP notificationEP) {
        notificationEPService.saveNotificationEP(notificationEP);
        return "redirect:/notificationEPs";
    }

    @PostMapping("/delete")
    public String deleteNotificationEP(@RequestParam Long id) {
        notificationEPService.deleteNotificationEP(id);
        return "redirect:/notificationEPs";
    }
}
