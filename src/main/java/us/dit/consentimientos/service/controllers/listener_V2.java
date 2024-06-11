package us.dit.consentimientos.service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dit.consentimientos.model.Signal;
import us.dit.consentimientos.service.services.fhir.FhirClient;
import us.dit.consentimientos.service.services.kie.KieUtilService;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.SubscriptionStatus;
import org.hl7.fhir.r5.model.Reference;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import java.util.Arrays;
import java.util.List;

@RestController
public class listener_V2 {

    private static final Logger logger = LogManager.getLogger();	

	@Autowired
	private KieUtilService kie;

    @Autowired
    private FhirClient fhirClient;

    //Establecemos un endpoint para recibir las notificaciones FHIR
    @PostMapping("/endpoint")
    public ResponseEntity<String> handleEndpoint(@RequestBody String json) {
        System.out.println("Received JSON:");
        System.out.println(json);

        // Variables para determinar el tipo de notificación
        boolean isResourceIncluded = false;
        String resourceId = null;
        String resourceType = null;
        String operationType = null;

        // Convertir el JSON recibido a un JSONObject
        JSONObject bundleJson = new JSONObject(json);

        // Obtener las entradas del bundle
        JSONArray entries = bundleJson.getJSONArray("entry");

        // Obtener el topic URL del recurso SubscriptionStatus
        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i).getJSONObject("resource");
            if (entry.getString("resourceType").equals("SubscriptionStatus")) {
                String topicUrl = entry.getString("topic");
                resourceType = fhirClient.getTopicResource(topicUrl);
                break;
            }
        }

        // Verificar si el recurso está incluido en el Bundle, obetener la operación y el ID del recurso relacionado
        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            if (entry.has("resource") && entry.getJSONObject("resource").getString("resourceType").equals(resourceType)) {
                isResourceIncluded = true;
                resourceId = entry.getJSONObject("resource").getString("id");
                operationType = entry.getJSONObject("request").getString("method");
                //System.out.println("Recurso " + resourceType + " incluido con ID: " + resourceId);
                break;
            } else if (entry.has("request") && entry.getJSONObject("request").getString("url").contains(resourceType)) {
                operationType = entry.getJSONObject("request").getString("method");
                if(!operationType.equals("DELETE")){
                    System.out.println("Operación no DELETE");
                    resourceId = entry.getString("fullUrl").split("/")[1];
                }
                //System.out.println("Recurso " + resourceType + " no incluido en el bundle, ID: " + resourceId);
            }
        }

        // Según el tipo de recurso se llamará a una función u otra, se pasan como argumentos el ID del recurso,
        // una bandera que indica si el recurso va incluido o no y el tipo de operación realizada
        System.out.println("ID: "+resourceId+". TIPO: "+resourceType+". Operación: "+operationType);
        switch (resourceType) {
            case "CarePlan":
                handleCarePlan(resourceId, isResourceIncluded, operationType);
                break;
            case "RequestOrchestration":
                handleRequestOrchestration(resourceId, isResourceIncluded, operationType);
                break;
            case "ServiceRequest":
                handleServiceRequest(resourceId, isResourceIncluded, operationType);
                break;
            case "DeviceRequest":
                handleDeviceRequest(resourceId, isResourceIncluded, operationType);
                break;
            case "EnrollmentRequest":
                handleEnrollmentRequest(resourceId, isResourceIncluded, operationType);
                break;
            case "Task":
                handleTask(resourceId, isResourceIncluded, operationType);
                break;
            case "Consent":
                handleConsent(resourceId, isResourceIncluded, operationType);
                break;
            case "DeviceDispense":
                handleDeviceDispense(resourceId, isResourceIncluded, operationType);
                break;
            case "QuestionnaireResponse":
                handleQuestionnaireResponse(resourceId, isResourceIncluded, operationType);
                break;
            default:
                System.err.println("Tipo de recurso no soportado.");
                break;
        }

        return ResponseEntity.ok(json);
    }

    // Métodos para manejar cada tipo de recurso en cada uno de ellos se distingue según la operación para determinar 
    // que señal enviar al servidor KIE
    private void handleCarePlan(String resourceId, boolean isResourceIncluded, String operationType) {
        System.out.println("El recurso CarePlan está " + (isResourceIncluded ? "incluido" : "no incluido") + " en el bundle.");
        System.out.println("Operación realizada: " + operationType);
        // Lógica adicional para manejar CarePlan
    }

    private void handleRequestOrchestration(String resourceId, boolean isResourceIncluded, String operationType) {
        System.out.println("El recurso RequestOrchestration está " + (isResourceIncluded ? "incluido" : "no incluido") + " en el bundle.");
        System.out.println("Operación realizada: " + operationType);
        // Lógica adicional para manejar RequestOrchestration
    }

    private void handleServiceRequest(String resourceId, boolean isResourceIncluded, String operationType) {
        //System.out.println("El recurso ServiceRequest está " + (isResourceIncluded ? "incluido" : "no incluido") + " en el bundle.");
        //System.out.println("ID del recurso: " + resourceId);
        //System.out.println("Operación realizada: " + operationType);
        switch(operationType){
            case "POST":
                System.out.println("Envio señal de creación ServiceRequest");
                break;
            case "PUT":
                System.out.println("Envio señal de modificación ServiceRequest");
                break;
            case "DELETE":
                System.out.println("Envio señal de eliminación ServiceRequest");
                break;    
        }
        // Lógica adicional para manejar ServiceRequest
    }

    private void handleDeviceRequest(String resourceId, boolean isResourceIncluded, String operationType) {
        System.out.println("El recurso DeviceRequest está " + (isResourceIncluded ? "incluido" : "no incluido") + " en el bundle.");
        System.out.println("Operación realizada: " + operationType);
        // Lógica adicional para manejar DeviceRequest
    }

    private void handleEnrollmentRequest(String resourceId, boolean isResourceIncluded, String operationType) {
        System.out.println("El recurso EnrollmentRequest está " + (isResourceIncluded ? "incluido" : "no incluido") + " en el bundle.");
        System.out.println("Operación realizada: " + operationType);
        // Lógica adicional para manejar EnrollmentRequest
    }

    private void handleTask(String resourceId, boolean isResourceIncluded, String operationType) {
        System.out.println("El recurso Task está " + (isResourceIncluded ? "incluido" : "no incluido") + " en el bundle.");
        System.out.println("Operación realizada: " + operationType);
        // Lógica adicional para manejar Task
    }

    private void handleConsent(String resourceId, boolean isResourceIncluded, String operationType) {
        System.out.println("El recurso Consent está " + (isResourceIncluded ? "incluido" : "no incluido") + " en el bundle.");
        System.out.println("Operación realizada: " + operationType);
        // Lógica adicional para manejar Consent
    }

    private void handleDeviceDispense(String resourceId, boolean isResourceIncluded, String operationType) {
        System.out.println("El recurso DeviceDispense está " + (isResourceIncluded ? "incluido" : "no incluido") + " en el bundle.");
        System.out.println("Operación realizada: " + operationType);
        // Lógica adicional para manejar DeviceDispense
    }

    private void handleQuestionnaireResponse(String resourceId, boolean isResourceIncluded, String operationType) {
        System.out.println("El recurso QuestionnaireResponse está " + (isResourceIncluded ? "incluido" : "no incluido") + " en el bundle.");
        System.out.println("Operación realizada: " + operationType);
        // Lógica adicional para manejar QuestionnaireResponse
    }

    @PostMapping()	
	public String sendSignal(@RequestBody Signal signal,HttpSession session) {
		/**
		 * Difunde una señal por todos los servidores KIE gestionados en la aplicación
		 */
		logger.info("Enviando una señal a todos");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails principal = (UserDetails) auth.getPrincipal();
		logger.info("Datos de usuario (principal)" + principal);
		kie.sendSignal(signal.getName(), signal.getMessage());		
		return "OK";
		}
}