package us.dit.consentimientos.service.services.fhir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import us.dit.consentimientos.service.services.fhir.SubscriptionTopicDetails;
import us.dit.consentimientos.service.services.fhir.SubscriptionDetails;
import us.dit.consentimientos.service.services.fhir.Filter;

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

    /*public void createSubscription(String topic, String payload) {
        System.out.println("Creating subscription with topic: " + topic + " and payload: " + payload);
        fhirClient.createSubscription(topic, payload);
    }*/

    public List<SubscriptionTopicDetails.FilterDetail> getFilters(String topicUrl) {
        // Llamar al cliente FHIR para obtener los filtros para el topicUrl dado
        return fhirClient.getFilters(topicUrl);
    }

    public void deleteSubscription(String subscriptionId) {
        fhirClient.deleteSubscription(subscriptionId);
    }

    public void createSubscription(String topicUrl, String payload, List<Filter> filters) {
        fhirClient.createSubscription(topicUrl, payload, filters);
    }
}