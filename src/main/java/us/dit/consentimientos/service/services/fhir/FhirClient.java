package us.dit.consentimientos.service.services.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Subscription;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.SubscriptionTopic;
import org.hl7.fhir.r5.model.SubscriptionTopic.SubscriptionTopicResourceTriggerComponent;
import org.hl7.fhir.r5.model.StringType;
import org.hl7.fhir.r5.model.Enumerations.SubscriptionState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.json.JSONArray;
import us.dit.consentimientos.service.services.fhir.SubscriptionTopicDetails;

//Esta clase desarrolla las distintas operaciones que se realizan sobre elementos FHIR
@Service
public class FhirClient {

    @Value("${fhir.server.url}")
    private String fhirServerUrl;

    @Value("${fhir.server.username}")
    private String username;

    @Value("${fhir.server.password}")
    private String password;

    private final RestTemplate restTemplate;

    public FhirClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createHeaders() {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.valueOf("application/fhir+json"));
        return headers;
    }

    public List<SubscriptionTopicDetails> getSubscriptionTopics() {
        try {
            System.out.println("obteniendo tópicos");
            String url = fhirServerUrl + "/SubscriptionTopic";
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String json = response.getBody();
            //Pasamos a Bundle
            FhirContext ctx = FhirContext.forR5();
            IParser parser = ctx.newJsonParser();
            Bundle bundle = parser.parseResource(Bundle.class, json);

            if (bundle == null || bundle.getEntry().isEmpty()) {
                return Collections.emptyList();
            }

            List<SubscriptionTopicDetails> topicDetails = new ArrayList<>();
            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                SubscriptionTopic topic = (SubscriptionTopic) entry.getResource();
                String name = topic.getTitle();
                String id = topic.getIdElement().getIdPart();
                String topicUrl = topic.getUrl();
                topicDetails.add(new SubscriptionTopicDetails(name, id, topicUrl));
        }

            return topicDetails;
        } catch (ResourceAccessException e) {
            System.err.println("Error accessing FHIR server: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<SubscriptionDetails> getSubscriptions() {
        try {
            System.out.println("1. obteniendo subscripciones");
            String url = fhirServerUrl + "/Subscription";
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.valueOf("application/fhir+json")); // Asegurar que el Content-Type sea correcto
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String json = response.getBody();

            JSONObject bundleJson = new JSONObject(json);
            List<SubscriptionDetails> subscriptionDetails = new ArrayList<>();
            if (bundleJson.has("entry")) {
                JSONArray entries = bundleJson.getJSONArray("entry");
                for (int i = 0; i < entries.length(); i++) {
                    JSONObject entry = entries.getJSONObject(i).getJSONObject("resource");

                    String endpoint = entry.optString("endpoint", "N/A"); 
                    String payloadType = entry.optString("contentType", "N/A");
                    String topicReference = entry.optString("topic", "N/A");
                    String id = entry.optString("id", "N/A");

                    System.out.println(topicReference);
                    String topicTitle = getTopicTitle(topicReference);

                    subscriptionDetails.add(new SubscriptionDetails(endpoint, payloadType, topicTitle, id, topicReference));
                }
            }
            return subscriptionDetails;

        } catch (ResourceAccessException e) {
            System.err.println("Error accessing FHIR server: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public String getTopicTitle(String referencia) {
        try {
            String url = referencia;
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // Asegurar el Content-Type
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String json = response.getBody();
            
            // Pasamos a SubscriptionTopic
            FhirContext ctx = FhirContext.forR5();
            IParser parser = ctx.newJsonParser();
            SubscriptionTopic topic = parser.parseResource(SubscriptionTopic.class, json);

            return topic.getTitle();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.GONE) {
                System.err.println("Resource was deleted: " + e.getMessage());
            } else {
                System.err.println("Error accessing FHIR server: " + e.getMessage());
            }
            return "Unknown";
        } catch (ResourceAccessException e) {
            System.err.println("Error accessing FHIR server: " + e.getMessage());
            return "Unknown";
        }
    }

    public String getTopicResource(String referencia) {
        try {
            String url = referencia;
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); 
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String json = response.getBody();

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(json);

            // Extract the resource from the SubscriptionTopic
            if (jsonResponse.has("resourceTrigger")) {
                JSONObject resourceTrigger = jsonResponse.getJSONArray("resourceTrigger").getJSONObject(0);
                if (resourceTrigger.has("resource")) {
                    return resourceTrigger.getString("resource");
                } else {
                    throw new RuntimeException("No resource found in the resourceTrigger");
                }
            } else {
                throw new RuntimeException("No resourceTrigger found in the SubscriptionTopic");
            }
        } catch (Exception e) {
            System.err.println("Error accessing FHIR server: " + e.getMessage());
            return "Unknown";
        }
    }


    public List<String> getSubscriptionTopicIds() {
        try {
            String url = fhirServerUrl + "/SubscriptionTopic";
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String json = response.getBody();
            
            // Convertir JSON a FHIR Bundle
            FhirContext ctx = FhirContext.forR5();
            IParser parser = ctx.newJsonParser();
            Bundle bundle = parser.parseResource(Bundle.class, json);

            if (bundle == null || bundle.getEntry().isEmpty()) {
                return Collections.emptyList();
            }

            return bundle.getEntry().stream()
                    .map(entry -> (SubscriptionTopic) entry.getResource())
                    .map(SubscriptionTopic::getIdElement)
                    .map(id -> id.getIdPart())
                    .collect(Collectors.toList());
        } catch (ResourceAccessException e) {
            System.err.println("Error accessing FHIR server: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    public void createSubscription(String topicUrl, String payload) {
        try {
            String url = fhirServerUrl + "/Subscription";
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Construir el JSON manualmente
            JSONObject subscriptionJson = new JSONObject();
            subscriptionJson.put("resourceType", "Subscription");
            subscriptionJson.put("status", "active");
            subscriptionJson.put("topic", topicUrl);

            JSONObject channelType = new JSONObject();
            channelType.put("code", "rest-hook");

            subscriptionJson.put("channelType", channelType);
            subscriptionJson.put("endpoint", "http://localhost:8090/endpoint");
            subscriptionJson.put("heartbeatPeriod", 60);
            subscriptionJson.put("timeout", 300);
            subscriptionJson.put("content", payload);
            subscriptionJson.put("contentType", "application/fhir+json");

            HttpEntity<String> entity = new HttpEntity<>(subscriptionJson.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("Subscription created successfully.");
            } else {
                System.err.println("Failed to create subscription. Status code: " + response.getStatusCode());
            }
        } catch (ResourceAccessException e) {
            System.err.println("Error accessing FHIR server: " + e.getMessage());
        } catch (HttpClientErrorException e) {
            System.err.println("Client error: " + e.getMessage());
            System.err.println("Response body: " + e.getResponseBodyAsString());
        }
    }

    public void createTopic(String topicTitle, String resource, String interaction) {
        try {
            String url = fhirServerUrl + "/SubscriptionTopic";
            HttpHeaders headers = createHeaders();

            // Construir el JSON manualmente
            JSONObject topicJson = new JSONObject();
            topicJson.put("resourceType", "SubscriptionTopic");
            topicJson.put("title", topicTitle);
            topicJson.put("status", "active");

            JSONObject resourceTrigger = new JSONObject();
            resourceTrigger.put("resource", resource);
            resourceTrigger.put("supportedInteraction", new JSONArray().put(interaction));

            topicJson.put("resourceTrigger", new JSONArray().put(resourceTrigger));
            topicJson.put("notificationShape", new JSONArray().put(new JSONObject().put("resource", resource)));

            HttpEntity<String> entity = new HttpEntity<>(topicJson.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("Topic created successfully.");
                String responseBody = response.getBody();
                JSONObject responseJson = new JSONObject(responseBody);

                // Extraer el ID del tópico creado
                String topicId = responseJson.getString("id");

                // Construir la URL completa usando el ID extraído
                String topicUrl = fhirServerUrl + "/SubscriptionTopic/" + topicId;

                // Crear un nuevo campo URL
                responseJson.put("url", topicUrl);

                // Realizar el PUT con la URL completa
                HttpEntity<String> updateEntity = new HttpEntity<>(responseJson.toString(), headers);
                ResponseEntity<String> updateResponse = restTemplate.exchange(topicUrl, HttpMethod.PUT, updateEntity, String.class);
                if (updateResponse.getStatusCode() == HttpStatus.OK) {
                    System.out.println("Topic URL updated successfully.");
                } else {
                    System.err.println("Failed to update topic URL. Status code: " + updateResponse.getStatusCode());
                }
            } else {
                System.err.println("Failed to create topic. Status code: " + response.getStatusCode());
            }
        } catch (ResourceAccessException e) {
            System.err.println("Error accessing FHIR server: " + e.getMessage());
        }
    }

    public void deleteTopic(String topicId) {
        try {
            String url = fhirServerUrl + "/SubscriptionTopic/" + topicId;
            System.out.println("4. eliminando topico" + url);
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                System.out.println("Topic deleted successfully.");
            } else {
                System.err.println("Failed to delete topic. Status code: " + response.getStatusCode());
            }
        } catch (ResourceAccessException e) {
            System.err.println("Error accessing FHIR server: " + e.getMessage());
        }
    }
    public void deleteSubscription(String subscriptionId) {
        try {
            String url = fhirServerUrl + "/Subscription/" + subscriptionId;
            System.out.println("3. eliminando" + url);
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            System.out.println("Subscription deleted successfully.");
        } catch (ResourceAccessException e) {
            System.err.println("Error accessing FHIR server: " + e.getMessage());
        }
    }
}
