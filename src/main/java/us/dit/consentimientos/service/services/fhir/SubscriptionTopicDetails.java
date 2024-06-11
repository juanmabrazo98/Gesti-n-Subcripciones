package us.dit.consentimientos.service.services.fhir;

public class SubscriptionTopicDetails {
    private String name;
    private String url;
    private String id;

    // Constructor, getters y setters
    public SubscriptionTopicDetails(String name, String id, String url) {
        this.name = name;
        this.id = id;
        this.url = url;
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
