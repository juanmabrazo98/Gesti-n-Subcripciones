package us.dit.consentimientos.service.controllers;

import us.dit.consentimientos.service.services.fhir.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import us.dit.consentimientos.service.services.fhir.SubscriptionTopicDetails;
import us.dit.consentimientos.service.services.fhir.SubscriptionDetails;
import us.dit.consentimientos.service.services.fhir.Filter;

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
    public String createSubscription(@RequestParam String topicUrl, @RequestParam String payload, Model model) {
        //subscriptionService.createSubscription(topicUrl, payload);
        //return "redirect:/";
        List<SubscriptionTopicDetails.FilterDetail> filters = subscriptionService.getFilters(topicUrl);
        model.addAttribute("topicUrl", topicUrl);
        model.addAttribute("payload", payload);
        model.addAttribute("filters", filters);
        return "subscription-form";
    }

    @PostMapping("/delete-subscription")
    public String deleteSubscription(@RequestParam String subscriptionId){
        subscriptionService.deleteSubscription(subscriptionId);
        return "redirect:/";
    }

    @PostMapping("/submit-filters")
    public String submitFilters(@RequestParam Map<String, String> requestParams, Model model) {
        List<Filter> filters = new ArrayList<>();
        String topicUrl = requestParams.get("topicUrl");
        String payload = requestParams.get("payload");

        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.startsWith("filters[") && value != null && !value.isEmpty()) {
                String parameter = key.substring(8, key.length() - 1);
                String comparatorKey = "comparators[" + parameter + "]";
                String modifierKey = "modifiers[" + parameter + "]";

                String comparator = requestParams.get(comparatorKey);
                String modifier = requestParams.get(modifierKey);

                Filter filter = new Filter(parameter, value, comparator, modifier);
                filters.add(filter);
            }
        }

        subscriptionService.createSubscription(topicUrl, payload, filters);

        return "redirect:/";
    }
    
}
