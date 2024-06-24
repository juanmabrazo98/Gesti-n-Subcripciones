package us.dit.consentimientos.service.services;

import java.util.List;

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
}
