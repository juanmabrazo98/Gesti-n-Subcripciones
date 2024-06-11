package us.dit.consentimientos.service.services.fhir;

public class SubscriptionDetails {
    private String endpoint;
    private String payloadType;
    private String topic;
    private String id;
    private String topicUrl;

    // Constructor, getters y setters
    public SubscriptionDetails(String endpoint, String payloadType, String topic, String id, String topicUrl) {
        this.endpoint = endpoint;
        this.payloadType = payloadType;
        this.topic = topic;
        this.id = id;
        this.topicUrl = topicUrl;
    }

    // Getters y Setters
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getPayloadType() { return payloadType; }
    public void setPayloadType(String payloadType) { this.payloadType = payloadType; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTopicUrl() {return topicUrl;}
    public void setTopicUrl(String topicUrl) {this.topicUrl = topicUrl;}
}
