package us.dit.consentimientos.service.services.fhir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import us.dit.consentimientos.service.services.fhir.SubscriptionTopicDetails;
import us.dit.consentimientos.service.services.fhir.SubscriptionDetails;

@Service
public class SubscriptionService {

    private final FhirClient fhirClient;
    @Value("${fhir.server.url}")
    private String fhirServerUrl;

    @Autowired
    public SubscriptionService(FhirClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    public List<SubscriptionTopicDetails> getSubscriptionTopics() {
        return fhirClient.getSubscriptionTopics();
    }

    public List<SubscriptionDetails> getSubscriptions() {
        return fhirClient.getSubscriptions();
    }

    public List<String> getSubscriptionTopicIds() {
        return fhirClient.getSubscriptionTopicIds();
    }

    public void createSubscription(String topic, String payload) {
        System.out.println("Creating subscription with topic: " + topic + " and payload: " + payload);
        fhirClient.createSubscription(topic, payload);
    }

    public void createTopic(String topicTitle, String resource, String interaction) {
        // Implementar la lógica para crear un nuevo tópico en el servidor FHIR
        fhirClient.createTopic(topicTitle, resource, interaction);
    }

    public void deleteTopic(String topicId) {
        // Implementar la lógica para eliminar un tópico en el servidor FHIR
        fhirClient.deleteTopic(topicId);
    }

    public void deleteTopicAndRelatedSubscriptions(String topicId) {
        // Obtener la URL completa del tópico
        String topicUrl = fhirServerUrl + "/SubscriptionTopic/" + topicId;

        // Obtener todas las suscripciones
        List<SubscriptionDetails> subscriptions = getSubscriptions();
        List<String> topicUrls = subscriptions.stream().map(SubscriptionDetails::getTopicUrl).collect(Collectors.toList());
         System.out.println("Debug: topic URLs. " + topicUrls);
        // Filtrar las suscripciones que están relacionadas con el tópico a eliminar
        List<String> relatedSubscriptionIds = subscriptions.stream()
                .filter(subscription -> topicUrl.equals(subscription.getTopicUrl())) // Uso de getTopicUrl()
                .map(SubscriptionDetails::getId)
                .collect(Collectors.toList());
        System.out.println("2. listado de subscripciones relacionadas con el topic: " + relatedSubscriptionIds);
        // Eliminar todas las suscripciones relacionadas
        relatedSubscriptionIds.forEach(subscriptionId -> fhirClient.deleteSubscription(subscriptionId));

        // Eliminar el tópico
        fhirClient.deleteTopic(topicId);
    }
}