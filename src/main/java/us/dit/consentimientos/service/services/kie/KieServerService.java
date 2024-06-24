package us.dit.consentimientos.service.services.kie;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.dit.consentimientos.service.entities.KieServer;
import us.dit.consentimientos.service.repositories.KieServerRepository;

@Service
public class KieServerService {

    private final KieServerRepository kieServerRepository;

    @Autowired
    public KieServerService(KieServerRepository kieServerRepository) {
        this.kieServerRepository = kieServerRepository;
    }

    public List<KieServer> getAllKieServers() {
        return kieServerRepository.findAll();
    }

    public KieServer saveKieServer(KieServer kieServer) {
        return kieServerRepository.save(kieServer);
    }

    public void deleteKieServer(String url) {
        kieServerRepository.deleteById(url);
    }
}

