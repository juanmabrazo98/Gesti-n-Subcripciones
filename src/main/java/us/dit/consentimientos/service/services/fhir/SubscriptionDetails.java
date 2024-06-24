/*package us.dit.consentimientos.service.services.fhir;

public class SubscriptionDetails {
    private String endpoint;
    private String topic;
    private String id;

    // Constructor, getters y setters
    public SubscriptionDetails(String endpoint, String topic, String id) {
        this.endpoint = endpoint;
        this.topic = topic;
        this.id = id;
    }

    // Getters y Setters
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}*/
package us.dit.consentimientos.service.services.fhir;

import java.util.List;

public class SubscriptionDetails {
    private String endpoint;
    private String topic;
    private String id;
    private List<FilterDetail> filters; // Añadido

    // Constructor, getters y setters
    public SubscriptionDetails(String endpoint, String topic, String id, List<FilterDetail> filters) {
        this.endpoint = endpoint;
        this.topic = topic;
        this.id = id;
        this.filters = filters; // Añadido
    }

    // Getters y Setters
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<FilterDetail> getFilters() { return filters; } // Añadido
    public void setFilters(List<FilterDetail> filters) { this.filters = filters; } // Añadido

    // Clase interna FilterDetail
    public static class FilterDetail {
        private String filterParameter;
        private String comparator;
        private String modifier;
        private String value;

        public FilterDetail(String filterParameter, String comparator, String modifier, String value) {
            this.filterParameter = filterParameter;
            this.comparator = comparator;
            this.modifier = modifier;
            this.value = value;
        }

        public String getFilterParameter() { return filterParameter; }
        public void setFilterParameter(String filterParameter) { this.filterParameter = filterParameter; }
        public String getComparator() { return comparator; }
        public void setComparator(String comparator) { this.comparator = comparator; }
        public String getModifier() { return modifier; }
        public void setModifier(String modifier) { this.modifier = modifier; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}

