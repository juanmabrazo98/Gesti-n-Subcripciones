package us.dit.consentimientos.service.controllers;

import us.dit.consentimientos.service.services.fhir.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import us.dit.consentimientos.service.services.fhir.SubscriptionTopicDetails;
import us.dit.consentimientos.service.services.fhir.SubscriptionDetails;

//Clase que controla las llamadas a los métodos necesarios al navegar por la interfaz web
@Controller
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<SubscriptionTopicDetails> topics = subscriptionService.getSubscriptionTopics();
        List<SubscriptionDetails> subscriptions = subscriptionService.getSubscriptions();
        List<String> topicIds = subscriptionService.getSubscriptionTopicIds();
        model.addAttribute("subscriptionTopics", topics);
        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("topicIds", topicIds);
        return "index";
    }
    @GetMapping("/topics")
    public String getTopicsPage(Model model) {
        List<SubscriptionTopicDetails> topics = subscriptionService.getSubscriptionTopics();
        model.addAttribute("subscriptionTopics", topics);
        return "topics";
    }

    @PostMapping("/create-subscription")
    public String createSubscription(@RequestParam String topicUrl, @RequestParam String payload) {
        subscriptionService.createSubscription(topicUrl, payload);
        return "redirect:/";
    }

    @PostMapping("/create-topic")
    public String createTopic(@RequestParam String topicTitle, @RequestParam String resource, @RequestParam String interaction) {
        subscriptionService.createTopic(topicTitle, resource, interaction);
        return "redirect:/topics";
    }

    @PostMapping("/delete-topic")
    public String deleteTopic(@RequestParam String topicId) {
        subscriptionService.deleteTopicAndRelatedSubscriptions(topicId);
        return "redirect:/topics";
    }
}
