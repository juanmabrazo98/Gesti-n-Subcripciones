package us.dit.consentimientos.service.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import us.dit.consentimientos.service.entities.NotificationEP;
import us.dit.consentimientos.service.services.fhir.Filter;
import us.dit.consentimientos.service.services.fhir.SubscriptionDetails;
import us.dit.consentimientos.service.services.fhir.SubscriptionService;
import us.dit.consentimientos.service.services.fhir.SubscriptionTopicDetails;
import us.dit.consentimientos.service.services.kie.NotificationEPService;

//Clase que controla las llamadas a los métodos necesarios al navegar por la interfaz web
@Controller
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private NotificationEPService notificationEPService;
    
    @GetMapping("/")
    public String getHomePage(Model model) {
        return "index";
    }
    @GetMapping("/subscriptions")
    public String getSubscriptionPage(Model model, @RequestParam String fhirUrl) {

        String fhirUrlFull="http://" + fhirUrl + "/fhir";
        System.out.println(fhirUrlFull);

        List<SubscriptionTopicDetails> topics = subscriptionService.getSubscriptionTopics(fhirUrlFull);
        List<SubscriptionDetails> subscriptions = subscriptionService.getSubscriptions(fhirUrlFull);
        List<String> topicIds = subscriptionService.getSubscriptionTopicIds(fhirUrlFull);
        model.addAttribute("subscriptionTopics", topics);
        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("topicIds", topicIds);
        model.addAttribute("fhirUrl", fhirUrlFull);
        
        return "subscriptions-manager";
    }

    @PostMapping("/create-subscription")
    public String createSubscription(@RequestParam String topicUrl, @RequestParam String payload, @RequestParam String fhirUrl, Model model) { 
        List<SubscriptionTopicDetails.FilterDetail> filters = subscriptionService.getFilters(topicUrl, fhirUrl);
        model.addAttribute("topicUrl", topicUrl);
        model.addAttribute("payload", payload);
        model.addAttribute("filters", filters);
        model.addAttribute("fhirUrl", fhirUrl);

         //obtener recurso e interacción del topic
         String resource = subscriptionService.getTopicResource(topicUrl);
         String interaction = subscriptionService.getTopicInteraction(topicUrl);
         String endpoint;
         System.out.println("recurso: "+ resource + " interaction: "+interaction);
         //Comparar si existe ya
         Optional<NotificationEP> optionalNotificationEP = notificationEPService.findNotificationEPByResourceAndInteraction(resource, interaction);
         if (optionalNotificationEP.isPresent()) {
             //si existe cogemos el id y este será nuestro endpoint
             NotificationEP existingNotificationEP = optionalNotificationEP.get();
             endpoint = "http://localhost:8090/notification/" + existingNotificationEP.getId();
         } else {
            //Si no existe se crea un endpoint nuevo
             String signalName = interaction+"-"+resource; 
             NotificationEP newNotificationEP = new NotificationEP();
             newNotificationEP.setResource(resource);
             newNotificationEP.setInteraction(interaction);
             newNotificationEP.setSignalName(signalName);
             NotificationEP savedNotificationEP = notificationEPService.saveNotificationEP(newNotificationEP);
             endpoint = "http://localhost:8090/notification/" + savedNotificationEP.getId();
             // Resto de la lógica
         }

        model.addAttribute("endpoint", endpoint);

        return "subscription-form";
    }

    @PostMapping("/delete-subscription")
    public String deleteSubscription(@RequestParam String subscriptionId){
        subscriptionService.deleteSubscription(subscriptionId);
        return "redirect:/";
    }

    @PostMapping("/submit-filters")
    public String submitFilters(@RequestParam Map<String, String> requestParams,@RequestParam String fhirUrl, @RequestParam String endpoint, Model model) {
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

        subscriptionService.createSubscription(topicUrl, payload, filters, fhirUrl, endpoint);

        String url = fhirUrl.replace("http://", "").replace("/fhir", "");
        return "redirect:/subscriptions?fhirUrl="+url;
    }
    
}
