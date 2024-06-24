package us.dit.consentimientos.service.services.fhir;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final FhirClient fhirClient;

    @Autowired
    public SubscriptionService(FhirClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    public List<SubscriptionTopicDetails> getSubscriptionTopics( String url) {
        return fhirClient.getSubscriptionTopics(url);
    }

    public List<SubscriptionDetails> getSubscriptions(String url) {
        return fhirClient.getSubscriptions(url);
    }

    public List<String> getSubscriptionTopicIds(String url) {
        return fhirClient.getSubscriptionTopicIds(url);
    }

    public List<SubscriptionTopicDetails.FilterDetail> getFilters(String topicUrl, String fhirUrl) {
        // Llamar al cliente FHIR para obtener los filtros para el topicUrl dado
        return fhirClient.getFilters(topicUrl, fhirUrl);
    }

    public void deleteSubscription(String subscriptionId) {
        fhirClient.deleteSubscription(subscriptionId);
    }

    public String getTopicResource(String topicUrl){
        return fhirClient.getTopicResource(topicUrl);
    }
    public String getTopicInteraction(String topicUrl){
        return fhirClient.getTopicInteraction(topicUrl);
    }
    

    public void createSubscription(String topicUrl, String payload, List<Filter> filters, String fhirUrl, String endpoint) {
        fhirClient.createSubscription(topicUrl, payload, filters, fhirUrl, endpoint);
    }
}