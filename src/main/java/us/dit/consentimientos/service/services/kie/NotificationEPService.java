package us.dit.consentimientos.service.services.kie;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.dit.consentimientos.service.entities.NotificationEP;
import us.dit.consentimientos.service.repositories.NotificationEPRepository;

@Service
public class NotificationEPService {
    @Autowired
    private NotificationEPRepository notificationEPRepository;

    public List<NotificationEP> getAllNotificationEPs() {
        return notificationEPRepository.findAll();
    }

    public NotificationEP saveNotificationEP(NotificationEP notificationEP) {
        return notificationEPRepository.save(notificationEP);
    }

    public void deleteNotificationEP(Long id) {
        notificationEPRepository.deleteById(id);
    }

    public Optional<NotificationEP> findNotificationEPByResourceAndInteraction(String resource, String interaction) {
        return notificationEPRepository.findByResourceAndInteraction(resource, interaction);
    }

    public Optional<NotificationEP> findById(Long id) {
        return notificationEPRepository.findById(id);
    }
}
