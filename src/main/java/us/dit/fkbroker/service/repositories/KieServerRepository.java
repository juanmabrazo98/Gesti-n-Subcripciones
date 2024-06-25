package us.dit.fkbroker.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import us.dit.fkbroker.service.entities.KieServer;

@Repository
public interface KieServerRepository extends JpaRepository<KieServer, String> {
}
