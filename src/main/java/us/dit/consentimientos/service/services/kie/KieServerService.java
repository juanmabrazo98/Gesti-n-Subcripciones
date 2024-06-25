package us.dit.consentimientos.service.services.kie;

import java.util.List;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.dit.consentimientos.service.entities.KieServer;
import us.dit.consentimientos.service.entities.NotificationEP;
import us.dit.consentimientos.service.repositories.KieServerRepository;

@Service
public class KieServerService {

    private static final Logger logger = LoggerFactory.getLogger(KieServerService.class);

    @Autowired
    private KieServerRepository kieServerRepository;

    public List<KieServer> getAllKieServers() {
        return kieServerRepository.findAll();
    }

    public KieServer saveKieServer(KieServer kieServer) {
        return kieServerRepository.save(kieServer);
    }

    public void deleteKieServer(String url) {
        kieServerRepository.deleteById(url);
    }

    public void sendSignalToAllKieServers(NotificationEP notificationEP, String mensaje) {
        List<KieServer> kieServers = getAllKieServers();
        for (KieServer kieServer : kieServers) {
            String serverUrl = kieServer.getUrl();
            String username = kieServer.getUsu(); // assuming KieServer entity has username and password fields
            String password = kieServer.getPwd();

            logger.info("Aquí va el código para enviar a un único servidor kie");
            MarshallingFormat FORMAT = MarshallingFormat.JSON;
            KieServicesConfiguration conf;
            KieServicesClient kieServicesClient;
            ProcessServicesClient processClient;
            try {
                conf = KieServicesFactory.newRestConfiguration(serverUrl, username, password);
                conf.setMarshallingFormat(FORMAT);
                kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
                processClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
            } catch (Exception e) {
                logger.error("Error creando KieServicesClient o obteniendo ProcessServicesClient: ", e);
                continue;
            }
            //Envío la señal a cada contenedor del servidor
            try {
                KieContainerResourceList containersList = kieServicesClient.listContainers().getResult();
                List<KieContainerResource> kieContainers = containersList.getContainers();
                for (KieContainerResource container : kieContainers) {
                    logger.info("Enviando a " + serverUrl + ". la señal " + notificationEP.getSignalName());
                    processClient.signal(container.getContainerId(), notificationEP.getSignalName(), mensaje);
                }
            } catch (Exception e) {
                logger.error("Error enviando señal a los contenedores: ", e);
            }
        }
    }
}
