package us.dit.consentimientos.service.controllers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import us.dit.consentimientos.service.entities.NotificationEP;
import us.dit.consentimientos.service.services.fhir.FhirClient;
import us.dit.consentimientos.service.services.kie.KieServerService;
import us.dit.consentimientos.service.services.kie.NotificationEPService;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationEPService notificationEPService;

    @Autowired
    private KieServerService kieServerService;

    @Autowired
    private FhirClient fhirClient;

    @PostMapping("/{id}")
    public ResponseEntity<String> sendNotification(@PathVariable Long id, @RequestBody String json) {
        Optional<NotificationEP> optionalNotificationEP = notificationEPService.findById(id);
        if (optionalNotificationEP.isPresent()) {
            // Responder inmediatamente con 200 OK
            CompletableFuture.runAsync(() -> {
                NotificationEP notificationEP = optionalNotificationEP.get();
                String idRecurso=fhirClient.getNotificationResourceId(json);
                System.out.println("Llamamos a sendsignal. Id del recurso: "+idRecurso);
                kieServerService.sendSignalToAllKieServers(notificationEP, idRecurso);
            });
        } else {
            // Manejar el caso cuando no se encuentra la entidad
            throw new RuntimeException("NotificationEP not found with id: " + id);
        }

        return ResponseEntity.ok(json);
    }
}