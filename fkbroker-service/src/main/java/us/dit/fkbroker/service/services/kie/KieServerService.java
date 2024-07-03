package us.dit.fkbroker.service.services.kie;

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

import us.dit.fkbroker.service.entities.KieServer;
import us.dit.fkbroker.service.entities.NotificationEP;
import us.dit.fkbroker.service.repositories.KieServerRepository;

/**
 * Servicio para gestionar las operaciones sobre los servidores KIE.
 */
@Service
public class KieServerService {

    private static final Logger logger = LoggerFactory.getLogger(KieServerService.class);

    @Autowired
    private KieServerRepository kieServerRepository;

    /**
     * Obtiene todos los servidores KIE.
     * 
     * @return una lista de objetos KieServer que representan todos los servidores KIE.
     */
    public List<KieServer> getAllKieServers() {
        return kieServerRepository.findAll();
    }

    /**
     * Guarda un servidor KIE en la base de datos.
     * 
     * @param kieServer el objeto KieServer a guardar.
     * @return el objeto KieServer guardado.
     */
    public KieServer saveKieServer(KieServer kieServer) {
        return kieServerRepository.save(kieServer);
    }

    /**
     * Elimina un servidor KIE de la base de datos por su URL.
     * 
     * @param url la URL del servidor KIE a eliminar.
     */
    public void deleteKieServer(String url) {
        kieServerRepository.deleteById(url);
    }

    /**
     * Envía una señal a todos los servidores KIE configurados.
     * 
     * @param notificationEP el objeto NotificationEP que contiene los detalles de la señal.
     * @param mensaje el mensaje a enviar como señal.
     */
    public void sendSignalToAllKieServers(NotificationEP notificationEP, String mensaje) {
        List<KieServer> kieServers = getAllKieServers();
        for (KieServer kieServer : kieServers) {
            String serverUrl = kieServer.getUrl();
            String username = kieServer.getUsu();
            String password = kieServer.getPwd();

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
            // Envío la señal a cada contenedor del servidor
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
