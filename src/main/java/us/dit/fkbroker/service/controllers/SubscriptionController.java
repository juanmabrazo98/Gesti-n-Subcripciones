package us.dit.fkbroker.service.controllers;

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

import us.dit.fkbroker.service.entities.NotificationEP;
import us.dit.fkbroker.service.services.fhir.Filter;
import us.dit.fkbroker.service.services.fhir.SubscriptionDetails;
import us.dit.fkbroker.service.services.fhir.SubscriptionService;
import us.dit.fkbroker.service.services.fhir.SubscriptionTopicDetails;
import us.dit.fkbroker.service.services.kie.NotificationEPService;

/**
 * Controlador que gestiona las llamadas a los métodos necesarios al navegar por la interfaz web.
 */
@Controller
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private NotificationEPService notificationEPService;

    /**
     * Maneja las solicitudes GET para obtener la página principal.
     * 
     * @param model el modelo de Spring para añadir atributos.
     * @return el nombre de la vista "index".
     */
    @GetMapping("/")
    public String getHomePage(Model model) {
        return "index";
    }

    /**
     * Maneja las solicitudes GET para obtener la página de suscripciones.
     * 
     * @param model el modelo de Spring para añadir atributos.
     * @param fhirUrl la URL del servidor FHIR.
     * @return el nombre de la vista "subscriptions-manager".
     */
    @GetMapping("/subscriptions")
    public String getSubscriptionPage(Model model, @RequestParam String fhirUrl) {

        String fhirUrlFull = "http://" + fhirUrl + "/fhir";
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

    /**
     * Maneja las solicitudes POST para crear una nueva suscripción.
     * 
     * @param topicUrl la URL del tema de la suscripción.
     * @param payload el payload de la suscripción.
     * @param fhirUrl la URL del servidor FHIR.
     * @param model el modelo de Spring para añadir atributos.
     * @return el nombre de la vista "subscription-form".
     */
    @PostMapping("/create-subscription")
    public String createSubscription(@RequestParam String topicUrl, @RequestParam String payload, @RequestParam String fhirUrl, Model model) {
        List<SubscriptionTopicDetails.FilterDetail> filters = subscriptionService.getFilters(topicUrl, fhirUrl);
        model.addAttribute("topicUrl", topicUrl);
        model.addAttribute("payload", payload);
        model.addAttribute("filters", filters);
        model.addAttribute("fhirUrl", fhirUrl);

        // Obtener recurso e interacción del topic
        String resource = subscriptionService.getTopicResource(topicUrl);
        String interaction = subscriptionService.getTopicInteraction(topicUrl);
        String endpoint;
        System.out.println("recurso: " + resource + " interaction: " + interaction);

        // Comparar si existe ya
        Optional<NotificationEP> optionalNotificationEP = notificationEPService.findNotificationEPByResourceAndInteraction(resource, interaction);
        if (optionalNotificationEP.isPresent()) {
            // Si existe, obtener el id y este será nuestro endpoint
            NotificationEP existingNotificationEP = optionalNotificationEP.get();
            endpoint = "http://localhost:8090/notification/" + existingNotificationEP.getId();
        } else {
            // Si no existe, se crea un endpoint nuevo
            String signalName = interaction + "-" + resource;
            NotificationEP newNotificationEP = new NotificationEP();
            newNotificationEP.setResource(resource);
            newNotificationEP.setInteraction(interaction);
            newNotificationEP.setSignalName(signalName);
            NotificationEP savedNotificationEP = notificationEPService.saveNotificationEP(newNotificationEP);
            endpoint = "http://localhost:8090/notification/" + savedNotificationEP.getId();
        }

        model.addAttribute("endpoint", endpoint);

        return "subscription-form";
    }

    /**
     * Maneja las solicitudes POST para eliminar una suscripción.
     * 
     * @param subscriptionId el ID de la suscripción a eliminar.
     * @return una redirección a la página principal.
     */
    @PostMapping("/delete-subscription")
    public String deleteSubscription(@RequestParam String subscriptionId) {
        subscriptionService.deleteSubscription(subscriptionId);
        return "redirect:/";
    }

    /**
     * Maneja las solicitudes POST para enviar los filtros de una suscripción.
     * 
     * @param requestParams los parámetros de la solicitud que contienen los filtros.
     * @param fhirUrl la URL del servidor FHIR.
     * @param endpoint el endpoint de la suscripción.
     * @param model el modelo de Spring para añadir atributos.
     * @return una redirección a la página de suscripciones.
     */
    @PostMapping("/submit-filters")
    public String submitFilters(@RequestParam Map<String, String> requestParams, @RequestParam String fhirUrl, @RequestParam String endpoint, Model model) {
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
        return "redirect:/subscriptions?fhirUrl=" + url;
    }
}
