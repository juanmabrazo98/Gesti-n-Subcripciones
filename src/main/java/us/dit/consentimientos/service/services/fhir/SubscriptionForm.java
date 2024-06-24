package us.dit.consentimientos.service.services.fhir;

import java.util.Map;

public class SubscriptionForm {
    private String topicUrl;
    private String payload;
    private Map<String, String> filters;

    // Getter y setter para topicUrl
    public String getTopicUrl() {
        return topicUrl;
    }

    public void setTopicUrl(String topicUrl) {
        this.topicUrl = topicUrl;
    }

    // Getter y setter para payload
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    // Getter y setter para filters
    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }
}